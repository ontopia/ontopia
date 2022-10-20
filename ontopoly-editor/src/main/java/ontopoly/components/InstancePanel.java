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

import ontopoly.model.FieldsView;
import ontopoly.model.Topic;
import ontopoly.model.TopicType;
import ontopoly.models.FieldInstanceModel;
import ontopoly.models.FieldsViewModel;
import ontopoly.models.TopicModel;
import ontopoly.models.TopicTypeModel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

public abstract class InstancePanel extends Panel {

  private TopicModel<Topic> topicModel;
  private TopicTypeModel topicTypeModel;
  private FieldsViewModel fieldsViewModel;
  
  private boolean isReadOnly;
  
  public InstancePanel(String id, TopicModel<Topic> topicModel, TopicTypeModel topicTypeModel, FieldsViewModel fieldsViewModel, boolean _isReadOnly, boolean traversable) {
    super(id);
    this.topicModel = topicModel;
    this.topicTypeModel = topicTypeModel;
    this.fieldsViewModel = fieldsViewModel;
    this.isReadOnly = _isReadOnly;
    
    // Add lock panel
    if (isReadOnly) {
      add(new Label("lockPanel").setVisible(false));
    } else {
      LockPanel lockPanel = new LockPanel("lockPanel", topicModel, isReadOnly) {
        @Override
        protected void onLockLost(AjaxRequestTarget target, Topic topic) {
          InstancePanel.this.onLockLost(target, topic);
        }
        @Override
        protected void onLockWon(AjaxRequestTarget target, Topic topic) {        
          InstancePanel.this.onLockWon(target, topic);
        }
      };
      if (lockPanel.isLockedByOther()) {
        isReadOnly = true;
      }
      add(lockPanel);
    }

    // Add fields panel
    createFields(isReadOnly, traversable);
    
    // optional close button
    WebMarkupContainer instanceButtons = new WebMarkupContainer("instanceButtons") {
      @Override
      public boolean isVisible() {
        return isButtonsVisible();
      }     
    };
    instanceButtons.add(new Button("okButton", new ResourceModel("button.ok")));
    add(instanceButtons);
  }
  
  protected boolean isButtonsVisible() {
    return getPage().getPageParameters().getString("buttons") != null;
  }
  
  protected abstract void onLockLost(AjaxRequestTarget target, Topic topic);
  
  protected abstract void onLockWon(AjaxRequestTarget target, Topic topic);
  
  public boolean isReadOnly() {
    return isReadOnly;
  }
  
  private void createFields(boolean isReadOnly, boolean traversable) {
    Topic topic = topicModel.getTopic();    
    TopicType type = topicTypeModel.getTopicType();
    TopicType specificType = topic.getMostSpecificTopicType(type);
    if (specificType == null) {
      specificType = type;
    }
    FieldsView fieldsView = fieldsViewModel.getFieldsView();
    
    List<FieldInstanceModel> fieldInstanceModels = FieldInstanceModel.wrapInFieldInstanceModels(topic.getFieldInstances(specificType, fieldsView));
    FieldInstancesPanel fieldInstancesPanel = new FieldInstancesPanel("fieldsPanel", fieldInstanceModels, fieldsViewModel, isReadOnly, traversable);
    fieldInstancesPanel.setRenderBodyOnly(true);
    add(fieldInstancesPanel);    
  }

  @Override
  public void onDetach() {
    topicModel.detach();
    topicTypeModel.detach();
    fieldsViewModel.detach();
    super.onDetach();
  }
  
}
