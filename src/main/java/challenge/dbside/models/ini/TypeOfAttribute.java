package challenge.dbside.models.ini;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "types_attributes")
public class TypeOfAttribute {

    @Id
    //@GeneratedValue
    @Column(name = "attribute_id")
    private Integer id;

    private Integer type_of_attribute;

    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "listAttributes")
    private List<TypeOfEntity> typeEntities;

    public TypeOfAttribute() {

    }

    public TypeOfAttribute(Integer id, String name, Integer type) {
        this.id = id;
        this.name = name;
        this.type_of_attribute = type;
    }

    public TypeOfAttribute(String name) {
        this.name = name;
    }

    public Integer getType_of_attribute() {
        return type_of_attribute;
    }

    public void setType_of_attribute(Integer type_of_attribute) {
        this.type_of_attribute = type_of_attribute;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TypeOfEntity> getTypeEntities() {
        return typeEntities;
    }

    public void setTypeEntities(List<TypeOfEntity> typeEntities) {
        this.typeEntities = typeEntities;
    }

    public String toString() {
        return "\n    ID_TypeOfAttribute: " + id + "  Name: " + name + "  ";
    }
}
