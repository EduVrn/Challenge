package challenge.dbside.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.*;
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
import challenge.dbside.models.status.ChallengeDefinitionStatus;

@Entity
@EAVEntity
@Persister(impl = EAVPersister.class)
public class ChallengeDefinition extends BaseEntity {

    private static final Logger logger = LoggerFactory.getLogger(ChallengeDefinition.class);

    public ChallengeDefinition() {
        super(EAVGlobalContext.getTypeOfEntity(ChallengeDefinition.class.getSimpleName().toLowerCase()).getId());
        images = new ArrayList();
        tags = new ArrayList();

        creators = new ArrayList();
        allAcceptors = new ArrayList();
        comments = new ArrayList();
        challengeInstances = new ArrayList();
    }

    private String name;
    private String date;
    private String description;
    private String status;
    private Integer rating;

    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinTable(name = "eav_relationship",
            joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
    )
    @Persister(impl = EAVCollectionPersister.class)
    private List<Image> images;

    //TODO chaldeftag
    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinTable(name = "eav_relationship",
            joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
    )
    @Persister(impl = EAVCollectionPersister.class)
    private List<Tag> tags;

    //TODO change it (change mapping many-to-many to one-to-many
    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinTable(name = "eav_relationship",
            joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
    )
    @Persister(impl = EAVCollectionPersister.class)
    private List<User> creators;

    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinTable(name = "eav_relationship",
            joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
    )
    @Persister(impl = EAVCollectionPersister.class)
    private List<User> allAcceptors;

    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinTable(name = "eav_relationship",
            joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
    )
    @Persister(impl = EAVCollectionPersister.class)
    private List<Comment> comments;

    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinTable(name = "eav_relationship",
            joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
    )
    @Persister(impl = EAVCollectionPersister.class)
    private List<ChallengeInstance> challengeInstances;

    public List<ChallengeInstance> getChallengeInstances() {
        return challengeInstances;
    }

    public void setChallengeInstances(List<ChallengeInstance> challengeInstances) {
        this.challengeInstances = challengeInstances;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<User> getAllAcceptors() {
        List<User> acceptors = new ArrayList<>();
        challengeInstances.forEach((instance) -> {
            acceptors.add(instance.getAcceptor());
        });
        return acceptors;
    }

    public void setAllAcceptors(List<User> acceptors) {
        this.allAcceptors = acceptors;
    }

    public List<User> getCreators() {
        return creators;
    }

    public void setCreators(List<User> creators) {
        this.creators = creators;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
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

    //TODO change to base date integer
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
    //TODO change to base date integer

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

    public ChallengeDefinitionStatus getStatus() {
        return ChallengeDefinitionStatus.valueOf(status);
    }

    public void setStatus(ChallengeDefinitionStatus status) {
        this.status = status.name();
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public Image getMainImageEntity() {
        for (Image img : images) {
            if (img.getIsMain()) {
                return img;
            }
        }
        //TODO is it possible?
        return new Image();
    }

    public void addRating(Integer changeRating) {
        rating += changeRating;
    }

    public void removeAllTags() {
        tags.clear();
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void setCreator(User user) {
        creators = new ArrayList();
        creators.add(user);
    }
    //TODO change creator

    public User getCreator() {
        if (creators.size() >= 1) {
            return creators.get(0);
        }
        return new User();
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public void addImage(Image image) {
        images.add(image);
    }

    public void addChallengeInstance(ChallengeInstance instance) {
        challengeInstances.add(instance);
    }

    public static final Comparator<ChallengeDefinition> COMPARE_BY_RATING = (ChallengeDefinition left, ChallengeDefinition right)
            -> Integer.signum(right.getRating() - left.getRating());

}
