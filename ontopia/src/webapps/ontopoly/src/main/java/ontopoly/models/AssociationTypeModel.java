package ontopoly.models;


import net.ontopia.topicmaps.core.TopicIF;
import ontopoly.OntopolyContext;
import ontopoly.model.AssociationTypeIF;
import ontopoly.model.OntopolyTopicMapIF;

import org.apache.wicket.model.LoadableDetachableModel;

public class AssociationTypeModel extends LoadableDetachableModel<AssociationTypeIF> {
  private String topicMapId;
  private String topicId;
  
  public AssociationTypeModel(AssociationTypeIF associationType) {
    super(associationType);
    if (associationType == null)
      throw new NullPointerException("associationType parameter cannot be null.");
       
    OntopolyTopicMapIF topicMap = associationType.getTopicMap();
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

  public AssociationTypeIF getAssociationType() {
    return (AssociationTypeIF)getObject();
  }
  
  @Override
  protected AssociationTypeIF load() {
    OntopolyTopicMapIF tm = OntopolyContext.getTopicMap(topicMapId);
    return tm.findAssociationType(topicId);
  }
}
