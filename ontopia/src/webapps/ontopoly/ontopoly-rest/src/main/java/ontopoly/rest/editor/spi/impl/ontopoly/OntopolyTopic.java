package ontopoly.rest.editor.spi.impl.ontopoly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ontopoly.model.Topic;
import ontopoly.model.TopicType;
import ontopoly.rest.editor.spi.PrestoField;
import ontopoly.rest.editor.spi.PrestoTopic;
import ontopoly.rest.editor.spi.PrestoType;
import ontopoly.utils.OntopolyUtils;

public class OntopolyTopic implements PrestoTopic {

  private final OntopolySession session;
  private final Topic topic;

  OntopolyTopic(OntopolySession session, Topic topic) {
    this.session = session;
    this.topic = topic;    
  }
  
  public boolean equals(Object o) {
    if (o instanceof OntopolyTopic) {
      OntopolyTopic other = (OntopolyTopic)o;
      return other.topic.equals(this.topic);
    }
    return false;
  }
  
  public int hashCode() {
    return this.topic.hashCode();
  }
  
  static Topic getWrapped(PrestoTopic topic) {
    return ((OntopolyTopic)topic).topic;
  }

  public String getId() {
    return topic.getId();
  }

  public String getDatabaseId() {
    return topic.getTopicMap().getId();
  }

  public String getName() {
    return topic.getName();
  }
  
  public PrestoType getType() {
    TopicType defaultTopicType = OntopolyUtils.getDefaultTopicType(topic);
    return session.getSchemaProvider().getTypeById(defaultTopicType.getId());
  }

  static Collection<PrestoTopic> wrap(OntopolySession session, Collection<Topic> topics) {
    List<PrestoTopic> result = new ArrayList<PrestoTopic>(topics.size());
    for (Topic topic : topics) {
      result.add(new OntopolyTopic(session, topic));
    }
    return result;
  }
  
  public Collection<Object> getValues(PrestoField field) {
    return session.getDataProvider().getValues(this, field);
  }

}
