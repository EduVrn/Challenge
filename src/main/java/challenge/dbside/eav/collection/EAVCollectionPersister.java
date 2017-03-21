package challenge.dbside.eav.collection;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cfg.Configuration;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.eav.EAVGlobalContext;
import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.jdbc.Expectation;
import org.hibernate.jdbc.Expectations;
import org.hibernate.loader.collection.CollectionInitializer;
import org.hibernate.mapping.Collection;

import org.hibernate.persister.collection.BasicCollectionPersister;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.sql.Delete;
import org.hibernate.sql.SelectFragment;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.type.AssociationType;
import org.hibernate.type.IntegerType;
import org.jboss.logging.Logger;

import challenge.dbside.eav.EAVPersister;
import challenge.dbside.eav.collection.batching.EAVBatchingCollectionInitializerBuilder;

import org.hibernate.tool.hbm2ddl.EAVDeployerImpl;


public class EAVCollectionPersister extends BasicCollectionPersister {
	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class,
			EAVCollectionPersister.class.getName() );

	//final static String eavRelationshipName = "relationship";
	final static String[] eavColumnName = {"entity_id1", "entity_id2", "attribute_id"};

	private static final String firstId = "entity_id1";
	private static final String secId = "entity_id2";
	private static final String attrId = "attribute_id";

	public EAVCollectionPersister(Collection collection, 
			CollectionRegionAccessStrategy cacheAccessStrategy,
			Configuration cfg, 
			SessionFactoryImplementor factory) throws MappingException, CacheException {

		super( collection, cacheAccessStrategy, cfg, factory);
	}


	private BasicBatchKey removeBatchKey;

	@Override
	public void remove(Serializable id, SessionImplementor session) throws HibernateException {
		if ( !isInverse && isRowDeleteEnabled() ) {

			if ( LOG.isDebugEnabled() ) {
				LOG.debugf( "Deleting collection: %s",
						MessageHelper.collectionInfoString( this, id, getFactory() ) );
			}

			// Remove all the old entries

			try {
				int offset = 1;
				PreparedStatement st = null;
				Expectation expectation = Expectations.appropriateExpectation( getDeleteAllCheckStyle() );
				boolean callable = isDeleteAllCallable();
				boolean useBatch = expectation.canBeBatched();
				String sql = getSQLDeleteString();
				if ( useBatch ) {
					if ( removeBatchKey == null ) {
						removeBatchKey = new BasicBatchKey(
								getRole() + "#REMOVE",
								expectation
								);
					}
					st = session.getTransactionCoordinator()
							.getJdbcCoordinator()
							.getBatch( removeBatchKey )
							.getBatchStatement( sql, callable );
				}
				else {
					st = session.getTransactionCoordinator()
							.getJdbcCoordinator()
							.getStatementPreparer()
							.prepareStatement( sql, callable );
				}

				try {
					offset += expectation.prepare( st );
					
					
					writeKey( st, id, offset, session );
					Integer attrId = null;
					
					String role = this.getRole();
					role = role.substring(role.lastIndexOf(".") + 1).toLowerCase();
					attrId = EAVGlobalContext.getTypeOfAttribute(role).getId();
					offset++;
					getKeyType().nullSafeSet( st, attrId, offset, session );
					
					
					
					if ( useBatch ) {
						session.getTransactionCoordinator()
						.getJdbcCoordinator()
						.getBatch( removeBatchKey )
						.addToBatch();
					}
					else {
						expectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
					}
				}
				catch ( SQLException sqle ) {
					if ( useBatch ) {
						session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
					}
					throw sqle;
				}
				finally {
					if ( !useBatch ) {
						session.getTransactionCoordinator().getJdbcCoordinator().release( st );
					}
				}

				LOG.debug( "Done deleting collection" );
			}
			catch ( SQLException sqle ) {
				throw sqlExceptionHelper.convert(
						sqle,
						"could not delete collection: " +
								MessageHelper.collectionInfoString( this, id, getFactory() ),
								getSQLDeleteString()
						);
			}

		}

	}

	@Override
	protected String generateDeleteString() {

		Delete delete = new Delete()
				.setTableName( qualifiedTableName )
				.addPrimaryKeyColumns( keyColumnNames );

		if ( hasWhere ) delete.setWhere( sqlWhereString );

		if ( getFactory().getSettings().isCommentsEnabled() ) {
			delete.setComment( "delete collection " + getRole() );
		}
		return "/*GDSColPersister*/" + delete.toStatementString() + " and " + " attribute_id = ? "  + "/*GDSColPersister*/";
	}

	@Override
	protected String generateDeleteRowString() {

		Delete delete = new Delete()
				.setTableName( qualifiedTableName );

		if ( hasIdentifier ) {
			delete.addPrimaryKeyColumns( new String[]{ identifierColumnName } );
		}
		else if ( hasIndex && !indexContainsFormula ) {
			delete.addPrimaryKeyColumns( ArrayHelper.join( keyColumnNames, indexColumnNames ) );
		}
		else {
			delete.addPrimaryKeyColumns( keyColumnNames );
			delete.addPrimaryKeyColumns( elementColumnNames, elementColumnIsInPrimaryKey, elementColumnWriters );
		}

		if ( getFactory().getSettings().isCommentsEnabled() ) {
			delete.setComment( "delete collection row " + getRole() );
		}

		return "/*GDRSColPersister*/" + delete.toStatementString() + " and attribute_id = ?" + "/*GDRSColPersister*/";
	}

	@Override
	protected String generateInsertRowString() {
		String sqlSuper = super.generateInsertRowString();

		//change
		//	insert into relationship (entity_id1, entity_id2) values (?, ?)
		//to
		//	insert into relationship (entity_id1, entity_id2, attribute_id) values (?, ?, {id_attr})

		//TODO HARDCODE
		int start = "insert into ".length() + "eav_relationship".length() + " (".length() 
				+ firstId.length() + ",".length() + secId.length() + 1;

		String changeIns = sqlSuper.substring(0, start) + ", " + attrId + ") values(?, ?, ?)";
		//changeIns += "0)";

		return changeIns;
	}

	@Override
	public String selectFragment(
			Joinable rhs,
			String rhsAlias,
			String lhsAlias,
			String entitySuffix,
			String collectionSuffix,
			boolean includeCollectionColumns) {
		// we need to determine the best way to know that two joinables
		// represent a single many-to-many...
		if ( rhs != null && isManyToMany() && !rhs.isCollection() ) {
			AssociationType elementType = ( ( AssociationType ) getElementType() );
			if ( rhs.equals( elementType.getAssociatedJoinable( getFactory() ) ) ) {
				String m = manyToManySelectFragment( rhs, rhsAlias, lhsAlias, collectionSuffix );
				return m;
			}
		}
		return includeCollectionColumns ? selectFragment( lhsAlias, collectionSuffix ) : "";
	}

	private String manyToManySelectFragment(
			Joinable rhs,
			String rhsAlias,
			String lhsAlias,
			String collectionSuffix) {
		SelectFragment frag = generateSelectFragment( lhsAlias, collectionSuffix );

		String[] elementColumnNames = rhs.getKeyColumnNames();
		frag.addColumns( rhsAlias, elementColumnNames, elementColumnAliases );
		appendIndexColumns( frag, lhsAlias );
		appendIdentifierColumns( frag, lhsAlias );

		String res = frag.toFragmentString().substring( 2 ); 

		return "/*MTMSelFragmCollection*/" + res + "/*MTMSelFragmCollection*/"; //strip leading ','
	}


	@Override
	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
		EAVPersister e = (EAVPersister)this.getElementPersister();
		String res = e.propertySelectFragmentFragment(alias, "suf", includeSubclasses).toFragmentString();

		String baseSelectValues = "(select attribute_id, entity_id, text_value from "
				+ EAVDeployerImpl.getTValuesName()
				+ " where attribute_id = ?)";
		
		//TODO change join entity_id1 to entity_id2 to back relation
		EntityMetamodel mm = e.getEntityMetamodel();
		String[] columnReaderTemplates = e.getSubclassColumnReaderTemplateClosure();
		int[] columnTableNumbers = e.getSubclassColumnTableNumberClosurePub(); 
		//e.getColumn
		String joinings = "";
		String joinedAlias = "";
		String name = this.getCollectionType().getRole();
		name = name.substring(name.lastIndexOf(".") + 1).toLowerCase();

		joinings += "  \n and " + alias + ".attribute_id = " + EAVGlobalContext.getTypeOfAttribute(name).getId() + "\n ";
		if(!alias.equals("this_")) {
			joinedAlias = alias;
			alias = "eav_join_";
		}
		
		for(int i = 0; i < mm.getPropertySpan(); i++ ) {
			for(int k = 0; k < e.getPropertyColumnNames(i).length; k++) {
				String joining = "";
				String col = e.getPropertyColumnNames(i)[k].toLowerCase();
				if(col.equals("type_of_entity")) {
					continue;
				}
				col = col.replaceAll("_", "");
				String table = alias + "tbl_" + col; //"employee2_" /*+ "0_"* / + "tbl_" + col;
				String subalias = EAVPersister.generateTableAlias( alias, columnReaderTemplates[k], 
						columnTableNumbers[k] ).toLowerCase();

				joining += "left outer join";
				//joining += "inner join";
				Integer attributeId = null;
				try {
					attributeId = EAVGlobalContext.getTypeOfAttribute(col).getId();
				}
				catch(Exception ex) {
					throw new RuntimeException("stop");
					//ex.printStackTrace();;
				}
				Integer lastParam = baseSelectValues.lastIndexOf("?"); 

				String selValuesAttr = new StringBuilder(baseSelectValues).replace(
						lastParam, lastParam + 1, attributeId.toString()).toString(); 

				joining += selValuesAttr;

				joining += " as " + table + "\n";
				
				String colName = this.getElementColumnNames()[0];
				joining += " 	on " + table + ".attribute_id = " 
						+ attributeId + "\n" + "		and "  
						//+ table + ".entity_id = " + joinedAlias + ".entity_id2\n";
						+ table + ".entity_id = " + joinedAlias + "." + colName
								+ "\n";
				//colsBuf += ",\n 	tbl_" + col + "." + "text_value as " + col;

				joinings += joining;
			}	
		}
		

		return "\n /*{FJFColllection}*/  " + joinings + "\n /*{FJFColllection}*/  ";
	}

	@Override
	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses, Set<String> treatAsDeclarations) {
		return "/*{fromJoinFragment add}*/";
	}

	@Override
	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
		//String name = this.getNodeName().toLowerCase();
		//Integer attributeId = EAVGlobalContext.getTypeOfAttribute(name).getId();
		 //+ alias + "." + this.getElementColumnNames()[0] + " = " + attributeId + 
		
		return "/*{whereJoinFragment}*/ " + " /*{whereJoinFragment}*/ ";
	}

	@Override
	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses, Set<String> treatAsDeclarations) {
		return "/*{whereJoinFragment add}*/";
	}

	@Override
	protected CollectionInitializer createCollectionInitializer(LoadQueryInfluencers loadQueryInfluencers)
			throws MappingException {
		return EAVBatchingCollectionInitializerBuilder.getBuilder( getFactory() )
				.createBatchingCollectionInitializer( this, batchSize, getFactory(), loadQueryInfluencers );
	}

	protected int writeElement(PreparedStatement st, Object elt, int i, SessionImplementor session)
			throws HibernateException, SQLException {
		getElementType().nullSafeSet( st, elt, i, elementColumnIsSettable, session );
		return i + ArrayHelper.countTrue( elementColumnIsSettable );

	}

	@Override
	public void recreate(PersistentCollection collection, Serializable id, SessionImplementor session)
			throws HibernateException {

		if ( !isInverse && isRowInsertEnabled() ) {

			if ( LOG.isDebugEnabled() ) {
				LOG.debugf( "Inserting collection: %s",
						MessageHelper.collectionInfoString( this, collection, id, session ) );
			}

			try {
				// create all the new entries
				Iterator entries = collection.entries( this );
				if ( entries.hasNext() ) {
					Expectation expectation = Expectations.appropriateExpectation( getInsertCheckStyle() );
					collection.preInsert( this );
					int i = 0;
					int count = 0;
					while ( entries.hasNext() ) {

						final Object entry = entries.next();
						if ( collection.entryExists( entry, i ) ) {
							int offset = 1;
							PreparedStatement st = null;
							boolean callable = isInsertCallable();
							boolean useBatch = expectation.canBeBatched();
							String sql = getSQLInsertRowString();

							if ( useBatch ) {
								if ( recreateBatchKey == null ) {
									recreateBatchKey = new BasicBatchKey(
											getRole() + "#RECREATE",
											expectation
											);
								}
								st = session.getTransactionCoordinator()
										.getJdbcCoordinator()
										.getBatch( recreateBatchKey )
										.getBatchStatement( sql, callable );
							}
							else {
								st = session.getTransactionCoordinator()
										.getJdbcCoordinator()
										.getStatementPreparer()
										.prepareStatement( sql, callable );
							}

							try {
								offset += expectation.prepare( st );

								// TODO: copy/paste from insertRows()
								int loc = writeKey( st, id, offset, session );
								if ( hasIdentifier ) {
									loc = writeIdentifier( st, collection.getIdentifier( entry, i ), loc, session );
								}
								if ( hasIndex /* && !indexIsFormula */) {
									loc = writeIndex( st, collection.getIndex( entry, i, this ), loc, session );
								}

								loc = writeElement( st, collection.getElement( entry ), loc, session );
								String attrName = this.getNodeName().toLowerCase();
								Integer attrId = null;
								try {
									attrId = EAVGlobalContext.getTypeOfAttribute(attrName).getId();
								}
								catch(Exception ex) {
									ex.printStackTrace();
								}
								IntegerType attrPar = new IntegerType();
								attrPar.nullSafeSet(st, attrId, loc, session);
								loc++;

								if ( useBatch ) {
									session.getTransactionCoordinator()
									.getJdbcCoordinator()
									.getBatch( recreateBatchKey )
									.addToBatch();
								}
								else {
									expectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
								}

								collection.afterRowInsert( this, entry, i );
								count++;
							}
							catch ( SQLException sqle ) {
								if ( useBatch ) {
									session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
								}
								throw sqle;
							}
							finally {
								if ( !useBatch ) {
									session.getTransactionCoordinator().getJdbcCoordinator().release( st );
								}
							}

						}
						i++;
					}

					LOG.debugf( "Done inserting collection: %s rows inserted", count );

				}
				else {
					LOG.debug( "Collection was empty" );
				}
			}
			catch ( SQLException sqle ) {
				throw sqlExceptionHelper.convert(
						sqle,
						"could not insert collection: " +
								MessageHelper.collectionInfoString( this, collection, id, session ),
								getSQLInsertRowString()
						);
			}
		}
	}

}

