package challenge.dbside.services.ini.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import challenge.dbside.models.*;
import challenge.dbside.dao.ini.MediaDaoEntity;
import challenge.dbside.services.ini.MediaServiceEntity;

@Service("storageServiceUser")
public class MediaServiceEntityImpl implements MediaServiceEntity {

    @Autowired
    private MediaDaoEntity dao;

    @Override
    @Transactional
    public void save(BaseEntity entity) {
        dao.save(entity);
    }

    @Override
    @Transactional
    public void update(BaseEntity entity) {
        dao.update(entity);
    }
    
    @Override
    @Transactional
    public void delete(BaseEntity entity) {
    	
    	//BaseEntity entity1 = dao.findById(entity.getId());
    	
        dao.delete(entity);
    }
    
    @Override
    public<T extends BaseEntity> List<T> getAll(Class classType) {
        return dao.getAll(classType);
    }

	@Override
	public BaseEntity findById(Integer idEntity) {
		return dao.findById(idEntity);
	}
    
    
    
}
