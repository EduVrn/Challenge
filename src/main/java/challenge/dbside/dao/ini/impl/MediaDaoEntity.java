package challenge.dbside.dao.ini.impl;

import challenge.dbside.models.BaseEntity;
import java.math.BigInteger;
import java.util.List;

import javax.persistence.*;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import challenge.dbside.dao.ini.MediaDao;

@Repository
public class MediaDaoEntity<E extends BaseEntity> implements MediaDao<E> {

    @PersistenceContext
    private EntityManager em;

    private PlatformTransactionManager transactionManager;

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        //this.transactionManager = transactionManager;
    }

    public Integer getNextId() {
        Query q = em.createNativeQuery("select nextval('serial')");
        BigInteger bi = (BigInteger) q.getResultList().get(0);
        return bi.intValue();
    }

    @Override
    public void save(BaseEntity entity) {
        entity.setId(getNextId());
        em.persist(entity);
    }

    @Override
    public List<E> getAll(Class<E> classType) {
        List<E> list = em.createQuery("from " + classType.getSimpleName(), classType).getResultList();
        return list;
    }

    @Override
    public void delete(BaseEntity entity) {
        em.remove(em.merge(entity));
    }

    @Override
    public void update(BaseEntity entity) {
        em.merge(entity);
    }

    @Override
    public E findById(Integer id, Class<E> classType) {
        TypedQuery<BaseEntity> query = em.createQuery(
                "SELECT c FROM BaseEntity c"
                + " WHERE c.id = ?1", BaseEntity.class);
        return classType.cast(query.setParameter(1, id).getSingleResult());
    }
}
