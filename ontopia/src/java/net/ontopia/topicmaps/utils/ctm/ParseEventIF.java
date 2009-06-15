
// $Id: ParseEventIF.java,v 1.1 2009/02/09 08:20:16 lars.garshol Exp $

package net.ontopia.topicmaps.utils.ctm;

/**
 * INTERNAL: Represents a stored parse event, ready to be replayed.
 */
public interface ParseEventIF {

  public void replay(ParseEventHandlerIF handler);
  
}