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

import ontopoly.model.FieldAssignment;
import ontopoly.model.FieldInstance;
import ontopoly.model.RoleField;
import ontopoly.models.FieldDefinitionModel;
import ontopoly.models.FieldInstanceModel;
import ontopoly.models.FieldValueModel;
import ontopoly.models.FieldValuesModel;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

public class FieldInstanceAssociationUnaryPanel extends AbstractFieldInstancePanel {
  
	public FieldInstanceAssociationUnaryPanel(String id, final FieldInstanceModel fieldInstanceModel, 
	    final boolean readonly) {
		super(id, fieldInstanceModel);

		FieldInstance fieldInstance = fieldInstanceModel.getFieldInstance();
		FieldAssignment fieldAssignment = fieldInstance.getFieldAssignment();
		RoleField fieldDefinition = (RoleField)fieldAssignment.getFieldDefinition(); 
		
    add(new FieldDefinitionLabel("fieldLabel", new FieldDefinitionModel(fieldDefinition)));
    
    // set up container
		this.fieldValuesContainer = new WebMarkupContainer("fieldValuesContainer");
		fieldValuesContainer.setOutputMarkupId(true);    
    add(fieldValuesContainer);

		// add feedback panel
    this.feedbackPanel = new FeedbackPanel("feedback", new AbstractFieldInstancePanelFeedbackMessageFilter());
    feedbackPanel.setOutputMarkupId(true);
    fieldValuesContainer.add(feedbackPanel);
    
    // add field values component(s)
    this.fieldValuesModel = new FieldValuesModel(fieldInstanceModel); // NOTE: no need to do any sorting here
    this.listView = new ListView<FieldValueModel>("fieldValues", fieldValuesModel) {
      // NOTE: no need to validate cardinality here
//      @Override
//      protected void onBeforeRender() {
//        validateCardinality(FieldInstanceAssociationUnaryPanel.this);        
//        super.onBeforeRender();
//      }
		  public void populateItem(final ListItem<FieldValueModel> item) {
		    FieldValueModel fieldValueModel = item.getModelObject();

        // TODO: make sure non-existing value field gets focus if last edit happened there
          
        // unary
        FieldInstanceAssociationUnaryField unaryField = new FieldInstanceAssociationUnaryField("fieldValue", fieldValueModel, readonly);
        unaryField.getCheckBox().add(new FieldUpdatingBehaviour(false));
        item.add(unaryField);
        
        addNewFieldValueCssClass(item, fieldValuesModel, fieldValueModel);
	    }
		};
	  listView.setReuseItems(true);	  
	  fieldValuesContainer.add(listView);	  
	}

}
