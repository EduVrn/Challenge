package challenge.dbside.models;

import javax.persistence.Entity;
import javax.persistence.Table;

import challenge.dbside.ini.ContextType;
import challenge.dbside.models.common.IdAttrGet;
import challenge.dbside.models.dbentity.Attribute;
import challenge.dbside.models.dbentity.DBSource;
import challenge.dbside.models.status.ChallengeDefinitionStatus;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;

public class ChallengeDefinition extends BaseEntity implements Commentable {
    
    public ChallengeDefinition() {
        super(ChallengeDefinition.class.getSimpleName());        
    }

    public ChallengeDefinition(DBSource dataSource) {
        super(dataSource);
    }
    
    public List<User> getAllAcceptors() {
        List<User> acceptors = new ArrayList<>();
        
        List<DBSource> list = (List<DBSource>)getDataSource().getRelations_l().get(IdAttrGet.refAcceptorChalIns());
        if(list != null) {
        	list.forEach((chalInsDB)->{
        		acceptors.add(new ChallengeInstance(chalInsDB).getAcceptor());
        	});
        }
        return acceptors;   
    }
    
    public String getName() {
        return getDataSource().getAttributes().get(IdAttrGet.IdName()).getValue();
    }

    public void setName(String name) {
    	getDataSource().getAttributes().get(IdAttrGet.IdName()).setValue(name);
    }

    public String getDescription() {
        return getDataSource().getAttributes().get(IdAttrGet.IdDescr()).getValue();
    }

    public void setDescription(String description) {
    	getDataSource().getAttributes().get(IdAttrGet.IdDescr()).setValue(description);
    }

    public String getImageRef() {
        return "../images/" + getDataSource().getAttributes().get(IdAttrGet.IdImgRef()).getValue();
    }

    public void setImageRef(String image) {
    	getDataSource().getAttributes().get(IdAttrGet.IdImgRef()).setValue(image);
    }

    public Date getDate()  {
        try {
            DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
            String ddt = getDataSource().getAttributes().get(IdAttrGet.IdDate()).getValue();
            Date result = df.parse(ddt);
            return result;
        } catch (Exception ex) {
        	ex.printStackTrace();
        	return (new Date(0));
        	//new Date() == current date,
            //return (new Date());
        }
    }

    public void setDate(Date date) {
    	getDataSource().getAttributes().get(IdAttrGet.IdDate()).setValue(date.toString());
    }

    public User getCreator() {
    	DBSource source = (DBSource)(((List<DBSource>)getDataSource().getRelations_r().get(IdAttrGet.refCreatedChal())).get(0));    	
    	return new User(source);
    }

    public void setCreator(User creator) {
    	getDataSource().getRelations_r().put(IdAttrGet.refCreatedChal(), creator.getDataSource());
    }
    
    public ChallengeDefinitionStatus getStatus() {    	
        return ChallengeDefinitionStatus.valueOf((getDataSource().getAttributes().get(IdAttrGet.IdChalDefStat())).getValue());
    }
    
    public void setStatus(ChallengeDefinitionStatus status) {
    	getDataSource().getAttributes().get(IdAttrGet.IdChalDefStat()).setValue(status.name());
    }
    
    
    public void addChallengeInstance(ChallengeInstance chalIns) {
    	getDataSource().getRelations_l().put(IdAttrGet.refChalIns(), chalIns.getDataSource());
    }
    
}
