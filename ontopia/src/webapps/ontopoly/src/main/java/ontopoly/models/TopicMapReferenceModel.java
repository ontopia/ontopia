package ontopoly.models;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.sysmodel.TopicMapReference;
import ontopoly.utils.OntopolyContext;

import org.apache.wicket.model.LoadableDetachableModel;

public class TopicMapReferenceModel extends LoadableDetachableModel {

  private String topicMapReferenceId;

  public TopicMapReferenceModel(TopicMapReference topicMapReference) {
    super(topicMapReference);
    if (topicMapReference == null)
      throw new NullPointerException("topicMapSource parameter cannot be null.");
       
    this.topicMapReferenceId = topicMapReference.getId(); 
  }
  
  public TopicMapReferenceModel(String topicMapSourceId) {
    if (topicMapSourceId == null)
      throw new NullPointerException("topicMapSourceId parameter cannot be null.");
    this.topicMapReferenceId = topicMapSourceId;    
  }

  public TopicMapReference getTopicMapReference() {
    return (TopicMapReference)getObject();
  }

  @Override
  protected Object load() {
 // retrieve topicMapReference from ontopoly repository
    return OntopolyContext.getOntopolyRepository().getReference(topicMapReferenceId);
  }

}
