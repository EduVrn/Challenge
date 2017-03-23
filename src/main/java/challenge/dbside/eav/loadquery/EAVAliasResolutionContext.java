package challenge.dbside.eav.loadquery;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.loader.EntityAliases;
import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
import org.hibernate.loader.plan.exec.spi.CollectionReferenceAliases;
import org.hibernate.loader.plan.exec.spi.EntityReferenceAliases;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Loadable;

public class EAVAliasResolutionContext extends AliasResolutionContextImpl {

    private int currentTableAliasSuffix;
    private int currentAliasSuffix;
    private Map<String, CollectionReferenceAliases> collectionReferenceAliasesMap;
    private Map<String, EntityReferenceAliases> entityReferenceAliasesMap;
    private Map<String, String> querySpaceUidToSqlTableAliasMap;

    public EAVAliasResolutionContext(SessionFactoryImplementor sessionFactory) {
        super(sessionFactory);

        this.currentAliasSuffix = 0;
        System.out.println("EAVAliasResolutionContext ");
    }

    public EAVAliasResolutionContext(SessionFactoryImplementor sessionFactory, int suffixSeed) {
        super(sessionFactory);
        throw new RuntimeException("not supported");
    }

    @Override
    public CollectionReferenceAliases resolveCollectionReferenceAliases(String querySpaceUid) {
        return collectionReferenceAliasesMap == null ? null : collectionReferenceAliasesMap.get(querySpaceUid);
    }

    private String createTableAlias(EntityPersister entityPersister) {
        return createTableAlias(StringHelper.unqualifyEntityName(entityPersister.getEntityName()));
    }

    private String createTableAlias(String name) {
        return StringHelper.generateAlias(name, currentTableAliasSuffix++);
    }

    @Override
    public EntityReferenceAliases generateEntityReferenceAliases(String uid, EntityPersister entityPersister) {
        return generateEntityReferenceAliases(uid, createTableAlias(entityPersister), entityPersister);
    }

    private EntityReferenceAliases generateEntityReferenceAliases(
            String uid,
            String tableAlias,
            EntityPersister entityPersister) {

        EntityAliases aliases = createEntityAliases(entityPersister);
        final EAVEntityReferenceAliasesImpl entityReferenceAliases = new EAVEntityReferenceAliasesImpl(aliases);
        registerQuerySpaceAliases(uid, entityReferenceAliases);
        return entityReferenceAliases;
    }

    private void registerQuerySpaceAliases(String querySpaceUid, EntityReferenceAliases entityReferenceAliases) {
        if (entityReferenceAliasesMap == null) {
            entityReferenceAliasesMap = new HashMap<String, EntityReferenceAliases>();
        }
        entityReferenceAliasesMap.put(querySpaceUid, entityReferenceAliases);
        registerSqlTableAliasMapping(querySpaceUid, entityReferenceAliases.getTableAlias());
    }

    private void registerQuerySpaceAliases(String querySpaceUid, CollectionReferenceAliases collectionReferenceAliases) {
        if (collectionReferenceAliasesMap == null) {
            collectionReferenceAliasesMap = new HashMap<String, CollectionReferenceAliases>();
        }
        collectionReferenceAliasesMap.put(querySpaceUid, collectionReferenceAliases);
        registerSqlTableAliasMapping(querySpaceUid, collectionReferenceAliases.getCollectionTableAlias());
    }

    private EntityAliases createEntityAliases(EntityPersister entityPersister) {
        Loadable el = (Loadable) entityPersister;
        String suffix = createSuffix();

        return new EAVEntityAliases(el, suffix);
    }

    private String createSuffix() {
        return Integer.toString(currentAliasSuffix++) + '_';
    }

    private void registerSqlTableAliasMapping(String querySpaceUid, String sqlTableAlias) {
        if (querySpaceUidToSqlTableAliasMap == null) {
            querySpaceUidToSqlTableAliasMap = new HashMap<String, String>();
        }
        String old = querySpaceUidToSqlTableAliasMap.put(querySpaceUid, sqlTableAlias);
        if (old != null) {
            if (old.equals(sqlTableAlias)) {
                // silently ignore...
            } else {
                throw new IllegalStateException(
                        String.format(
                                "Attempt to register multiple SQL table aliases [%s, %s, etc] against query space uid [%s]",
                                old,
                                sqlTableAlias,
                                querySpaceUid
                        )
                );
            }
        }
    }

}
