package ontopoly.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.RoleField;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicType;
import ontopoly.utils.TopicComparator;

import org.apache.wicket.model.LoadableDetachableModel;

public class PlayerTypesModel extends LoadableDetachableModel<List<TopicType>> {

  private RoleFieldModel roleFieldModel;
  private FieldInstanceModel fieldInstanceModel;
  
  public PlayerTypesModel(FieldInstanceModel fieldInstanceModel, RoleFieldModel roleFieldModel) {
    this.fieldInstanceModel = fieldInstanceModel;
    this.roleFieldModel = roleFieldModel; 
  }
  
  @Override
  protected List<TopicType> load() {
    List<TopicType> result = new ArrayList<TopicType>(); 
    RoleField roleField = roleFieldModel.getRoleField();
    Collection topicTypes = roleField.getAllowedPlayerTypes(fieldInstanceModel.getFieldInstance().getInstance());
    Iterator iter = topicTypes.iterator();
    while (iter.hasNext()) {
      TopicType topicType = (TopicType)iter.next();        
      result.add(topicType);
    }
    Collections.sort(result, TopicComparator.INSTANCE);
    return result;
  }
}
