package ontopoly.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ontopoly.model.FieldInstanceIF;
import ontopoly.model.RoleFieldIF;
import ontopoly.model.OntopolyTopicIF;
import ontopoly.utils.TopicComparator;

import org.apache.wicket.model.LoadableDetachableModel;

public abstract class PossiblePlayersModel extends LoadableDetachableModel<List<OntopolyTopicIF>> {
  private FieldInstanceModel fieldInstanceModel;
  private RoleFieldModel roleFieldModel;
  
  public PossiblePlayersModel(FieldInstanceModel fieldInstanceModel,
                              RoleFieldModel roleFieldModel) {
    this.fieldInstanceModel = fieldInstanceModel;
    // NOTE: this is the association field that is to be assigned a player
    this.roleFieldModel = roleFieldModel; 
  }
  
  @Override
  protected List<OntopolyTopicIF> load() {
    FieldInstanceIF fieldInstance = fieldInstanceModel.getFieldInstance();
    RoleFieldIF roleField = roleFieldModel.getRoleField();    
    List<OntopolyTopicIF> result = new ArrayList<OntopolyTopicIF>(roleField.getAllowedPlayers(fieldInstance.getInstance()));
    filterPlayers(result);
    Collections.sort(result, TopicComparator.INSTANCE);
    return result;
  }

  protected abstract void filterPlayers(Collection<OntopolyTopicIF> players);

  @Override
  protected void onDetach() {
    fieldInstanceModel.detach();
    roleFieldModel.detach();
    super.onDetach();
  }   
  
}
