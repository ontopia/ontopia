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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import ontopoly.model.FieldsView;
import ontopoly.model.Topic;
import ontopoly.model.TopicMap;
import ontopoly.model.TopicType;
import ontopoly.models.FieldsViewModel;
import ontopoly.models.TopicModel;
import ontopoly.models.TopicTypeModel;
import ontopoly.pages.InstancePage;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.ResourceModel;

public class TopicTypesFunctionBoxPanel extends Panel {
  
  public TopicTypesFunctionBoxPanel(String id, TopicModel<Topic> topicModel, TopicTypeModel topicTypeModel, FieldsViewModel fieldsViewModel) {
    super(id);
    add(new Label("title", new ResourceModel("topictypes.list.header")));

    List<TopicType> types = topicModel.getTopic().getTopicTypes();
    if (types.isEmpty()) {
      setVisible(false);
    }
    
    TopicType currentTopicType = topicTypeModel.getTopicType();
    FieldsView currentView = fieldsViewModel.getFieldsView();
    
    RepeatingView rv = new RepeatingView("rows");
    add(rv);
    
    Iterator<TopicType> iter =  types.iterator();
    while (iter.hasNext()) {
      TopicType topicType = iter.next();      
      boolean isCurrentTopicType = Objects.equals(currentTopicType, topicType);
      
      WebMarkupContainer parent =  new WebMarkupContainer(rv.newChildId());
      rv.add(parent);
      
      Topic topic = topicModel.getTopic();
      TopicMap tm = topic.getTopicMap();
      
      Map<String,String> pageParametersMap = new HashMap<String,String>();
      pageParametersMap.put("topicMapId", tm.getId());
      pageParametersMap.put("topicId", topic.getId());
      pageParametersMap.put("topicTypeId", topicType.getId());
      
      if (currentView != null && isCurrentTopicType) {
        pageParametersMap.put("viewId", currentView.getId());
      }
      
      String linkText = topicType.getName();
      OntopolyBookmarkablePageLink link =
        new OntopolyBookmarkablePageLink("link", InstancePage.class, new PageParameters(pageParametersMap), linkText);
      link.setEnabled(!isCurrentTopicType);
      
      parent.add(link);
    }
  }
  
}
