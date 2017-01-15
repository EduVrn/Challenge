package challenge.dbside.dao.ini.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import challenge.dbside.dao.ini.MediaDao;
import challenge.dbside.ini.ContextType;
import challenge.dbside.models.BaseEntity;
import challenge.dbside.models.dbentity.Attribute;
import challenge.dbside.models.dbentity.DBSource;

@Repository
public class MediaDaoEntity<E extends BaseEntity> implements MediaDao<E> {

    @PersistenceContext
    private EntityManager em;

    private PlatformTransactionManager transactionManager;

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        //TODO: persistence
        //this.transactionManager = transactionManager;
    }

    public Integer getNextId() {
        Query q = em.createNativeQuery("select nextval('serial')");
        BigInteger bi = (BigInteger) q.getResultList().get(0);
        return bi.intValue();
    }

    @Override
    public void save(BaseEntity entity) {
        Integer id = getNextId();
        entity.setId(id);
        for (Attribute attr : entity.getDataSource().getAttributes().values()) {
            attr.setEntity_id(id);
        }

        em.persist(entity.getDataSource());
    }

    @Override
    public List<E> getAll(Class<E> classType) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Integer typeEntity = ContextType.getInstance().getTypeEntity(classType.getSimpleName()).getTypeEntityID();
        List<DBSource> list = em.createQuery("from " + DBSource.class.getSimpleName()
                + " where type_of_entity = "
                + typeEntity, DBSource.class).getResultList();

        List<E> listG = new ArrayList();
        for (DBSource el : list) {
            E p = classType.getDeclaredConstructor(DBSource.class).newInstance(el);
            listG.add(p);
        }

        return listG;
    }

    @Override
    public void delete(BaseEntity entity) {
        String hqlDelete = "delete from DBSource where entity_id = :id";
        int deletedEntities = em.createQuery(hqlDelete)
                .setParameter("id", entity.getId())
                .executeUpdate();
    }

    @Override
    public void update(BaseEntity entity) {
        em.merge(entity.getDataSource());
    }

    @Override
    public E findById(Integer id, Class<E> classType) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        TypedQuery<DBSource> query = em.createQuery("SELECT c FROM " + DBSource.class.getSimpleName()
                + " c WHERE c.id = ?1 and type_of_entity = "
                + ContextType.getInstance().getTypeEntity(classType.getSimpleName()).getTypeEntityID(), DBSource.class);
        Object obj = query.setParameter(1, id).getSingleResult();
        return classType.cast(classType.getDeclaredConstructor(DBSource.class).newInstance(obj));
    }
}
