package ontopoly.rest.editor.spi;

public interface PrestoProvider {

  public PrestoSession createSession(String databaseId);
  
}
