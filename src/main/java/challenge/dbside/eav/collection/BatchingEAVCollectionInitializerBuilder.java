package challenge.dbside.eav.collection;

import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.collection.BatchingCollectionInitializerBuilder;
import org.hibernate.loader.collection.CollectionInitializer;
import org.hibernate.loader.collection.DynamicBatchingCollectionInitializerBuilder;
import org.hibernate.loader.collection.PaddedBatchingCollectionInitializerBuilder;
import org.hibernate.persister.collection.QueryableCollection;

public class BatchingEAVCollectionInitializerBuilder extends BatchingCollectionInitializerBuilder {

    public static BatchingCollectionInitializerBuilder getBuilder(SessionFactoryImplementor factory) {
        switch (factory.getSettings().getBatchFetchStyle()) {
            case PADDED: {
                return PaddedBatchingCollectionInitializerBuilder.INSTANCE;
            }
            case DYNAMIC: {
                return DynamicBatchingCollectionInitializerBuilder.INSTANCE;
            }
            default: {
                return org.hibernate.loader.collection.plan.LegacyBatchingCollectionInitializerBuilder.INSTANCE;
            }
        }
    }

    @Override
    protected CollectionInitializer createRealBatchingCollectionInitializer(QueryableCollection persister,
            int maxBatchSize, SessionFactoryImplementor factory, LoadQueryInfluencers influencers) {
        throw new RuntimeException("not supported");
    }

    @Override
    protected CollectionInitializer createRealBatchingOneToManyInitializer(QueryableCollection persister,
            int maxBatchSize, SessionFactoryImplementor factory, LoadQueryInfluencers influencers) {
        throw new RuntimeException("not supported");
    }

}
