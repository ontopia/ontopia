
// $Id: JavaPredicate.java,v 1.1 2007/11/27 09:13:38 geir.gronmo Exp $

package net.ontopia.topicmaps.query.spi;

import java.util.Map;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;
import net.ontopia.topicmaps.query.impl.basic.BasicPredicateIF;
import net.ontopia.topicmaps.query.impl.utils.PredicateSignature;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Abstract predicate class that works as a common
 * superclass for the real predicate classes. Don't subclass this one
 * directly.<p>
 */
public abstract class JavaPredicate implements BasicPredicateIF {

  private String moduleURI;
  private String predicateName;
  private TopicMapIF topicmap;
  private Map parameters;
  
  public String getName() {
    return predicateName;
  }

  public String getSignature() {
    return ".?!+";
  }

  public int getCost(boolean[] boundparams) {
    for (int i=0; i < boundparams.length; i++) {
      if (!boundparams[i]) return PredicateDrivenCostEstimator.INFINITE_RESULT;
    }
    return PredicateDrivenCostEstimator.FILTER_RESULT;
  }
  
  // -- default setter implementations

  public String getModuleURI() {
    return moduleURI;
  }
  
  public void setModuleURI(String moduleURI) {
    this.moduleURI = moduleURI;
  }

  public String getPredicateName() {
    return predicateName;
  }
  
  public void setPredicateName(String predicateName) {
    this.predicateName = predicateName;
  }

  public TopicMapIF getTopicMap() {
    return topicmap;
  }
  
  public void setTopicMap(TopicMapIF topicmap) {
    this.topicmap = topicmap;
  }

  public Map getParameters() {
    return parameters;
  }
  
  public void setParameters(Map parameters) {
    this.parameters = parameters;
  }

  /**
   * INTERNAL: Internal machinery.
   */
  public abstract QueryMatches satisfy(QueryMatches matches, Object[] arguments) throws InvalidQueryException;
  
}
