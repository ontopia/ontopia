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
import ontopoly.model.TopicMap;
import ontopoly.models.TopicMapModel;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

public abstract class CreateInstanceFunctionBoxPanel extends Panel {
  
  public CreateInstanceFunctionBoxPanel(String id, final TopicMapModel topicMapModel) {
    super(id);
    
    add(new Label("title", getTitleModel()));
    
    final TextField<String> nameField = new TextField<String>("content", new Model<String>(""));
    nameField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        // no-op
      }
    });
    add(nameField);

    Button button = new Button("button", getButtonModel());
    button.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        TopicMap topicMap = topicMapModel.getTopicMap();
        Topic instance = createInstance(topicMap, nameField.getModel().getObject());
        Map<String,String> pageParametersMap = new HashMap<String,String>();
        pageParametersMap.put("topicMapId", topicMap.getId());
        pageParametersMap.put("topicId", instance.getId());
        if (instance.isOntologyTopic()) {
          pageParametersMap.put("ontology", "true");
        }
        setResponsePage(getInstancePageClass(), new PageParameters(pageParametersMap));
        setRedirect(true);
      }          
    });
    add(button);
  }

  protected abstract IModel<String> getTitleModel();

  protected IModel<String> getButtonModel() {
    return new ResourceModel("create");
  }
    
  protected abstract Class<? extends Page> getInstancePageClass();
  
  protected abstract Topic createInstance(TopicMap topicMap, String name);
  
}
