package ontopoly.model;

public interface LifeCycleListener {
  
  public void onAfterCreate(Topic topic, TopicType topicType);

  public void onBeforeDelete(Topic topic);
  
  public void onAfterAdd(FieldInstance fieldInstance, Object value);
  
  public void onBeforeRemove(FieldInstance fieldInstance, Object value);    
  
}
