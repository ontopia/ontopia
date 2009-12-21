package ontopoly.models;


import net.ontopia.topicmaps.core.TopicIF;
import ontopoly.OntopolyContext;
import ontopoly.model.RoleType;
import ontopoly.model.TopicMap;

import org.apache.wicket.model.LoadableDetachableModel;

public class RoleTypeModel extends LoadableDetachableModel<RoleType> {

  private static final long serialVersionUID = -4066710557389722040L;

  private String topicMapId;

  private String topicId;
  
  public RoleTypeModel(RoleType roleType) {
    super(roleType);
    if (roleType == null)
      throw new NullPointerException("roleType parameter cannot be null.");
       
    TopicMap topicMap = roleType.getTopicMap();
    this.topicMapId = topicMap.getId();    
    this.topicId = roleType.getId(); 
  }

  public RoleTypeModel(String topicMapId, String topicId) {
    if (topicMapId == null)
      throw new NullPointerException("topicMapId parameter cannot be null.");
    if (topicId == null)
      throw new NullPointerException("topicId parameter cannot be null.");
    this.topicMapId = topicMapId;
    this.topicId = topicId;
  }

  public RoleType getRoleType() {
    return (RoleType)getObject();
  }
  
  @Override
  protected RoleType load() {
    TopicMap tm = OntopolyContext.getTopicMap(topicMapId);
    TopicIF topicIf = tm.getTopicIFById(topicId);
    return new RoleType(topicIf, tm);
  }
  
}
