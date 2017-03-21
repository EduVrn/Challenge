package challenge.dbside.eav.collection.batching;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
import org.hibernate.loader.plan.exec.internal.BasicCollectionLoadQueryDetails;
import org.hibernate.loader.plan.exec.internal.BatchingLoadQueryDetailsFactory;
import org.hibernate.loader.plan.exec.internal.EntityLoadQueryDetails;
import org.hibernate.loader.plan.exec.internal.OneToManyLoadQueryDetails;
import org.hibernate.loader.plan.exec.internal.RootHelper;
import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
import org.hibernate.loader.plan.spi.CollectionReturn;
import org.hibernate.loader.plan.spi.EntityReturn;
import org.hibernate.loader.plan.spi.LoadPlan;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.Queryable;

public class EAVBatchingLoadQueryDetailsFactory {

	private EAVBatchingLoadQueryDetailsFactory() {
	}

	/**
	 * Returns a EntityLoadQueryDetails object from the given inputs.
	 *
	 * @param loadPlan The load plan
	 * @param keyColumnNames The columns to load the entity by (the PK columns or some other unique set of columns)
	 * @param buildingParameters And influencers that would affect the generated SQL (mostly we are concerned with those
	 * that add additional joins here)
	 * @param factory The SessionFactory
	 *
	 * @return The EntityLoadQueryDetails
	 */
	public static LoadQueryDetails makeEntityLoadQueryDetails(
			LoadPlan loadPlan,
			String[] keyColumnNames,
			QueryBuildingParameters buildingParameters,
			SessionFactoryImplementor factory) {

		// TODO: how should shouldUseOptionalEntityInformation be used?
		// final int batchSize = buildingParameters.getBatchSize();
		// final boolean shouldUseOptionalEntityInformation = batchSize == 1;

		final EntityReturn rootReturn = RootHelper.INSTANCE.extractRootReturn( loadPlan, EntityReturn.class );
		final String[] keyColumnNamesToUse = keyColumnNames != null
				? keyColumnNames
				: ( (Queryable) rootReturn.getEntityPersister() ).getIdentifierColumnNames();
		// Should be just one querySpace (of type EntityQuerySpace) in querySpaces.  Should we validate that?
		// Should we make it a util method on Helper like we do for extractRootReturn ?
		//final AliasResolutionContextImpl aliasResolutionContext = new EAVAliasResolutionContext( factory );
		final AliasResolutionContextImpl aliasResolutionContext2 = new AliasResolutionContextImpl( factory );
		
		
		LoadQueryDetails loadQueryDetails = new EAVEntityLoadQueryDetails(
				loadPlan,
				keyColumnNamesToUse,
				aliasResolutionContext2,
				rootReturn,
				buildingParameters,
				factory
		); 
		return loadQueryDetails;
	}
	
	public static LoadQueryDetails makeCollectionLoadQueryDetails(
			CollectionPersister collectionPersister,
			LoadPlan loadPlan,
			QueryBuildingParameters buildingParameters) {
		final CollectionReturn rootReturn = RootHelper.INSTANCE.extractRootReturn( loadPlan, CollectionReturn.class );
		final AliasResolutionContextImpl aliasResolutionContext = new AliasResolutionContextImpl(
				collectionPersister.getFactory()
		);
		
		LoadQueryDetails details = null;
		if(collectionPersister.isOneToMany()) {
			throw new RuntimeException("not supported");
		}
		else {
			details = new EAVCollectionLoaderQueryDetails(
					loadPlan,
					aliasResolutionContext,
					rootReturn,
					buildingParameters,
					collectionPersister.getFactory()
			);
		}
						
		return details;
	}
	
}
