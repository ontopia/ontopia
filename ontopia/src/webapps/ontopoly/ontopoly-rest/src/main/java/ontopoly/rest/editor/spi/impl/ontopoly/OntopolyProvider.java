package ontopoly.rest.editor.spi.impl.ontopoly;

import ontopoly.rest.editor.spi.PrestoSession;
import ontopoly.rest.editor.spi.PrestoProvider;

public class OntopolyProvider implements PrestoProvider {
  
  public PrestoSession createSession(String databaseId) {
    return new OntopolySession(databaseId);
  }

  
}
