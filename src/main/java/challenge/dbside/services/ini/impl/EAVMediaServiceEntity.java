package challenge.dbside.services.ini.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import challenge.dbside.dao.ini.impl.EAVMediaDaoEntity;
import challenge.dbside.models.BaseEntity;
import challenge.dbside.services.ini.MediaService;

@Service("EAVStorageServiceUser")
@Transactional
public class EAVMediaServiceEntity<E extends BaseEntity> implements MediaService<E> {

    @Autowired
    private EAVMediaDaoEntity dao;

    @Override
    public void save(E entity) {
        dao.save(entity);
    }

    @Override
    public List<E> getAll(Class<E> classType) {
        return dao.getAll(classType);
    }

    @Override
    public void update(E entity) {
        dao.update(entity);
    }

    @Override
    public void delete(E entity) {
        dao.delete(entity);
    }

    //TODO change it to EAVBaseEntity ??
    @Override
    public E findById(Object id, Class<E> classType) {
        return (E) dao.findById(id, classType);
    }

}
