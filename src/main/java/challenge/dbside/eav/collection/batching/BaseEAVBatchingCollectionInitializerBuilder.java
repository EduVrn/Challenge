package challenge.dbside.eav.collection.batching;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.loader.Loader;
import org.hibernate.loader.collection.BasicCollectionLoader;
import org.hibernate.loader.collection.CollectionInitializer;
import org.hibernate.loader.collection.OneToManyLoader;
import org.hibernate.loader.collection.plan.BatchingCollectionInitializer;
import org.hibernate.persister.collection.QueryableCollection;

public class BaseEAVBatchingCollectionInitializerBuilder extends EAVAbstractBatchingCollectionInitializerBuilder {

    public static final BaseEAVBatchingCollectionInitializerBuilder INSTANCE = new BaseEAVBatchingCollectionInitializerBuilder();

    @Override
    public CollectionInitializer createRealBatchingCollectionInitializer(
            QueryableCollection persister,
            int maxBatchSize,
            SessionFactoryImplementor factory,
            LoadQueryInfluencers loadQueryInfluencers) throws MappingException {

        int[] batchSizes = ArrayHelper.getBatchSizes(maxBatchSize);
        Loader[] loaders = new Loader[batchSizes.length];
        for (int i = 0; i < batchSizes.length; i++) {
            loaders[i] = new BasicCollectionLoader(persister, batchSizes[i], factory, loadQueryInfluencers);
        }
        return new EAVBatchingCollectionInitializer(persister, batchSizes, loaders);
    }

    @Override
    public CollectionInitializer createRealBatchingOneToManyInitializer(
            QueryableCollection persister,
            int maxBatchSize,
            SessionFactoryImplementor factory,
            LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
        //throw new RuntimeException("not supported");
        final int[] batchSizes = ArrayHelper.getBatchSizes(maxBatchSize);
        final Loader[] loaders = new Loader[batchSizes.length];
        for (int i = 0; i < batchSizes.length; i++) {
            loaders[i] = new OneToManyLoader(persister, batchSizes[i], factory, loadQueryInfluencers);
        }
        return new EAVBatchingCollectionInitializer(persister, batchSizes, loaders);
    }

    public static class EAVBatchingCollectionInitializer extends BatchingCollectionInitializer {

        private final int[] batchSizes;
        private final Loader[] loaders;

        public EAVBatchingCollectionInitializer(
                QueryableCollection persister,
                int[] batchSizes,
                Loader[] loaders) {
            super(persister);
            this.batchSizes = batchSizes;
            this.loaders = loaders;
        }

        public void initialize(Serializable id, SessionImplementor session) throws HibernateException {
            Serializable[] batch = session.getPersistenceContext().getBatchFetchQueue()
                    .getCollectionBatch(getCollectionPersister(), id, batchSizes[0]);

            for (int i = 0; i < batchSizes.length - 1; i++) {
                final int smallBatchSize = batchSizes[i];
                if (batch[smallBatchSize - 1] != null) {
                    Serializable[] smallBatch = new Serializable[smallBatchSize];
                    System.arraycopy(batch, 0, smallBatch, 0, smallBatchSize);
                    loaders[i].loadCollectionBatch(session, smallBatch, getCollectionPersister().getKeyType());
                    return; //EARLY EXIT!
                }
            }

            loaders[batchSizes.length - 1].loadCollection(session, id, getCollectionPersister().getKeyType());
        }
    }
}
