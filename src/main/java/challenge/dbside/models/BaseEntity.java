package challenge.dbside.models;

import challenge.dbside.models.dbentity.DBSource;
import java.util.Objects;

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

    public BaseEntity(String nameClass) {
        dataSource = new DBSource(nameClass);
    }

    public BaseEntity(DBSource dt) {
        dataSource = dt;
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
        BaseEntity other = (BaseEntity) obj;
        if (!this.getId().equals(other.getId())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.dataSource);
        return hash;
    }
}
