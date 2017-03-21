package challenge.dbside.eav.batching;

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.entity.UniqueEntityLoader;
import org.hibernate.persister.entity.OuterJoinLoadable;

import challenge.dbside.eav.EAVEntityPlanLoader;



/**
 * Base class for LoadPlan-based BatchingEntityLoaderBuilder implementations.  Mainly we handle the common
 * "no batching" case here to use the LoadPlan-based EntityLoader
 *
 * @author Steve Ebersole
 * 
 * @version sergpc@yandex.ru add 4 support EAV model
 */

public abstract class EAVAbstractBatchingEntityLoaderBuilder extends EAVBatchingEntityLoaderBuilder {

	@Override
	protected UniqueEntityLoader buildNonBatchingLoader(
			OuterJoinLoadable persister,
			LockMode lockMode,
			SessionFactoryImplementor factory,
			LoadQueryInfluencers influencers) {
		
		return EAVEntityPlanLoader.forEntity( persister ).withLockMode( lockMode ).withInfluencers( influencers ).byPrimaryKey();
	}

	@Override
	protected UniqueEntityLoader buildNonBatchingLoader(
			OuterJoinLoadable persister,
			LockOptions lockOptions,
			SessionFactoryImplementor factory,
			LoadQueryInfluencers influencers) {
		throw new RuntimeException("not supported");
		//return EntityLoader.forEntity( persister ).withLockOptions( lockOptions ).withInfluencers( influencers ).byPrimaryKey();
	}

}
