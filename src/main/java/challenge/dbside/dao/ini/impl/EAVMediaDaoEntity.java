package challenge.dbside.dao.ini.impl;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;

import challenge.dbside.dao.ini.MediaDao;
import challenge.dbside.models.BaseEntity;
import org.hibernate.Session;

@Repository("EAVDAOEntity")
public class EAVMediaDaoEntity<E extends BaseEntity> implements MediaDao<E> {

    @Autowired
    private SessionFactory sessionFactory;

    private PlatformTransactionManager transactionManager;

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
    }

    public Integer getNextId() {
        SQLQuery q = sessionFactory.getCurrentSession().createSQLQuery("select nextval('serial') as val");
        q.getQueryReturns();
        return ((BigInteger) q.list().get(0)).intValue();
    }

    @Override
    public void save(E entity) {
        Integer id = getNextId();
        entity.setId(id);

        sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public List<E> getAll(Class<E> classType) {
        List<E> list = sessionFactory.getCurrentSession().createCriteria(classType).list();
        Set setUniqueRes = new LinkedHashSet(list);
        List<E> uniqueList = new ArrayList<E>();
        uniqueList.addAll(setUniqueRes);
        return uniqueList;
    }

    //TODO see it
    @Override
    public void delete(E entity) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        String sql = String.format("delete from eav_entities where entity_id = %s", entity.getId());
        session.createSQLQuery(sql).executeUpdate();
        session.getTransaction().commit();
    }

    @Override
    public void update(E entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    //TODO change it to EAVBaseEntity ??
    @Override
    public E findById(Object id, Class<E> classType) {
        Object obj = sessionFactory.getCurrentSession().get(classType, (Serializable) id);

        return (E) obj;
    }

}
