package ontopoly.pages;

import ontopoly.model.Topic;
import ontopoly.models.TopicModel;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;

public class EnterTopicPage extends AbstractProtectedOntopolyPage {

  public EnterTopicPage() {	  
  }
  
  public EnterTopicPage(PageParameters parameters) {
	super(parameters);
	
    Topic topic = new TopicModel<Topic>(parameters.getString("topicMapId"), parameters.getString("topicId")).getTopic();
    
    Class<? extends Page> pageClass;
    if (topic.isTopicType())
      pageClass = InstancesPage.class;
//    else if (topic.isAssociationType())
//      pageClass = InstancePage.class;
//    else if (topic.isOccurrenceType())
//      pageClass = InstancePage.class;
//    else if (topic.isNameType())
//      pageClass = InstancePage.class;
//    else if (topic.isRoleType())
//      pageClass = InstancePage.class;
    else
      pageClass = InstancePage.class;
    
    // redirect page
    PageParameters params = new PageParameters();
    params.add("topicMapId", topic.getTopicMap().getId());
    params.add("topicId", topic.getId());
    setResponsePage(pageClass, params);
    setRedirect(true);
  }
  
}
