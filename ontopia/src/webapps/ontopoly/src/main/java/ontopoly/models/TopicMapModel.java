package ontopoly.models;

import ontopoly.OntopolyContext;
import ontopoly.model.OntopolyTopicMapIF;

import org.apache.wicket.model.LoadableDetachableModel;

public class TopicMapModel extends LoadableDetachableModel<OntopolyTopicMapIF> {

  private static final long serialVersionUID = -6589204980069242599L;

  private String topicMapId;

  public TopicMapModel(OntopolyTopicMapIF topicMap) {
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
  
  public OntopolyTopicMapIF getTopicMap() {
    return (OntopolyTopicMapIF) getObject();
  }

  @Override
  protected OntopolyTopicMapIF load() {
    // retrive topicMap from ontopoly model
    return OntopolyContext.getTopicMap(topicMapId);
  }
}
