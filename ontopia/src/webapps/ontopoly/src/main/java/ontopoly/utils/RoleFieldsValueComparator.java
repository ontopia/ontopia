package ontopoly.utils;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import ontopoly.model.RoleFieldIF;
import ontopoly.model.OntopolyTopicIF;
import ontopoly.models.RoleFieldModel;
import ontopoly.models.TopicModel;

public class RoleFieldsValueComparator implements Comparator<Object>, Serializable {

  private TopicModel topicModel;
  private List roleFieldModels;
  
  public RoleFieldsValueComparator(TopicModel topicModel, List roleFieldModels) {
    this.topicModel = topicModel;
    this.roleFieldModels = roleFieldModels;
  }
  
  public int compare(Object o1, Object o2) {
    RoleFieldIF.ValueIF rfv1 = (RoleFieldIF.ValueIF)o1;
    RoleFieldIF.ValueIF rfv2 = (RoleFieldIF.ValueIF)o2;
    
    for (int i=0; i < roleFieldModels.size(); i++) {
      RoleFieldModel roleFieldModel = (RoleFieldModel)roleFieldModels.get(i);
      RoleFieldIF roleField = roleFieldModel.getRoleField();
      OntopolyTopicIF topic = topicModel.getTopic();
    
      OntopolyTopicIF t1 = rfv1.getPlayer(roleField, topic);
      OntopolyTopicIF t2 = rfv2.getPlayer(roleField, topic);
      int retval = TopicComparator.INSTANCE.compare(t1, t2);
      if (retval != 0) return retval;
    }
    return 0;
  }

}
