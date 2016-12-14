package challenge.dbside.services.ini.impl;

import org.springframework.beans.factory.annotation.Autowired;

import challenge.dbside.dao.ini.MediaDaoTypeOfEntity;
import challenge.dbside.models.ini.TypeOfEntity;
import challenge.dbside.services.ini.MediaServiceTypeOfEntity;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("storageServiceTypeOfEntity")
public class MediaServiceTypeOfEntityImpl implements MediaServiceTypeOfEntity {

    @Autowired
    private MediaDaoTypeOfEntity dao;
    
    @Override
    @Transactional
    public void save(TypeOfEntity type) {
        dao.save(type);
    }
    
}
