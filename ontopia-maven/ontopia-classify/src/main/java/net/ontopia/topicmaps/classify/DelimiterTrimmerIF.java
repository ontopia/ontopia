
// $Id: DelimiterTrimmerIF.java,v 1.1 2006/11/17 08:50:19 grove Exp $

package net.ontopia.topicmaps.classify;

import java.util.Collection;

import net.ontopia.utils.*;

/**
 * INTERNAL: 
 */
public interface DelimiterTrimmerIF {
  
  public int trimStart(String token);
  
  public int trimEnd(String token);
  
}
