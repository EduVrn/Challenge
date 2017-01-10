package challenge.dbside.ini;

import challenge.dbside.models.ini.TypeOfAttribute;
import challenge.dbside.models.ini.TypeOfEntity;

import java.util.HashMap;
import java.util.Map;

public class ContextType {

    private static ContextType context;

    private ContextType() {
        availableAttributes = new HashMap();
        availableEntities = new HashMap();
    }

    public static ContextType getInstance() {
        if (context == null) {
            context = new ContextType();
        }

        return context;
    }

    private Map<String, TypeOfEntity> availableEntities;
    private Map<String, TypeOfAttribute> availableAttributes;

    public TypeOfAttribute getTypeAttribute(String name) {
        return availableAttributes.get(name);
    }

    public TypeOfEntity getTypeEntity(String name) {
        return availableEntities.get(name);
    }

    public void add(TypeOfAttribute attr) {
        availableAttributes.put(attr.getName(), attr);
    }

    public void add(TypeOfEntity entity) {
        availableEntities.put(entity.getNameTypeEntity(), entity);
    }
}
