package challenge.dbside.services.ini.impl;

import challenge.dbside.dao.ini.MediaDaoTypeOfAttribute;
import challenge.dbside.models.ini.TypeOfAttribute;
import challenge.dbside.services.ini.MediaServiceTypeOfAttribute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("storageServiceTypeOfAttribute")
public class MediaServiceTypeOfAttributeImpl implements MediaServiceTypeOfAttribute {

    @Autowired
    private MediaDaoTypeOfAttribute dao;
    
    @Override
    @Transactional
    public void save(TypeOfAttribute type) {
        dao.save(type);
    }
}
