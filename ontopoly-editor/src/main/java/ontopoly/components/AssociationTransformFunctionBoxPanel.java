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

import ontopoly.model.Topic;
import ontopoly.models.TopicModel;
import ontopoly.pages.AssociationTransformPage;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

public class AssociationTransformFunctionBoxPanel extends Panel {
  
  public AssociationTransformFunctionBoxPanel(String id, final TopicModel<Topic> topicModel) {
    super(id);
    add(new Label("title", new ResourceModel("transform.association.instances")));   
    
    Button addButton = new Button("button", new ResourceModel("transform"));
    addButton.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        Topic instance = topicModel.getTopic();
        Map<String,String> pageParametersMap = new HashMap<String,String>();
        pageParametersMap.put("topicMapId", instance.getTopicMap().getId());
        pageParametersMap.put("topicId", instance.getId());
        setResponsePage(AssociationTransformPage.class, new PageParameters(pageParametersMap));
      }          
    });
    add(addButton);
  }
  
}
