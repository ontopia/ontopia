package ontopoly.rest.editor.spi;

import java.util.Collection;

public interface PrestoTopic {

  public String getId();

  public PrestoDataProvider getDataProvider();

  public String getName();

  public String getTypeId();
  
  public Collection<Object> getValues(PrestoField field);

}
