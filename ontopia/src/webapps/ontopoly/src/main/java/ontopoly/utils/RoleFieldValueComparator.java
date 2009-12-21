package ontopoly.utils;

import java.io.Serializable;
import java.util.Comparator;

import ontopoly.model.RoleField;
import ontopoly.model.Topic;
import ontopoly.models.RoleFieldModel;
import ontopoly.models.TopicModel;

public class RoleFieldValueComparator implements Comparator<Object>, Serializable {

  private TopicModel topicModel;
  private RoleFieldModel roleFieldModel;
  
  public RoleFieldValueComparator(TopicModel topicModel, RoleFieldModel roleFieldModel) {
    this.topicModel = topicModel;
    this.roleFieldModel = roleFieldModel;
  }
  
  public int compare(Object o1, Object o2) {
    RoleField.ValueIF rfv1 = (RoleField.ValueIF)o1; 
    RoleField.ValueIF rfv2 = (RoleField.ValueIF)o2;
    
    RoleField roleField = roleFieldModel.getRoleField();
    Topic topic = topicModel.getTopic();
    
    Topic t1 = rfv1.getPlayer(roleField, topic);
    Topic t2 = rfv2.getPlayer(roleField, topic);
    return TopicComparator.INSTANCE.compare(t1, t2);
    
//    int retval = ObjectUtils.compare(rfv1.getArity(), rfv2.getArity());
//    if (retval != 0) return retval;
//    
//    // make copy of o1 data, so that we can sort it
//    RoleField[] rfields1 = new RoleField[rfv1.getArity()];
//    System.arraycopy(rfv1.getRoleFields(), 0, rfields1, 0, rfields1.length);;
//    Topic[] players1 = new Topic[rfields1.length];
//    System.arraycopy(rfv1.getPlayers(), 0, players1, 0, players1.length);;    
//    ObjectUtils.sortParallel(rfields1, players1, RoleFieldComparator.INSTANCE);  
//
//    // make copy of o2 data, so that we can sort it
//    RoleField[] rfields2 = new RoleField[rfv2.getArity()];
//    System.arraycopy(rfv2.getRoleFields(), 0, rfields2, 0, rfields2.length);;
//    Topic[] players2 = new Topic[rfields2.length];
//    System.arraycopy(rfv2.getPlayers(), 0, players2, 0, players2.length);;
//    ObjectUtils.sortParallel(rfields2, players2, RoleFieldComparator.INSTANCE);  
//
//    // compare players
//    retval = ObjectUtils.compareArrays(players1, players2, TopicComparator.INSTANCE);
//    if (retval != 0) return retval;
// 
//    // compare fields
//    return ObjectUtils.compareArrays(rfields1, rfields2, RoleFieldComparator.INSTANCE);
  }

}
