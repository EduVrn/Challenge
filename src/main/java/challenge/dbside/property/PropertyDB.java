package challenge.dbside.property;

import javax.persistence.*;

@Entity
@Table(name = "property")
public class PropertyDB {
	
	@Id
	private String name;
	
	
	private String value;

	
	public PropertyDB() {
		
	}

	public PropertyDB(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String nameProperty) {
		this.name = nameProperty;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
