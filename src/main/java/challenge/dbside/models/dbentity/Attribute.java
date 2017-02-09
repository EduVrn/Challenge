package challenge.dbside.models.dbentity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

@Entity(name = "values")
@IdClass(AttributePK.class)
public class Attribute implements Serializable {

    public Attribute() {

    }

    public Attribute(Integer id) {
        this.attribute_id = id;
    }

    @Id
    //@GeneratedValue(strategy=GenerationType.SEQUENCE)
    @Column(name = "attribute_id", insertable = true, updatable = false)
    private Integer attribute_id;

    @Id
    @Column(name = "entity_id", insertable = true, updatable = false)
    private Integer entity_id;

    public Integer getAttribute_id() {
        return attribute_id;
    }

    public void setAttribute_id(Integer attribute_id) {
        this.attribute_id = attribute_id;
    }

    public Integer getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(Integer entity_id) {
        this.entity_id = entity_id;
    }

    @Column(name = "text_value", updatable = true, insertable = true)
    private String value;

    @Column(name = "date_value", updatable = true, insertable = true)
    private Date date_value;

    @Column(name = "int_value", updatable = true, insertable = true)
    private Integer int_value;
    
    @Column(name = "boolean_value", updatable = true, insertable = true)
    private Boolean boolean_value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    public Integer getIntValue() {
        return int_value;
    }

    public void setIntValue(Integer value) {
        this.int_value = value;
    }

    public Date getDateValue() {
        return date_value;
    }

    public void setDateValue(Date value) {
        this.date_value = value;
    }
    
    public Boolean getBooleanValue() {
        return boolean_value;
    }
    
    public void setBooleanValue(Boolean value) {
        this.boolean_value = value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Attribute)) {
            return false;
        }
        Attribute target = (Attribute) o;

        if (attribute_id != null ? !attribute_id.equals(target.getAttribute_id()) : target.getAttribute_id() != null) {
            return false;
        }
        if (entity_id != null ? !entity_id.equals(target.getEntity_id()) : target.getEntity_id() != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = attribute_id != null ? attribute_id.hashCode() : 0;
        result = 31 * result + (entity_id != null ? entity_id.hashCode() : 0);

        return result;
    }

    public String toString() {

        return "	entity_id: " + this.entity_id
                + "  attribute_id: " + this.attribute_id
                + "  value: " + this.value;
    }

}
