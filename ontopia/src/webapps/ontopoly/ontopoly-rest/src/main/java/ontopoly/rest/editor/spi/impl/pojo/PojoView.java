package ontopoly.rest.editor.spi.impl.pojo;

import ontopoly.rest.editor.spi.PrestoSchemaProvider;
import ontopoly.rest.editor.spi.PrestoView;

public class PojoView implements PrestoView {

    private String id;
    private String name;
    private PrestoSchemaProvider schemaProvider;
    
    PojoView(String id, PrestoSchemaProvider schemaProvider) {
        this.id = id;
        this.schemaProvider = schemaProvider;        
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof PojoView) {
            PojoView o = (PojoView)other;
            return id.equals(o.id);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public PrestoSchemaProvider getSchemaProvider() {
        return schemaProvider;
    }

    protected void setName(String name) {
        this.name = name;
    }

}
