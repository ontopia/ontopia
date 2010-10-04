package ontopoly.components;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

public class TopicTypesFunctionBoxPanel extends Panel {
  
  public TopicTypesFunctionBoxPanel(String id, TopicModel topicModel,
                                    TopicTypeModel topicTypeModel,
                                    FieldsViewModel fieldsViewModel) {
    super(id);
    add(new Label("title", new ResourceModel("topictypes.list.header")));

    List types = topicModel.getTopic().getTopicTypes();
    if (types.isEmpty())
      setVisible(false);
    
    TopicTypeIF currentTopicType = topicTypeModel.getTopicType();
    FieldsViewIF currentView = fieldsViewModel.getFieldsView();
    
    RepeatingView rv = new RepeatingView("rows");
    add(rv);
    
    Iterator iter =  types.iterator();
    while (iter.hasNext()) {
      TopicTypeIF topicType = (TopicTypeIF) iter.next();      
      boolean isCurrentTopicType = ObjectUtils.equals(currentTopicType, topicType);
      
      WebMarkupContainer parent =  new WebMarkupContainer(rv.newChildId());
      rv.add(parent);
      
      OntopolyTopicIF topic = topicModel.getTopic();
      OntopolyTopicMapIF tm = topic.getTopicMap();
      
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
  
  protected Class getInstancePageClass() {
    return InstancePage.class;
  }
  
}
