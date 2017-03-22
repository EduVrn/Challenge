package challenge.dbside.eav.collection.batching.loader;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.eav.EAVGlobalContext;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.loader.collection.CollectionInitializer;
import org.hibernate.loader.collection.plan.CollectionLoader;
import org.hibernate.loader.plan.build.internal.FetchStyleLoadPlanBuildingAssociationVisitationStrategy;
import org.hibernate.loader.plan.build.spi.MetamodelDrivenLoadPlanBuilder;
import org.hibernate.loader.plan.exec.internal.AbstractLoadPlanBasedLoader;
import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
import org.hibernate.loader.plan.spi.LoadPlan;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

import challenge.dbside.eav.collection.batching.EAVBatchingLoadQueryDetailsFactory;


/*AbstractLoadPlanBasedCollectionInitializer*/
public class EAVCollectionLoader extends AbstractLoadPlanBasedLoader implements CollectionInitializer {

    private static final Logger log = CoreLogging.logger(CollectionLoader.class);

    public static Builder forCollection(QueryableCollection collectionPersister) {
        return new Builder(collectionPersister);
    }

    private final QueryableCollection collectionPersister;
    private final LoadQueryDetails staticLoadQuery;

    public EAVCollectionLoader(QueryableCollection collectionPersister, QueryBuildingParameters buildingParameters) {
        super(collectionPersister.getFactory());
        this.collectionPersister = collectionPersister;

        final FetchStyleLoadPlanBuildingAssociationVisitationStrategy strategy
                = new FetchStyleLoadPlanBuildingAssociationVisitationStrategy(
                        collectionPersister.getFactory(),
                        buildingParameters.getQueryInfluencers(),
                        buildingParameters.getLockMode() != null
                                ? buildingParameters.getLockMode()
                                : buildingParameters.getLockOptions().getLockMode()
                );

        final LoadPlan plan = MetamodelDrivenLoadPlanBuilder.buildRootCollectionLoadPlan(strategy, collectionPersister);
        this.staticLoadQuery = EAVBatchingLoadQueryDetailsFactory.makeCollectionLoadQueryDetails(
                collectionPersister,
                plan,
                buildingParameters
        );
    }

    public void initialize(Serializable id, SessionImplementor session)
            throws HibernateException {
        if (log.isDebugEnabled()) {
            log.debugf("Loading collection: %s",
                    MessageHelper.collectionInfoString(collectionPersister, id, getFactory()));
        }

        try {
            final QueryParameters qp = new QueryParameters();
            String name = collectionPersister.getCollectionType().getRole();
            name = name.substring(name.lastIndexOf(".") + 1).toLowerCase();

            final Serializable[] ids = new Serializable[]{id, EAVGlobalContext.getTypeOfAttribute(name).getId()};
            qp.setPositionalParameterTypes(new Type[]{collectionPersister.getKeyType(), collectionPersister.getKeyType()});
            qp.setPositionalParameterValues(ids);
            qp.setCollectionKeys(ids);

            executeLoad(
                    session,
                    qp,
                    staticLoadQuery,
                    true,
                    null
            );
        } catch (SQLException sqle) {
            throw getFactory().getSQLExceptionHelper().convert(
                    sqle,
                    "could not initialize a collection: "
                    + MessageHelper.collectionInfoString(collectionPersister, id, getFactory()),
                    staticLoadQuery.getSqlStatement()
            );
        }

        log.debug("Done loading collection");
    }

    protected QueryableCollection collectionPersister() {
        return collectionPersister;
    }

    @Override
    protected LoadQueryDetails getStaticLoadQuery() {
        return staticLoadQuery;
    }

    @Override
    protected int[] getNamedParameterLocs(String name) {
        throw new AssertionFailure("no named parameters");
    }

    @Override
    protected void autoDiscoverTypes(ResultSet rs) {
        throw new AssertionFailure("Auto discover types not supported in this loader");
    }

    public static class Builder {

        private final QueryableCollection collectionPersister;
        private int batchSize = 1;
        private LoadQueryInfluencers influencers = LoadQueryInfluencers.NONE;

        private Builder(QueryableCollection collectionPersister) {
            this.collectionPersister = collectionPersister;
        }

        public Builder withBatchSize(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public Builder withInfluencers(LoadQueryInfluencers influencers) {
            this.influencers = influencers;
            return this;
        }

        public EAVCollectionLoader byKey() {
            final QueryBuildingParameters buildingParameters = new QueryBuildingParameters() {
                public LoadQueryInfluencers getQueryInfluencers() {
                    return influencers;
                }

                public int getBatchSize() {
                    return batchSize;
                }

                public LockMode getLockMode() {
                    return LockMode.NONE;
                }

                public LockOptions getLockOptions() {
                    return null;
                }
            };
            return new EAVCollectionLoader(collectionPersister, buildingParameters);
        }
    }
}
