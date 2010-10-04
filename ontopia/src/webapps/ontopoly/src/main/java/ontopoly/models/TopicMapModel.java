package ontopoly.models;


import ontopoly.OntopolyContext;
import ontopoly.model.TopicMap;

import org.apache.wicket.model.LoadableDetachableModel;

public class TopicMapModel extends LoadableDetachableModel<TopicMap> {

  private static final long serialVersionUID = -6589204980069242599L;

  private String topicMapId;

  public TopicMapModel(TopicMap topicMap) {
    super(topicMap);
    if (topicMap != null) {
      this.topicMapId = topicMap.getId();
    }
  }

  public TopicMapModel(String topicMapId) {
    if (topicMapId == null)
      throw new NullPointerException("topicMapId parameter cannot be null.");
    this.topicMapId = topicMapId;    
  }

  public String getTopicMapId() {
    return topicMapId;
  }
  
  public TopicMap getTopicMap() {
    return (TopicMap)getObject();
  }

  @Override
  protected TopicMap load() {
    // retrive topicMap from ontopoly model
    return OntopolyContext.getTopicMap(topicMapId);
  }
}
