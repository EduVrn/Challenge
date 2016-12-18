package challenge.dbside.models;

import challenge.dbside.ini.ContextType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "entities")
public class ChallengeInstance extends BaseEntity {

    public ChallengeInstance() {
        super(ChallengeInstance.class.getSimpleName());
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
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
