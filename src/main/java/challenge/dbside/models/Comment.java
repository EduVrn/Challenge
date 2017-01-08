package challenge.dbside.models;

import challenge.dbside.ini.ContextType;
import challenge.dbside.models.common.IdAttrGet;
import challenge.dbside.models.dbentity.DBSource;
import challenge.dbside.models.ini.TypeOfAttribute;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;


public class Comment extends BaseEntity { //TODO: add implements Commentable ???

    public Comment() {
        super(Comment.class.getSimpleName());
    }

    public Comment(DBSource dataSource) {
    	super(dataSource);
    }
        
    public User getAuthor() {
    	List<DBSource> list = (List<DBSource>)getDataSource().getRelations_l().get(IdAttrGet.refAutorComment());    	
    	return	new User(list.get(0));
    }

    public void setAuthor(User author) {
    	getDataSource().getRelations_l().remove(IdAttrGet.refAutorComment());
    	getDataSource().getRelations_l().put(IdAttrGet.refAutorComment(), author.getDataSource());
    }

    public String getMessage() {
        return getDataSource().getAttributes().get(IdAttrGet.IdMessage()).getValue();
    }

    public void setMessage(String msg) {
        getDataSource().getAttributes().get(IdAttrGet.IdMessage()).setValue(msg);
    }

    public Date getDate() {
    	return (Date) getDataSource().getAttributes().get(IdAttrGet.IdDate()).getDateValue();
    }

    public void setDate(Date date) {
        getDataSource().getAttributes().get(IdAttrGet.IdDate()).setDateValue(date);
    }

    
    
    public int getSubCommentsCount() {
        int result = 0;
        
        for(DBSource commentDB : getDataSource().getChildren()) {
        	result++;
        	//TODO: optimize it, create getAllChildCount in DBSource ???
        	result += new Comment(commentDB).getSubCommentsCount();
        }
        return result;
    }

    public List<Comment> getComments() {    	
        List<Comment> comments = new ArrayList<>();
        getDataSource().getChildren().forEach((commentDB) -> {
        	comments.add(new Comment(commentDB));
        });
        return comments;
    }
}
