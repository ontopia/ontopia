package net.ontopia.presto.spi;

import java.util.Collection;

public interface PrestoSchemaProvider {

  String getDatabaseId();
  
  PrestoType getTypeById(String typeId);
  
  Collection<PrestoType> getRootTypes();

}
