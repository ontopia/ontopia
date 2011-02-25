package ontopoly.rest.editor.spi.impl.pojo;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ontopoly.rest.editor.spi.PrestoSchemaProvider;
import ontopoly.rest.editor.spi.PrestoType;

public class PojoSchemaProvider implements PrestoSchemaProvider {

    private String databaseId;
    
    private Map<String,PrestoType> typesMap = new HashMap<String,PrestoType>();

    public static PojoSchemaProvider getSchemaProvider(String databaseId, String schemaFile) {
        return PojoSchemaModel.parse(databaseId, schemaFile);
    }
    
    public String getDatabaseId() {
        return databaseId;
    }

    public PrestoType getTypeById(String typeId) {
        PrestoType type = typesMap.get(typeId);
        if (type == null) {
            throw new RuntimeException("Unknown type: " + typeId);
        }
        return type;
    }

    public Collection<PrestoType> getRootTypes() {
        Collection<PrestoType> result = new HashSet<PrestoType>(typesMap.values());
        Set<PrestoType> notSuperTypes = new HashSet<PrestoType>();
        for (PrestoType type : result) {
            notSuperTypes.addAll(type.getDirectSubTypes());
        }
        result.removeAll(notSuperTypes);
        return result;
    }

    protected void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }

    protected void addType(PrestoType type) {
        this.typesMap.put(type.getId(), type);        
    }

}
