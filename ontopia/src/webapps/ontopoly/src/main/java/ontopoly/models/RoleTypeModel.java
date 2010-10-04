package ontopoly.models;

import net.ontopia.topicmaps.core.TopicIF;
import ontopoly.OntopolyContext;
import ontopoly.model.RoleTypeIF;
import ontopoly.model.OntopolyTopicMapIF;

import org.apache.wicket.model.LoadableDetachableModel;

public class RoleTypeModel extends LoadableDetachableModel<RoleTypeIF> {
  private static final long serialVersionUID = -4066710557389722040L;
  private String topicMapId;
  private String topicId;
  
  public RoleTypeModel(RoleTypeIF roleType) {
    super(roleType);
    if (roleType == null)
      throw new NullPointerException("roleType parameter cannot be null.");
       
    OntopolyTopicMapIF topicMap = roleType.getTopicMap();
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

  public RoleTypeIF getRoleType() {
    return (RoleTypeIF)getObject();
  }
  
  @Override
  protected RoleTypeIF load() {
    OntopolyTopicMapIF tm = OntopolyContext.getTopicMap(topicMapId);
    return tm.findRoleType(topicId);
  }
  
}
