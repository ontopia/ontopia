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

import java.util.Arrays;
import java.util.List;

import ontopoly.components.FooterPanel;
import ontopoly.components.MenuPanel;
import ontopoly.components.TopicMapHeaderPanel;
import ontopoly.model.TopicMap;
import ontopoly.models.TopicMapModel;
import ontopoly.pojos.MenuItem;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.ResourceModel;

public abstract class OntopolyAbstractPage extends AbstractProtectedOntopolyPage {

  protected static final int NONE_SELECTED = -1;
  
  protected static final int DESCRIPTION_PAGE_INDEX_IN_MAINMENU = 0;

  protected static final int ADMIN_PAGE_INDEX_IN_MAINMENU = 1;

  protected static final int ONTOLOGY_INDEX_IN_MAINMENU = 2;

  protected static final int INSTANCES_PAGE_INDEX_IN_MAINMENU = 3;

  private TopicMapModel topicMapModel;
  
  public OntopolyAbstractPage() {	  
  }
  
  public OntopolyAbstractPage(PageParameters parameters) {
    super(parameters);
    this.topicMapModel = new TopicMapModel(parameters.getString("topicMapId"));
    // NOTE: subclasses must call initParentComponents
  }

  protected void initParentComponents() {
    add(new TopicMapHeaderPanel("header", topicMapModel, getMainMenuItems(topicMapModel), getMainMenuIndex()));
    add(new FooterPanel("footer"));
    
    add(new Label("title", new AbstractReadOnlyModel<String>() {
      @Override
      public String getObject() {
        return "[Ontopoly] " + getTopicMapModel().getTopicMap().getName();   
      }
    }));
    add(new MenuPanel("lowerMenu", getMainMenuItems(topicMapModel), NONE_SELECTED));        
  }
  
  protected abstract int getMainMenuIndex();
  
  protected TopicMapModel getTopicMapModel() {
    return topicMapModel;    
  }
  
  protected TopicMap getTopicMap() {
    return topicMapModel.getTopicMap();
  }
  
  @Override
  public void onDetach() {
    topicMapModel.detach();
    super.onDetach();
  }
  
  private static List<MenuItem> getMainMenuItems(TopicMapModel topicMapModel) {
    PageParameters parameters = new PageParameters();
    parameters.add("topicMapId", topicMapModel.getTopicMap().getId());

    List<MenuItem> mainMenuItems = Arrays.asList(new MenuItem[] {
        new MenuItem(new Label("caption", new ResourceModel("description")),
            DescriptionPage.class, parameters),
        new MenuItem(new Label("caption", new ResourceModel("administration")),
            AdminPage.class, parameters),
        new MenuItem(new Label("caption", new ResourceModel("ontology")),
            TopicTypesPage.class, parameters),
        new MenuItem(new Label("caption", new ResourceModel("instances")),
            InstanceTypesPage.class, parameters) });
    return mainMenuItems;
  }
  
}
