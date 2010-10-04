package ontopoly.models;

import net.ontopia.topicmaps.core.TopicIF;
import ontopoly.OntopolyContext;
import ontopoly.model.OntopolyTopicMapIF;
import ontopoly.model.TopicTypeIF;

import org.apache.wicket.model.LoadableDetachableModel;

public class TopicTypeModel extends LoadableDetachableModel<TopicTypeIF> {
  private static final long serialVersionUID = -8374148020034895666L;
  private String topicMapId;
  private String topicId;

  public TopicTypeModel(TopicTypeIF topicType) {
    super(topicType);
    if (topicType != null) {
      this.topicMapId = topicType.getTopicMap().getId();
      this.topicId = topicType.getId();
    }
  }
  
  public TopicTypeModel(String topicMapId, String topicId) {
    if (topicMapId == null)
      throw new NullPointerException("topicMapId parameter cannot be null.");
    if (topicId == null)
      throw new NullPointerException("topicId parameter cannot be null.");
    this.topicMapId = topicMapId;
    this.topicId = topicId;
  }

  public TopicTypeIF getTopicType() {
    return (TopicTypeIF) getObject();
  }

  @Override
  protected TopicTypeIF load() {
    if (topicMapId == null)
      return null;
    OntopolyTopicMapIF tm = OntopolyContext.getTopicMap(topicMapId);
    return tm.findTopicType(topicId);
  }
}
