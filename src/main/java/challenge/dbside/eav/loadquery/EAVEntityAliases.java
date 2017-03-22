package challenge.dbside.eav.loadquery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.hibernate.loader.EntityAliases;
import org.hibernate.persister.entity.Loadable;
import org.hibernate.persister.walking.spi.AttributeDefinition;

public class EAVEntityAliases implements EntityAliases {

    private final String suffix;
    private final Map userProvidedAliases;

    private final String[] suffixedKeyColumns;
    private final String[][] suffixedPropertyColumns;

    public EAVEntityAliases(Loadable persister, String suffix) {
        this.suffix = suffix;
        this.userProvidedAliases = Collections.EMPTY_MAP;

        suffixedKeyColumns = new String[]{"id"};
        ArrayList<AttributeDefinition> attrs = new ArrayList<AttributeDefinition>(
                (Collection<AttributeDefinition>) persister.getAttributes());

        suffixedPropertyColumns = new String[attrs.size()][];
        for (int i = 0; i < attrs.size(); i++) {
            AttributeDefinition a = attrs.get(i);
            if (!a.getType().isCollectionType()) {
                suffixedPropertyColumns[i] = new String[1];
                suffixedPropertyColumns[i][0] = a.getName();
            } else {
                suffixedPropertyColumns[i] = new String[0];
            }
        }

        //[[], [], [DESCRIPT2_0_0_], [], [NAME3_0_0_]]
        //description, name
        //this( Collections.EMPTY_MAP, persister, suffix );
    }

    public String[] getSuffixedKeyAliases() {
        return suffixedKeyColumns;
    }

    public String getSuffixedDiscriminatorAlias() {
        throw new RuntimeException("not supported");
    }

    public String[] getSuffixedVersionAliases() {
        throw new RuntimeException("not supported");
    }

    public String[][] getSuffixedPropertyAliases() {
        return suffixedPropertyColumns;
    }

    public String[][] getSuffixedPropertyAliases(Loadable persister) {
        throw new RuntimeException("not supported");
        //return null;
    }

    public String getRowIdAlias() {
        throw new RuntimeException("not supported");
    }

    public String getSuffix() {
        return suffix;
    }

}
