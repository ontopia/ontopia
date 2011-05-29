
package net.ontopia.topicmaps.classify;

import java.util.Collection;

import net.ontopia.utils.*;

/**
 * INTERNAL: 
 */
public interface TokenizerIF {

  public void setText(String text);
  
  public boolean next();
  
  public String getToken();
  
}
