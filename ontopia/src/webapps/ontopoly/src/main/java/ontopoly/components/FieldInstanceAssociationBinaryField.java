package ontopoly.components;

import java.util.Collection;
import java.util.List;

import ontopoly.model.FieldInstanceIF;
import ontopoly.model.InterfaceControlIF;
import ontopoly.model.RoleFieldIF;
import ontopoly.model.OntopolyTopicIF;
import ontopoly.model.TopicTypeIF;
import ontopoly.models.FieldInstanceModel;
import ontopoly.models.FieldValueModel;
import ontopoly.models.FieldsViewModel;
import ontopoly.models.PossiblePlayersModel;
import ontopoly.models.RoleFieldModel;
import ontopoly.models.TopicModel;
import ontopoly.pages.AbstractOntopolyPage;
import ontopoly.utils.OntopolyUtils;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

public abstract class FieldInstanceAssociationBinaryField extends Panel {

  private FormComponent formComponent;
  
  public FieldInstanceAssociationBinaryField(String id, 
      final RoleFieldModel valueFieldModel,
      final FieldValueModel fieldValueModel, FieldsViewModel fieldsViewModel, 
      final boolean readonly, boolean embedded, boolean traversable, boolean allowAdd) {
    super(id);
    FieldInstanceModel fieldInstanceModel = fieldValueModel.getFieldInstanceModel();
    RoleFieldIF valueField = valueFieldModel.getRoleField();
    
    if (fieldValueModel.isExistingValue()) {
      RoleFieldIF.ValueIF value = (RoleFieldIF.ValueIF)fieldValueModel.getObject();            
      OntopolyTopicIF oPlayer = value.getPlayer(valueField, fieldInstanceModel.getFieldInstance().getInstance());

      if (embedded) {
        TopicTypeIF defaultTopicType = OntopolyUtils.getDefaultTopicType(oPlayer);
        List<FieldInstanceIF> fieldInstances = oPlayer.getFieldInstances(defaultTopicType, fieldsViewModel.getFieldsView());
        // if no matching fields show link to topic instead
        if (fieldInstances.isEmpty()) {
          // player link
          TopicLink playerLink = new TopicLink("player", new TopicModel<OntopolyTopicIF>(oPlayer), fieldsViewModel);
          playerLink.setEnabled(traversable);
          add(playerLink);          
        } else {
          // embedded topic
          List<FieldInstanceModel> fieldInstanceModels = FieldInstanceModel.wrapInFieldInstanceModels(fieldInstances);
          FieldInstancesPanel fip = new FieldInstancesPanel("player", fieldInstanceModels, fieldsViewModel, readonly, traversable);
          fip.setRenderBodyOnly(true);
          add(fip);
        }
      } else {
        // player link
        TopicLink playerLink = new TopicLink("player", new TopicModel<OntopolyTopicIF>(oPlayer), fieldsViewModel);
        playerLink.setEnabled(traversable);
        add(playerLink);
      }
      
    } else {
      InterfaceControlIF interfaceControl = valueField.getInterfaceControl();
      
      if (readonly || interfaceControl.isSearchDialog() || interfaceControl.isBrowseDialog() || !allowAdd) {
        add(new Label("player").setVisible(false));
      
      } else if (interfaceControl.isDropDownList()) {
        // default is drop-down list
        TopicModel<OntopolyTopicIF> selectedModel = new TopicModel<OntopolyTopicIF>(null);
        PossiblePlayersModel choicesModel = new PossiblePlayersModel(fieldInstanceModel, valueFieldModel) {
          @Override
          protected void filterPlayers(Collection<OntopolyTopicIF> players) {
            AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
            page.filterTopics(players);
          }            
        };
        
        TopicDropDownChoice<OntopolyTopicIF> choice = new TopicDropDownChoice<OntopolyTopicIF>("player", selectedModel, choicesModel) {        
          @Override
          protected void onModelChanged() {
            super.onModelChanged();
            FieldInstanceAssociationBinaryField.this.onNewSelection(fieldValueModel, (OntopolyTopicIF)getModelObject());
          }
        };        
        add(choice);
        this.formComponent = choice;

      } else if (interfaceControl.isAutoComplete()) {
        AssociationFieldAutoCompleteTextField autoCompleteField 
          = new AssociationFieldAutoCompleteTextField("player", new Model<String>(null), valueFieldModel) {
          @Override
          protected void filterPlayers(List<OntopolyTopicIF> players) {
            AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
            page.filterTopics(players);
          }            
          @Override
          protected void onTopicSelected(OntopolyTopicIF topic) {
            FieldInstanceAssociationBinaryField.this.onNewSelection(fieldValueModel, topic);              
          }          
        };
        autoCompleteField.setOutputMarkupId(true);
        add(autoCompleteField);
        this.formComponent = autoCompleteField.getTextField();
        
      } else {
        throw new RuntimeException("Unsupported interface control: " + interfaceControl);
      }
    }
  }
  
  protected void onNewSelection(FieldValueModel fieldValueModel,
                                OntopolyTopicIF selectedTopic) {
    FieldInstanceModel fieldInstanceModel = fieldValueModel.getFieldInstanceModel();
    FieldInstanceIF fieldInstance = fieldInstanceModel.getFieldInstance();
    OntopolyTopicIF currentTopic = fieldInstance.getInstance();
              
    RoleFieldIF currentField = (RoleFieldIF)fieldInstance.getFieldAssignment().getFieldDefinition();          
    RoleFieldIF selectedField = getOtherBinaryRoleField(currentField);

    // check with page to see if add is allowed
    AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
    if (page.isAddAllowed(currentTopic, currentField, selectedTopic, selectedField)) {
      performNewSelection(fieldValueModel, selectedField, selectedTopic);
    }
  }

  protected abstract void performNewSelection(FieldValueModel fieldValueModel,
                                              RoleFieldIF selectedField,
                                              OntopolyTopicIF selectedTopic);
  
  protected RoleFieldIF getOtherBinaryRoleField(RoleFieldIF thisField) {
    Collection otherRoleFields = thisField.getFieldsForOtherRoles();
    if (otherRoleFields.size() != 1)
      throw new RuntimeException("Binary association does not have two fields.");
    RoleFieldIF otherField = (RoleFieldIF)otherRoleFields.iterator().next();
    return otherField;
  }
  
  public FormComponent getUpdateableComponent() {
    return formComponent;
  }

}
