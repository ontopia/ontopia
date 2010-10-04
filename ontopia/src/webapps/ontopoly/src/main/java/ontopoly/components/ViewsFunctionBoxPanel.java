package ontopoly.components;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.ontopia.utils.ObjectUtils;
import ontopoly.model.FieldsViewIF;
import ontopoly.model.OntopolyTopicIF;
import ontopoly.model.OntopolyTopicMapIF;
import ontopoly.model.TopicTypeIF;
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
  
  public ViewsFunctionBoxPanel(String id, TopicModel topicModel,
                               TopicTypeModel topicTypeModel,
                               FieldsViewModel fieldsViewModel) {
    super(id);
    add(new Label("title", new ResourceModel("views.list.header")));

    TopicTypeIF topicType = topicTypeModel.getTopicType();
    
    Collection<FieldsViewIF> views = topicType.getFieldViews(false, false);
    if (views.isEmpty())
      setVisible(false);
    
    FieldsViewIF currentView = fieldsViewModel.getFieldsView();
    FieldsViewIF defaultView = topicType.getTopicMap().getDefaultFieldsView();
    if (currentView == null)
      currentView = defaultView;
    
    RepeatingView rv = new RepeatingView("rows");
    add(rv);
    
    for (FieldsViewIF view : views) {
      WebMarkupContainer parent =  new WebMarkupContainer(rv.newChildId());
      rv.add(parent);
      
      OntopolyTopicIF topic = topicModel.getTopic();
      OntopolyTopicMapIF tm = topic.getTopicMap();
      TopicTypeIF tt = topicTypeModel.getTopicType();
      
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
