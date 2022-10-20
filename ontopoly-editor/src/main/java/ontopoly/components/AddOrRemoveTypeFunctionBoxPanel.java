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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ontopoly.model.Topic;
import ontopoly.model.TopicType;
import ontopoly.models.AvailableTopicTypesModel;
import ontopoly.models.TopicModel;
import ontopoly.pages.AbstractOntopolyPage;
import ontopoly.pages.InstancePage;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

public class AddOrRemoveTypeFunctionBoxPanel extends Panel {

  protected final TopicModel<TopicType> selectedModel = new TopicModel<TopicType>(null, TopicModel.TYPE_TOPIC_TYPE);
  
  public AddOrRemoveTypeFunctionBoxPanel(String id, final TopicModel<Topic> topicModel) {
    super(id);
    add(new Label("title", new ResourceModel("add.remove.type.instance")));   

    AvailableTopicTypesModel choicesModel = new AvailableTopicTypesModel(topicModel) {
      @Override
      protected boolean getShouldIncludeExistingTypes() {
        return true;
      }
      @Override
      protected boolean filter(Topic o) {
        AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
        return page.filterTopic(o);
      }                              
    };
    TopicDropDownChoice<TopicType> choice = new TopicDropDownChoice<TopicType>("typesList", selectedModel, choicesModel);
    choice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        // no-op
      }
    });
    add(choice);
    
    Button addButton = new Button("addButton", new ResourceModel("add"));
    addButton.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        TopicType topicType = (TopicType)selectedModel.getObject();
        if (topicType == null) {
          return;
        }
        Topic instance = topicModel.getTopic();
        instance.addTopicType(topicType);
        Map<String,String> pageParametersMap = new HashMap<String,String>();
        pageParametersMap.put("topicMapId", instance.getTopicMap().getId());
        pageParametersMap.put("topicId", instance.getId());
        setResponsePage(InstancePage.class, new PageParameters(pageParametersMap));
      }          
    });
    add(addButton);

    Button removeButton = new Button("removeButton", new ResourceModel("remove"));
    removeButton.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        TopicType topicType = (TopicType)selectedModel.getObject();
        if (topicType == null) {
          return;
        }
        Topic instance = topicModel.getTopic();
        Collection<TopicType> topicTypes = instance.getTopicTypes();
        if (!(topicTypes.size() == 1 && topicTypes.contains(topicType))) {
          // only remove topic type if it won't end up without a type at all
          instance.removeTopicType(topicType);
        }
        Map<String,String> pageParametersMap = new HashMap<String,String>();
        pageParametersMap.put("topicMapId", instance.getTopicMap().getId());
        pageParametersMap.put("topicId", instance.getId());
        setResponsePage(InstancePage.class, new PageParameters(pageParametersMap));
      }          
    });
    add(removeButton);
  }

  @Override
  public void onDetach() {
    selectedModel.detach();
    super.onDetach();
  }
  
}
