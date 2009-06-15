package ontopoly.components;

import java.util.HashMap;
import java.util.Map;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.Topic;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicMap;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicType;
import ontopoly.models.TopicModel;
import ontopoly.models.TopicTypeModel;
import ontopoly.pages.InstancePage;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.ResourceModel;

public class CreateOrCopyInstanceFunctionBoxPanel extends Panel {
  
  public CreateOrCopyInstanceFunctionBoxPanel(String id, final TopicModel topicModel, final TopicTypeModel topicTypeModel) {
    super(id);
    add(new Label("title", new AbstractReadOnlyModel() {
      @Override
      public Object getObject() {
        return new ResourceModel("create.new").getObject() + " " + topicTypeModel.getTopicType().getName();   
      }      
    }));
  
    Button createButton = new Button("createButton", new ResourceModel("create"));
    createButton.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        Topic instance = topicModel.getTopic();
        TopicMap topicMap = instance.getTopicMap();
        TopicType topicType = topicTypeModel.getTopicType();
        Topic newInstance = topicType.createInstance(null);
        Map pageParametersMap = new HashMap();
        pageParametersMap.put("topicMapId", topicMap.getId());
        pageParametersMap.put("topicId", newInstance.getId());
        if (newInstance.isOntologyTopic())
          pageParametersMap.put("ontology", "true");
        setResponsePage(InstancePage.class, new PageParameters(pageParametersMap));
      }          
    });
    add(createButton);

    Button copyButton = new Button("copyButton", new ResourceModel("copy"));
    copyButton.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        // FIXME: perhaps we should not copy the names as this is somewhat confusing
        Topic instance = topicModel.getTopic();
        TopicMap topicMap = instance.getTopicMap();
        Topic newInstance = instance.copyCharacteristics();
        Map pageParametersMap = new HashMap();
        pageParametersMap.put("topicMapId", topicMap.getId());
        pageParametersMap.put("topicId", newInstance.getId());
        if (newInstance.isOntologyTopic())
          pageParametersMap.put("ontology", "true");
        setResponsePage(InstancePage.class, new PageParameters(pageParametersMap));
      }          
    });
    add(copyButton);
  }
  
  protected Class getInstancePageClass() {
    return InstancePage.class;
  }
  
}
