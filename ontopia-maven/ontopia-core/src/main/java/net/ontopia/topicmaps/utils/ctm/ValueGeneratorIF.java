
package net.ontopia.topicmaps.utils.ctm;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;

/**
 * Common interface for generating both literal values and topics.
 */
public interface ValueGeneratorIF {

  /**
   * Returns true if this generator produces a topic.
   */
  public boolean isTopic();
  
  public String getLiteral();
  
  public LocatorIF getDatatype();

  /**
   * Returns a locator if the literal is a locator. Otherwise it
   * throws an exception.
   */
  public LocatorIF getLocator();
  
  public ValueGeneratorIF copy();

  public TopicIF getTopic();
  
}
