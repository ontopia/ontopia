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

import java.util.HashMap;
import java.util.Map;

import ontopoly.OntopolySession;
import ontopoly.model.Topic;
import ontopoly.model.TopicMap;
import ontopoly.model.TopicType;
import ontopoly.models.TopicModel;
import ontopoly.models.TopicTypeModel;
import ontopoly.pages.InstancePage;

import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.ResourceModel;

public class CreateOrCopyInstanceFunctionBoxPanel extends Panel {
  
  public CreateOrCopyInstanceFunctionBoxPanel(String id, final TopicModel<Topic> topicModel, final TopicTypeModel topicTypeModel) {
    super(id);
    add(new Label("title", new AbstractReadOnlyModel<String>() {
      @Override
      public String getObject() {
        return new ResourceModel("create.new").getObject() + " " + topicTypeModel.getTopicType().getName();   
      }      
    }));
  
    Button createButton = new Button("createButton", new ResourceModel("create"));
    createButton.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        Topic instance = topicModel.getTopic();
        TopicMap topicMap = instance.getTopicMap();
        TopicType topicType = topicTypeModel.getTopicType();
        Topic newInstance = topicType.createInstance(null);
        Map<String,String> pageParametersMap = new HashMap<String,String>();
        pageParametersMap.put("topicMapId", topicMap.getId());
        pageParametersMap.put("topicId", newInstance.getId());
        if (newInstance.isOntologyTopic()) {
          pageParametersMap.put("ontology", "true");
        }
        setResponsePage(InstancePage.class, new PageParameters(pageParametersMap));
      }          
    });
    add(createButton);

    Button copyButton = new Button("copyButton", new ResourceModel("copy")) {
      @Override
      public boolean isEnabled() {
        // only display copy button for non-ontology topics
        return !topicModel.getTopic().isOntologyTopic() || ((OntopolySession)Session.get()).isAdministrationEnabled();
        
      }
    };
    copyButton.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        // FIXME: perhaps we should not copy the names as this is somewhat confusing
        Topic instance = topicModel.getTopic();
        TopicMap topicMap = instance.getTopicMap();
        Topic newInstance = instance.copyCharacteristics();
        Map<String,String> pageParametersMap = new HashMap<String,String>();
        pageParametersMap.put("topicMapId", topicMap.getId());
        pageParametersMap.put("topicId", newInstance.getId());
        if (newInstance.isOntologyTopic()) {
          pageParametersMap.put("ontology", "true");
        }
        setResponsePage(InstancePage.class, new PageParameters(pageParametersMap));
      }          
    });
    add(copyButton);
  }
  
}
