package challenge.dbside.eav.collection.batching;

import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.collection.CollectionInitializer;
import org.hibernate.persister.collection.QueryableCollection;

import challenge.dbside.eav.collection.batching.loader.EAVCollectionLoader;

/**
 * Base class for LoadPlan-based BatchingCollectionInitializerBuilder
 * implementations. Mainly we handle the common "no batching" case here to use
 * the LoadPlan-based CollectionLoader
 *
 * @author Gail Badner
 *
 *
 * @changer sergpc@yandex.ru
 *
 * @info change AbstractBatchingCollectionInitializerBuilder 4 EAV Model
 * implementation
 *
 */
public abstract class EAVAbstractBatchingCollectionInitializerBuilder extends EAVBatchingCollectionInitializerBuilder {

    @Override
    protected CollectionInitializer buildNonBatchingLoader(
            QueryableCollection persister,
            SessionFactoryImplementor factory,
            LoadQueryInfluencers influencers) {

        return EAVCollectionLoader.forCollection(persister).withInfluencers(influencers).byKey();
    }

}
