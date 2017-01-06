package challenge.dbside.models;

import challenge.dbside.models.dbentity.Attribute;
import challenge.dbside.models.dbentity.DBSource;

public class BaseEntity {

	private DBSource dataSource;
	
	public BaseEntity() {
		
	}
	
	public Integer getId() {
		return dataSource.getId();
	}
	
	public void setId(Integer id) {
		dataSource.setId(id);
		


	}
	
	/*public BaseEntity(DBSource dataSource) {
		this.dataSource = dataSource;
	}*/
	
	public DBSource getDataSourse() {
    	return dataSource;
    }
	
	public BaseEntity(String nameClass) {
		dataSource = new DBSource(nameClass);
	}
	
	public BaseEntity(DBSource dt) {
		dataSource = dt;
		
	}
}
