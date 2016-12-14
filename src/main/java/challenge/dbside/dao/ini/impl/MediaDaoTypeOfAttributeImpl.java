package challenge.dbside.dao.ini.impl;

import challenge.dbside.dao.ini.MediaDaoTypeOfAttribute;
import challenge.dbside.models.ini.TypeOfAttribute;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

@Repository
public class MediaDaoTypeOfAttributeImpl implements MediaDaoTypeOfAttribute {
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public void save(TypeOfAttribute type) {
        em.persist(type);
    }
    
    
}
