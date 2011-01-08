package ontopoly.rest.editor.spi;

import java.util.Collection;

public interface PrestoTopic {

  public String getId();

  public String getDatabaseId();

  public String getName();

  public PrestoType getType();
  
  public Collection<Object> getValues(PrestoField field);

}
