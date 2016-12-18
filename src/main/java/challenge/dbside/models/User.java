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
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "relationship",
            joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
    )
    @Where(clause = "type_of_entity = 2")
    private List<ChallengeDefinition> listOfChallenges;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "relationship",
            joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
    )
    @Where(clause = "type_of_entity = 3")
    private List<ChallengeInstance> listOfAcceptedChallenges;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "relationship",
            joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
    )
    @Where(clause = "type_of_entity = 1")
    private List<User> friends;

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
        return listOfAcceptedChallenges;
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
}
