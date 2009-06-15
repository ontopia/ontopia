
// $Id: TokenizerIF.java,v 1.3 2006/11/15 13:29:00 grove Exp $

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
