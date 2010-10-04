
package ontopoly.model;

public interface LifeCycleListenerIF {
  
  public void onAfterCreate(OntopolyTopicIF topic, TopicTypeIF topicType);

  public void onBeforeDelete(OntopolyTopicIF topic);
  
  public void onAfterAdd(FieldInstanceIF fieldInstance, Object value);
  
  public void onBeforeRemove(FieldInstanceIF fieldInstance, Object value);    
  
}
