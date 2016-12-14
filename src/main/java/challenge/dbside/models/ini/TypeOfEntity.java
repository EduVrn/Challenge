package challenge.dbside.models.ini;

import java.util.ArrayList;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "types_of_entities")
public class TypeOfEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_of_entity_id")
    private int typeEntityID;

    @Column(name = "name")
    private String nameTypeEntity;

    @ManyToMany(fetch = FetchType.EAGER/*, cascade = {CascadeType.ALL}*/)
    @JoinTable(name = "entity_attributes",
            joinColumns = @JoinColumn(name = "type_of_entity_id"),
            inverseJoinColumns = @JoinColumn(name = "attribute_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"type_of_entity_id", "attribute_id"}))
    private List<TypeOfAttribute> listAttributes;

    public TypeOfEntity() {
        listAttributes = new ArrayList<TypeOfAttribute>();
    }
    
    public TypeOfEntity(String nameTypeEntity) {
        this.nameTypeEntity = nameTypeEntity;
        listAttributes = new ArrayList<TypeOfAttribute>();
    }
    
    public void add(TypeOfAttribute type) {
        listAttributes.add(type);
    }

    public List<TypeOfAttribute> getAttributes() {
        return listAttributes;
    }

    public void setAttributes(List<TypeOfAttribute> attributes) {
        this.listAttributes = attributes;
    }

    public int getTypeEntityID() {
        return typeEntityID;
    }

    public void setTypeEntityID(int typeEntityID) {
        this.typeEntityID = typeEntityID;
    }

    public String getNameTypeEntity() {
        return nameTypeEntity;
    }

    public void setNameTypeEntity(String nameTypeEntity) {
        this.nameTypeEntity = nameTypeEntity;
    }

    public String toString() {
        String type = "TypeEntityID:" + typeEntityID + "  nameTypeEntity: " + nameTypeEntity;
        /*for(TypeOfAttribute t : listTypeOffAttribute) {
 			type += t;
 		}*/

        return type;
    }

}
