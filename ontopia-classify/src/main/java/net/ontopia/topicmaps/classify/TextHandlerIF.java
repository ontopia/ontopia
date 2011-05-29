
package net.ontopia.topicmaps.classify;

import java.util.Collection;

import net.ontopia.utils.*;

/**
 * INTERNAL: Callback interface used by format modules to tell the
 * classification framework about the structure of classifiable
 * content.<p>
 *
 * The calls to startRegion can be nested, but they must have
 * been unnested via calls to endRegion at the time when the end of
 * the classifiable content has been reached.
 */
public interface TextHandlerIF {

  /**
   * INTERNAL: Starts a new document region. Regions can be nested.
   */
  public void startRegion(String regionName);

  /**
   * INTERNAL: Text found in the classifiable content. Subsequent
   * calls to this method is allowed.
   */  
  public void text(char[] ch, int start, int length);

  /**
   * INTERNAL: Ends the current document region.
   */  
  public void endRegion();
    
}
