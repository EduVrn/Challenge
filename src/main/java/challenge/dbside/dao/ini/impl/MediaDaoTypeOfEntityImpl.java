package challenge.dbside.dao.ini.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import challenge.dbside.dao.ini.MediaDaoTypeOfEntity;
import challenge.dbside.models.ini.TypeOfEntity;

@Repository
public class MediaDaoTypeOfEntityImpl implements MediaDaoTypeOfEntity {

    @PersistenceContext
    private EntityManager em;
    
    @Override
    public void save(TypeOfEntity typeOfEntity) {
        em.persist(typeOfEntity);
    }
    
}
