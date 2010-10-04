package ontopoly.utils;

import java.io.Serializable;
import java.util.Comparator;

import ontopoly.model.RoleFieldIF;
import ontopoly.model.OntopolyTopicIF;
import ontopoly.models.RoleFieldModel;
import ontopoly.models.TopicModel;

public class RoleFieldValueComparator implements Comparator<Object>, Serializable {
  private TopicModel topicModel;
  private RoleFieldModel roleFieldModel;
  
  public RoleFieldValueComparator(TopicModel topicModel,
                                  RoleFieldModel roleFieldModel) {
    this.topicModel = topicModel;
    this.roleFieldModel = roleFieldModel;
  }
  
  public int compare(Object o1, Object o2) {
    RoleFieldIF.ValueIF rfv1 = (RoleFieldIF.ValueIF)o1; 
    RoleFieldIF.ValueIF rfv2 = (RoleFieldIF.ValueIF)o2;
    
    RoleFieldIF roleField = roleFieldModel.getRoleField();
    OntopolyTopicIF topic = topicModel.getTopic();
    
    OntopolyTopicIF t1 = rfv1.getPlayer(roleField, topic);
    OntopolyTopicIF t2 = rfv2.getPlayer(roleField, topic);
    return TopicComparator.INSTANCE.compare(t1, t2);
  }

}
