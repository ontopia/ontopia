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

import ontopoly.model.FieldDefinition;
import ontopoly.model.Topic;
import ontopoly.models.FieldDefinitionModel;
import ontopoly.models.TopicTypeModel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

public abstract class FieldsEditorAddPanel extends Panel {

  public FieldsEditorAddPanel(String id, final TopicTypeModel topicTypeModel, final FieldDefinitionModel fieldDefinitionModel) {
    super(id);
    
    FieldDefinition fieldDefinition = fieldDefinitionModel.getFieldDefinition(); 

    WebMarkupContainer container = new WebMarkupContainer("field", fieldDefinitionModel);
    add(container);

    container.add(new FieldDefinitionLabel("fieldLabel", fieldDefinitionModel) {
      @Override
      protected boolean isOntologyTypeLinkEnabled(Topic topic) {
        return true;
      }      
    });

    container.add(FieldsEditorExistingPanel.getFieldType("valueType", fieldDefinition));

    OntopolyImageLink button = new OntopolyImageLink("button", "add-left.gif", new ResourceModel("icon.add-left.assign-field")) {
      @Override
      public void onClick(AjaxRequestTarget target) {
        onAddField(topicTypeModel, fieldDefinitionModel, target);
      }
    };
    container.add(button);
     
  }

  protected abstract void onAddField(TopicTypeModel topicTypeModel, FieldDefinitionModel fdm, AjaxRequestTarget target);            

}
