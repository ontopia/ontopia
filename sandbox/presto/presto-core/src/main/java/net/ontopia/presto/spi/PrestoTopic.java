package net.ontopia.presto.spi;

import java.util.Collection;

public interface PrestoTopic {

  String getId();

  String getName();

  String getTypeId();
  
  Collection<Object> getValues(PrestoField field);

}
