package ontopoly.components;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.ontopia.utils.ObjectUtils;
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

public class ViewsFunctionBoxPanel extends Panel {
  
  public ViewsFunctionBoxPanel(String id, TopicModel<Topic> topicModel, TopicTypeModel topicTypeModel, FieldsViewModel fieldsViewModel) {
    super(id);
    add(new Label("title", new ResourceModel("views.list.header")));

    Topic topic = topicModel.getTopic();
    TopicMap tm = topic.getTopicMap();
    TopicType topicType = topicTypeModel.getTopicType();
    FieldsView fieldsView = fieldsViewModel.getFieldsView();
    
    Collection<FieldsView> views = topic.getFieldViews(topicType, fieldsView);
    if (views.isEmpty())
      setVisible(false);
    
    FieldsView currentView = fieldsViewModel.getFieldsView();
    FieldsView defaultView = FieldsView.getDefaultFieldsView(topicType.getTopicMap());
    if (currentView == null)
      currentView = defaultView;
    
    RepeatingView rv = new RepeatingView("rows");
    add(rv);
    
    for (FieldsView view : views) {
      WebMarkupContainer parent =  new WebMarkupContainer(rv.newChildId());
      rv.add(parent);
      
      
      Map<String,String> pageParametersMap = new HashMap<String,String>();
      pageParametersMap.put("topicMapId", tm.getId());
      pageParametersMap.put("topicId", topic.getId());
      pageParametersMap.put("topicTypeId", topicType.getId());
      if (ObjectUtils.different(view, defaultView))
        pageParametersMap.put("viewId", view.getId());
      if (topic.isOntologyTopic())
        pageParametersMap.put("ontology", "true");
      
      String viewName = view.getName();
      OntopolyBookmarkablePageLink link =
        new OntopolyBookmarkablePageLink("link", InstancePage.class, new PageParameters(pageParametersMap), viewName);
      link.setEnabled(ObjectUtils.different(currentView, view));
      
      parent.add(link);
    }
  }
  
}
