package challenge.dbside.models;

import java.io.Serializable;

import javax.persistence.*;


public class AttributePK implements Serializable {
	@Column(name="entity_id", insertable=true, updatable=false)
	private Integer entity_id;
	
	@Column(name="attribute_id", insertable=true, updatable=false)
	private Integer attribute_id;
	
	
	public Integer getEnitity_id() {
		return entity_id;
	}


	public void setEnitity_id(Integer entity_id) {
		this.entity_id = entity_id;
	}
	
	
	public Integer getAttribute_id() {
		return attribute_id;
	}
	
	
	public void setAttribute_id(Integer attribute_id) {
		this.attribute_id = attribute_id;
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (this == o) 
			return true;
		if (!(o instanceof AttributePK)) 
			return false;
		AttributePK targetId = (AttributePK) o;
		
		if (entity_id != null ? !entity_id.equals(targetId.entity_id) : targetId.entity_id != null)
			return false;
		if (attribute_id != null ? !attribute_id.equals(targetId.attribute_id) : targetId.attribute_id != null) 
			return false;
		
		return true;
	}
	
	
	@Override
	public int hashCode() {
		int result = entity_id != null ? entity_id.hashCode() : 0;
		result = 31 * result + (attribute_id != null ? attribute_id.hashCode() : 0);
		
		return result;
	}
	
}
