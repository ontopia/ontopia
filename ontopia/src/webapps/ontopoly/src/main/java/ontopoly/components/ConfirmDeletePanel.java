package ontopoly.components;

import java.util.Collection;
import java.util.Iterator;

import ontopoly.model.LifeCycleListenerIF;
import ontopoly.model.OntopolyTopicIF;
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
  private TopicModel<OntopolyTopicIF> topicModel = new TopicModel<OntopolyTopicIF>(null);
  
  public ConfirmDeletePanel(String id, final Component refreshComponent) {
    super(id);
    
    add(new Label("topic", new AbstractReadOnlyModel<String>() {
      @Override
      public String getObject() {
        OntopolyTopicIF topic = topicModel.getTopic();
        return topic == null ? null : topic.getName();
      }
    }));
    add(new Label("dependent", new AbstractReadOnlyModel<Integer>() {
      @Override
      public Integer getObject() {
        OntopolyTopicIF topic = topicModel.getTopic();
        int size = topic == null ? 0 : topic.getDependentObjects().size();
        return new Integer(size);
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
  
  public boolean isVisible() {
    return topicModel.getTopic() != null; 
  }
  
  protected void setTopic(OntopolyTopicIF topic) {
    this.topicModel.setObject(topic);
  }
  
  protected LifeCycleListenerIF getListener() {
    return (AbstractOntopolyPage)getPage();    
  }
  
  protected void onDeleteTopic(AjaxRequestTarget target) {
    try {
      OntopolyTopicIF topic = topicModel.getTopic();
      if (topic != null) {
        Collection dependentObjects = topic.getDependentObjects();
        LifeCycleListenerIF listener = getListener();
        // remove dependent objects
        Iterator diter = dependentObjects.iterator();
        while (diter.hasNext()) {
          OntopolyTopicIF dtopic = (OntopolyTopicIF)diter.next();
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
