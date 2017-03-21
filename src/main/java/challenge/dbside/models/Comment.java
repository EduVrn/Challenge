package challenge.dbside.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Persister;
import org.hibernate.eav.EAVEntity;
import org.hibernate.eav.EAVGlobalContext;
import org.hibernate.validator.constraints.NotBlank;

import challenge.dbside.eav.EAVPersister;
import challenge.dbside.eav.collection.EAVCollectionPersister;

@Entity
@EAVEntity
@Persister(impl=EAVPersister.class)
public class Comment extends BaseEntity {

	public Comment() {
		super(EAVGlobalContext.getTypeOfEntity(Comment.class.getSimpleName().toLowerCase()).getId());
		comments = new ArrayList();
		authors = new ArrayList();
		backComments = new ArrayList();
		
		votesAgainst = new ArrayList();
		votesFor = new ArrayList();
	}
	
	private String message;
	private Date date;
	
    @NotNull
    @NotBlank
    @Size(min = 5, max = 250, message = "{error.comment.length}")
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	@ManyToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER)
	@JoinTable(name="eav_relationship",
			joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),            
			inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
					)
	@Persister(impl=EAVCollectionPersister.class)
	private List<Comment> comments;
	
	@ManyToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER)
	@JoinTable(name="eav_relationship",
			joinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id"),            
			inverseJoinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id")
					)
	@Persister(impl=EAVCollectionPersister.class)
	private List<Comment> backComments;
	
	//TODO change many-to-many to one-to-many
	@ManyToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER)
	@JoinTable(name="eav_relationship",
			joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),            
			inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
					)
	@Persister(impl=EAVCollectionPersister.class)
	private List<User> authors;
	@ManyToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER)
	@JoinTable(name="eav_relationship",
			joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),            
			inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
					)
	@Persister(impl=EAVCollectionPersister.class)
	private List<User> votesFor;
	
	@ManyToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER)
	@JoinTable(name="eav_relationship",
			joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),            
			inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
					)
	@Persister(impl=EAVCollectionPersister.class)
	private List<User> votesAgainst;
	
	
    public List<Comment> getBackComments() {
		return backComments;
	}
	public void setBackComments(List<Comment> backComments) {
		this.backComments = backComments;
	}
	public List<User> getAuthors() {
		return authors;
	}
	public void setAuthors(List<User> authors) {
		this.authors = authors;
	}
	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	public List<User> getVotesFor() {
		return votesFor;
	}
	public void setVotesFor(List<User> votesFor) {
		this.votesFor = votesFor;
	}
	public List<User> getVotesAgainst() {
		return votesAgainst;
	}
	public void setVotesAgainst(List<User> votesAgainst) {
		this.votesAgainst = votesAgainst;
	}
	
	public void addVoteFor(User voter) {
		votesFor.add(voter);
	}
	
	public void addVoteAgainst(User voter) {
		votesAgainst.add(voter);
	}
	
	public boolean rmVoteFor(User voter) {
		return votesFor.remove(voter);
	}
	public boolean rmVoteAgainst(User voter) {
		return votesAgainst.remove(voter);
	}
	
	
	public void setParentComment(Comment parent) {
		backComments = new ArrayList();
		backComments.add(parent);
	}
	public Comment getParentComment() {
		return backComments.size() == 0 ? null : backComments.get(0);
	}
	public User getAuthor() {
		return authors.get(0);
	}
	public void setAuthor(User user) {
		authors = new ArrayList();
		authors.add(user);
	}
	
	public void addComment(Comment comment) {
		comments.add(comment);
	}
	public int getSubCommentsCount() {
        int result = 0;
        for(Comment com : this.comments) {
        	result++;
        	result += com.getSubCommentsCount();
        }
        return result;
    }
	
	public static final Comparator<Comment> COMPARE_BY_DATE = (Comment leftToCompare, Comment rightToCompare)
            -> rightToCompare.getDate().compareTo(leftToCompare.getDate());
	
}
