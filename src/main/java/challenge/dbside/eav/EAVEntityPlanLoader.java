package challenge.dbside.eav;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.MappingException;
import org.hibernate.eav.EAVGlobalContext;
import org.hibernate.eav.EAVTypeOfAttribute;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.loader.entity.UniqueEntityLoader;
import org.hibernate.loader.plan.build.internal.FetchGraphLoadPlanBuildingStrategy;
import org.hibernate.loader.plan.build.internal.FetchStyleLoadPlanBuildingAssociationVisitationStrategy;
import org.hibernate.loader.plan.build.internal.LoadGraphLoadPlanBuildingStrategy;
import org.hibernate.loader.plan.build.spi.LoadPlanBuildingAssociationVisitationStrategy;
import org.hibernate.loader.plan.build.spi.MetamodelDrivenLoadPlanBuilder;
import org.hibernate.loader.plan.exec.internal.AbstractLoadPlanBasedLoader;
import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
import org.hibernate.loader.plan.spi.LoadPlan;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

import challenge.dbside.eav.collection.batching.EAVBatchingLoadQueryDetailsFactory;

public class EAVEntityPlanLoader extends AbstractLoadPlanBasedLoader implements UniqueEntityLoader {

    //any level logger
    protected static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, EAVEntityPlanLoader.class.getName());

    private static final Logger log = CoreLogging.logger(EAVEntityPlanLoader.class);

    private final OuterJoinLoadable entityPersister;
    private final Type uniqueKeyType;
    private final String entityName;

    private final LoadQueryDetails eavLoadQuery;

    private EAVEntityPlanLoader(
            SessionFactoryImplementor factory,
            OuterJoinLoadable persister,
            String[] uniqueKeyColumnNames,
            Type uniqueKeyType,
            QueryBuildingParameters buildingParameters) throws MappingException {
        super(factory);

        this.entityPersister = persister;
        this.uniqueKeyType = uniqueKeyType;
        this.entityName = entityPersister.getEntityName();

        final LoadPlanBuildingAssociationVisitationStrategy strategy;
        if (buildingParameters.getQueryInfluencers().getFetchGraph() != null) {
            strategy = new FetchGraphLoadPlanBuildingStrategy(
                    factory, buildingParameters.getQueryInfluencers(), buildingParameters.getLockMode()
            );
        } else if (buildingParameters.getQueryInfluencers().getLoadGraph() != null) {
            strategy = new LoadGraphLoadPlanBuildingStrategy(
                    factory, buildingParameters.getQueryInfluencers(), buildingParameters.getLockMode()
            );
        } else {
            strategy = new FetchStyleLoadPlanBuildingAssociationVisitationStrategy(
                    factory, buildingParameters.getQueryInfluencers(), buildingParameters.getLockMode()
            );
        }

        final LoadPlan plan = MetamodelDrivenLoadPlanBuilder.buildRootEntityLoadPlan(strategy, entityPersister);
        this.eavLoadQuery = EAVBatchingLoadQueryDetailsFactory.makeEntityLoadQueryDetails(
                plan,
                uniqueKeyColumnNames,
                buildingParameters,
                factory
        );

        if (log.isDebugEnabled()) {
            if (buildingParameters.getLockOptions() != null) {
                log.debugf(
                        "Static select for entity %s [%s:%s]: %s",
                        getEntityName(),
                        buildingParameters.getLockOptions().getLockMode(),
                        buildingParameters.getLockOptions().getTimeOut(),
                        getStaticLoadQuery().getSqlStatement()
                );
            } else if (buildingParameters.getLockMode() != null) {
                log.debugf(
                        "Static select for entity %s [%s]: %s",
                        getEntityName(),
                        buildingParameters.getLockMode(),
                        getStaticLoadQuery().getSqlStatement()
                );
            }
        }
    }

    @Override
    protected LoadQueryDetails getStaticLoadQuery() {
        return eavLoadQuery;
    }

    protected String getEntityName() {
        return entityName;
    }

    public final List loadEntityBatch(
            final SessionImplementor session,
            final Serializable[] ids,
            final Type idType,
            final Object optionalObject,
            final String optionalEntityName,
            final Serializable optionalId,
            final EntityPersister persister,
            LockOptions lockOptions) throws HibernateException {

        if (log.isDebugEnabled()) {
            log.debugf("Batch loading entity: %s", MessageHelper.infoString(persister, ids, getFactory()));
        }

        final Type[] types = new Type[ids.length];
        Arrays.fill(types, idType);
        List result;
        try {
            final QueryParameters qp = new QueryParameters();
            qp.setPositionalParameterTypes(types);
            qp.setPositionalParameterValues(ids);
            qp.setLockOptions(lockOptions);

            result = executeLoad(
                    session,
                    qp,
                    eavLoadQuery,
                    false,
                    null
            );
        } catch (SQLException sqle) {
            throw getFactory().getSQLExceptionHelper().convert(
                    sqle,
                    "could not load an entity batch: " + MessageHelper.infoString(entityPersister, ids, getFactory()),
                    eavLoadQuery.getSqlStatement()
            );
        }

        log.debug("Done entity batch load");

        return result;

    }

    @Deprecated
    public Object load(Serializable id, Object optionalObject, SessionImplementor session) throws HibernateException {
        return load(id, optionalObject, session, LockOptions.NONE);
    }

    public Object load(Serializable id, Object optionalObject, SessionImplementor session, LockOptions lockOptions) {

        final Object result;
        try {
            final QueryParameters qp = new QueryParameters();

            String simpleName = entityPersister.getEntityName()
                    .substring(entityPersister.getEntityName().lastIndexOf(".") + 1).toLowerCase();

            List<EAVTypeOfAttribute> attrs = EAVGlobalContext.getSimpleTypeOfAttribute(simpleName);

            Type[] types = new Type[(attrs.size()) * 3 + 1];
            Arrays.fill(types, entityPersister.getIdentifierType());
            qp.setPositionalParameterTypes(types);

            Object[] args = new Object[(attrs.size()) * 3 + 1];
            int i = 0;
            for (EAVTypeOfAttribute t : attrs) {
                /*if(t.getName().equals("parent_id")) {
					continue;
				}*/
                args[i++] = id;
                args[i++] = t.getId();
                args[i++] = t.getId();
            }
            args[i++] = id;

            qp.setPositionalParameterValues(args);
            qp.setOptionalObject(optionalObject);
            qp.setOptionalEntityName(entityPersister.getEntityName());
            qp.setOptionalId(id);
            qp.setLockOptions(lockOptions);

            final List results = executeLoad(
                    session,
                    qp,
                    eavLoadQuery,
                    false,
                    null
            );
            result = extractEntityResult(results);
        } catch (SQLException sqle) {
            throw getFactory().getSQLExceptionHelper().convert(
                    sqle,
                    "could not load an entity: " + MessageHelper.infoString(
                            entityPersister,
                            id,
                            entityPersister.getIdentifierType(),
                            getFactory()
                    ),
                    eavLoadQuery.getSqlStatement()
            );
        }

        log.debugf("Done entity load : %s#%s", getEntityName(), id);
        return result;
    }

    protected Object extractEntityResult(List results) {
        if (results.size() == 0) {
            return null;
        } else if (results.size() == 1) {
            return results.get(0);
        } else {
            final Object row = results.get(0);
            if (row.getClass().isArray()) {
                // the logical type of the result list is List<Object[]>.  See if the contained
                // array contains just one element, and return that if so
                final Object[] rowArray = (Object[]) row;
                if (rowArray.length == 1) {
                    return rowArray[0];
                }
            } else {
                return row;
            }
        }

        throw new HibernateException("Unable to interpret given query results in terms of a load-entity query");
    }

    protected int[] getNamedParameterLocs(String name) {
        throw new AssertionFailure("no named parameters");
    }

    protected void autoDiscoverTypes(ResultSet rs) {
        throw new AssertionFailure("Auto discover types not supported in this loader");
    }

    public static Builder forEntity(OuterJoinLoadable persister) {
        return new Builder(persister);
    }

    public static class Builder {

        private final OuterJoinLoadable persister;
        private int batchSize = 1;
        private LoadQueryInfluencers influencers = LoadQueryInfluencers.NONE;
        private LockMode lockMode = LockMode.NONE;
        private LockOptions lockOptions;

        public Builder(OuterJoinLoadable persister) {
            this.persister = persister;
        }

        public Builder withBatchSize(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public Builder withInfluencers(LoadQueryInfluencers influencers) {
            this.influencers = influencers;
            return this;
        }

        public Builder withLockMode(LockMode lockMode) {
            this.lockMode = lockMode;
            return this;
        }

        public Builder withLockOptions(LockOptions lockOptions) {
            this.lockOptions = lockOptions;
            return this;
        }

        public EAVEntityPlanLoader byPrimaryKey() {
            return byUniqueKey(persister.getIdentifierColumnNames(), persister.getIdentifierType());
        }

        public EAVEntityPlanLoader byUniqueKey(String[] keyColumnNames, Type keyType) {
            return new EAVEntityPlanLoader(
                    persister.getFactory(),
                    persister,
                    keyColumnNames,
                    keyType,
                    new QueryBuildingParameters() {
                public LoadQueryInfluencers getQueryInfluencers() {
                    return influencers;
                }

                public int getBatchSize() {
                    return batchSize;
                }

                public LockMode getLockMode() {
                    return lockMode;
                }

                public LockOptions getLockOptions() {
                    return lockOptions;
                }
            }
            );
        }
    }
}
