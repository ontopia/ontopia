package ontopoly.components;

import java.util.HashMap;
import java.util.Map;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.Topic;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicMap;
import ontopoly.models.TopicMapModel;

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
    
    final TextField nameField = new TextField("content", new Model(""));
    nameField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
      }
    });
    add(nameField);

    Button button = new Button("button", getButtonModel());
    button.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        TopicMap topicMap = topicMapModel.getTopicMap();
        Topic instance = createInstance(topicMap, nameField.getModelObjectAsString());
        Map pageParametersMap = new HashMap();
        pageParametersMap.put("topicMapId", topicMap.getId());
        pageParametersMap.put("topicId", instance.getId());
        if (instance.isOntologyTopic())
          pageParametersMap.put("ontology", "true");
        setResponsePage(getInstancePageClass(), new PageParameters(pageParametersMap));
      }          
    });
    add(button);
  }

  protected abstract IModel getTitleModel();

  protected IModel getButtonModel() {
    return new ResourceModel("create");
  }
    
  protected abstract Class getInstancePageClass();
  
  protected abstract Topic createInstance(TopicMap topicMap, String name);
  
}
