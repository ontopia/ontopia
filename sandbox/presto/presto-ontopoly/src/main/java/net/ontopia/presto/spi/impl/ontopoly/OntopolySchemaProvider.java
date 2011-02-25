package net.ontopia.presto.spi.impl.ontopoly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ontopoly.model.Topic;
import ontopoly.model.TopicMap;
import ontopoly.model.TopicType;
import net.ontopia.presto.spi.PrestoSchemaProvider;
import net.ontopia.presto.spi.PrestoType;

public class OntopolySchemaProvider implements PrestoSchemaProvider {

  OntopolySession session;
  
  public OntopolySchemaProvider(OntopolySession session) {
    this.session = session;
  }

  protected TopicMap getTopicMap() {
    return session.getTopicMap();
  }

  public String getDatabaseId() {
    return session.getDatabaseId();
  }
  
  public Collection<PrestoType> getRootTypes() {
    List<TopicType> rootTopicTypes = getTopicMap().getRootTopicTypes();
    List<PrestoType> result = new ArrayList<PrestoType>(rootTopicTypes.size());
    for (TopicType topicType : rootTopicTypes) {
      if (!topicType.isSystemTopic()) {
        result.add(new OntopolyType(session, topicType));
      }
    }
    return result;
  }

  public PrestoType getTypeById(String typeId) {
    TopicMap topicMap = getTopicMap();
    Topic topicType_ = topicMap.getTopicById(typeId);
    if (topicType_ == null && session.getStableIdPrefix() != null) {
      topicType_ = topicMap.getTopicById(session.getStableIdPrefix() + typeId);
    }
    if (topicType_ == null) {
      throw new RuntimeException("Unknown type: " + typeId);
    }
    TopicType topicType = new TopicType(topicType_.getTopicIF(), topicMap);
    return new OntopolyType(session, topicType);
  }

}
