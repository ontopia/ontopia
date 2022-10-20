/*
 * #!
 * Ontopoly Editor
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */
package ontopoly.components;

import java.util.Collection;
import java.util.List;

import ontopoly.model.FieldInstance;
import ontopoly.model.InterfaceControl;
import ontopoly.model.RoleField;
import ontopoly.model.Topic;
import ontopoly.model.TopicType;
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

public abstract class FieldInstanceAssociationBinaryField extends Panel {

  private FormComponent<Topic> formComponent;
  
  public FieldInstanceAssociationBinaryField(String id, 
      final RoleFieldModel valueFieldModel,
      final FieldValueModel fieldValueModel, FieldsViewModel fieldsViewModel, 
      final boolean readonly, boolean embedded, boolean traversable, boolean allowAdd) {
    super(id);
    FieldInstanceModel fieldInstanceModel = fieldValueModel.getFieldInstanceModel();
    RoleField valueField = valueFieldModel.getRoleField();
    
    if (fieldValueModel.isExistingValue()) {
      RoleField.ValueIF value = (RoleField.ValueIF)fieldValueModel.getObject();            
      Topic oPlayer = value.getPlayer(valueField, fieldInstanceModel.getFieldInstance().getInstance());

      if (embedded) {
        TopicType defaultTopicType = OntopolyUtils.getDefaultTopicType(oPlayer);
        List<FieldInstance> fieldInstances = oPlayer.getFieldInstances(defaultTopicType, fieldsViewModel.getFieldsView());
        // if no matching fields show link to topic instead
        if (fieldInstances.isEmpty()) {
          // player link
          TopicLink<Topic> playerLink = new TopicLink<Topic>("player", new TopicModel<Topic>(oPlayer), fieldsViewModel);
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
        TopicLink<Topic> playerLink = new TopicLink<Topic>("player", new TopicModel<Topic>(oPlayer), fieldsViewModel);
        playerLink.setEnabled(traversable);
        add(playerLink);
      }
      
    } else {

      InterfaceControl interfaceControl = valueField.getInterfaceControl();
      
      if (readonly || interfaceControl.isSearchDialog() || interfaceControl.isBrowseDialog() || !allowAdd) {
        add(new Label("player").setVisible(false));
      
      } else if (interfaceControl.isDropDownList()) {
        // default is drop-down list
        TopicModel<Topic> selectedModel = new TopicModel<Topic>(null);
        PossiblePlayersModel choicesModel = new PossiblePlayersModel(fieldInstanceModel, valueFieldModel) {
          @Override
          protected void filterPlayers(Collection<Topic> players) {
            AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
            page.filterTopics(players);
          }            
        };
        
        TopicDropDownChoice<Topic> choice = new TopicDropDownChoice<Topic>("player", selectedModel, choicesModel) {        
          @Override
          protected void onModelChanged() {
            super.onModelChanged();
            FieldInstanceAssociationBinaryField.this.onNewSelection(fieldValueModel, (Topic)getModelObject());
          }
        };        
        add(choice);
        this.formComponent = choice;

      } else if (interfaceControl.isAutoComplete()) {
        AssociationFieldAutoCompleteTextField autoCompleteField 
          = new AssociationFieldAutoCompleteTextField("player", new TopicModel<Topic>(null), valueFieldModel) {
          @Override
          protected void filterPlayers(List<Topic> players) {
            AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
            page.filterTopics(players);
          }            
          @Override
          protected void onTopicSelected(Topic topic) {
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
  
  protected void onNewSelection(FieldValueModel fieldValueModel, Topic selectedTopic) {
    FieldInstanceModel fieldInstanceModel = fieldValueModel.getFieldInstanceModel();
    FieldInstance fieldInstance = fieldInstanceModel.getFieldInstance();
    Topic currentTopic = fieldInstance.getInstance();
              
    RoleField currentField = (RoleField)fieldInstance.getFieldAssignment().getFieldDefinition();          
    RoleField selectedField = getOtherBinaryRoleField(currentField);

    // check with page to see if add is allowed
    AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
    if (page.isAddAllowed(currentTopic, currentField, selectedTopic, selectedField)) {
      performNewSelection(fieldValueModel, selectedField, selectedTopic);
    }
  }

  protected abstract void performNewSelection(FieldValueModel fieldValueModel, RoleField selectedField, Topic selectedTopic);
  
  protected RoleField getOtherBinaryRoleField(RoleField thisField) {
    Collection<RoleField> otherRoleFields = thisField.getFieldsForOtherRoles();
    if (otherRoleFields.size() != 1) {
      throw new RuntimeException("Binary association does not have two fields.");
    }
    RoleField otherField = otherRoleFields.iterator().next();
    return otherField;
  }
  
  public FormComponent<Topic> getUpdateableComponent() {
    return formComponent;
  }

}
