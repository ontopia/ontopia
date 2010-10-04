package ontopoly.models;

import net.ontopia.topicmaps.core.TopicIF;
import ontopoly.OntopolyContext;
import ontopoly.model.NameTypeIF;
import ontopoly.model.OntopolyTopicMapIF;

import org.apache.wicket.model.LoadableDetachableModel;

public class NameTypeModel extends LoadableDetachableModel {
  private static final long serialVersionUID = 732564717599079747L;
  private String topicMapId;
  private String topicId;

  public NameTypeModel(String topicMapId, String topicId) {
    if (topicMapId == null)
      throw new NullPointerException("topicMapId parameter cannot be null.");
    if (topicId == null)
      throw new NullPointerException("topicId parameter cannot be null.");
    this.topicMapId = topicMapId;
    this.topicId = topicId;
  }

  public NameTypeIF getNameType() {
    return (NameTypeIF)getObject();    
  }
  
  @Override
  protected Object load() {
    OntopolyTopicMapIF tm = OntopolyContext.getTopicMap(topicMapId);
    return tm.findNameType(topicId);
  }
}
