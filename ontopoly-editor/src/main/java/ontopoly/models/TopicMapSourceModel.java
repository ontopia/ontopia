package ontopoly.models;

import ontopoly.sysmodel.TopicMapSource;

public class TopicMapSourceModel extends MutableLoadableDetachableModel<TopicMapSource> {

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
  public void setObject(TopicMapSource source) {
    super.setObject(source);
    this.topicMapSourceId = source.getId(); 
  }

  @Override
  protected TopicMapSource load() {
    // retrieve topicMapSource from ontopoly repository
    return new TopicMapSource(topicMapSourceId);
  }
}
