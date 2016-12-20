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

import ontopoly.model.FieldInstance;
import ontopoly.model.RoleField;
import ontopoly.model.Topic;
import ontopoly.models.FieldInstanceModel;
import ontopoly.models.FieldValueModel;
import ontopoly.pages.AbstractOntopolyPage;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class FieldInstanceAssociationUnaryField extends Panel {

  private CheckBox checkbox;
  
  public FieldInstanceAssociationUnaryField(String id, final FieldValueModel fieldValueModel, final boolean readonly) {
    super(id);

    IModel<Boolean> selectedModel = new Model<Boolean>(Boolean.valueOf(fieldValueModel.isExistingValue()));
      
    this.checkbox = new CheckBox("player", selectedModel) {        
      @Override
      protected void onModelChanged() {
        super.onModelChanged();        
        
        FieldInstanceModel fieldInstanceModel = fieldValueModel.getFieldInstanceModel();
        FieldInstance fieldInstance = fieldInstanceModel.getFieldInstance();
        Topic currentTopic = fieldInstance.getInstance();
        
        RoleField currentField = (RoleField)fieldInstance.getFieldAssignment().getFieldDefinition();
        Boolean state = getModelObject();
        
        AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
        if (state.booleanValue()) {
          if (page.isAddAllowed(currentTopic, currentField)) {
            RoleField.ValueIF value = RoleField.createValue(1);
            value.addPlayer(currentField, currentTopic);          
            fieldInstance.addValue(value, page.getListener());
          }
        } else {
          if (page.isRemoveAllowed(currentTopic, currentField)) {
            RoleField.ValueIF value = RoleField.createValue(1);
            value.addPlayer(currentField, currentTopic);          
            fieldInstance.removeValue(value, page.getListener());
          }
        }
      }
    };
    checkbox.setEnabled(!readonly);
    add(checkbox);
  }
  
  public CheckBox getCheckBox() {
    return checkbox;
  }

}
