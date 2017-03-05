package challenge.dbside.services.ini.impl;

import org.springframework.beans.factory.annotation.Autowired;
import challenge.dbside.dao.ini.impl.MediaDaoTypeOfEntity;
import challenge.dbside.models.ini.TypeOfEntity;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import challenge.dbside.services.ini.MediaService;

@Service("storageServiceTypeOfEntity")
public class MediaServiceTypeOfEntity implements MediaService<TypeOfEntity> {

    @Autowired
    private MediaDaoTypeOfEntity dao;
    
    @Override
    @Transactional
    public void save(TypeOfEntity type) {
        dao.save(type);
    }

    @Override
    public List<TypeOfEntity> getAll(Class<TypeOfEntity> classType) {
        return dao.getAll(classType);
    }

    @Override
    @Transactional
    public void update(TypeOfEntity entity) {
        dao.update(entity);
    }

    @Override
    @Transactional
    public void delete(TypeOfEntity entity) {
        dao.delete(entity);
    }

    @Override
    public TypeOfEntity findById(Object id, Class<TypeOfEntity> classType) {
        return dao.findById(id, classType);
    }
}
