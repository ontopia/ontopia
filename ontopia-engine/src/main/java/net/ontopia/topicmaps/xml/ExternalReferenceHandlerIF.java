
package net.ontopia.topicmaps.xml;

import net.ontopia.infoset.core.LocatorIF;

/**
 * PUBLIC: An interface that can be implemented by handlers that would
 * like to intercept external references in topic map documents.</p>
 */
public interface ExternalReferenceHandlerIF {

  /**
   * PUBLIC: Receive notification of a reference to an external topic
   * map.</p>
   *
   * @return The locator which should be used to resolve the
   * reference; if null is returned it will be interpreted as that the
   * reference should not be traversed.
   */
  public LocatorIF externalTopicMap(LocatorIF address);

  /**
   * PUBLIC: Receive notification of a reference to an external
   * topic.</p>
   *
   * @return The locator which should be used to resolve the
   * reference; if null is returned it will be interpreted as that the
   * reference should not be traversed.
   */
  public LocatorIF externalTopic(LocatorIF address);

}




