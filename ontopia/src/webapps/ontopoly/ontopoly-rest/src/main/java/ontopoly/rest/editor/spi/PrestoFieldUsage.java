package ontopoly.rest.editor.spi;

import java.util.Collection;

public interface PrestoFieldUsage extends PrestoField {

  PrestoType getType();
    
  PrestoView getView();
    
  PrestoView getValueView();

  Collection<PrestoType> getAvailableFieldCreateTypes();

  Collection<PrestoType> getAvailableFieldValueTypes(); // ISSUE: can this be made internal?

}
