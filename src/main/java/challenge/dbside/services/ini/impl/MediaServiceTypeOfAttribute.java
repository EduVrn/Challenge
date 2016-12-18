package challenge.dbside.services.ini.impl;

import challenge.dbside.dao.ini.impl.MediaDaoTypeOfAttribute;
import challenge.dbside.models.ini.TypeOfAttribute;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import challenge.dbside.services.ini.MediaService;

@Service("storageServiceTypeOfAttribute")
public class MediaServiceTypeOfAttribute implements MediaService<TypeOfAttribute> {

    @Autowired
    private MediaDaoTypeOfAttribute dao;
    
    @Override
    @Transactional
    public void save(TypeOfAttribute type) {
        dao.save(type);
    }

    @Override
    public List<TypeOfAttribute> getAll(Class<TypeOfAttribute> classType) {
        return dao.getAll(classType);
    }

    @Override
    public void update(TypeOfAttribute entity) {
        dao.update(entity);
    }

    @Override
    public void delete(TypeOfAttribute entity) {
        dao.delete(entity);
    }

    @Override
    public TypeOfAttribute findById(Integer id, Class<TypeOfAttribute> classType) {
        return dao.findById(id, classType);
    }
}
