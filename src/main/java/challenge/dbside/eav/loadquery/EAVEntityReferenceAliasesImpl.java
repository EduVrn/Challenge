package challenge.dbside.eav.loadquery;

import org.hibernate.loader.EntityAliases;
import org.hibernate.loader.plan.exec.spi.EntityReferenceAliases;

public class EAVEntityReferenceAliasesImpl implements EntityReferenceAliases {

    private final EntityAliases columnAliases;

    public EAVEntityReferenceAliasesImpl(EntityAliases columnAliases) {
        this.columnAliases = columnAliases;
    }

    public String getTableAlias() {
        return "";
    }

    public EntityAliases getColumnAliases() {
        // TODO Auto-generated method stub
        return columnAliases;
    }

}
