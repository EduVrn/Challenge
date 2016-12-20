package challenge.dbside.models;

import challenge.dbside.ini.ContextType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "entities")
public class ChallengeInstance extends BaseEntity {

    public ChallengeInstance() {
        super(ChallengeInstance.class.getSimpleName());
    }

    @OneToOne(cascade=CascadeType.ALL) 
    @JoinTable(name="relationship", 
            joinColumns={@JoinColumn(name ="entity_id2", referencedColumnName="entity_id")},
            inverseJoinColumns={@JoinColumn(name ="entity_id1", referencedColumnName="entity_id")})
    private User acceptor;

    public String getName() {
        return this.getAttributes()
                .get(ContextType.getInstance().getTypeAttribute("name").getId()).getValue();
    }

    public void setName(String name) {
        this.getAttributes()
                .get(ContextType.getInstance().getTypeAttribute("name").getId()).setValue(name);
    }

    public User getAcceptor() {
        return acceptor;
    }

    public void setAcceptor(User acceptor) {
        this.acceptor = acceptor;
    }
}
