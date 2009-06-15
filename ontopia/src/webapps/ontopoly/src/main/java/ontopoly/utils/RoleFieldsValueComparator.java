package ontopoly.utils;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.RoleField;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.Topic;
import ontopoly.models.RoleFieldModel;
import ontopoly.models.TopicModel;

public class RoleFieldsValueComparator implements Comparator, Serializable {

  private TopicModel topicModel;
  private List roleFieldModels;
  
  public RoleFieldsValueComparator(TopicModel topicModel, List roleFieldModels) {
    this.topicModel = topicModel;
    this.roleFieldModels = roleFieldModels;
  }
  
  public int compare(Object o1, Object o2) {
    RoleField.ValueIF rfv1 = (RoleField.ValueIF)o1;
    RoleField.ValueIF rfv2 = (RoleField.ValueIF)o2;

    for (int i=0; i < roleFieldModels.size(); i++) {
      RoleFieldModel roleFieldModel = (RoleFieldModel)roleFieldModels.get(i);
      RoleField roleField = roleFieldModel.getRoleField();
      Topic topic = topicModel.getTopic();
    
      Topic t1 = rfv1.getPlayer(roleField, topic);
      Topic t2 = rfv2.getPlayer(roleField, topic);
      int retval = TopicComparator.INSTANCE.compare(t1, t2);
      if (retval != 0) return retval;
    }
    return 0;
  }

}
