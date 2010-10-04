package ontopoly.models;

import net.ontopia.topicmaps.core.TopicIF;
import ontopoly.OntopolyContext;
import ontopoly.model.OccurrenceTypeIF;
import ontopoly.model.OntopolyTopicMapIF;

import org.apache.wicket.model.LoadableDetachableModel;

public class OccurrenceTypeModel extends LoadableDetachableModel {
  private static final long serialVersionUID = 8685431361043298701L;
  private String topicMapId;
  private String topicId;

  public OccurrenceTypeModel(String topicMapId, String topicId) {
    if (topicMapId == null)
      throw new NullPointerException("topicMapId parameter cannot be null.");
    if (topicId == null)
      throw new NullPointerException("topicId parameter cannot be null.");
    this.topicMapId = topicMapId;
    this.topicId = topicId;
  }

  public OccurrenceTypeIF getOccurrenceType() {
    return (OccurrenceTypeIF)getObject();
  }
  
  @Override
  protected Object load() {
    OntopolyTopicMapIF tm = OntopolyContext.getTopicMap(topicMapId);
    return tm.findOccurrenceType(topicId);
  }
}
