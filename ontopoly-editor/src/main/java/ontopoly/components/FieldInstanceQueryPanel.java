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

import java.util.List;

import ontopoly.LockManager;
import ontopoly.OntopolySession;
import ontopoly.model.FieldAssignment;
import ontopoly.model.FieldDefinition;
import ontopoly.model.FieldInstance;
import ontopoly.model.Topic;
import ontopoly.model.TopicType;
import ontopoly.models.FieldDefinitionModel;
import ontopoly.models.FieldInstanceModel;
import ontopoly.models.FieldValueModel;
import ontopoly.models.FieldValuesModel;
import ontopoly.models.FieldsViewModel;
import ontopoly.models.TopicModel;
import ontopoly.utils.OntopolyUtils;

import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

public class FieldInstanceQueryPanel extends AbstractFieldInstancePanel {

  public FieldInstanceQueryPanel(String id, final FieldInstanceModel fieldInstanceModel, 
      final FieldsViewModel fieldsViewModel, final boolean readonly, final boolean traversable) {
    this(id, fieldInstanceModel, fieldsViewModel, readonly, false, traversable);
  }

  public FieldInstanceQueryPanel(String id, final FieldInstanceModel fieldInstanceModel, 
      final FieldsViewModel fieldsViewModel, final boolean readonly, final boolean embedded, final boolean traversable) {
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

    WebMarkupContainer fieldValuesList = new WebMarkupContainer("fieldValuesList");
    fieldValuesContainer.add(fieldValuesList);

    // add field values component(s)
    this.fieldValuesModel = new FieldValuesModel(fieldInstanceModel); // NOTE: no comparator
    fieldValuesModel.setAutoExtraField(false);
    
    // preload values to trigger query syntax errors
    fieldValuesModel.getObject();
    
    this.listView = new ListView<FieldValueModel>("fieldValues", fieldValuesModel) {
      @Override
      public void populateItem(final ListItem<FieldValueModel> item) {
        final FieldValueModel fieldValueModel = item.getModelObject();

        final WebMarkupContainer fieldValueButtons = new WebMarkupContainer("fieldValueButtons");
        fieldValueButtons.setOutputMarkupId(true);
        item.add(fieldValueButtons);

        Object value = fieldValueModel.getObject();
        
        final boolean isTopicValue;
        final String topicMapId;
        final String topicId;
        final boolean isLockedByOther;
        
        if (value instanceof Topic) {
          isTopicValue = true;
          Topic oPlayer = (Topic)value;
          
          topicMapId = (oPlayer == null ? null : oPlayer.getTopicMap().getId());
          topicId = (oPlayer == null ? null : oPlayer.getId());
  
          // acquire lock for embedded topic
          if (embedded && fieldValueModel.isExistingValue()) {
            OntopolySession session = (OntopolySession)Session.get();
            String lockerId = session.getLockerId(getRequest());
            LockManager.Lock lock = session.lock(oPlayer, lockerId);
            isLockedByOther = !lock.ownedBy(lockerId);
          } else {
            isLockedByOther = false;
          }
          final boolean _readonly = readonly || isLockedByOther;
          
          if (embedded) {
            TopicType defaultTopicType = OntopolyUtils.getDefaultTopicType(oPlayer);
            List<FieldInstance> fieldInstances = oPlayer.getFieldInstances(defaultTopicType, fieldsViewModel.getFieldsView());
            // if no matching fields show link to topic instead
            if (fieldInstances.isEmpty()) {
              // player link
              TopicLink<Topic> playerLink = new TopicLink<Topic>("fieldValue", new TopicModel<Topic>(oPlayer), fieldsViewModel);
              playerLink.setEnabled(traversable);
              item.add(playerLink);          
            } else {
              // embedded topic
              List<FieldInstanceModel> fieldInstanceModels = FieldInstanceModel.wrapInFieldInstanceModels(fieldInstances);
              FieldInstancesPanel fip = new FieldInstancesPanel("fieldValue", fieldInstanceModels, fieldsViewModel, _readonly, traversable);
              fip.setRenderBodyOnly(true);
              item.add(fip);
            }
          } else {
            // player link
            TopicLink<Topic> playerLink = new TopicLink<Topic>("fieldValue", new TopicModel<Topic>(oPlayer), fieldsViewModel);
            playerLink.setEnabled(traversable);
            item.add(playerLink);
          }
        } else {
          isTopicValue = true;
          topicMapId = null;
          topicId = null;
          isLockedByOther = false;
          item.add(new Label("fieldValue", new Model<String>(value == null ? null : value.toString())));
        }
        
        // embedded goto button
        OntopolyImageLink gotoButton = new OntopolyImageLink("goto", "goto.gif", new ResourceModel("icon.goto.topic")) {
          @Override
          public boolean isVisible() {
            if (!isTopicValue) {
              return false;
            }
            FieldValueModel fieldValueModel = item.getModelObject();
            return embedded && fieldValueModel.isExistingValue();  
          }
          @Override
          public void onClick(AjaxRequestTarget target) {
            // navigate to topic
            PageParameters pageParameters = new PageParameters();
            pageParameters.put("topicMapId", topicMapId);
            pageParameters.put("topicId", topicId);
            setResponsePage(getPage().getClass(), pageParameters);
            setRedirect(true);
          }
        };
        fieldValueButtons.add(gotoButton);

        // embedded lock button
        OntopolyImageLink lockButton = new OntopolyImageLink("lock", "lock.gif", new ResourceModel("icon.topic.locked")) {
          @Override
          public boolean isVisible() {
            return embedded && isLockedByOther;  
          }
          @Override
          public void onClick(AjaxRequestTarget target) {
            // no-op
          }
        };
        fieldValueButtons.add(lockButton);

      }
    };
    listView.setReuseItems(true);
    fieldValuesList.add(listView);

    // empty for now
    this.fieldInstanceButtons = new WebMarkupContainer("fieldInstanceButtons");
    fieldInstanceButtons.setOutputMarkupId(true);
    add(fieldInstanceButtons);
  }
  
}
