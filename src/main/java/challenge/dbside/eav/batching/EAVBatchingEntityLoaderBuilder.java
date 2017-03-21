package challenge.dbside.eav.batching;

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;

import org.hibernate.loader.entity.EntityLoader;
import org.hibernate.loader.entity.UniqueEntityLoader;
import org.hibernate.persister.entity.OuterJoinLoadable;



/**
 * The contract for building {@link UniqueEntityLoader} capable of performing batch-fetch loading.  Intention
 * is to build these instances, by first calling the static {@link #getBuilder}, and then calling the appropriate
 * {@link #buildLoader} method.
 *
 * @author Steve Ebersole
 *
 * @see org.hibernate.loader.BatchFetchStyle
 * 
 * @version sergpc@yandex.ru add 4 support EAV model
 */

public abstract class EAVBatchingEntityLoaderBuilder {

	public static EAVBatchingEntityLoaderBuilder getBuilder(SessionFactoryImplementor factory) {
		switch ( factory.getSettings().getBatchFetchStyle() ) {
			case PADDED: {
				throw new RuntimeException("not supported");
				//return PaddedBatchingEntityLoaderBuilder.INSTANCE;
			}
			case DYNAMIC: {
				throw new RuntimeException("not supported");
				//return DynamicBatchingEntityLoaderBuilder.INSTANCE;
			}
			default: {
				return BaseEAVBatchingEntityLoaderBuilder.INSTANCE;
				//throw new RuntimeException("not supported");
				//return BatchingEAVEntityLoaderBuilder.INSTANCE;
				//return org.hibernate.loader.entity.plan.LegacyBatchingEntityLoaderBuilder.INSTANCE;
//				return LegacyBatchingEntityLoaderBuilder.INSTANCE;
			}
		}
	}
	
	/**
	 * Builds a batch-fetch capable loader based on the given persister, lock-mode, etc.
	 *
	 * @param persister The entity persister
	 * @param batchSize The maximum number of ids to batch-fetch at once
	 * @param lockMode The lock mode
	 * @param factory The SessionFactory
	 * @param influencers Any influencers that should affect the built query
	 *
	 * @return The loader.
	 */
	public UniqueEntityLoader buildLoader(
			OuterJoinLoadable persister,
			int batchSize,
			LockMode lockMode,
			SessionFactoryImplementor factory,
			LoadQueryInfluencers influencers) {
		if ( batchSize <= 1 ) {
			// no batching
			return buildNonBatchingLoader( persister, lockMode, factory, influencers );
		}
		return buildBatchingLoader( persister, batchSize, lockMode, factory, influencers );
	}
	
	protected UniqueEntityLoader buildNonBatchingLoader(
			OuterJoinLoadable persister,
			LockMode lockMode,
			SessionFactoryImplementor factory,
			LoadQueryInfluencers influencers) {
		return new EntityLoader( persister, lockMode, factory, influencers );
	}
	
	/*
	protected UniqueEntityLoader buildNonBatchingLoader(
			OuterJoinLoadable persister,
			LockMode lockMode,
			SessionFactoryImplementor factory,
			LoadQueryInfluencers influencers) {
		return new EntityLoader( persister, lockMode, factory, influencers );
	}*/

	protected abstract UniqueEntityLoader buildBatchingLoader(
			OuterJoinLoadable persister,
			int batchSize,
			LockMode lockMode,
			SessionFactoryImplementor factory,
			LoadQueryInfluencers influencers);
	
	public UniqueEntityLoader buildLoader(
			OuterJoinLoadable persister,
			int batchSize,
			LockOptions lockOptions,
			SessionFactoryImplementor factory,
			LoadQueryInfluencers influencers) {
		if ( batchSize <= 1 ) {
			// no batching
			return buildNonBatchingLoader( persister, lockOptions, factory, influencers );
		}
		return buildBatchingLoader( persister, batchSize, lockOptions, factory, influencers );
	}

	protected UniqueEntityLoader buildNonBatchingLoader(
			OuterJoinLoadable persister,
			LockOptions lockOptions,
			SessionFactoryImplementor factory,
			LoadQueryInfluencers influencers) {
		return new EntityLoader( persister, lockOptions, factory, influencers );
	}

	protected abstract UniqueEntityLoader buildBatchingLoader(
			OuterJoinLoadable persister,
			int batchSize,
			LockOptions lockOptions,
			SessionFactoryImplementor factory,
			LoadQueryInfluencers influencers);

	
	
}
