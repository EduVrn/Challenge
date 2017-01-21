package challenge.dbside.services.ini.impl;

import challenge.dbside.dao.ini.impl.MediaDaoEntity;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import challenge.dbside.models.*;
import challenge.dbside.services.ini.MediaService;

@Service("storageServiceUser")
public class MediaServiceEntity<E extends BaseEntity> implements MediaService<E> {

    @Autowired
    private MediaDaoEntity dao;

    @Override
    @Transactional
    public void save(E entity) {
        dao.save(entity);
    }

    @Override
    @Transactional
    public void update(E entity) {
        dao.update(entity);
    }

    @Override
    @Transactional
    public void delete(E entity) {
        dao.delete(entity);
    }

    @Override
    public List<E> getAll(Class<E> classType) {
    	List<E> list = null;
        
        try {
        	list = dao.getAll(classType);
        }
        catch(Exception e) {
        	//InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException 
        	e.printStackTrace();
        	throw new RuntimeException(e);
        }    	
    	return list;
    }

    @Override
    public E findById(Integer id, Class<E> classType) {
    	E el = null;    	    	
    	try {
    		el = classType.cast(dao.findById(id, classType));
    	}
    	catch(Exception e) {
    		//InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
    		e.printStackTrace();
    		throw new RuntimeException(e);
    	}    	
    	return el;
    }
}
