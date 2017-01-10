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

    public ChallengeInstance(ChallengeDefinition chalDef) {
        super(ChallengeInstance.class.getSimpleName());
        setName(chalDef.getName());
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinTable(name = "relationship",
            joinColumns = {
                @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id")})
    private User acceptor;

    public String getName() {
        return (String) this.getAttributes()
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

    public ChallengeStatus getStatus() {
        return ChallengeStatus.valueOf(this.getAttributes()
                .get(ContextType.getInstance().getTypeAttribute("chalStatus").getId()).getValue());
    }

    public void setStatus(ChallengeStatus status) {
        this.getAttributes()
                .get(ContextType.getInstance().getTypeAttribute("chalStatus").getId()).setValue(status.name());
    }
}
