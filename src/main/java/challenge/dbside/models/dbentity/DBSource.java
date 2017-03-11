package challenge.dbside.models.dbentity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.map.MultiValueMap;

import challenge.dbside.ini.ContextType;
import challenge.dbside.models.ini.TypeAttribute;
import challenge.dbside.models.ini.TypeOfEntity;

public class DBSource {

    private Integer id;
    private Integer entityType;

    private Integer parentId;

    private Map<Integer, Attribute> attributes;

    private DBSource parent;
    private Set<DBSource> children;

    private MultiMap rel;
    private MultiMap backRel;

    public DBSource() {
        attributes = new HashMap();
        children = new HashSet<>();
        rel = new MultiValueMap();
        backRel = new MultiValueMap();

        rel.put(-1, this);
        backRel.put(-3, this);

    }

    public DBSource(String entityName) {

        children = new HashSet<>();

        TypeOfEntity type = ContextType.getInstance().getTypeEntity(entityName);
        entityType = type.getTypeEntityID();

        attributes = new HashMap();
        rel = new MultiValueMap();
        backRel = new MultiValueMap();

        rel.put(-1, this);
        backRel.put(-3, this);

        type.getAttributes().forEach((t) -> {
            if (t.getType_of_attribute() != TypeAttribute.REF.getValue()
                    && t.getType_of_attribute() != TypeAttribute.REF.getValue()) {
                Attribute attr = new Attribute(t.getId());
                attributes.put(t.getId(), attr);
            } else {

            }
        });
    }

    public DBSource(String name, String surname) {
        attributes = new HashMap<>();
        children = new HashSet<>();
        rel = new MultiValueMap();
        backRel = new MultiValueMap();

        rel.put(-1, this);
        backRel.put(-3, this);
        entityType = 1;

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

    public Integer getEntityType() {
        return entityType;
    }

    public void setEntityType(Integer entityType) {
        this.entityType = entityType;
    }

    public void addChild(DBSource child) {
        children.add(child);
    }

    public Set<DBSource> getChildren() {
        return children;
    }

    public void setChildren(Set<DBSource> children) {
        this.children = children;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public DBSource getParent() {
        return parent;
    }

    public void setParent(DBSource parent) {
        this.parent = parent;
    }

    public void addRel(Integer key, DBSource entity) {
        rel.put(key, entity);
    }

    public void addBackRel(Integer key, DBSource entity) {
        backRel.put(key, entity);
    }

    public MultiMap getRel() {
        return rel;
    }

    public void setRel(MultiMap rel) {
        this.rel = rel;
    }

    public MultiMap getBackRel() {
        return backRel;
    }

    public void setBackRel(MultiMap backRel) {
        this.backRel = backRel;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Map<Integer, Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<Integer, Attribute> attributes) {
        this.attributes = attributes;
    }
    
}
