package ontopoly.rest.editor.spi.impl.ontopoly;

import ontopoly.model.Topic;
import ontopoly.rest.editor.spi.PrestoChangeSet;
import ontopoly.rest.editor.spi.PrestoDataProvider;
import ontopoly.rest.editor.spi.PrestoTopic;
import ontopoly.rest.editor.spi.PrestoType;

public class OntopolyDataProvider implements PrestoDataProvider {

  OntopolySession session;
  
  public OntopolyDataProvider(OntopolySession session) {
    this.session = session;
  }

  public PrestoTopic getTopicById(String id) {
    Topic topic = session.getTopicMap().getTopicById(id);
    if (topic == null) {
      throw new RuntimeException("Unknown topic: " + id);
    }
    return new OntopolyTopic(session, topic);
  }

  public PrestoChangeSet createTopic(PrestoType type) {
    return new OntopolyChangeSet(session, type);
  }
  
  public PrestoChangeSet updateTopic(PrestoTopic topic) {
    return new OntopolyChangeSet(session, topic);
  }

  public boolean removeTopic(PrestoTopic topic) {
    OntopolyTopic.getWrapped(topic).remove(null);
    return true;
  }

  public void close() {
    // no-op
  }

}
