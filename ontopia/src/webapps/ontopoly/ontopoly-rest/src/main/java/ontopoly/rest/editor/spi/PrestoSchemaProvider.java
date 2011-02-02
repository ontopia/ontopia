package ontopoly.rest.editor.spi;

import java.util.Collection;

public interface PrestoSchemaProvider {

  public String getDatabaseId();
  
  public PrestoType getTypeById(String typeId);
  
  public Collection<PrestoType> getRootTypes();

}
