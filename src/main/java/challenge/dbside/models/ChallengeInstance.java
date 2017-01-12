package challenge.dbside.models;


import challenge.dbside.models.common.IdAttrGet;
import challenge.dbside.models.dbentity.DBSource;
import challenge.dbside.models.status.ChallengeStatus;

import java.util.List;


public class ChallengeInstance extends BaseEntity {

		
    public ChallengeInstance() {
        super(ChallengeInstance.class.getSimpleName());
    }
    
    public ChallengeInstance(DBSource dataSource) {
    	super(dataSource);
    }
    
    public ChallengeInstance(ChallengeDefinition chalDef) {
        super(ChallengeInstance.class.getSimpleName());
        setName(chalDef.getName());
    }
    
    public ChallengeDefinition getChallengeRoot() {
    	return new ChallengeDefinition(getDataSource().getParent());
    }
    
    public void setChallengeRoot(ChallengeDefinition rootChallenge) {
    	getDataSource().setParent(rootChallenge.getDataSource());
    }
    
    
    public String getName() {     
        return (String)getDataSource().getAttributes().get(IdAttrGet.IdName()).getValue();
    }

    public void setName(String name) {
        getDataSource().getAttributes().get(IdAttrGet.IdName()).setValue(name);
    }

    public User getAcceptor() {
    	List list = (List<DBSource>)getDataSource().getBackRel().get(IdAttrGet.refAcChalIns());
    	
    	DBSource userDB = (DBSource) (list.get(0));
    	return new User(userDB);
    }

    public void setAcceptor(User acceptor) {    	
    	getDataSource().getBackRel().put(IdAttrGet.refAcChalIns(), acceptor.getDataSource());
    }
    
    public ChallengeStatus getStatus() {
        return ChallengeStatus.valueOf(getDataSource().getAttributes().get(IdAttrGet.IdChalStat()).getValue());
    }
    
    public void setStatus(ChallengeStatus status) {
        getDataSource().getAttributes().get(IdAttrGet.IdChalStat()).setValue(status.name());
    }
}
