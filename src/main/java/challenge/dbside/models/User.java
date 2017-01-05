package challenge.dbside.models;

import java.util.*;

import javax.persistence.*;

import challenge.dbside.ini.ContextType;

import org.hibernate.annotations.Where;

@Entity
@Table(name = "entities")
//@DiscriminatorValue(value="User")
public class User extends BaseEntity {

    public User() {
        super(User.class.getSimpleName());

        listOfChallenges = new ArrayList<>();
        listOfAcceptedChallenges = new ArrayList<>();
        friends = new ArrayList<>();
        comments = new ArrayList<>();
    }

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Where(clause = "type_of_entity = 2")
    private List<ChallengeDefinition> listOfChallenges;

    @OneToMany(mappedBy = "acceptor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Where(clause = "type_of_entity = 3")
    private List<ChallengeInstance> listOfAcceptedChallenges;

    //TODO: EAGER fetch type
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "relationship",
            joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
    )
    @Where(clause = "type_of_entity = 1")
    private List<User> friends;
    
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Where(clause = "type_of_entity = 4")
    private List<Comment> comments;

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
    
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setAuthor(this);
    }

    public void setFriends(List<User> users) {
        this.friends = users;
    }

    public List<User> getFriends() {
        return this.friends;
    }

    public void addFriend(User user) {
        friends.add(user);
    }

    public String getName() {
        return this.getAttributes()
                .get(ContextType.getInstance().getTypeAttribute("name").getId()).getValue();
    }

    public void setName(String name) {
        this.getAttributes()
                .get(ContextType.getInstance().getTypeAttribute("name").getId()).setValue(name);
    }

    public void addChallenge(ChallengeDefinition chal) {
        chal.setCreator(this);
        chal.setStatus(ChallengeDefinitionStatus.CREATED);
        listOfChallenges.add(chal);
    }

    public void addAcceptedChallenge(ChallengeInstance chal) {
        chal.setAcceptor(this);
        listOfAcceptedChallenges.add(chal);
    }

    public List<ChallengeDefinition> getChallenges() {
        return listOfChallenges;
    }

    public List<ChallengeInstance> getAcceptedChallenges() {
        List<ChallengeInstance> accepted = new ArrayList<>();
        listOfAcceptedChallenges.forEach(chal -> {
            if (chal.getStatus() == ChallengeStatus.ACCEPTED) {
                accepted.add(chal);
            }
        });
        return accepted;
    }

    public List<ChallengeInstance> getChallengeRequests() {
        List<ChallengeInstance> requests = new ArrayList<>();
        listOfAcceptedChallenges.forEach(chal -> {
            if (chal.getStatus() == ChallengeStatus.AWAITING) {
                requests.add(chal);
            }
        });
        return requests;
    }

    public void acceptChallenge(ChallengeInstance chal) {
        List<ChallengeInstance> requests = getChallengeRequests();
        if (requests.contains(chal)) {
            chal.setStatus(ChallengeStatus.ACCEPTED);
            chal.setAcceptor(this);
        }
    }

    public void declineChallenge(ChallengeInstance chal) {
        List<ChallengeInstance> requests = getChallengeRequests();
        if (requests.contains(chal)) {
            listOfAcceptedChallenges.remove(chal);
            chal.setAcceptor(null);
            chal.setParent(null);
        }
    }

    @Override
    public String toString() {
        String entityInfo = super.toString();
        StringBuilder info = new StringBuilder();
        info.append(entityInfo);
        info.append("\nFriends: \n");
        friends.forEach((u) -> {
            info.append("\nid: ").append(u.getId()).append(" name:").append(u.getName());
        });
        info.append("\nChallenges: \n");
        listOfChallenges.forEach((c) -> {
            info.append("\nid: ").append(c.getId()).append(" name: ").append(c.getName());
        });
        return info.toString();
    }

    public void setImageRef(String imageRef) {
        this.
                getAttributes().get(ContextType.getInstance().getTypeAttribute("imageref").getId()).setValue(imageRef);
    }

    public String getImageRef() {
        return "../images/" + this.getAttributes()
                .get(ContextType.getInstance().getTypeAttribute("imageref").getId()).getValue();
    }

}
