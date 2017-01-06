package challenge.dbside.models;

import javax.persistence.Entity;
import javax.persistence.Table;

import challenge.dbside.ini.ContextType;
import challenge.dbside.models.dbentity.Attribute;
import challenge.dbside.models.dbentity.DBSource;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;

public class ChallengeDefinition extends BaseEntity {

    /*@OneToOne(cascade = CascadeType.ALL)
    @JoinTable(name = "relationship",
            joinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id")
    )
    private User creator;*/
    
    public ChallengeDefinition() {
        super(ChallengeDefinition.class.getSimpleName());
        
    }

    public ChallengeDefinition(DBSource dataSource) {
        super(dataSource);
    }
    
    public List<User> getAllAcceptors() {
        List<User> acceptors = new ArrayList<>();
        /*this.getChildren().forEach((chalInstance) -> {
            if (chalInstance instanceof ChallengeInstance)
                acceptors.add(((ChallengeInstance) chalInstance).getAcceptor());
        });*/
        return acceptors;
    }
    
    public List<Comment> getComments() {
        List<Comment> comments = new ArrayList<>();
        
        getDataSourse().getChildren().forEach((child) -> {
        	
        	
        });
        
        /*dataSource.getChildren().forEach((child) -> {
            if (child instanceof Comment)
                comments.add((Comment) child);
        });*/
        return comments;
    }
    
    public void addComment(Comment comment) {
    	
    	
    	
    	//dataSource.addChidlren(comment.getDataSourse());
    }

    public String getName() {
        return getDataSourse().getAttributes()
                .get(ContextType.getInstance().getTypeAttribute("name").getId()).getValue();
    }

    public void setName(String name) {
    	getDataSourse().getAttributes()
                .get(ContextType.getInstance().getTypeAttribute("name").getId()).setValue(name);
    }

    public String getDescription() {
    	Integer id = ContextType.getInstance().getTypeAttribute("description").getId();
    	Attribute attr = getDataSourse().getAttributes().get(id);
    	String str = attr.getValue();
    	
        return str;
    }

    public void setDescription(String description) {
    	getDataSourse().getAttributes()
                .get(ContextType.getInstance().getTypeAttribute("description").getId()).setValue(description);
    }

    public String getImageRef() {
        return "../images/" + getDataSourse().getAttributes()
                .get(ContextType.getInstance().getTypeAttribute("imageref").getId()).getValue();
    }

    public void setImageRef(String image) {
    	getDataSourse().getAttributes()
                .get(ContextType.getInstance().getTypeAttribute("imageref").getId()).setValue(image);
    }

    public Date getDate()  {
        try {
            DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
            Date result = df.parse(getDataSourse().getAttributes()
                    .get(ContextType.getInstance().getTypeAttribute("date").getId()).getValue());
            return result;
        } catch (Exception ex) {
        	return (new Date(0));
        	//new Date() == current date,
            //return (new Date());
        }
    }

    public void setDate(Date date) {
    	getDataSourse().getAttributes()
                .get(ContextType.getInstance().getTypeAttribute("date").getId()).setValue(date.toString());
    }

    public User getCreator() {
    	User user = new User(getDataSourse()); 

    	String n = user.getName();
    	
    	return user;
    }

    public void setCreator(User creator) {
    	getDataSourse().setParent(creator.getDataSourse());
    }
    
    public ChallengeDefinitionStatus getStatus() {
        return ChallengeDefinitionStatus.valueOf(this.getDataSourse().getAttributes()
                .get(ContextType.getInstance().getTypeAttribute("chalDefStatus").getId()).getValue());
    }
    
    public void setStatus(ChallengeDefinitionStatus status) {
    	getDataSourse().getAttributes()
                .get(ContextType.getInstance().getTypeAttribute("chalDefStatus").getId()).setValue(status.name());
    }
    
    
    public void addChallengeInstance(ChallengeInstance chalIns) {
    	getDataSourse().getRelations()
    		.put(ContextType.getInstance().getTypeAttribute("challengeInstances").getId(), chalIns.getDataSourse());
    }
    
}
