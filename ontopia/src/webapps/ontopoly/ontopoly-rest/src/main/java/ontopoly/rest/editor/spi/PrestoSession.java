package ontopoly.rest.editor.spi;



public interface PrestoSession {

  public String getDatabaseId();
  
  public String getDatabaseName();
  
  public void abort();

  public void commit();

  public void close();

  public PrestoDataProvider getDataProvider();

  public PrestoSchemaProvider getSchemaProvider();
  
}
