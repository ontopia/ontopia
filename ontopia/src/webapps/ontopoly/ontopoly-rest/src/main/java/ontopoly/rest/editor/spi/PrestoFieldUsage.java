package ontopoly.rest.editor.spi;

import java.util.Collection;

public interface PrestoFieldUsage extends PrestoField {

    public PrestoType getType();
    
    public PrestoView getView();
    
    public PrestoView getValueView();

    public Collection<PrestoType> getAvailableFieldCreateTypes();

    public Collection<PrestoType> getAvailableFieldValueTypes(); // ISSUE: can be make internal

}
