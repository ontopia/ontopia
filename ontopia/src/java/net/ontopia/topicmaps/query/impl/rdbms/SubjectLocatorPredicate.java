
// $Id: SubjectLocatorPredicate.java,v 1.12 2008/07/23 13:26:04 geir.gronmo Exp $

package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.persistence.query.jdo.JDOBoolean;
import net.ontopia.persistence.query.jdo.JDOContains;
import net.ontopia.persistence.query.jdo.JDOEquals;
import net.ontopia.persistence.query.jdo.JDOField;
import net.ontopia.persistence.query.jdo.JDONotEquals;
import net.ontopia.persistence.query.jdo.JDONull;
import net.ontopia.persistence.query.jdo.JDOObject;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.rdbms.SubjectLocator;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Implements the 'subject-locator(topic,locator)' predicate.
 */
public class SubjectLocatorPredicate
  extends net.ontopia.topicmaps.query.impl.basic.SubjectLocatorPredicate
  implements JDOPredicateIF {

  public SubjectLocatorPredicate(TopicMapIF topicmap) {
    super(topicmap);
  }

  // --- JDOPredicateIF implementation

  public boolean isRecursive() {
    return false;
  }

  public void prescan(QueryBuilder builder, List arguments) {
  }

  public boolean buildQuery(QueryBuilder builder, List expressions, List arguments)
    throws InvalidQueryException {
    
    // Interpret arguments
    Object[] args = arguments.toArray();
    
    // TMObjectIF
    if (builder.isArgumentOfType(args[1], String.class)) {

      if (args[0] instanceof TopicIF && args[1] instanceof String) {
          
        // Do direct predicate evaluation
        TopicIF topic = (TopicIF)args[0];
        LocatorIF locator = null;
        try {
          locator = new SubjectLocator(new URILocator((String)args[1]));
        } catch (java.net.MalformedURLException e) {
          throw new InvalidQueryException("Not a valid URI: " + args[1]);
        }

        if (topic.getSubjectLocators().contains(locator))
          expressions.add(JDOBoolean.TRUE);
        else
          expressions.add(JDOBoolean.FALSE);
          
      } else {          
        JDOValueIF jv_topic = builder.createJDOValue(args[0]);
        JDOValueIF jv_locator = builder.createJDOVariable("L", SubjectLocator.class);
        JDOValueIF jv_uri = builder.createJDOValue(args[1]);
          
        // JDOQL: T.indicators.contains(L) && L.address = U
        expressions.add(new JDOContains(new JDOField(jv_topic, "subjects"), jv_locator));
        expressions.add(new JDOEquals(new JDOField(jv_locator, "address"), jv_uri));
          
        // JDOQL: T.topicmap = TOPICMAP
        expressions.add(new JDOEquals(new JDOField(jv_topic, "topicmap"),
                                      new JDOObject(topicmap)));
      }

      return true;
    }
    
    return true;
  }
  
}
