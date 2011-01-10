package ontopoly.rest.editor.spi;

import java.util.Collection;

public interface PrestoSchemaProvider {

  public String getDatabaseId();
  
  public PrestoType getTypeById(String typeId);

  public PrestoView getViewById(String viewId);

  public PrestoField getFieldById(String fieldId, PrestoType type, PrestoView view);
  
  public PrestoView getDefaultView();
  
  public Collection<PrestoType> getRootTypes();

}
