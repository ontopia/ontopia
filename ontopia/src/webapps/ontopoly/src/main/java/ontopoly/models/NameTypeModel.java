package ontopoly.models;


import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.NameType;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicMap;
import ontopoly.utils.OntopolyContext;

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

  public NameType getNameType() {
    return (NameType)getObject();    
  }
  
  @Override
  protected Object load() {
    TopicMap tm = OntopolyContext.getTopicMap(topicMapId);
    TopicIF topicIf = tm.getTopicIFById(topicId);
    return new NameType(topicIf, tm);
  }
}
