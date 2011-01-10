package ontopoly.rest.editor.spi;


public interface PrestoView {

  public String getId();
  
  public String getName();

  public PrestoSchemaProvider getSchemaProvider();
  
}
