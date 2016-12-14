package challenge.dbside.dao.ini.impl;

import challenge.dbside.dao.ini.MediaDaoEntity;
import challenge.dbside.models.BaseEntity;
import java.math.BigInteger;
import java.util.List;

import javax.persistence.*;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;

import challenge.dbside.models.*;

@Repository
public class MediaDaoEntityImpl implements MediaDaoEntity {

    @PersistenceContext
    private EntityManager em;

    private PlatformTransactionManager transactionManager;

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        //this.transactionManager = transactionManager;
    }
    
    
    public Integer getNextId() {
    	Query q = em.createNativeQuery("select nextval('serial')");
    	BigInteger bi = (BigInteger) q.getResultList().get(0);
    	System.out.println("id: " + bi.toString());
    	
    	return bi.intValue();
    }

    public void save(BaseEntity entity) {
    	
    	entity.setId(getNextId());
    	System.out.println("Save id: " + entity.getId());
        em.persist(entity);
    }

    public<T extends BaseEntity> List<T> getAll(Class classType) { 
        List<T> list = em.createQuery("from " + classType.getSimpleName(), classType).getResultList();
        return list;
    }

    @Override
    public void delete(BaseEntity entity) {
    	//TODO: maybe change it a template?
    	//BaseEntity entity1 = em.find(BaseEntity.class, entity.getId());
    	System.out.println("Delete id: " + entity.getId());
    	em.remove(em.merge(entity));
    }

    @Override
    public void update(BaseEntity entity) {
    	System.out.println("Update id: " + entity.getId());
    	em.merge(entity);
    }


	@Override
	public BaseEntity findById(Integer idEntity) {
		return em.find(BaseEntity.class, idEntity);
	}
   
}


