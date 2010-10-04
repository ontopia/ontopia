package ontopoly.components;

import java.util.HashMap;
import java.util.Map;

import ontopoly.model.OntopolyTopicIF;
import ontopoly.model.OntopolyTopicMapIF;
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
      }
    });
    add(nameField);

    Button button = new Button("button", getButtonModel());
    button.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        OntopolyTopicMapIF topicMap = topicMapModel.getTopicMap();
        OntopolyTopicIF instance = createInstance(topicMap, nameField.getDefaultModelObjectAsString());
        Map<String,String> pageParametersMap = new HashMap<String,String>();
        pageParametersMap.put("topicMapId", topicMap.getId());
        pageParametersMap.put("topicId", instance.getId());
        if (instance.isOntologyTopic())
          pageParametersMap.put("ontology", "true");
        setResponsePage(getInstancePageClass(), new PageParameters(pageParametersMap));
      }          
    });
    add(button);
  }

  protected abstract IModel<String> getTitleModel();

  protected IModel<String> getButtonModel() {
    return new ResourceModel("create");
  }
    
  protected abstract Class<? extends Page> getInstancePageClass();
  
  protected abstract OntopolyTopicIF createInstance(OntopolyTopicMapIF topicMap, String name);
  
}
