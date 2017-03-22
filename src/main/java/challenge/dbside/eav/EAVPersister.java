package challenge.dbside.eav;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.MappingException;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.engine.OptimisticLockStyle;
import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.jdbc.Expectation;
import org.hibernate.jdbc.Expectations;
import org.hibernate.loader.entity.UniqueEntityLoader;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Selectable;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.sql.Delete;
import org.hibernate.sql.Insert;
import org.hibernate.sql.SelectFragment;
import org.hibernate.sql.Update;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

import challenge.dbside.eav.batching.EAVBatchingEntityLoaderBuilder;

import org.hibernate.eav.EAVGlobalContext;

import org.hibernate.tool.hbm2ddl.EAVDeployerImpl;

public class EAVPersister extends SingleTableEntityPersister {

    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, EAVPersister.class.getName());

    /*
	static final int countTable = 2;
	static final int ENTITY_TABLE = 0;
	static final int VALUES_TABLE = 1;
	private static final String attrFieldName = "attribute_id";
	private static final String[] availableTableNames = {
			"entities", "values"};*/
    private static final String attrFieldName = "attribute_id";

    private final boolean[] subclassColumnLazyClosure;
    private final boolean[] subclassColumnSelectableClosure;
    private final boolean[] subclassFormulaLazyClosure;

    private int batchSize;

    public EAVPersister(PersistentClass persistentClass, EntityRegionAccessStrategy cacheAccessStrategy,
            NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy, SessionFactoryImplementor factory,
            Mapping mapping) throws HibernateException {
        super(persistentClass, cacheAccessStrategy, naturalIdRegionAccessStrategy, factory, mapping);
        this.batchSize = persistentClass.getBatchSize();

        ArrayList columnSelectables = new ArrayList();
        ArrayList formulasLazy = new ArrayList();
        ArrayList columnsLazy = new ArrayList();
        final boolean lazyAvailable = isInstrumented();
        Iterator iter = persistentClass.getSubclassPropertyClosureIterator();
        while (iter.hasNext()) {
            Property prop = (Property) iter.next();

            Iterator colIter = prop.getColumnIterator();

            Boolean lazy = Boolean.valueOf(prop.isLazy() && lazyAvailable);
            while (colIter.hasNext()) {
                Selectable thing = (Selectable) colIter.next();
                if (thing.isFormula()) {
                    formulasLazy.add(lazy);
                } else {
                    columnsLazy.add(lazy);
                    columnSelectables.add(Boolean.valueOf(prop.isSelectable()));
                }
            }
        }
        subclassColumnLazyClosure = ArrayHelper.toBooleanArray(columnsLazy);
        subclassColumnSelectableClosure = ArrayHelper.toBooleanArray(columnSelectables);
        subclassFormulaLazyClosure = ArrayHelper.toBooleanArray(formulasLazy);
    }

    private BasicBatchKey updateBatchKey;

    @Override
    protected String generateDeleteString(int j) {
        Delete delete = new Delete()
                .setTableName(getTableName(j))
                .addPrimaryKeyColumns(getKeyColumns(j));
        if (j == 0) {
            delete.setVersionColumnName(getVersionColumnName());
        }
        if (getFactory().getSettings().isCommentsEnabled()) {
            delete.setComment("delete " + getEntityName());
        }
        return "/*GDSPersister*/" + delete.toStatementString() + "/*GDSPersister*/";
    }

    private BasicBatchKey deleteBatchKey;

    @Override
    protected void delete(
            final Serializable id,
            final Object version,
            final int j,
            final Object object,
            final String sql,
            final SessionImplementor session,
            final Object[] loadedState) throws HibernateException {

        if (isInverseTable(j)) {
            return;
        }

        final boolean useVersion = j == 0 && isVersioned();
        final boolean callable = isDeleteCallable(j);
        final Expectation expectation = Expectations.appropriateExpectation(deleteResultCheckStyles[j]);
        final boolean useBatch = j == 0 && isBatchable() && expectation.canBeBatched();
        if (useBatch && deleteBatchKey == null) {
            deleteBatchKey = new BasicBatchKey(
                    getEntityName() + "#DELETE",
                    expectation
            );
        }

        final boolean traceEnabled = LOG.isTraceEnabled();
        if (traceEnabled) {
            LOG.tracev("Deleting entity: {0}", MessageHelper.infoString(this, id, getFactory()));
            if (useVersion) {
                LOG.tracev("Version: {0}", version);
            }
        }

        if (isTableCascadeDeleteEnabled(j)) {
            if (traceEnabled) {
                LOG.tracev("Delete handled by foreign key constraint: {0}", getTableName(j));
            }
            return; //EARLY EXIT!
        }

        try {
            //Render the SQL query
            PreparedStatement delete;
            int index = 1;
            if (useBatch) {
                delete = session.getTransactionCoordinator()
                        .getJdbcCoordinator()
                        .getBatch(deleteBatchKey)
                        .getBatchStatement(sql, callable);
            } else {
                delete = session.getTransactionCoordinator()
                        .getJdbcCoordinator()
                        .getStatementPreparer()
                        .prepareStatement(sql, callable);
            }

            try {

                index += expectation.prepare(delete);

                // Do the key. The key is immutable so we can use the _current_ object state - not necessarily
                // the state at the time the delete was issued
                getIdentifierType().nullSafeSet(delete, id, index, session);
                index += getIdentifierColumnSpan();

                // We should use the _current_ object state (ie. after any updates that occurred during flush)
                if (useVersion) {
                    getVersionType().nullSafeSet(delete, version, index, session);
                } else if (isAllOrDirtyOptLocking() && loadedState != null) {
                    boolean[] versionability = getPropertyVersionability();
                    Type[] types = getPropertyTypes();
                    for (int i = 0; i < getEntityMetamodel().getPropertySpan(); i++) {
                        if (isPropertyOfTable(i, j) && versionability[i]) {
                            // this property belongs to the table and it is not specifically
                            // excluded from optimistic locking by optimistic-lock="false"
                            boolean[] settable = types[i].toColumnNullness(loadedState[i], getFactory());
                            types[i].nullSafeSet(delete, loadedState[i], index, settable, session);
                            index += ArrayHelper.countTrue(settable);
                        }
                    }
                }

                if (useBatch) {
                    session.getTransactionCoordinator().getJdbcCoordinator().getBatch(deleteBatchKey).addToBatch();
                } else {
                    check(session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate(delete), id, j, expectation, delete);
                }

            } catch (SQLException sqle) {
                if (useBatch) {
                    session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
                }
                throw sqle;
            } finally {
                if (!useBatch) {
                    session.getTransactionCoordinator().getJdbcCoordinator().release(delete);
                }
            }

        } catch (SQLException sqle) {
            throw getFactory().getSQLExceptionHelper().convert(
                    sqle,
                    "could not delete: "
                    + MessageHelper.infoString(this, id, getFactory()),
                    sql
            );

        }

    }

    public void delete(Serializable id, Object version, Object object, SessionImplementor session)
            throws HibernateException {
        final int span = getTableSpan();
        boolean isImpliedOptimisticLocking = !getEntityMetamodel().isVersioned() && isAllOrDirtyOptLocking();
        Object[] loadedState = null;
        if (isImpliedOptimisticLocking) {
            // need to treat this as if it where optimistic-lock="all" (dirty does *not* make sense);
            // first we need to locate the "loaded" state
            //
            // Note, it potentially could be a proxy, so doAfterTransactionCompletion the location the safe way...
            final EntityKey key = session.generateEntityKey(id, this);
            Object entity = session.getPersistenceContext().getEntity(key);
            if (entity != null) {
                EntityEntry entry = session.getPersistenceContext().getEntry(entity);
                loadedState = entry.getLoadedState();
            }
        }

        final String[] deleteStrings;
        if (isImpliedOptimisticLocking && loadedState != null) {
            // we need to utilize dynamic delete statements
            deleteStrings = generateSQLDeletStrings(loadedState);
        } else {
            // otherwise, utilize the static delete statements
            deleteStrings = getSQLDeleteStrings();
        }

        for (int j = span - 1; j >= 0; j--) {
            delete(id, version, j, object, deleteStrings[j], session, loadedState);
        }

    }

    private String[] generateSQLDeletStrings(Object[] loadedState) {
        int span = getTableSpan();
        String[] deleteStrings = new String[span];
        for (int j = span - 1; j >= 0; j--) {
            Delete delete = new Delete()
                    .setTableName(getTableName(j))
                    .addPrimaryKeyColumns(getKeyColumns(j));
            if (getFactory().getSettings().isCommentsEnabled()) {
                delete.setComment("delete " + getEntityName() + " [" + j + "]");
            }

            boolean[] versionability = getPropertyVersionability();
            Type[] types = getPropertyTypes();
            for (int i = 0; i < getEntityMetamodel().getPropertySpan(); i++) {
                if (isPropertyOfTable(i, j) && versionability[i]) {
                    // this property belongs to the table and it is not specifically
                    // excluded from optimistic locking by optimistic-lock="false"
                    String[] propertyColumnNames = getPropertyColumnNames(i);
                    boolean[] propertyNullness = types[i].toColumnNullness(loadedState[i], getFactory());
                    for (int k = 0; k < propertyNullness.length; k++) {
                        if (propertyNullness[k]) {
                            delete.addWhereFragment(propertyColumnNames[k] + " = ?");
                        } else {
                            delete.addWhereFragment(propertyColumnNames[k] + " is null");
                        }
                    }
                }
            }
            deleteStrings[j] = "/*GSDSPersister*/" + delete.toStatementString() + "/*GSDSPersister*/";
        }
        return deleteStrings;
    }

    @Override
    public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
        // NOTE : Not calling createJoin here is just a performance optimization

        //return fromTableFragmentA(alias);
        return getSubclassTableSpan() == 1
                ? ""
                : createJoin(alias, innerJoin, includeSubclasses, Collections.<String>emptySet()).toFromFragmentString();
    }

    protected boolean update(
            final Serializable id,
            final Object[] fields,
            final Object[] oldFields,
            final Object rowId,
            final boolean[] includeProperty,
            final int j,
            final Object oldVersion,
            final Object object,
            final String sql,
            final SessionImplementor session) throws HibernateException {

        final Expectation expectation = Expectations.appropriateExpectation(updateResultCheckStyles[j]);
        final boolean useBatch = j == 0 && expectation.canBeBatched() && isBatchable(); //note: updates to joined tables can't be batched...
        if (useBatch && updateBatchKey == null) {
            updateBatchKey = new BasicBatchKey(
                    getEntityName() + "#UPDATE",
                    expectation
            );
        }
        final boolean callable = isUpdateCallable(j);
        final boolean useVersion = j == 0 && isVersioned();

        if (LOG.isTraceEnabled()) {
            LOG.tracev("Updating entity: {0}", MessageHelper.infoString(this, id, getFactory()));
            if (useVersion) {
                LOG.tracev("Existing version: {0} -> New version:{1}", oldVersion, fields[getVersionProperty()]);
            }
        }

        try {
            int index = 1; // starting index
            final PreparedStatement update;
            if (useBatch) {
                update = session.getTransactionCoordinator()
                        .getJdbcCoordinator()
                        .getBatch(updateBatchKey)
                        .getBatchStatement(sql, callable);
            } else {
                update = session.getTransactionCoordinator()
                        .getJdbcCoordinator()
                        .getStatementPreparer()
                        .prepareStatement(sql, callable);
            }

            try {
                index += expectation.prepare(update);

                //Now write the values of fields onto the prepared statement
                index = dehydrate(id, fields, rowId, includeProperty, getPropertyColumnUpdateable(), j, update, session, index, true);

                // Write any appropriate versioning conditional parameters
                if (useVersion && getEntityMetamodel().getOptimisticLockStyle() == OptimisticLockStyle.VERSION) {
                    throw new RuntimeException("not supported");
                } else if (isAllOrDirtyOptLocking() && oldFields != null) {
                    boolean[] versionability = getPropertyVersionability(); //TODO: is this really necessary????
                    boolean[] includeOldField = getEntityMetamodel().getOptimisticLockStyle() == OptimisticLockStyle.ALL
                            ? getPropertyUpdateability()
                            : includeProperty;
                    Type[] types = getPropertyTypes();
                    for (int i = 0; i < getEntityMetamodel().getPropertySpan(); i++) {
                        boolean include = includeOldField[i]
                                && isPropertyOfTable(i, j)
                                && versionability[i]; //TODO: is this really necessary????
                        if (include) {
                            boolean[] settable = types[i].toColumnNullness(oldFields[i], getFactory());
                            types[i].nullSafeSet(
                                    update,
                                    oldFields[i],
                                    index,
                                    settable,
                                    session
                            );
                            index += ArrayHelper.countTrue(settable);
                        }
                    }
                }

                if (useBatch) {
                    session.getTransactionCoordinator().getJdbcCoordinator().getBatch(updateBatchKey).addToBatch();
                    return true;
                } else {
                    return check(session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate(update), id, j, expectation, update);
                }

            } catch (SQLException e) {
                if (useBatch) {
                    session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
                }
                throw e;
            } finally {
                if (!useBatch) {
                    session.getTransactionCoordinator().getJdbcCoordinator().release(update);
                }
            }

        } catch (SQLException e) {
            throw getFactory().getSQLExceptionHelper().convert(
                    e,
                    "could not update: " + MessageHelper.infoString(this, id, getFactory()),
                    sql
            );
        }
    }

    @Override
    public String filterFragment(String alias) throws MappingException {
        String name = this.getDiscriminatorValue().toString().toLowerCase();
        String result = alias + "." + "type_of_entity = " + EAVGlobalContext.getTypeOfEntity(name).getId();
        //String result = discriminatorFilterFragment(alias);
        if (hasWhere()) {
            result += " and " + getSQLWhereString(alias);
        }
        return result;
    }

    @Override
    public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
        // NOTE : Not calling createJoin here is just a performance optimization
        //this.getPropertyColumnNames(0);

        String name = this.getEntityMetamodel().getName();
        name = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
        return getSubclassTableSpan() == 1
                ? ""
                : createJoin(alias, innerJoin, includeSubclasses, Collections.<String>emptySet()).toWhereFragmentString();
    }

    @Override
    public String fromTableFragment(String name) {
        String tableAndAlias = EAVDeployerImpl.getTEntitiesName() + " " + name;

        String baseSelectValues = "(select * from "
                + EAVDeployerImpl.getTValuesName()
                + " where "
                + " attribute_id = ";
        String joinings = "";

        int i = 0;
        int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
        String[] columnAliases = getSubclassColumnAliasClosure();
        String[] columnReaderTemplates = getSubclassColumnReaderTemplateClosure();

        for (i = 0; i < getSubclassColumnClosure().length; i++) {
            boolean selectable = (!subclassColumnLazyClosure[i])
                    && !isSubclassTableSequentialSelect(columnTableNumbers[i])
                    /*TODO lazy mode&&*/ && subclassColumnSelectableClosure[i];
            if (selectable) {
                String joining = "";

                String cName = columnReaderTemplates[i].substring("$PlaceHolder$.".length()).toLowerCase();
                //TODO HARDCODE
                if (cName.toLowerCase().equals("type_of_entity")) {
                    continue;
                }
                String table = generateTableAlias(name + "tbl_" + cName/*columnReaderTemplates[i]*/, columnTableNumbers[i]);
                cName = cName.replaceAll("_", "");
                String idAttr = EAVGlobalContext.getTypeOfAttribute(cName).getId().toString();

                joining += "left outer join";
                //joining += "inner join";
                joining += baseSelectValues + idAttr + ")";
                joining += " " + table + "\n";
                joining += " 	on " + table + ".attribute_id = " + idAttr + "\n"
                        + "		 and " + table + "." + "entity_id = " + name + ".entity_id\n";

                joinings += joining;
            }
        }

        return "/*{FTFPersister}*/" + tableAndAlias + "\n " + joinings + "/*{FTFPersister}*/";
    }

    protected String generateUpdateString(final boolean[] includeProperty,
            final int j,
            final Object[] oldFields,
            final boolean useRowId) {

        // select the correct row by either pk or rowid
        String updates = "";
        for (int i = 0; i < getEntityMetamodel().getPropertySpan(); i++) {
            if (includeProperty[i] && isPropertyOfTable(i, j)) {
                for (int k = 0; k < getPropertyColumnNames(i).length; k++) {
                    Update update = new Update(getFactory().getDialect()).setTableName(EAVDeployerImpl.getTValuesName());
                    String col = getPropertyColumnNames(i)[k].toLowerCase();
                    //TODO HARDCODE
                    if (col.equals("type_of_entity")) {
                        continue;
                    }

                    if (useRowId) {
                        update.addPrimaryKeyColumns(new String[]{rowIdName}); //TODO: eventually, rowIdName[j]
                    } else {
                        update.addPrimaryKeyColumns(getKeyColumns(j));
                    }

                    String[] ar = {"text_value"};
                    boolean[] ar1 = {getPropertyColumnUpdateable()[i][k]};
                    String[] ar3 = {getPropertyColumnWriters(i)[k]};

                    update.addColumns(ar, ar1, ar3);

                    if (j == 0 && isVersioned() && getEntityMetamodel().getOptimisticLockStyle() == OptimisticLockStyle.VERSION) {
                        throw new RuntimeException("not supported");
                    } else if (isAllOrDirtyOptLocking() && oldFields != null) {
                        throw new RuntimeException("not supported");
                    }
                    if (getFactory().getSettings().isCommentsEnabled()) {
                        throw new RuntimeException("not supported");
                    }

                    update.addWhereColumn(EAVPersister.attrFieldName);

                    StringBuilder sqlUpdate = new StringBuilder(update.toStatementString());
                    col = col.replaceAll("_", "");
                    try {
                        sqlUpdate.replace(sqlUpdate.lastIndexOf("?"),
                                sqlUpdate.lastIndexOf("?") + 1,
                                EAVGlobalContext.getTypeOfAttribute(col).getId().toString());
                    } catch (Exception ex) {
                        ex.printStackTrace();

                    }
                    sqlUpdate.append(";\n");
                    updates += sqlUpdate.toString();
                }
            }
        }

        return updates;
    }

    private boolean isAllOrDirtyOptLocking() {
        return getEntityMetamodel().getOptimisticLockStyle() == OptimisticLockStyle.DIRTY
                || getEntityMetamodel().getOptimisticLockStyle() == OptimisticLockStyle.ALL;
    }

    @Override
    public String selectFragment(String alias, String suffix) {
        String selFragm = identifierSelectFragment(alias, suffix);
        //TODO HARDCODE
        String[] columnReaderTemplates = getSubclassColumnReaderTemplateClosure();
        String[] columnAliases = getSubclassColumnAliasClosure();
        String cName = columnReaderTemplates[0].substring("$PlaceHolder$.".length()).toLowerCase();
        String type_of_entity = alias + "." + cName + " as " + columnAliases[0] + suffix;
        selFragm += ", " + type_of_entity;

        String selFragmProp = propertySelectFragment(alias, suffix, false);
        String ret = "/*SFPersister*/" + selFragm + selFragmProp + "/*SFPersister*/";
        return ret;
    }

    public SelectFragment propertySelectFragmentFragment(
            String tableAlias,
            String suffix,
            boolean allProperties) {
        SelectFragment select = new SelectFragment()
                .setSuffix(suffix)
                .setUsedAliases(getIdentifierAliases());
        if (!tableAlias.equals("this_")) {
            tableAlias = "eav_join_";
        }

        int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
        String[] columnAliases = getSubclassColumnAliasClosure();
        String[] columnReaderTemplates = getSubclassColumnReaderTemplateClosure();
        for (int i = 0; i < getSubclassColumnClosure().length; i++) {
            boolean selectable = (allProperties || !subclassColumnLazyClosure[i])
                    && !isSubclassTableSequentialSelect(columnTableNumbers[i])
                    /*TODO lazy mode&&*/ && subclassColumnSelectableClosure[i];
            if (selectable) {

                String cName = columnReaderTemplates[i].substring("$PlaceHolder$.".length()).toLowerCase();
                //TODO HARDCODE
                if (cName.equals("type_of_entity")) {
                    continue;
                }
                String subalias = generateTableAlias(tableAlias + "tbl_" + cName/*columnReaderTemplates[i]*/, columnTableNumbers[i]);
                select.addColumnTemplate(subalias, "$PlaceHolder$.text_value", columnAliases[i]);
            }
        }

        int[] formulaTableNumbers = getSubclassFormulaTableNumberClosure();
        String[] formulaTemplates = getSubclassFormulaTemplateClosure();
        String[] formulaAliases = getSubclassFormulaAliasClosure();
        for (int i = 0; i < getSubclassFormulaTemplateClosure().length; i++) {
            throw new RuntimeException("not supported");
        }

        if (getEntityMetamodel().hasSubclasses()) {
            addDiscriminatorToSelect(select, tableAlias, suffix);
        }

        if (hasRowId()) {
            select.addColumn(tableAlias, rowIdName, ROWID_ALIAS);
        }

        return select;
    }

    public static String generateTableAlias(String rootAlias, String templateColName, int tableNumber) {
        StringBuilder buf = new StringBuilder().append(rootAlias);
        if (!rootAlias.endsWith("_")) {
            buf.append('_');
        }
        return buf.append(tableNumber).append('_').toString() + "tbl_"
                + templateColName.substring("$PlaceHolder$.".length());
    }

    public int[] getSubclassColumnTableNumberClosurePub() {
        return getSubclassColumnTableNumberClosure();
    }

    @Override
    protected String generateInsertString(boolean identityInsert, boolean[] includeProperty, int j) {
        Map<String, String> persistentColumns = new LinkedHashMap();
        Insert insert = new Insert(getFactory().getDialect())
                .setTableName(getTableName(j));

        // add normal properties
        for (int i = 0; i < this.getEntityMetamodel().getPropertySpan(); i++) {
            // the incoming 'includeProperty' array only accounts for insertable defined at the root level, it
            // does not account for partially generated composites etc.  We also need to account for generation
            // values

            if (includeProperty[i] && isPropertyOfTable(i, j)) {

                for (int k = 0; k < getPropertyColumnNames(i).length; k++) {
                    if (getPropertyColumnInsertable()[i][k] == true) {
                        String col = getPropertyColumnNames(i)[k];
                        persistentColumns.put(col, getPropertyColumnWriters(i)[k]);
                    }
                }
                insert.addColumns(getPropertyColumnNames(i), getPropertyColumnInsertable()[i], getPropertyColumnWriters(i));
            }
        }

        // add the discriminator
        if (j == 0) {
            System.out.println("WARNING: addDiscriminatorToInsert( insert ) not supported");
            //addDiscriminatorToInsert( insert );
        }

        // add the primary key
        if (j == 0 && identityInsert) {
            throw new RuntimeException("unsupported insert.addIdentityColumn( getKeyColumns( 0 )[0] )");
            //insert.addIdentityColumn( getKeyColumns( 0 )[0] );
        } else {
            int count = getKeyColumns(j).length;
            System.out.println("Count column: " + count);
            for (int k = 0; k < count; k++) {
                //String col = getPropertyColumnNames(j)[k];
                String col = getKeyColumns(j)[k];
                persistentColumns.put(col, "?");
            }
            insert.addColumns(getKeyColumns(j));
        }

        if (getFactory().getSettings().isCommentsEnabled()) {
            throw new RuntimeException("insert.setComment( 'insert ' + getEntityName() );");
        }

        String result = toStatementString(persistentColumns, getKeyColumns(j), false);

        // append the SQL to return the generated identifier
        if (j == 0 && identityInsert && useInsertSelectIdentity()) { //TODO: suck into Insert
            result = getFactory().getDialect().appendIdentitySelectToInsert(result);
        }

        return result;
    }

    @Override
    protected String generateIdentityInsertString(boolean[] includeProperty) {
        throw new RuntimeException("not supported");
    }

    private String toStatementString(Map<String, String> persistentColumns, String[] keyColumns, boolean isIdentityInsert) {
        StringBuilder buf = new StringBuilder();

        buf.append("insert into ")
                .append(EAVDeployerImpl.getTEntitiesName());
        //.append(availableTableNames[ENTITY_TABLE]);
        buf.append(" (");
        if (!isIdentityInsert) {
            buf.append("entity_id, ");
        }
        buf.append("parent_id, ");
        buf.append("type_of_entity");

        buf.append(") ");
        buf.append("VALUES( ");
        if (!isIdentityInsert) {
            //entity_id
            buf.append("?, ");
        }
        //type entity
        buf.append("0, ");
        buf.append(" ?);");

        //
        if (persistentColumns.size() == 0) {
            throw new RuntimeException("unsupported buf.append(' ').append( dialect.getNoColumnsInsertString() )");
        } else {
            Iterator iter = persistentColumns.keySet().iterator();
            while (iter.hasNext()) {
                String attrName = ((String) iter.next()).toLowerCase();
                if (Arrays.asList(keyColumns).contains(attrName)) {
                    continue;
                }
                //TODO HARDCODE
                if (attrName.equals("type_of_entity")) {
                    continue;
                }
                attrName = attrName.replaceAll("_", "");

                buf.append("\n");
                buf.append("insert into " + EAVDeployerImpl.getTValuesName());
                buf.append("(");
                buf.append("text_value, attribute_id, entity_id");
                buf.append(") ");
                buf.append(" values(?, ");
                //buf.append(GlobalContext.availableAttributes.get(attrName).getId());
                try {
                    buf.append(EAVGlobalContext.getTypeOfAttribute(attrName).getId());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                buf.append(", ?");
                buf.append(")");
                buf.append(";");
            }
        }
        System.out.println(buf.toString());
        return buf.toString();
    }

    @Override
    public Serializable insert(Object[] fields, Object object, SessionImplementor session)
            throws HibernateException {
        throw new RuntimeException("not supported");
    }

    private BasicBatchKey inserBatchKey;

    @Override
    protected int dehydrate(
            final Serializable id,
            final Object[] fields,
            final Object rowId,
            final boolean[] includeProperty,
            final boolean[][] includeColumns,
            final int j,
            final PreparedStatement ps,
            final SessionImplementor session,
            int index,
            boolean isUpdate) throws SQLException, HibernateException {

        if (LOG.isTraceEnabled()) {
            LOG.tracev("Dehydrating entity: {0}", MessageHelper.infoString(this, id, getFactory()));
        }
        if (!isUpdate) {
            index += dehydrateId(id, rowId, ps, session, index);

            getPropertyTypes()[0].nullSafeSet(ps, fields[0], index, includeColumns[0], session);
            index += ArrayHelper.countTrue(includeColumns[0]);

        }
        for (int i = 1/*type_of_entity*/; i < getEntityMetamodel().getPropertySpan(); i++) {
            if (includeProperty[i] && isPropertyOfTable(i, j)) {
                if (ArrayHelper.countTrue(includeColumns[i]) != 0) {
                    /*if(i == 1) {
						continue;
					}*/
                    getPropertyTypes()[i].nullSafeSet(ps, fields[i], index, includeColumns[i], session);
                    index += ArrayHelper.countTrue(includeColumns[i]); //TODO:  this is kinda slow...						
                    index += dehydrateId(id, rowId, ps, session, index);
                }
            }
        }
        return index;
    }

    private int dehydrateId(
            final Serializable id,
            final Object rowId,
            final PreparedStatement ps,
            final SessionImplementor session,
            int index) throws SQLException {
        if (rowId != null) {
            ps.setObject(index, rowId);
            return 1;
        } else if (id != null) {
            getIdentifierType().nullSafeSet(ps, id, index, session);
            return getIdentifierColumnSpan();
        }
        return 0;
    }

    private String getRootAlias() {
        return StringHelper.generateAlias(getEntityName());
    }

    //TODO is used?
    @Override
    protected String generateSnapshotSelectString() {
        //TODO is '?' or without where ??
        String baseSelectValues = "(select * from "
                + EAVDeployerImpl.getTValuesName()
                + " where entity_id = ? and attribute_id = ?)";

        String selectSQL = "";
        String colsBuf = "e.entity_id as id";

        selectSQL += "select ";

        String joinings = "";
        for (int i = 0; i < getEntityMetamodel().getPropertySpan(); i++) {
            for (int k = 0; k < getPropertyColumnNames(i).length; k++) {
                String joining = "";

                String col = getPropertyColumnNames(i)[k];
                String table = "tbl_" + col;

                joining += "left outer join";
                joining += baseSelectValues;
                joining += " " + table + "\n";
                joining += " 	on " + table + ".attribute_id = " + "?\n";

                colsBuf += ",\n 	tbl_" + col + "." + "text_value as " + col;

                joinings += joining;
            }
        }
        String from = "\nfrom " + EAVDeployerImpl.getTEntitiesName() + " e\n";
        String where = "where e.entity_id = ?";
        selectSQL += colsBuf + from + joinings;
        selectSQL += where;

        //TODO change logging
        System.out.println("generateSnapshotSelectString:");
        System.out.println(selectSQL + "\n");

        return selectSQL;
    }

    @Override
    protected UniqueEntityLoader createEntityLoader(
            LockMode lockMode,
            LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
        //TODO: disable batch loading if lockMode > READ?
        SessionFactoryImplementor factory = getFactory();

        EAVBatchingEntityLoaderBuilder builder = EAVBatchingEntityLoaderBuilder.getBuilder(factory);
        UniqueEntityLoader loader = builder.buildLoader(this, batchSize, lockMode, factory, loadQueryInfluencers);
        return loader;
    }

}
