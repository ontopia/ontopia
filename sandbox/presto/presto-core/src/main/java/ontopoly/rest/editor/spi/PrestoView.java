package ontopoly.rest.editor.spi;


public interface PrestoView {

  String getId();
  
  String getName();

  PrestoSchemaProvider getSchemaProvider();
  
}
