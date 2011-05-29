
package net.ontopia.topicmaps.utils.ctm;

/**
 * INTERNAL: Represents a stored parse event, ready to be replayed.
 */
public interface ParseEventIF {

  public void replay(ParseEventHandlerIF handler);
  
}
