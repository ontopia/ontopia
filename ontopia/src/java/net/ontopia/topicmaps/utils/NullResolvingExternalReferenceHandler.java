
// $Id: NullResolvingExternalReferenceHandler.java,v 1.3 2002/08/28 08:47:34 grove Exp $

package net.ontopia.topicmaps.utils;

import net.ontopia.topicmaps.xml.ExternalReferenceHandlerIF;
import net.ontopia.infoset.core.LocatorIF;

/**
 * PUBLIC: An implementation of ExternalReferenceHandlerIF that
 * prevents the traversal of external references by returning null
 * from all methods.<p>
 *
 * @since: 1.3.4
 */

public class NullResolvingExternalReferenceHandler
  implements ExternalReferenceHandlerIF {

  /**
   * PUBLIC: always returns null
   */
  public LocatorIF externalTopicMap(LocatorIF parm1) {
    return null;
  }

  /**
   * PUBLIC: always returns null
   */
  public LocatorIF externalTopic(LocatorIF parm1) {
    return null;
  }
}
