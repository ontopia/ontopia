
package net.ontopia.topicmaps.query.spi;

import java.util.Map;

import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * PUBLIC: Abstract SearcherIF convenience superclass used to get the
 * default implementation of the four set methods. Subclassing this
 * class ensures better forward compatibility.<p>
 */
public abstract class AbstractSearcher implements SearcherIF {

  protected String moduleURI;
  protected String predicateName;
  protected TopicMapIF topicmap;
  protected Map parameters;

  // -- default setter implementations
  
  public void setModuleURI(String moduleURI) {
    this.moduleURI = moduleURI;
  }
  
  public void setPredicateName(String predicateName) {
    this.predicateName = predicateName;
  }
  
  public void setTopicMap(TopicMapIF topicmap) {
    this.topicmap = topicmap;
  }

  public void setParameters(Map parameters) {
    this.parameters = parameters;
  }
  
}
