package ontopoly.components;

import java.util.HashMap;
import java.util.Map;

import ontopoly.model.OntopolyTopicIF;
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
  
  public AssociationTransformFunctionBoxPanel(String id, final TopicModel topicModel) {
    super(id);
    add(new Label("title", new ResourceModel("transform.association.instances")));   
    
    Button addButton = new Button("button", new ResourceModel("transform"));
    addButton.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        OntopolyTopicIF instance = topicModel.getTopic();
        Map<String,String> pageParametersMap = new HashMap<String,String>();
        pageParametersMap.put("topicMapId", instance.getTopicMap().getId());
        pageParametersMap.put("topicId", instance.getId());
        setResponsePage(AssociationTransformPage.class, new PageParameters(pageParametersMap));
      }          
    });
    add(addButton);
  }
  
}
