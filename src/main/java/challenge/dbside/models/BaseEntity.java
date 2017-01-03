package challenge.dbside.models;

import java.util.*;

import javax.persistence.*;

import challenge.dbside.ini.*;
import challenge.dbside.models.ini.*;

//add
//update
//remove
//select 
@Entity
@Table(name = "entities")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorFormula("'GenericClient'") /*for hide dbtype*/
//@DiscriminatorFormula("CASE "
//        + "WHEN DTYPE in ('User', Chal) THEN DTYPE"
//        + "ELSE 'Base'"
//        + "END")
public class BaseEntity {

    @Id
    //@GeneratedValue
    @Column(name = "entity_id", insertable = true, updatable = false)
    private Integer id;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER/*????*/) //TODO: make LAZY
    @MapKey(name = "attribute_id")
    @JoinColumns({
        @JoinColumn(name = "entity_id", referencedColumnName = "entity_id",
                insertable = false, updatable = false),})
    /*ID attribute, Attribute value*/
    private Map<Integer, Attribute> attributes;

    @Column(name = "type_of_entity")
    private Integer entityType;

    //TODO: parent 
    @ManyToOne(cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "parent_id")
    private BaseEntity parent;
    @OneToMany(/*mappedBy="parent",*/fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL}) //TODO: make LAZY
    @JoinColumn(name = "parent_id")
    private List<BaseEntity> children;

    public BaseEntity() {
        attributes = new HashMap<>();
        children = new ArrayList<>();
    }

    //call only from children 
    static int i = 0;

    public BaseEntity(String entityName) {
        children = new ArrayList<>();

        TypeOfEntity type = ContextType.getInstance().getTypeEntity(entityName);
        entityType = type.getTypeEntityID();

        attributes = new HashMap<>();
        type.getAttributes().forEach((t) -> {
            Attribute attr = new Attribute(t.getId());
            attributes.put(t.getId(), attr);
        });
    }

    //TODO: Only 4 DEBUG!!!
    public BaseEntity(String name, String surname) {
        //this.setId(1);
        children = new ArrayList<>();

        attributes = new HashMap<Integer, Attribute>();

        Attribute attr = new Attribute();
        attr.setValue(name);
        //attr.setEntity_id(1);
        attr.setAttribute_id(1);
        this.attributes.put(1, attr);

        attr = new Attribute();
        attr.setValue(surname);
        //attr.setEntity_id(1);
        attr.setAttribute_id(2);

        this.attributes.put(2, attr);
    }

    public BaseEntity getParent() {
        return parent;
    }

    public void setParent(BaseEntity parent) {
        this.parent = parent;
    }

    public List<BaseEntity> getChildren() {
        return children;
    }

    public void setChildren(List<BaseEntity> children) {
        this.children = children;
    }
    
    public void addChild(BaseEntity child) {
        children.add(child);
        child.setParent(this);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;

        for (Attribute attr : attributes.values()) {
            attr.setEntity_id(id);
        }
    }

    public Integer getEntityType() {
        return entityType;
    }

    public void setEntityType(Integer entityType) {
        this.entityType = entityType;
    }

    public Map<Integer, Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<Integer, Attribute> attributes) {
        this.attributes = attributes;
    }

    public String toString() {
        String str = "";
        str = "id: " + this.id + " type: " + this.entityType;
        for (Attribute attr : this.attributes.values()) {
            str += "\n" + attr;

        }
        return str;
    }

    @Override	//TODO: see it
    public int hashCode() {
        int result = id!=null ? id.hashCode() : 0;

        result = 31 * result + entityType.hashCode();

        return result;
    }

    @Override	//TODO: see it
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof BaseEntity)) {
            return false;
        }

        BaseEntity target = (BaseEntity) o;

        if (!this.id.equals(target.getId())) {
            return false;
        }

        return true;
    }

}
