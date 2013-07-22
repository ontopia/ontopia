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
import java.util.List;
import java.util.Map;

import ontopoly.OntopolyContext;
import ontopoly.models.TopicMapSourceModel;
import ontopoly.pages.TopicTypesPage;
import ontopoly.sysmodel.TopicMapSource;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

public class CreateNewTopicMapPanel extends Panel {
  
  private int numberOfSources;
  
  public CreateNewTopicMapPanel(String id) {
    super(id);
    
    IModel<List<TopicMapSource>> sourcesChoicesModel = new LoadableDetachableModel<List<TopicMapSource>>() {
      @Override
      protected List<TopicMapSource> load() {
        List<TopicMapSource> result = OntopolyContext.getOntopolyRepository().getEditableSources();
        numberOfSources = result.size();
        return result;
      }
    };
    
    List<TopicMapSource> sources = sourcesChoicesModel.getObject();

    TopicMapSourceModel topicMapSourceModel = null;
    if (numberOfSources > 0) {
      topicMapSourceModel = new TopicMapSourceModel((TopicMapSource)sources.get(0));
    }
    
    WebMarkupContainer sourcesDropDownContainer = new WebMarkupContainer("sourcesDropDownContainer") {
      @Override
      public boolean isVisible() {
        return numberOfSources > 1 ? true : false;
      }
    };
    sourcesDropDownContainer.setOutputMarkupPlaceholderTag(true);
    add(sourcesDropDownContainer);
    
    final AjaxOntopolyDropDownChoice<TopicMapSource> sourcesDropDown = new AjaxOntopolyDropDownChoice<TopicMapSource>("sourcesDropDown", 
        topicMapSourceModel, sourcesChoicesModel, new ChoiceRenderer<TopicMapSource>("title", "id"));
         
    sourcesDropDownContainer.add(sourcesDropDown);
    
    
    final AjaxOntopolyTextField nameField = new AjaxOntopolyTextField("content", new Model<String>(""));
    add(nameField);

    final Button button = new Button("button", new ResourceModel("create"));
    button.setOutputMarkupId(true);
    button.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        String name = nameField.getModel().getObject();
        if(!name.equals("")) {
          TopicMapSource topicMapSource = (TopicMapSource) sourcesDropDown.getModelObject();
          String referenceId = OntopolyContext.getOntopolyRepository().createOntopolyTopicMap(topicMapSource.getId(), name);
          
          Map<String,String> pageParametersMap = new HashMap<String,String>();
          pageParametersMap.put("topicMapId", referenceId);
          setResponsePage(TopicTypesPage.class, new PageParameters(pageParametersMap));
        }
      }          
    });
    add(button);
  }
  
  @Override
  public boolean isVisible() {
    return numberOfSources > 0 ? true : false;
  }
}

