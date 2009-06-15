package ontopoly.models;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.sysmodel.TopicMapSource;
import ontopoly.utils.OntopolyContext;

public class TopicMapSourceModel extends MutableLoadableDetachableModel {

  private String topicMapSourceId;

  public TopicMapSourceModel(TopicMapSource topicMapSource) {
    super(topicMapSource);
    if (topicMapSource == null)
      throw new NullPointerException("topicMapSource parameter cannot be null.");
       
    this.topicMapSourceId = topicMapSource.getId(); 
  }
  
  public TopicMapSourceModel(String topicMapSourceId) {
    if (topicMapSourceId == null)
      throw new NullPointerException("topicMapSourceId parameter cannot be null.");
    this.topicMapSourceId = topicMapSourceId;    
  }

  public TopicMapSource getTopicMapSource() {
    return (TopicMapSource)getObject();
  }

  @Override
  public void setObject(Object object) {
    super.setObject(object);
    this.topicMapSourceId = ((TopicMapSource)object).getId(); 
  }

  @Override
  protected Object load() {
    // retrieve topicMapSource from ontopoly repository
    return OntopolyContext.getOntopolyRepository().getSource(topicMapSourceId);
  }
}
