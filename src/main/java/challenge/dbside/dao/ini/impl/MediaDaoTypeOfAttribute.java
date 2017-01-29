package challenge.dbside.dao.ini.impl;

import challenge.dbside.models.ini.TypeOfAttribute;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import challenge.dbside.dao.ini.MediaDao;

@Repository
public class MediaDaoTypeOfAttribute implements MediaDao<TypeOfAttribute> {
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public void save(TypeOfAttribute type) {
        em.persist(type);
    }

    @Override
    public List<TypeOfAttribute> getAll(Class<TypeOfAttribute> classType) {
        List<TypeOfAttribute> list = em.createQuery("from " + classType.getSimpleName(), classType).getResultList();
        return list;
    }

    @Override
    public void delete(TypeOfAttribute entity) {
        em.remove(em.merge(entity));
    }

    @Override
    public void update(TypeOfAttribute entity) {
        em.merge(entity);
    }

    @Override
    public TypeOfAttribute findById(Object id, Class<TypeOfAttribute> classType) {
        return em.find(classType, id);
    }
}
