package net.ontopia.topicmaps.nav2.webapps.ontopoly.model;

public interface LifeCycleListener {
  
  public void onAfterCreate(Topic topic, TopicType topicType);

  public void onBeforeDelete(Topic topic);
  
  public void onAfterAdd(FieldInstance fieldInstance, Object value);

  public void onAfterReplace(FieldInstance fieldInstance, Object oldValue, Object newValue);
  
  public void onBeforeRemove(FieldInstance fieldInstance, Object value);    
  
}
