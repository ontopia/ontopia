package ontopoly.rest.editor.spi;

import java.util.Collection;

public interface PrestoTopic {

  String getId();

  PrestoDataProvider getDataProvider();

  String getName();

  String getTypeId();
  
  Collection<Object> getValues(PrestoField field);

}
