package ontopoly.models;


import net.ontopia.topicmaps.core.TopicIF;
import ontopoly.OntopolyContext;
import ontopoly.model.OccurrenceType;
import ontopoly.model.TopicMap;

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

  public OccurrenceType getOccurrenceType() {
    return (OccurrenceType)getObject();
  }
  
  @Override
  protected Object load() {
    TopicMap tm = OntopolyContext.getTopicMap(topicMapId);
    TopicIF topicIf = tm.getTopicIFById(topicId);
    return new OccurrenceType(topicIf, tm);
  }
}
