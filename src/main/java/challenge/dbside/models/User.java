package challenge.dbside.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.Persister;
import org.hibernate.eav.EAVEntity;
import org.hibernate.eav.EAVGlobalContext;

import challenge.dbside.eav.EAVPersister;
import challenge.dbside.eav.collection.EAVCollectionPersister;
import challenge.dbside.models.status.ChallengeInstanceStatus;

@Entity
@EAVEntity
@Persister(impl = EAVPersister.class)
public class User extends BaseEntity {

    public User() {
        super(EAVGlobalContext.getTypeOfEntity(User.class.getSimpleName().toLowerCase()).getId());

        backCreators = new ArrayList();
        images = new ArrayList();
        acceptedChallenges = new ArrayList();
        friends = new ArrayList();
        backSubscribers = new ArrayList();
        backReceivers = new ArrayList();
        votesAgainst = new ArrayList();
        votesFor = new ArrayList();
    }

    private String name;
    private Integer rating;
    //TODO change it (change mapping many-to-many to one-to-many

    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinTable(name = "eav_relationship",
            joinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id")
    )
    @Persister(impl = EAVCollectionPersister.class)
    private List<ChallengeDefinition> backCreators;

    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinTable(name = "eav_relationship",
            joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
    )
    @Persister(impl = EAVCollectionPersister.class)
    private List<Image> images;

    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinTable(name = "eav_relationship",
            joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
    )
    @Persister(impl = EAVCollectionPersister.class)
    private List<ChallengeInstance> acceptedChallenges;

    //TODO change friendsLeft and friendsRight, getFriends = friendsLeft + friendsRight
    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinTable(name = "eav_relationship",
            joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
    )
    @Persister(impl = EAVCollectionPersister.class)
    private List<User> friends;

    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinTable(name = "eav_relationship",
            joinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id")
    )
    @Persister(impl = EAVCollectionPersister.class)
    private List<ChallengeInstance> backSubscribers;

    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinTable(name = "eav_relationship",
            joinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id")
    )
    @Persister(impl = EAVCollectionPersister.class)
    private List<Request> backReceivers;

    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinTable(name = "eav_relationship",
            joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
    )
    @Persister(impl = EAVCollectionPersister.class)
    private List<ChallengeInstance> votesFor;

    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinTable(name = "eav_relationship",
            joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
    )
    @Persister(impl = EAVCollectionPersister.class)
    private List<ChallengeInstance> votesAgainst;

    public List<ChallengeInstance> getVotesFor() {
        return votesFor;
    }

    public void setVotesFor(List<ChallengeInstance> votesFor) {
        this.votesFor = votesFor;
    }

    public List<ChallengeInstance> getVotesAgainst() {
        return votesAgainst;
    }

    public void setVotesAgainst(List<ChallengeInstance> votesAgainst) {
        this.votesAgainst = votesAgainst;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public List<ChallengeDefinition> getBackCreators() {
        return backCreators;
    }

    public void setBackCreators(List<ChallengeDefinition> backCreators) {
        this.backCreators = backCreators;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public List<ChallengeInstance> getAcceptedChallenges() {
        return acceptedChallenges;
    }

    public void setAcceptedChallenges(List<ChallengeInstance> acceptedChallenges) {
        this.acceptedChallenges = acceptedChallenges;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public List<ChallengeInstance> getBackSubscribers() {
        return backSubscribers;
    }

    public void setBackSubscribers(List<ChallengeInstance> backSubscribers) {
        this.backSubscribers = backSubscribers;
    }

    public List<Request> getBackReceivers() {
        return backReceivers;
    }

    public void setBackReceivers(List<Request> backRequestReceiver) {
        this.backReceivers = backRequestReceiver;
    }

    public void addVoteFor(ChallengeInstance challenge) {
        votesFor.add(challenge);
    }

    public void addVoteAgainst(ChallengeInstance challenge) {
        votesAgainst.add(challenge);
    }

    public void addFriend(User user) {
        friends.add(user);
    }

    public void removeFriendRequest(Request request) {
        backReceivers.remove(request);
        request.removeReceiver(this);
    }

    public List<Request> getIncomingRequests() {
        return getBackReceivers();
    }

    public void acceptChallenge(ChallengeInstance chal) {
        acceptedChallenges.add(chal);
    }

    public void addSubscription(ChallengeInstance chal) {
        backSubscribers.add(chal);
    }

    public List<ChallengeDefinition> getChallenges() {
        return getBackCreators();
    }

    public Image getMainImageEntity() {
        for (Image img : images) {
            if (img.getIsMain()) {
                return img;
            }
        }
        //TODO it possible?
        return new Image();
    }
    public Image getCommentImageEntity() {
        for (Image img : images) {
            if (img.getIsForComment()) {
                return img;
            }
        }
        //TODO it possible?
        return new Image();
    }

    public void addRating(Integer addPart) {
        rating += addPart;
    }

    public void addImage(Image img) {
        images.add(img);
    }

    public List<ChallengeInstance> getSubscriptions() {
        return getBackSubscribers();
    }

    public List<User> getIncomingFriendRequestSenders() {
        List<User> friendsRequests = new ArrayList();
        for (Request req : getBackReceivers()) {
            if (req.getSubject() == null) {
                friendsRequests.add(req.getSender());
            }
        }
        return friendsRequests;
    }

    public void addChallenge(ChallengeDefinition chal) {
        this.backCreators.add(chal);
    }

    public boolean canAccept(ChallengeDefinition chal) {
        List<ChallengeInstance> instances = chal.getChallengeInstances();
        for (ChallengeInstance instance : instances) {
            if (instance.getAcceptor().equals(this) && instance.getStatus() == ChallengeInstanceStatus.ACCEPTED) {
                return false;
            }
        }
        return true;
    }

    public static final Comparator<User> COMPARE_BY_RATING = (User left, User right)
            -> Integer.signum(right.getRating() - left.getRating());
}
