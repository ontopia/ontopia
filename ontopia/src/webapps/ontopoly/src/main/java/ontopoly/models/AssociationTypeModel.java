package ontopoly.models;


import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.AssociationType;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicMap;
import ontopoly.utils.OntopolyContext;

import org.apache.wicket.model.LoadableDetachableModel;

public class AssociationTypeModel extends LoadableDetachableModel {

  private String topicMapId;

  private String topicId;
  
  public AssociationTypeModel(AssociationType associationType) {
    super(associationType);
    if (associationType == null)
      throw new NullPointerException("associationType parameter cannot be null.");
       
    TopicMap topicMap = associationType.getTopicMap();
    this.topicMapId = topicMap.getId();    
    this.topicId = associationType.getId(); 
  }
  
  public AssociationTypeModel(String topicMapId, String topicId) {
    if (topicMapId == null)
      throw new NullPointerException("topicMapId parameter cannot be null.");
    if (topicId == null)
      throw new NullPointerException("topicId parameter cannot be null.");
    this.topicMapId = topicMapId;
    this.topicId = topicId;
  }

  public AssociationType getAssociationType() {
    return (AssociationType)getObject();
  }
  
  @Override
  protected Object load() {
    TopicMap tm = OntopolyContext.getTopicMap(topicMapId);
    TopicIF topicIf = tm.getTopicIFById(topicId);
    return new AssociationType(topicIf, tm);
  }
}
