package challenge.dbside.models.ini;

import java.util.ArrayList;
import java.util.Iterator;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "types_of_entities")
public class TypeOfEntity {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
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
    
    public TypeOfEntity(String nameTypeEntity, int type_of_id) {
        this.nameTypeEntity = nameTypeEntity;
        listAttributes = new ArrayList<TypeOfAttribute>();
        this.typeEntityID = type_of_id;
    }
    
    public void add(TypeOfAttribute type) {
        listAttributes.add(type);
    }
    
    
    public void removeAttr(String nameAttr) {
    	for(Iterator<TypeOfAttribute> itr = listAttributes.listIterator();
    			itr.hasNext();) {
    		TypeOfAttribute t = itr.next();
    		if(t.getName().equals(nameAttr)) {
    			itr.remove();
    			break;
    		}    		
    	}
    }

    public List<TypeOfAttribute> getAttributes() {
        return listAttributes;
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
