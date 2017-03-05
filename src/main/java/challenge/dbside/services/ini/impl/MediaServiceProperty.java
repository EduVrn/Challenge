package challenge.dbside.services.ini.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import challenge.dbside.dao.ini.impl.MediaDaoProperty;
import challenge.dbside.property.PropertyDB;
import challenge.dbside.services.ini.MediaService;

@Service("storageServiceProperty")
public class MediaServiceProperty implements MediaService<PropertyDB> {

    @Autowired
    private MediaDaoProperty dao;

    @Override
    @Transactional
    public void save(PropertyDB entity) {
        dao.save(entity);
    }

    @Override
    public List<PropertyDB> getAll(Class<PropertyDB> classType) {
        return dao.getAll(classType);
    }

    @Override
    @Transactional
    public void update(PropertyDB entity) {
        dao.update(entity);
    }

    @Override
    @Transactional
    public void delete(PropertyDB entity) {
        dao.delete(entity);
    }

    @Override
    public PropertyDB findById(Object id, Class<PropertyDB> classType) {
        return dao.findById(id, classType);
    }

}
