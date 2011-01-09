package ontopoly.model;

public interface LifeCycleListener {
  
  public void onAfterCreate(Topic topic, TopicType topicType);

  public void onBeforeDelete(Topic topic);
  
  public void onAfterAdd(Topic topic, FieldDefinition fieldDefinition, Object value);
  
  public void onBeforeRemove(Topic topic, FieldDefinition fieldDefinition, Object value);    
  
}
