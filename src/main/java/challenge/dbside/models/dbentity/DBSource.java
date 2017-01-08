package challenge.dbside.models.dbentity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;

import challenge.dbside.ini.ContextType;
import challenge.dbside.models.ini.TypeAttribute;
import challenge.dbside.models.ini.TypeOfEntity;

import java.util.ArrayList;



public class DBSource {

	private Integer id;
	private Integer entityType;

	private Integer parentId;

	private Map<Integer, Attribute> attributes;

	private DBSource parent;
	private Set<DBSource> children;

	private MultiMap relations_l;
	private MultiMap relations_r;

	public DBSource() {		
		attributes = new HashMap();
		children = new HashSet();
		relations_l = new MultiHashMap();
		relations_r = new MultiHashMap();

		relations_l.put(-1, this);
		relations_r.put(-3, this);

	}

	public DBSource(String entityName) {		

		children = new HashSet();

		TypeOfEntity type = ContextType.getInstance().getTypeEntity(entityName);
		entityType = type.getTypeEntityID();        

		attributes = new HashMap();
		relations_l = new MultiHashMap();
		relations_r = new MultiHashMap();
		
		relations_l.put(-1, this);
		relations_r.put(-3, this);
		
		type.getAttributes().forEach((t) -> {
			if(t.getType_of_attribute() != TypeAttribute.REF_ONE_DIRECTIONAL.getValue() 
					&& t.getType_of_attribute() != TypeAttribute.REF_TWO_DIRECTIONAL.getValue()) {
				Attribute attr = new Attribute(t.getId());
				attributes.put(t.getId(), attr);
			}
			else {
				
			}
		});
	}

	public DBSource(String name, String surname) {		
		attributes = new HashMap();
		children = new HashSet();
		relations_l = new MultiHashMap();
		relations_r = new MultiHashMap();
		
		relations_l.put(-1, this);
		relations_r.put(-3, this);
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



	public void addChidlren(DBSource child) {
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



	public MultiMap getRelations_l() {
		return relations_l;
	}

	public void setRelations_l(MultiMap relations_l) {
		this.relations_l = relations_l;
	}

	public MultiMap getRelations_r() {
		return relations_r;
	}

	public void setRelations_r(MultiMap relations_r) {
		this.relations_r = relations_r;
	}

	public void addRelation_l(Integer key, DBSource entity) {
		relations_l.put(key, entity);
	}

	public void addRelation_r(Integer key, DBSource entity) {
		relations_r.put(key, entity);
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
