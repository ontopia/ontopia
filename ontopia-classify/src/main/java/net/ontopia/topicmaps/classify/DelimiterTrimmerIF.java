
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
