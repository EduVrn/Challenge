package challenge.dbside.eav.collection.batching;

import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.collection.BasicCollectionLoader;
import org.hibernate.loader.collection.CollectionInitializer;
import org.hibernate.loader.collection.OneToManyLoader;
import org.hibernate.persister.collection.QueryableCollection;

/**
 * Contract for building {@link CollectionInitializer} instances capable of
 * performing batch-fetch loading.
 *
 * @author Steve Ebersole
 *
 * @changer sergpc@yandex.ru
 *
 * @info change BatchingCollectionInitializerBuilder 4 EAV Model implementation
 *
 * @see org.hibernate.loader.BatchFetchStyle
 */
public abstract class EAVBatchingCollectionInitializerBuilder {

    public static EAVBatchingCollectionInitializerBuilder getBuilder(SessionFactoryImplementor factory) {
        switch (factory.getSettings().getBatchFetchStyle()) {
            case PADDED: {
                throw new RuntimeException("not supported");
            }
            case DYNAMIC: {
                throw new RuntimeException("not supported");
            }
            default: {
                return BaseEAVBatchingCollectionInitializerBuilder.INSTANCE;
            }
        }
    }

    /**
     * Builds a batch-fetch capable CollectionInitializer for basic and
     * many-to-many collections (collections with a dedicated collection table).
     *
     * @param persister THe collection persister
     * @param maxBatchSize The maximum number of keys to batch-fetch together
     * @param factory The SessionFactory
     * @param influencers Any influencers that should affect the built query
     *
     * @return The batch-fetch capable collection initializer
     */
    public CollectionInitializer createBatchingCollectionInitializer(
            QueryableCollection persister,
            int maxBatchSize,
            SessionFactoryImplementor factory,
            LoadQueryInfluencers influencers) {
        if (maxBatchSize <= 1) {
            // no batching
            return buildNonBatchingLoader(persister, factory, influencers);
        }

        return createRealBatchingCollectionInitializer(persister, maxBatchSize, factory, influencers);
    }

    protected abstract CollectionInitializer createRealBatchingCollectionInitializer(
            QueryableCollection persister,
            int maxBatchSize,
            SessionFactoryImplementor factory,
            LoadQueryInfluencers influencers);

    /**
     * Builds a batch-fetch capable CollectionInitializer for one-to-many
     * collections (collections without a dedicated collection table).
     *
     * @param persister THe collection persister
     * @param maxBatchSize The maximum number of keys to batch-fetch together
     * @param factory The SessionFactory
     * @param influencers Any influencers that should affect the built query
     *
     * @return The batch-fetch capable collection initializer
     */
    public CollectionInitializer createBatchingOneToManyInitializer(
            QueryableCollection persister,
            int maxBatchSize,
            SessionFactoryImplementor factory,
            LoadQueryInfluencers influencers) {
        if (maxBatchSize <= 1) {
            // no batching
            return buildNonBatchingLoader(persister, factory, influencers);
        }

        return createRealBatchingOneToManyInitializer(persister, maxBatchSize, factory, influencers);
    }

    protected abstract CollectionInitializer createRealBatchingOneToManyInitializer(
            QueryableCollection persister,
            int maxBatchSize,
            SessionFactoryImplementor factory,
            LoadQueryInfluencers influencers);

    protected CollectionInitializer buildNonBatchingLoader(
            QueryableCollection persister,
            SessionFactoryImplementor factory,
            LoadQueryInfluencers influencers) {
        return persister.isOneToMany()
                ? new OneToManyLoader(persister, factory, influencers)
                : new BasicCollectionLoader(persister, factory, influencers);
    }
}
