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

import ontopoly.OntopolyContext;
import ontopoly.model.TopicMap;
import ontopoly.models.TopicMapModel;
import ontopoly.pages.ModalConfirmPage;
import ontopoly.utils.WicketHacks;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

public abstract class DeleteTopicMapFunctionBoxPanel extends Panel {
  
  public DeleteTopicMapFunctionBoxPanel(String id) {
    super(id);
    add(new Label("title", new ResourceModel("delete.this.topic.map")));

    final ModalWindow deleteModal = new ModalWindow("deleteModal");
    ModalConfirmPage modalDeletePanel = new ModalConfirmPage(deleteModal.getContentId()) {
      @Override
      protected void onCloseCancel(AjaxRequestTarget target) {
        // close modal
        deleteModal.close(target);
      }
      @Override
      protected void onCloseOk(AjaxRequestTarget target) {
        // close modal
        deleteModal.close(target);
        // notify listeners
        TopicMap topicMap = getTopicMapModel().getTopicMap();
        onDeleteConfirmed(topicMap);        
        // delete topic map
        OntopolyContext.getOntopolyRepository().deleteTopicMap(topicMap.getId());
      }
      @Override
      protected Component getTitleComponent(String id) {
        return new Label(id, new ResourceModel("delete.confirm"));
      }
      @Override
      protected Component getMessageComponent(String id) {
        return new Label(id, new ResourceModel("delete.message.topicmap"));        
      }
    };
    
    deleteModal.setContent(modalDeletePanel);
    deleteModal.setTitle(new ResourceModel("ModalWindow.title.delete.topicmap").getObject().toString());
    deleteModal.setCookieName("deleteModal");
    add(deleteModal);

    Button createButton = new Button("deleteButton");
    createButton.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        WicketHacks.disableWindowUnloadConfirmation(target);        
        deleteModal.show(target);
      }          
    });
    add(createButton);
  }

  public abstract TopicMapModel getTopicMapModel();
  
  public abstract void onDeleteConfirmed(TopicMap topic);
  
}
