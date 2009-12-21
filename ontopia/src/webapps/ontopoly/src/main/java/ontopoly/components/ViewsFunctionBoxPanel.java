package ontopoly.components;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
  
  public ViewsFunctionBoxPanel(String id, TopicModel topicModel, TopicTypeModel topicTypeModel, FieldsViewModel fieldsViewModel) {
    super(id);
    add(new Label("title", new ResourceModel("views.list.header")));

    TopicType topicType = topicTypeModel.getTopicType();
    List views = topicType.getFieldViews(false, false);
    if (views.isEmpty())
      setVisible(false);
    
    FieldsView currentView = fieldsViewModel.getFieldsView();
    FieldsView defaultView = FieldsView.getDefaultFieldsView(topicType.getTopicMap());
    if (currentView == null)
      currentView = defaultView;
    
    RepeatingView rv = new RepeatingView("rows");
    add(rv);
    
    Iterator iter = views.iterator();
    while (iter.hasNext()) {
      FieldsView view = (FieldsView)iter.next();
      
      WebMarkupContainer parent =  new WebMarkupContainer(rv.newChildId());
      rv.add(parent);
      
      Topic topic = topicModel.getTopic();
      TopicMap tm = topic.getTopicMap();
      TopicType tt = topicTypeModel.getTopicType();
      
      Map<String,String> pageParametersMap = new HashMap<String,String>();
      pageParametersMap.put("topicMapId", tm.getId());
      pageParametersMap.put("topicId", topic.getId());
      pageParametersMap.put("topicTypeId", tt.getId());
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
  
  protected Class getInstancePageClass() {
    return InstancePage.class;
  }
  
}
