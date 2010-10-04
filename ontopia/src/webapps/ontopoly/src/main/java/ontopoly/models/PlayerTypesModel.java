package ontopoly.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ontopoly.model.RoleFieldIF;
import ontopoly.model.TopicTypeIF;
import ontopoly.utils.TopicComparator;

import org.apache.wicket.model.LoadableDetachableModel;

public class PlayerTypesModel extends LoadableDetachableModel<List<TopicTypeIF>> {

  private RoleFieldModel roleFieldModel;
  private FieldInstanceModel fieldInstanceModel;
  
  public PlayerTypesModel(FieldInstanceModel fieldInstanceModel,
                          RoleFieldModel roleFieldModel) {
    this.fieldInstanceModel = fieldInstanceModel;
    this.roleFieldModel = roleFieldModel; 
  }
  
  @Override
  protected List<TopicTypeIF> load() {
    List<TopicTypeIF> result = new ArrayList<TopicTypeIF>(); 
    RoleFieldIF roleField = roleFieldModel.getRoleField();
    Collection topicTypes = roleField.getAllowedPlayerTypes(fieldInstanceModel.getFieldInstance().getInstance());
    Iterator iter = topicTypes.iterator();
    while (iter.hasNext()) {
      TopicTypeIF topicType = (TopicTypeIF)iter.next();        
      result.add(topicType);
    }
    Collections.sort(result, TopicComparator.INSTANCE);
    return result;
  }
}
