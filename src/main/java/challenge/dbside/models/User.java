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

        listChallengeRoadMap = new ArrayList<ChallengeDefinition>();
        friends = new ArrayList<User>();
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "relationship",
            joinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")
    )
    @Where(clause = "type_of_entity = 2")
    private List<ChallengeDefinition> listChallengeRoadMap;

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

    public void addChallenge(ChallengeDefinition m) {
        listChallengeRoadMap.add(m);
    }

    @Override
    public String toString() {
        String entityInfo = super.toString();
        StringBuilder info = new StringBuilder();
        info.append(entityInfo);
        info.append("\nFriends: \n");
        for (User u : friends) {
            info.append("\nid: " + u.getId() + " name:" + u.getName());
        }
        info.append("\nChallenges: \n");
        for (ChallengeDefinition c : listChallengeRoadMap) {
            info.append("\nid: " + c.getId() + " name: " + c.getName());
        }
        return info.toString();
    }
}
