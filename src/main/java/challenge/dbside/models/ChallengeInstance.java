package challenge.dbside.models;

import challenge.dbside.ini.ContextType;
import challenge.dbside.models.common.IdAttrGet;
import challenge.dbside.models.dbentity.DBSource;
import challenge.dbside.models.status.ChallengeStatus;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;


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
    	return new ChallengeDefinition((DBSource)getDataSource().getRelations_r().get(IdAttrGet.refAcceptorChalIns()));
    }
    
    public void setChallengeRoot(ChallengeDefinition rootChallenge) {
    	getDataSource().getRelations_r().remove(IdAttrGet.refAcceptorChalIns());
    	getDataSource().getRelations_r().put(IdAttrGet.refAcceptorChalIns(), rootChallenge.getDataSource());
    }
    
    
    public String getName() {     
        return (String)getDataSource().getAttributes().get(IdAttrGet.IdName()).getValue();
    }

    public void setName(String name) {
        getDataSource().getAttributes().get(IdAttrGet.IdName()).setValue(name);
    }

    public User getAcceptor() {
    	DBSource userDB = (DBSource)(((List<DBSource>)getDataSource().getRelations_r().get(IdAttrGet.refAcceptedChalIns())).get(0));
    	return new User(userDB);
    }

    public void setAcceptor(User acceptor) {    	
    	getDataSource().getRelations_r().remove(IdAttrGet.refChalIns());
    	getDataSource().getRelations_r().put(IdAttrGet.refChalIns(), acceptor.getDataSource());
    }
    
    public ChallengeStatus getStatus() {
        return ChallengeStatus.valueOf(getDataSource().getAttributes().get(IdAttrGet.IdChalStat()).getValue());
    }
    
    public void setStatus(ChallengeStatus status) {
        getDataSource().getAttributes().get(IdAttrGet.IdChalStat()).setValue(status.name());
    }
}
