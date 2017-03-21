package challenge.dbside.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Persister;
import org.hibernate.eav.EAVEntity;
import org.hibernate.eav.EAVGlobalContext;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import challenge.dbside.eav.EAVPersister;
import challenge.dbside.eav.collection.EAVCollectionPersister;
import challenge.dbside.models.status.ChallengeInstanceStatus;

@Entity
@EAVEntity
@Persister(impl=EAVPersister.class)
public class ChallengeInstance extends BaseEntity {
	private static final Logger logger = LoggerFactory.getLogger(ChallengeInstance.class);
	
	public ChallengeInstance() {
		super(EAVGlobalContext.getTypeOfEntity(ChallengeInstance.class.getSimpleName().toLowerCase()).getId());
		
		images = new ArrayList();
		backAcceptedChallenges = new ArrayList();
		votesFor = new ArrayList();
		votesAgainst = new ArrayList();
		comments = new ArrayList();
		steps = new ArrayList();
		subscribers = new ArrayList();
	}
	
	public ChallengeInstance(ChallengeDefinition chal) {
		this();
		setName(chal.getName());
	}
	
	private String name;
	private String date;
	private String closingDate;
	private String description;
	private String status;
	
	@ManyToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER)
	@JoinTable(name="eav_relationship",
	joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),            
	inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
			)
	@Persister(impl=EAVCollectionPersister.class)
	private List<Image> images;
	

	@ManyToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER)
	@JoinTable(name="eav_relationship",
			joinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id"),            
			inverseJoinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id")
					)
	@Persister(impl=EAVCollectionPersister.class)
	private List<User> backAcceptedChallenges;

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
	
	@ManyToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER)
	@JoinTable(name="eav_relationship",
	joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),            
	inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
			)
	@Persister(impl=EAVCollectionPersister.class)
	private List<Comment> comments;
	
	@ManyToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER)
	@JoinTable(name="eav_relationship",
	joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),            
	inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
			)
	@Persister(impl=EAVCollectionPersister.class)
	private List<ChallengeStep> steps;
	
	@ManyToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER)
	@JoinTable(name="eav_relationship",
			joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),            
			inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
					)
	@Persister(impl=EAVCollectionPersister.class)
	private List<User> subscribers;
	
	@ManyToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER)
	@JoinTable(name="eav_relationship",
	joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),            
	inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
			)
	@Persister(impl=EAVCollectionPersister.class)
	private List<ChallengeDefinition> backChallengeInstances;
	
	
	public List<ChallengeDefinition> getBackChallengeInstances() {
		return backChallengeInstances;
	}
	public void setBackChallengeInstances(List<ChallengeDefinition> backChallengeInstances) {
		this.backChallengeInstances = backChallengeInstances;
	}
	public Date getClosingDate() {
		try {
			DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
			String ddt = this.closingDate;
			Date result = df.parse(ddt);
			return result;
		} catch (Exception ex) {
			logger.error("null data" + ex.getMessage());
			return (new Date(0));
		}
	}

	public void setClosingDate(Date closingDate) {
		this.closingDate = closingDate.toString();
	}

    @NotNull
    @NotBlank(message = "{error.name.blank}")
    @Size(min = 5, max = 40, message = "{error.name.length}")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name.trim();
	}
    @NotNull
    @Future(message = "{error.date}")
	public Date getDate() {
		try {
			DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
			String ddt = this.date;
			Date result = df.parse(ddt);
			return result;
		} catch (Exception ex) {
			logger.error("null data" + ex.getMessage());
			return (new Date(0));
		}
	}
	public void setDate(Date date) {
		this.date = date.toString();
	}

    @NotNull
    @NotBlank(message = "{error.name.blank}")
    @Size(min = 5, max = 250, message = "{error.description.length}")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description.trim();
	}
	public ChallengeInstanceStatus getStatus() {
		return ChallengeInstanceStatus.valueOf(status);
	}
	public void setStatus(ChallengeInstanceStatus status) {
		this.status = status.name();
	}
	public List<Image> getImages() {
		return images;
	}
	public void setImages(List<Image> images) {
		this.images = images;
	}
	public List<User> getBackAcceptedChallenges() {
		return backAcceptedChallenges;
	}
	public void setBackAcceptedChallenges(List<User> backAcceptedChallenges) {
		this.backAcceptedChallenges = backAcceptedChallenges;
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
	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	
	public void addComment(Comment comment) {
		comments.add(comment);
	}
	
	public void addVoteFor(User voter) {
		votesFor.add(voter);
	}
	
	public void addVoteAgainst(User voter) {
		votesAgainst.add(voter);
	}
	public List<ChallengeStep> getSteps() {
		return steps;
	}
	public void setSteps(List<ChallengeStep> steps) {
		this.steps = steps;
	}	
	public List<User> getSubscribers() {
		return subscribers;
	}
	public void setSubscribers(List<User> subscribers) {
		this.subscribers = subscribers;
	}

	
	public void setChallengeRoot(ChallengeDefinition chal) {
		backChallengeInstances = new ArrayList();
		backChallengeInstances.add(chal);
	}
	public ChallengeDefinition getChallengeRoot() {
		return backChallengeInstances.get(0); 
	}
	public void addSubscriber(User subscriber) {
		subscribers = new ArrayList();
		subscribers.add(subscriber);
	}
	public User getSubscriber() {
		return subscribers.get(0);
	}
	public void addStep(ChallengeStep step) {
		steps.add(step);
	}
	public void addImage(Image image) {
		images.add(image);
	}
	public void setAcceptor(User user) {
		backAcceptedChallenges = new ArrayList();
		backAcceptedChallenges.add(user);
	}
	public User getAcceptor() {
		if(backAcceptedChallenges.size() >= 1) {
			return backAcceptedChallenges.get(0);
		}
		return new User(); 
	}
	public Image getMainImageEntity() {
		for(Image img : images) {
			if(img.getIsMain()) {
				return img;
			}
		}
		//TODO is it possible?
		return new Image();
	}
}
