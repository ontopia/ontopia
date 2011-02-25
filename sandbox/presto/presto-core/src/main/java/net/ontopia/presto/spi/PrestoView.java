package net.ontopia.presto.spi;

public interface PrestoView {

  String getId();
  
  String getName();

  PrestoSchemaProvider getSchemaProvider();
  
}
