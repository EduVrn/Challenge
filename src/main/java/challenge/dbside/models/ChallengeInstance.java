package challenge.dbside.models;

import challenge.dbside.ini.ContextType;
import challenge.dbside.models.common.IdAttrGet;
import challenge.dbside.models.dbentity.DBSource;

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
    	return new ChallengeDefinition(this.getDataSourse().getParent());
    }
    
    public void setChallengeRoot(ChallengeDefinition rootChallenge) {
    	getDataSourse().setParent(rootChallenge.getDataSourse().getParent());
    }
    
    
    public String getName() {     
        return (String)getDataSourse().getAttributes().get(IdAttrGet.IdName()).getValue();
    }

    public void setName(String name) {
        getDataSourse().getAttributes().get(IdAttrGet.IdName()).setValue(name);
    }

    public User getAcceptor() {        
    	return (User)getDataSourse().getRelations().get(IdAttrGet.refAcceptorChalIns());
    }

    public void setAcceptor(User acceptor) {    	
    	getDataSourse().getRelations().remove(IdAttrGet.refAcceptorChalIns());
    	getDataSourse().getRelations().put(IdAttrGet.refAcceptorChalIns(), acceptor.getDataSourse());
    }
    
    public ChallengeStatus getStatus() {
        return ChallengeStatus.valueOf(getDataSourse().getAttributes().get(IdAttrGet.IdChalStat()).getValue());
    }
    
    public void setStatus(ChallengeStatus status) {
        getDataSourse().getAttributes().get(IdAttrGet.IdChalStat()).setValue(status.name());
    }
}
