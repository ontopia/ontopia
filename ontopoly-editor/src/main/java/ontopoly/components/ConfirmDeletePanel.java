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
import java.util.Iterator;

import ontopoly.model.LifeCycleListener;
import ontopoly.model.Topic;
import ontopoly.models.TopicModel;
import ontopoly.pages.AbstractOntopolyPage;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.ResourceModel;

public class ConfirmDeletePanel extends Panel {
  
  private TopicModel<Topic> topicModel = new TopicModel<Topic>(null);
  
  public ConfirmDeletePanel(String id, final Component refreshComponent) {
    super(id);
    
    add(new Label("topic", new AbstractReadOnlyModel<String>() {
      @Override
      public String getObject() {
        Topic topic = topicModel.getTopic();
        return topic == null ? null : topic.getName();
      }
    }));
    add(new Label("dependent", new AbstractReadOnlyModel<Integer>() {
      @Override
      public Integer getObject() {
        Topic topic = topicModel.getTopic();
        int size = topic == null ? 0 : topic.getDependentObjects().size();
        return size;
      }
    }));
    
    Button yesButton = new Button("yesButton", new ResourceModel("button.yes"));
    yesButton.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      public void onUpdate(AjaxRequestTarget target) {
        onDeleteTopic(target);
        target.addComponent(refreshComponent);
      }
    });
    add(yesButton);

    Button noButton = new Button("noButton", new ResourceModel("button.no"));
    noButton.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      public void onUpdate(AjaxRequestTarget target) {
        setTopic(null);
        target.addComponent(refreshComponent);
      }
    });
    add(noButton);
  }
  
  @Override
  public boolean isVisible() {
    return topicModel.getTopic() != null; 
  }
  
  protected void setTopic(Topic topic) {
    this.topicModel.setObject(topic);
  }
  
  protected LifeCycleListener getListener() {
    return (AbstractOntopolyPage)getPage();    
  }
  
  protected void onDeleteTopic(AjaxRequestTarget target) {
    try {
      Topic topic = topicModel.getTopic();
      if (topic != null) {
        Collection<Topic> dependentObjects = topic.getDependentObjects();
        LifeCycleListener listener = getListener();
        // remove dependent objects
        Iterator<Topic> diter = dependentObjects.iterator();
        while (diter.hasNext()) {
          Topic dtopic = diter.next();
          if (!dtopic.isSystemTopic()) {
            dtopic.remove(listener);
          }
        }
        // remove object
        topic.remove(listener);        
      }
    } finally {
      setTopic(null);
    }
  }
  
}
