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

public class TopicTypesFunctionBoxPanel extends Panel {
  
  public TopicTypesFunctionBoxPanel(String id, TopicModel<Topic> topicModel, TopicTypeModel topicTypeModel, FieldsViewModel fieldsViewModel) {
    super(id);
    add(new Label("title", new ResourceModel("topictypes.list.header")));

    List<TopicType> types = topicModel.getTopic().getTopicTypes();
    if (types.isEmpty())
      setVisible(false);
    
    TopicType currentTopicType = topicTypeModel.getTopicType();
    FieldsView currentView = fieldsViewModel.getFieldsView();
    
    RepeatingView rv = new RepeatingView("rows");
    add(rv);
    
    Iterator<TopicType> iter =  types.iterator();
    while (iter.hasNext()) {
      TopicType topicType = iter.next();      
      boolean isCurrentTopicType = ObjectUtils.equals(currentTopicType, topicType);
      
      WebMarkupContainer parent =  new WebMarkupContainer(rv.newChildId());
      rv.add(parent);
      
      Topic topic = topicModel.getTopic();
      TopicMap tm = topic.getTopicMap();
      
      Map<String,String> pageParametersMap = new HashMap<String,String>();
      pageParametersMap.put("topicMapId", tm.getId());
      pageParametersMap.put("topicId", topic.getId());
      pageParametersMap.put("topicTypeId", topicType.getId());
      
      if (currentView != null && isCurrentTopicType)
        pageParametersMap.put("viewId", currentView.getId());
      
      String linkText = topicType.getName();
      OntopolyBookmarkablePageLink link =
        new OntopolyBookmarkablePageLink("link", InstancePage.class, new PageParameters(pageParametersMap), linkText);
      link.setEnabled(!isCurrentTopicType);
      
      parent.add(link);
    }
  }
  
}
