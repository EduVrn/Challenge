package challenge.dbside.dao.ini.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import challenge.dbside.models.ini.TypeOfEntity;
import java.util.List;
import challenge.dbside.dao.ini.MediaDao;

@Repository
public class MediaDaoTypeOfEntity implements MediaDao<TypeOfEntity> {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void save(TypeOfEntity typeOfEntity) {
        em.persist(typeOfEntity);
    }

    @Override
    public List<TypeOfEntity> getAll(Class<TypeOfEntity> classType) {
        List<TypeOfEntity> list = em.createQuery("from " + classType.getSimpleName(), classType).getResultList();
        return list;
    }

    @Override
    public void delete(TypeOfEntity entity) {
        em.remove(em.merge(entity));
    }

    @Override
    public void update(TypeOfEntity entity) {
        em.merge(entity);
    }

    @Override
    public TypeOfEntity findById(Integer id, Class<TypeOfEntity> classType) {
        return em.find(classType, id);
    }

}
