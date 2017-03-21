package challenge.dbside.eav.collection.batching;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.loader.EntityAliases;
import org.hibernate.loader.plan.build.spi.LoadPlanTreePrinter;
import org.hibernate.loader.plan.exec.internal.AbstractLoadQueryDetails;
import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
import org.hibernate.loader.plan.exec.internal.EntityLoadQueryDetails;
import org.hibernate.loader.plan.exec.internal.FetchStats;
import org.hibernate.loader.plan.exec.internal.LoadQueryJoinAndFetchProcessor;
//import org.hibernate.loader.plan.exec.internal.EntityLoadQueryDetails.EntityLoaderReaderCollectorImpl;
import org.hibernate.loader.plan.exec.process.internal.AbstractRowReader;
import org.hibernate.loader.plan.exec.process.internal.EntityReferenceInitializerImpl;
import org.hibernate.loader.plan.exec.process.internal.EntityReturnReader;
import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessingContextImpl;
import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessorHelper;
import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessorImpl;
import org.hibernate.loader.plan.exec.process.spi.EntityReferenceInitializer;
import org.hibernate.loader.plan.exec.process.spi.ReaderCollector;
import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
import org.hibernate.loader.plan.exec.process.spi.RowReader;
import org.hibernate.loader.plan.exec.query.internal.SelectStatementBuilder;
import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
import org.hibernate.loader.plan.exec.spi.EntityReferenceAliases;
import org.hibernate.loader.plan.spi.CollectionReturn;
import org.hibernate.loader.plan.spi.EntityReturn;
import org.hibernate.loader.plan.spi.FetchSource;
import org.hibernate.loader.plan.spi.LoadPlan;
import org.hibernate.loader.plan.spi.QuerySpace;
import org.hibernate.loader.plan.spi.Return;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.hibernate.sql.ConditionFragment;
import org.hibernate.sql.DisjunctionFragment;
import org.hibernate.sql.InFragment;
import org.hibernate.type.ComponentType;
import org.hibernate.type.Type;
import org.hibernate.tool.hbm2ddl.EAVDeployerImpl;


public class EAVEntityLoadQueryDetails extends AbstractLoadQueryDetails {

	private final EntityReferenceAliases entityReferenceAliases;
	private final ReaderCollector readerCollector;
	
	protected EAVEntityLoadQueryDetails(LoadPlan loadPlan, String[] keyColumnNames,
			AliasResolutionContextImpl aliasResolutionContext, EntityReturn rootReturn,
			QueryBuildingParameters buildingParameters, SessionFactoryImplementor factory) {
		super(
				loadPlan,
				aliasResolutionContext,
				buildingParameters,
				modyfyKeyColumnsName(keyColumnNames, rootReturn),
				rootReturn,
				factory
		);
		
		this.entityReferenceAliases = aliasResolutionContext.generateEntityReferenceAliases(
				rootReturn.getQuerySpaceUid(),
				rootReturn.getEntityPersister()
		);
		this.readerCollector = new EntityLoaderReaderCollectorImpl(
				new EntityReturnReader( rootReturn ),
				new EntityReferenceInitializerImpl( rootReturn, entityReferenceAliases, true )
		);
		generate();
	}

	private static String[] modyfyKeyColumnsName(String[] keyColumns, EntityReturn rootReturn) {
		return keyColumns;
	}
	
	
	@Override
	protected ReaderCollector getReaderCollector() {
		return readerCollector;
	}

	@Override
	protected QuerySpace getRootQuerySpace() {
		return getQuerySpace( getRootEntityReturn().getQuerySpaceUid() );
	}

	private EntityReturn getRootEntityReturn() {
		return (EntityReturn) getRootReturn();
	}
	
	@Override
	protected String getRootTableAlias() {
		return entityReferenceAliases.getTableAlias();
	}

	@Override
	protected boolean shouldApplyRootReturnFilterBeforeKeyRestriction() {
		return false;
	}

	@Override
	protected void applyRootReturnSelectFragments(SelectStatementBuilder selectStatementBuilder) {
		final OuterJoinLoadable outerJoinLoadable = (OuterJoinLoadable) getRootEntityReturn().getEntityPersister();
		/*String selFragm = outerJoinLoadable.selectFragment(
				entityReferenceAliases.getTableAlias(),
				entityReferenceAliases.getColumnAliases().getSuffix()

		);*/
		String key = entityReferenceAliases.getTableAlias() + "." + "entity_id"  
				+ " as " + outerJoinLoadable.getKeyColumnNames()[0]; 
				
		String selFragm = key + colsBuf;
		selectStatementBuilder.appendSelectClauseFragment(selFragm);
	}

	private String colsBuf = "";
	
	
	/*=
	protected void applyRootReturnTableFragments(SelectStatementBuilder select) {
		final String fromTableFragment;
		final String rootAlias = entityReferenceAliases.getTableAlias();
		final OuterJoinLoadable outerJoinLoadable = (OuterJoinLoadable) getRootEntityReturn().getEntityPersister();
		if ( getQueryBuildingParameters().getLockOptions() != null ) {
			fromTableFragment = getSessionFactory().getDialect().appendLockHint(
					getQueryBuildingParameters().getLockOptions(),
					outerJoinLoadable.fromTableFragment( rootAlias )
			);
			select.setLockOptions( getQueryBuildingParameters().getLockOptions() );
		}
		else if ( getQueryBuildingParameters().getLockMode() != null ) {
			fromTableFragment = getSessionFactory().getDialect().appendLockHint(
					getQueryBuildingParameters().getLockMode(),
					outerJoinLoadable.fromTableFragment( rootAlias )
			);
			select.setLockMode( getQueryBuildingParameters().getLockMode() );
		}
		else {
			fromTableFragment = outerJoinLoadable.fromTableFragment( rootAlias );
		}
		select.appendFromClauseFragment( fromTableFragment + outerJoinLoadable.fromJoinFragment( rootAlias, true, true ) );
	}*/
	
	
	
	@Override
	protected void applyRootReturnTableFragments(SelectStatementBuilder select) {
		final String fromTableFragment;
		final String rootAlias = entityReferenceAliases.getTableAlias();
		final OuterJoinLoadable outerJoinLoadable = (OuterJoinLoadable) getRootEntityReturn().getEntityPersister();
		if ( getQueryBuildingParameters().getLockOptions() != null ) {
			throw new RuntimeException("not supported");
		}
		else if ( getQueryBuildingParameters().getLockMode() != null ) {
			fromTableFragment = EAVDeployerImpl.getTEntitiesName() + " " + rootAlias;
		}
		else {
			throw new RuntimeException("not supported");
		}
		String outerJoin = outerJoinLoadable.fromJoinFragment( rootAlias, true, true );
		
		
		String baseSelectValues = "(select * from "
				+ EAVDeployerImpl.getTValuesName()
				+ " where entity_id = ? and attribute_id = ?)";
		
		String joinings = "";
		
		
		//EntityAliases e = this.entityReferenceAliases.getColumnAliases();
		String[][] aliases = entityReferenceAliases.getColumnAliases().getSuffixedPropertyAliases();
		int i = 0;
		colsBuf += "/*{RRTFLoadQueryDetColBuf}*/";
		for(AttributeDefinition attr : outerJoinLoadable.getAttributes()) {
			if(!attr.getType().isCollectionType()) {
				String joining = "";
				String col = attr.getName();
				if(col.equals("type_of_entity")) {
					
					colsBuf += " ,\n " + rootAlias + ".parent_id" + " as " + aliases[i][0];
					i++;
					continue;
				}
				String table = "tbl_" + col;
				joining += "left outer join";
				joining += baseSelectValues;
				joining += " " + table + "\n";
				joining += " 	on " + table + ".attribute_id = " + "?\n";
				
				colsBuf += " ,\n 	tbl_" + col + "." + "text_value as " + aliases[i][0];
				
				joinings += joining;
			}
			i++;
		}
		colsBuf += "/*{RRTFLoadQueryDetColBuf}*/";
		
		String res = "/*{RRTFLoadQueryDet}*/" + fromTableFragment + "\n" + outerJoin 
				+ "\n" + joinings + "/*{RRTFLoadQueryDet}*/";
		select.appendFromClauseFragment(res);
	}

	@Override
	protected void applyRootReturnFilterRestrictions(SelectStatementBuilder selectStatementBuilder) {
		final Queryable rootQueryable = (Queryable) getRootEntityReturn().getEntityPersister();
		selectStatementBuilder.appendRestrictions(
				rootQueryable.filterFragment(
						entityReferenceAliases.getTableAlias(),
						Collections.emptyMap()
				)
		);
	}

	@Override
	protected void applyRootReturnWhereJoinRestrictions(SelectStatementBuilder selectStatementBuilder) {
		final Joinable joinable = (OuterJoinLoadable) getRootEntityReturn().getEntityPersister();
		
		String where = joinable.whereJoinFragment(
				entityReferenceAliases.getTableAlias(), true, true);
		
		selectStatementBuilder.appendRestrictions(where);
	}

	@Override
	protected void applyRootReturnOrderByFragments(SelectStatementBuilder selectStatementBuilder) {
	}

	
	private static class EntityLoaderReaderCollectorImpl extends ReaderCollectorImpl {
		private final EntityReturnReader entityReturnReader;

		public EntityLoaderReaderCollectorImpl(
				EntityReturnReader entityReturnReader,
				EntityReferenceInitializer entityReferenceInitializer) {
			this.entityReturnReader = entityReturnReader;
			add( entityReferenceInitializer );
		}

		public RowReader buildRowReader() {
			return new EntityLoaderRowReader( this );
		}

		public EntityReturnReader getReturnReader() {
			return entityReturnReader;
		}
	}

	private static class EntityLoaderRowReader extends AbstractRowReader {
		private final EntityReturnReader rootReturnReader;

		public EntityLoaderRowReader(EntityLoaderReaderCollectorImpl entityLoaderReaderCollector) {
			super( entityLoaderReaderCollector );
			this.rootReturnReader = entityLoaderReaderCollector.getReturnReader();
		}

		@Override
		public Object readRow(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException {
			final ResultSetProcessingContext.EntityReferenceProcessingState processingState =
					rootReturnReader.getIdentifierResolutionContext( context );
			// if the entity reference we are hydrating is a Return, it is possible that its EntityKey is
			// supplied by the QueryParameter optional entity information
			if ( context.shouldUseOptionalEntityInformation() && context.getQueryParameters().getOptionalId() != null ) {
				EntityKey entityKey = ResultSetProcessorHelper.getOptionalObjectKey(
						context.getQueryParameters(),
						context.getSession()
				);
				processingState.registerIdentifierHydratedForm( entityKey.getIdentifier() );
				processingState.registerEntityKey( entityKey );
				final EntityPersister entityPersister = processingState.getEntityReference().getEntityPersister();
				if ( entityPersister.getIdentifierType().isComponentType()  ) {
					final ComponentType identifierType = (ComponentType) entityPersister.getIdentifierType();
					if ( !identifierType.isEmbedded() ) {
						addKeyManyToOnesToSession(
								context,
								identifierType,
								entityKey.getIdentifier()
						);
					}
				}
			}
			return super.readRow( resultSet, context );
		}

		private void addKeyManyToOnesToSession(ResultSetProcessingContextImpl context, ComponentType componentType, Object component ) {
			for ( int i = 0 ; i < componentType.getSubtypes().length ; i++ ) {
				final Type subType = componentType.getSubtypes()[ i ];
				final Object subValue = componentType.getPropertyValue( component, i, context.getSession() );
				if ( subType.isEntityType() ) {
					( (Session) context.getSession() ).buildLockRequest( LockOptions.NONE ).lock( subValue );
				}
				else if ( subType.isComponentType() ) {
					addKeyManyToOnesToSession( context, (ComponentType) subType, subValue  );
				}
			}
		}

		@Override
		protected Object readLogicalRow(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException {
			return rootReturnReader.read( resultSet, context );
		}
	}
}
