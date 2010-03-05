package ontopoly.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


import ontopoly.model.FieldInstance;
import ontopoly.model.RoleField;
import ontopoly.model.Topic;
import ontopoly.utils.TopicComparator;

import org.apache.wicket.model.LoadableDetachableModel;

public abstract class PossiblePlayersModel extends LoadableDetachableModel<List<Topic>> {

  private FieldInstanceModel fieldInstanceModel;
  private RoleFieldModel roleFieldModel;
  
  public PossiblePlayersModel(FieldInstanceModel fieldInstanceModel, RoleFieldModel roleFieldModel) {
    this.fieldInstanceModel = fieldInstanceModel;
    // NOTE: this is the association field that is to be assigned a player
    this.roleFieldModel = roleFieldModel; 
  }
  
  @Override
  protected List<Topic> load() {
    FieldInstance fieldInstance = fieldInstanceModel.getFieldInstance();
    RoleField roleField = roleFieldModel.getRoleField();    
    List<Topic> result = new ArrayList<Topic>(roleField.getAllowedPlayers(fieldInstance.getInstance()));
    filterPlayers(result);
    Collections.sort(result, TopicComparator.INSTANCE);
    return result;
  }

  protected abstract void filterPlayers(Collection<Topic> players);

  @Override
  protected void onDetach() {
      fieldInstanceModel.detach();
      roleFieldModel.detach();
      super.onDetach();
  }   
  
}
