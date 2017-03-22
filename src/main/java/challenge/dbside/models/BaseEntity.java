package challenge.dbside.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import org.hibernate.annotations.DiscriminatorFormula;
import org.hibernate.eav.EAVEntity;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "eav_entities")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name="type_of_entity")
public abstract class BaseEntity {

    public BaseEntity() {

    }

    public BaseEntity(Integer type_of_entity) {
        this.type_of_entity = type_of_entity;
    }

    @Id
    //@GeneratedValue
    @Column(name = "entity_id", insertable = true, updatable = false)
    private Integer id;

    private Integer type_of_entity;

    public Integer getType_of_entity() {
        return type_of_entity;
    }

    public void setType_of_entity(Integer type_of_entity) {
        this.type_of_entity = type_of_entity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BaseEntity other = (BaseEntity) obj;
        if (!this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        Integer id = this.getId() != null ? this.getId().hashCode() : 0;
        hash = 29 * hash + id;
        return hash;
    }

    public String toString() {
        return this.getId().toString();
    }

}
