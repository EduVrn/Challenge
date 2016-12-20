package challenge.dbside.models;

import javax.persistence.Entity;
import javax.persistence.Table;

import challenge.dbside.ini.ContextType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;

@Entity
@Table(name = "entities")
//@DiscriminatorValue(value="Chal")
public class ChallengeDefinition extends BaseEntity {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinTable(name = "relationship", 
            joinColumns = @JoinColumn(name = "entity_id2", referencedColumnName = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "entity_id1", referencedColumnName = "entity_id")
    )
    private User creator;

    public ChallengeDefinition() {
        super(ChallengeDefinition.class.getSimpleName());
    }

    public List<User> getAllAcceptors() {
        List<User> acceptors = new ArrayList<>();
        this.getChildren().forEach((chalInstance) -> {
            acceptors.add(((ChallengeInstance) chalInstance).getAcceptor());
        });
        return acceptors;
    }

    public String getName() {
        return this.getAttributes()
                .get(ContextType.getInstance().getTypeAttribute("name").getId()).getValue();
    }

    public void setName(String name) {
        this.getAttributes()
                .get(ContextType.getInstance().getTypeAttribute("name").getId()).setValue(name);
    }

    public String getDescription() {
        return this.getAttributes()
                .get(ContextType.getInstance().getTypeAttribute("description").getId()).getValue();
    }

    public void setDescription(String description) {
        this.getAttributes()
                .get(ContextType.getInstance().getTypeAttribute("description").getId()).setValue(description);
    }

    public String getImageRef() {
        return "images/" + this.getAttributes()
                .get(ContextType.getInstance().getTypeAttribute("imageref").getId()).getValue();
    }

    public void setImageRef(String description) {
        this.getAttributes()
                .get(ContextType.getInstance().getTypeAttribute("imageref").getId()).setValue(description);
    }

    public String getDate() {
        return this.getAttributes()
                .get(ContextType.getInstance().getTypeAttribute("date").getId()).getValue();
    }

    public void setDate(Date date) {
        this.getAttributes()
                .get(ContextType.getInstance().getTypeAttribute("date").getId()).setValue(date.toString());
    }

    public User getCreator() {
        return creator;
    }

     public void setCreator(User creator) {
        this.creator = creator;
    }
}
