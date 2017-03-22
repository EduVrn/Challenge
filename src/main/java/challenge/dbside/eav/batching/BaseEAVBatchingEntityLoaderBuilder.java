package challenge.dbside.eav.batching;

import java.io.Serializable;
import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.loader.entity.UniqueEntityLoader;
import org.hibernate.loader.entity.plan.BatchingEntityLoader;
import org.hibernate.loader.entity.plan.EntityLoader;
import org.hibernate.persister.entity.OuterJoinLoadable;

public class BaseEAVBatchingEntityLoaderBuilder extends EAVAbstractBatchingEntityLoaderBuilder {

    public static final BaseEAVBatchingEntityLoaderBuilder INSTANCE = new BaseEAVBatchingEntityLoaderBuilder();

    public BaseEAVBatchingEntityLoaderBuilder() {
        super();
    }

    @Override
    protected UniqueEntityLoader buildBatchingLoader(OuterJoinLoadable persister, int batchSize, LockMode lockMode,
            SessionFactoryImplementor factory, LoadQueryInfluencers influencers) {
        throw new RuntimeException("not supported");
        //return EAVEntityLoader.forEntity( persister ).withLockMode( lockMode ).withInfluencers( influencers ).byPrimaryKey();
    }

    @Override
    protected UniqueEntityLoader buildBatchingLoader(OuterJoinLoadable persister, int batchSize,
            LockOptions lockOptions, SessionFactoryImplementor factory, LoadQueryInfluencers influencers) {
        throw new RuntimeException("not supported");
    }

    public static class EAVEntityBatchingEntityLoader extends BatchingEntityLoader {

        private final int[] batchSizes;
        private final EntityLoader[] loaders;

        public EAVEntityBatchingEntityLoader(
                OuterJoinLoadable persister,
                int maxBatchSize,
                LockMode lockMode,
                SessionFactoryImplementor factory,
                LoadQueryInfluencers loadQueryInfluencers) {
            super(persister);
            this.batchSizes = ArrayHelper.getBatchSizes(maxBatchSize);
            this.loaders = new EntityLoader[batchSizes.length];
            final EntityLoader.Builder entityLoaderBuilder = EntityLoader.forEntity(persister)
                    .withInfluencers(loadQueryInfluencers)
                    .withLockMode(lockMode);
            for (int i = 0; i < batchSizes.length; i++) {
                this.loaders[i] = entityLoaderBuilder.withBatchSize(batchSizes[i]).byPrimaryKey();
            }
        }

        public EAVEntityBatchingEntityLoader(
                OuterJoinLoadable persister,
                int maxBatchSize,
                LockOptions lockOptions,
                SessionFactoryImplementor factory,
                LoadQueryInfluencers loadQueryInfluencers) {
            super(persister);
            this.batchSizes = ArrayHelper.getBatchSizes(maxBatchSize);
            this.loaders = new EntityLoader[batchSizes.length];
            final EntityLoader.Builder entityLoaderBuilder = EntityLoader.forEntity(persister)
                    .withInfluencers(loadQueryInfluencers)
                    .withLockOptions(lockOptions);
            for (int i = 0; i < batchSizes.length; i++) {
                this.loaders[i] = entityLoaderBuilder.withBatchSize(batchSizes[i]).byPrimaryKey();
            }
        }

        public Object load(Serializable id, Object optionalObject, SessionImplementor session, LockOptions lockOptions) {
            final Serializable[] batch = session.getPersistenceContext()
                    .getBatchFetchQueue()
                    .getEntityBatch(persister(), id, batchSizes[0], persister().getEntityMode());

            for (int i = 0; i < batchSizes.length - 1; i++) {
                final int smallBatchSize = batchSizes[i];
                if (batch[smallBatchSize - 1] != null) {
                    Serializable[] smallBatch = new Serializable[smallBatchSize];
                    System.arraycopy(batch, 0, smallBatch, 0, smallBatchSize);
                    // for now...
                    final List results = loaders[i].loadEntityBatch(
                            session,
                            smallBatch,
                            persister().getIdentifierType(),
                            optionalObject,
                            persister().getEntityName(),
                            id,
                            persister(),
                            lockOptions
                    );
                    //EARLY EXIT
                    return getObjectFromList(results, id, session);
                }
            }
            return (loaders[batchSizes.length - 1]).load(id, optionalObject, session, lockOptions);
        }
    }

}
