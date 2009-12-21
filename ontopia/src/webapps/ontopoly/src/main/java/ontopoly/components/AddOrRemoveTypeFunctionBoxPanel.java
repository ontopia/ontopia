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
  
  public AddOrRemoveTypeFunctionBoxPanel(String id, final TopicModel topicModel) {
    super(id);
    add(new Label("title", new ResourceModel("add.remove.type.instance")));   

    final TopicModel<TopicType> selectedModel = new TopicModel<TopicType>(null, TopicModel.TYPE_TOPIC_TYPE);
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
      }
    });
    add(choice);
    
    Button addButton = new Button("addButton", new ResourceModel("add"));
    addButton.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        TopicType topicType = (TopicType)selectedModel.getObject();
        if (topicType == null) return;
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
        if (topicType == null) return;
        Topic instance = topicModel.getTopic();
        Collection topicTypes = instance.getTopicTypes();
        if (!(topicTypes.size() == 1 && topicTypes.contains(topicType)))
          // only remove topic type if it won't end up without a type at all
          instance.removeTopicType(topicType);
        Map<String,String> pageParametersMap = new HashMap<String,String>();
        pageParametersMap.put("topicMapId", instance.getTopicMap().getId());
        pageParametersMap.put("topicId", instance.getId());
        setResponsePage(InstancePage.class, new PageParameters(pageParametersMap));
      }          
    });
    add(removeButton);
  }
  
  protected Class getInstancePageClass() {
    return InstancePage.class;
  }
  
}
