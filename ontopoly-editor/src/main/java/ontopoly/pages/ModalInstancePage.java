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
package ontopoly.pages;

import java.util.Objects;
import ontopoly.components.InstancePanel;
import ontopoly.model.Topic;
import ontopoly.models.FieldsViewModel;
import ontopoly.models.TopicModel;
import ontopoly.models.TopicTypeModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class ModalInstancePage extends Panel {
  
  private WebMarkupContainer popupContent;
  private TopicModel<Topic> topicModel;
  private TopicTypeModel topicTypeModel;
  private FieldsViewModel fieldsViewModel;
  private boolean isReadOnly;
  private boolean traversable = false; // FIXME: hardcoded
  
  public ModalInstancePage(String id, TopicModel<Topic> topicModel, TopicTypeModel topicTypeModel, FieldsViewModel fieldsViewModel) {
    super(id);
    this.topicModel = topicModel;
    this.topicTypeModel = topicTypeModel;
    this.fieldsViewModel = fieldsViewModel;
    
    // page is read-only if topic type is read-only
    this.isReadOnly = ((topicTypeModel != null && topicTypeModel.getTopicType().isReadOnly()) || (Objects.equals(getRequest().getParameter("ro"), "true")));

    this.popupContent = new WebMarkupContainer("popupContent");
    popupContent.setOutputMarkupId(true);
    add(popupContent);
    
    popupContent.add(createInstancePanel("instancePanel"));
    
    Button closeOkButton = new Button("closeOK");
    closeOkButton.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        onCloseOk(target);
      }
    });
    popupContent.add(closeOkButton);
  }  

  protected abstract void onCloseOk(AjaxRequestTarget target);

  private InstancePanel createInstancePanel(final String id) {
    return new InstancePanel(id, topicModel, topicTypeModel, fieldsViewModel, isReadOnly, traversable) {
      @Override
      protected boolean isButtonsVisible() {
        return false; // Don't show buttons as there will already be a set of buttons visible.
      }
      @Override
      protected void onLockLost(AjaxRequestTarget target, Topic topic) {
        popupContent.replace(createInstancePanel(id));
        target.addComponent(popupContent);        
      }      
      @Override
      protected void onLockWon(AjaxRequestTarget target, Topic topic) {
        popupContent.replace(createInstancePanel(id));
        target.addComponent(popupContent);        
      }      
    };
  }

  @Override
  protected void onDetach() {
    topicModel.detach();
    topicTypeModel.detach();
    fieldsViewModel.detach();
    super.onDetach();
  }
}
