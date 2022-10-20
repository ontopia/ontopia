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

import net.ontopia.topicmaps.core.TopicNameIF;
import ontopoly.model.Cardinality;
import ontopoly.model.FieldAssignment;
import ontopoly.model.FieldDefinition;
import ontopoly.model.FieldInstance;
import ontopoly.model.NameField;
import ontopoly.models.FieldDefinitionModel;
import ontopoly.models.FieldInstanceModel;
import ontopoly.models.FieldValueModel;
import ontopoly.models.FieldValuesModel;
import ontopoly.utils.NameComparator;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;

public class FieldInstanceNamePanel extends AbstractFieldInstancePanel {

  public FieldInstanceNamePanel(String id, final FieldInstanceModel fieldInstanceModel, 
      final boolean readonly) {
    super(id, fieldInstanceModel);

    FieldInstance fieldInstance = fieldInstanceModel.getFieldInstance();
    FieldAssignment fieldAssignment = fieldInstance.getFieldAssignment();
    FieldDefinition fieldDefinition = fieldAssignment.getFieldDefinition(); 

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
    this.fieldValuesModel = new FieldValuesModel(fieldInstanceModel, NameComparator.INSTANCE);

    this.listView = new ListView<FieldValueModel>("fieldValues", fieldValuesModel) {
      @Override
      protected void onBeforeRender() {
        validateCardinality();
        super.onBeforeRender();
      }
      @Override
      public void populateItem(final ListItem<FieldValueModel> item) {
        final FieldValueModel fieldValueModel = item.getModelObject();

        // TODO: make sure non-existing value field gets focus if last edit happened there

        final WebMarkupContainer fieldValueButtons = new WebMarkupContainer("fieldValueButtons");
        fieldValueButtons.setOutputMarkupId(true);
        item.add(fieldValueButtons);

        // remove button
        FieldInstanceRemoveButton removeButton = 
          new FieldInstanceRemoveButton("remove", "remove-value.gif", fieldValueModel) { 
          @Override
          public boolean isVisible() {
            Cardinality cardinality = fieldValuesModel.getFieldInstanceModel().getFieldInstance().getFieldAssignment().getCardinality();
            if (fieldValuesModel.size() == 1 && cardinality.isMinOne()) {
              return false;
            } else {
              return !readonly && fieldValueModel.isExistingValue();
            }
          }
          @Override
          public void onClick(AjaxRequestTarget target) {
            super.onClick(target);
            listView.removeAll();
            updateDependentComponents(target);
          }
        };
        fieldValueButtons.add(removeButton);  

        if (readonly) {
          item.add(new Label("fieldValue", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
              TopicNameIF tn = (TopicNameIF)fieldValueModel.getObject();
              return (tn == null ? null : tn.getValue());              
            }

          }));
        } else {
          NameField nf = (NameField)fieldInstanceModel.getFieldInstance().getFieldAssignment().getFieldDefinition();
          final FieldUpdatingBehaviour fuBehaviour = new FieldUpdatingBehaviour(true);

          int height = nf.getHeight(); 
          if (height > 1) {
            FieldInstanceTextArea nameField = new FieldInstanceTextArea("fieldValue", fieldValueModel);
            nameField.setCols(nf.getWidth());
            nameField.setRows(height);
            nameField.add(fuBehaviour);
            item.add(nameField);

          } else {
            FieldInstanceTextField nameField = new FieldInstanceTextField("fieldValue", fieldValueModel);
            nameField.setCols(nf.getWidth());
            nameField.add(fuBehaviour);
            item.add(nameField);           
          }

          //// add focus behaviour to default name field
          // if (nf.getNameType().isUntypedName())
          //   nameField.add(new FocusOnLoadBehaviour());
        }

        addNewFieldValueCssClass(item, fieldValuesModel, fieldValueModel);
      }
    };
    listView.setReuseItems(true);
    fieldValuesContainer.add(listView);

    this.fieldInstanceButtons = new WebMarkupContainer("fieldInstanceButtons");
    fieldInstanceButtons.setOutputMarkupId(true);
    add(fieldInstanceButtons);

    OntopolyImageLink addButton = new OntopolyImageLink("add", "add.gif") { 
      @Override
      public void onClick(AjaxRequestTarget target) {
        boolean showExtraField = !fieldValuesModel.getShowExtraField();
        fieldValuesModel.setShowExtraField(showExtraField, true);
        updateDependentComponents(target);
        listView.removeAll();
      }
      @Override
      public boolean isVisible() {
        if (readonly) {
          return false;
        }
        Cardinality cardinality = fieldValuesModel.getFieldInstanceModel().getFieldInstance().getFieldAssignment().getCardinality();
        return !cardinality.isMaxOne() && fieldValuesModel.containsExisting();
      }      
      @Override public String getImage() {
        return fieldValuesModel.getShowExtraField() ? "remove.gif" : "add.gif";
      }
      @Override public IModel<String> getTitleModel() {
        return new ResourceModel(fieldValuesModel.getShowExtraField() ? "icon.remove.hide-field" : "icon.add.add-value");
      }      
    };  
    addButton.setOutputMarkupId(true);
    fieldInstanceButtons.add(addButton);

    Cardinality cardinality = fieldAssignment.getCardinality();
    if (cardinality.isMaxOne()) {
      addButton.setVisible(false);
    }
  }

}
