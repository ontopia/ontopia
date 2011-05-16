
// $Id: NoFollowTopicRefExternalReferenceHandler.java,v 1.2 2006/11/08 11:35:10 larsga Exp $

package net.ontopia.topicmaps.utils;

import net.ontopia.topicmaps.xml.ExternalReferenceHandlerIF;
import net.ontopia.infoset.core.LocatorIF;

/**
 * PUBLIC: An implementation of ExternalReferenceHandlerIF that
 * prevents the traversal of external topic references. External topic
 * maps will be resolved.<p>
 *
 * @since 3.2
 */

public class NoFollowTopicRefExternalReferenceHandler
  implements ExternalReferenceHandlerIF {

  /**
   * PUBLIC: External topic maps are resolved.
   */
  public LocatorIF externalTopicMap(LocatorIF loc) {
    return loc;
  }

  /**
   * PUBLIC: External topics are not resolved.
   */
  public LocatorIF externalTopic(LocatorIF loc) {
    return null;
  }
}
