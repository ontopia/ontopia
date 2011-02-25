package ontopoly.rest.editor.spi;



public interface PrestoSession {

  String getDatabaseId();
  
  String getDatabaseName();
  
  void abort();

  void commit();

  void close();

  PrestoDataProvider getDataProvider();

  PrestoSchemaProvider getSchemaProvider();
  
}
