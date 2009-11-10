package ontopoly.pages;

import java.util.Collection;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.FieldDefinition;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.FieldInstance;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.LifeCycleListener;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.Topic;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicType;
import ontopoly.OntopolySession;
import ontopoly.utils.OntopolyUtils;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractOntopolyPage extends WebPage implements LifeCycleListener {
 
  protected static Logger log = LoggerFactory.getLogger(AbstractOntopolyPage.class);
  
  private boolean isReadOnlyPage;
  
  public AbstractOntopolyPage() {	  
  }
  
  public AbstractOntopolyPage(PageParameters params) {
    super(params);
    
    // add header contributor for stylesheet
    add(CSSPackageResource.getHeaderContribution(getStylesheet()));
  }

  protected String getStylesheet() {
    return "resources/ontopoly.resources.Resources/stylesheet.css";
  }
  
  public OntopolySession getOntopolySession() { 
    return (OntopolySession) getSession(); 
  }  
  
  public boolean isShortcutsEnabled() { 
    return getOntopolySession().isShortcutsEnabled(); 
  }
    
  public boolean isAnnotationEnabled() { 
    return getOntopolySession().isAnnotationEnabled(); // || isAdministrationEnabled(); 
  }

  public boolean isAdministrationEnabled() {
    return getOntopolySession().isAdministrationEnabled();
  }
  
  public boolean isReadOnlyPage() {
    return isReadOnlyPage;
  }

  public void setReadOnlyPage(boolean isReadOnlyPage) {
    this.isReadOnlyPage = isReadOnlyPage;
  }

  public boolean isAddAllowed(Topic parent, FieldDefinition fdParent, Topic child, FieldDefinition fdChild) {
//    System.out.println("AA: " + parent + " " + child);
    return true;
  }
  
  public boolean isAddAllowed(Topic parent, FieldDefinition fdParent) {
//    System.out.println("AAu: " + parent);
    return true;
  }
  
  public boolean isRemoveAllowed(Topic parent, FieldDefinition fdParent, Topic child, FieldDefinition fdChild) {
//    System.out.println("RA: " + parent + " " + child);
    return true;
  }
  
  public boolean isRemoveAllowed(Topic parent, FieldDefinition fdParent) {
//    System.out.println("RAu: " + parent);
    return true;
  }
  
  public boolean isCreateAllowed(Topic parent, FieldDefinition fdParent, TopicType childType, FieldDefinition fdChild) {
//    System.out.println("CA: " + parent + " " + childType);
    return true;
  }

  public LifeCycleListener getListener() {
    return this;
  }
  
  // LifeCycleListener implementation
  
  public void onAfterCreate(Topic topic, TopicType topicType) {    
//    System.out.println("oAC: " + topic + " " + topicType);
  }

  public void onBeforeDelete(Topic topic) {
//    System.out.println("oBD: " + topic);    
  }

  public void onAfterAdd(FieldInstance fieldInstance, Object value) {
//    System.out.println("oAA: " + fieldInstance + " " + value);
  }

  public void onAfterReplace(FieldInstance fieldInstance, Object oldValue, Object newValue) {
//    System.out.println("oAP: " + fieldInstance + " " + oldValue + " -> " + newValue);
  }

  public void onBeforeRemove(FieldInstance fieldInstance, Object value) {    
//    System.out.println("oBR: " + fieldInstance + " " + value);
  }
  
  /**
   * Access filter to tell whether user has default access rights to topic is not.
   * @param topic the topic to check rights for
   * @return true if access allowed
   */
  public boolean filterTopic(Topic topic) {
    if (isAdministrationEnabled())
      return OntopolyUtils.filterTopicByAdministratorRole(topic);
    else if (isAnnotationEnabled())
      return OntopolyUtils.filterTopicByAnnotationRole(topic);
    else
      return OntopolyUtils.filterTopicByDefaultRole(topic);
  }

  /**
   * Checks access for a collection of topics. Topics that the user does 
   * not have access to will be removed from the collection.
   * @param topics the topics to check rights for
   */
  public void filterTopics(Collection<? extends Topic> topics) {
    if (isAdministrationEnabled())
      OntopolyUtils.filterTopicsByAdministratorRole(topics);
    else if (isAnnotationEnabled())
      OntopolyUtils.filterTopicsByAnnotationRole(topics);
    else
      OntopolyUtils.filterTopicsByDefaultRole(topics);
  }
  
  /**
   * Returns the display name of the given topic. This method is meant as 
   * an extension point for retrieval of topic names.
   * @param topic
   * @return
   */
  public String toString(Topic topic) {
    if (topic instanceof FieldDefinition)
      return ((FieldDefinition)topic).getFieldName();
    else
      return topic.getName();
  }
 
  /**
   * Given the topic return the page class to use. This method is used 
   * in various places around the application to generate links to topics.
   * Subclasses may override it.
   * @param topic
   * @return
   */
  public Class<? extends Page> getPageClass(Topic topic) {
    return getClass();
  }
  
  /**
   * Given the topic return the page parameters to use. This method is used 
   * in various places around the application to generate links to topics. 
   * Subclasses may override it.
   * @param topic
   * @return
   */
  public PageParameters getPageParameters(Topic topic) {
    // WARNING: if you do a change here then you may also want to do so in EmbeddedInstancePage.
    PageParameters params = new PageParameters();
    params.put("topicMapId", topic.getTopicMap().getId());
    params.put("topicId", topic.getId());
    PageParameters thisParams = getPageParameters();
    if (thisParams != null) {
      // forward ontology parameter (if applicable)
      String ontology = thisParams.getString("ontology");
      if (ontology != null && topic.isOntologyTopic())
        params.put("ontology", "true");
    }    
    return params;    
  }
  
}
