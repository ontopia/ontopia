package ontopoly.models;

import ontopoly.OntopolyContext;
import ontopoly.sysmodel.TopicMapReference;

import org.apache.wicket.model.LoadableDetachableModel;

public class TopicMapReferenceModel extends LoadableDetachableModel<TopicMapReference> {

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
  protected TopicMapReference load() {
 // retrieve topicMapReference from ontopoly repository
    return OntopolyContext.getOntopolyRepository().getReference(topicMapReferenceId);
  }

}
