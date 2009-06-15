
// $Id: SubjectIdentifierPredicate.java,v 1.9 2008/06/13 08:17:54 geir.gronmo Exp $

package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.List;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.persistence.query.jdo.JDOBoolean;
import net.ontopia.persistence.query.jdo.JDOContains;
import net.ontopia.persistence.query.jdo.JDOEquals;
import net.ontopia.persistence.query.jdo.JDOField;
import net.ontopia.persistence.query.jdo.JDOObject;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.rdbms.SubjectIndicatorLocator;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Implements the 'subject-identifier(topic,locator)' predicate.
 */
public class SubjectIdentifierPredicate
  extends net.ontopia.topicmaps.query.impl.basic.SubjectIdentifierPredicate
  implements JDOPredicateIF {

  public SubjectIdentifierPredicate(TopicMapIF topicmap) {
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
          locator = new SubjectIndicatorLocator(new URILocator((String)args[1]));
        } catch (java.net.MalformedURLException e) {
          throw new InvalidQueryException("Not a valid URI: " + args[1]);
        }

        if (topic.getSubjectIdentifiers().contains(locator))
          expressions.add(JDOBoolean.TRUE);
        else
          expressions.add(JDOBoolean.FALSE);
          
      } else {          
        JDOValueIF jv_topic = builder.createJDOValue(args[0]);
        JDOValueIF jv_locator = builder.createJDOVariable("L", SubjectIndicatorLocator.class);
        JDOValueIF jv_uri = builder.createJDOValue(args[1]);
          
        // JDOQL: T.indicators.contains(L) && L.address = U
        expressions.add(new JDOContains(new JDOField(jv_topic, "indicators"), jv_locator));
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
