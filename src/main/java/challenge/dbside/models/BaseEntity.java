package challenge.dbside.models;


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
	
	public DBSource getDataSource() {
    	return dataSource;
    }
	
	/*
	public Object remove(MultiMap mmap, Object key, Object item) {
		Collection valuesForKey = (Collection) mmap.get(key);
		if (valuesForKey == null) {
            return null;
        }
		valuesForKey.remove(item);
		if (valuesForKey.isEmpty()){
            ((Map)mmap).remove(key);
        }
		return item;
	}*/
	
	public BaseEntity(String nameClass) {
		dataSource = new DBSource(nameClass);
	}
	
	public BaseEntity(DBSource dt) {
		dataSource = dt;
	}
	 
	public boolean equals(Object obj) {
        return (this.getId() == ((BaseEntity)obj).getId());
    }
}
