
// $Id: IRIAsArgumentGenerator.java,v 1.1 2009/02/27 12:01:16 lars.garshol Exp $

package net.ontopia.topicmaps.utils.ctm;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.utils.PSI;

/**
 * INTERNAL: A special generator that's used when an IRI is passed as
 * an argument to a template because this can be either a topic
 * reference or an IRI literal, and we don't know which.
 */
public class IRIAsArgumentGenerator implements ValueGeneratorIF {
  private ParseContextIF context;
  private LocatorIF locator;
  
  public IRIAsArgumentGenerator(ParseContextIF context, LocatorIF locator) {
    this.context = context;
    this.locator = locator;
  }

  public boolean isTopic() {
    return true;
  }  
  
  public TopicIF getTopic() {
    return context.makeTopicBySubjectIdentifier(locator);
  }

  public ValueGeneratorIF copy() {
    return this; // should be OK
  }
  
  public String getLiteral() {
    return locator.getAddress();
  }
  
  public LocatorIF getDatatype() {
    return PSI.getXSDURI();
  }

  public LocatorIF getLocator() {
    return locator;
  }
}
