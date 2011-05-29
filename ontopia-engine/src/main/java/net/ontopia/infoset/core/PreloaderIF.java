
package net.ontopia.infoset.core;

import java.io.IOException;

/**
 * INTERNAL: Interface for preloading locators.<p>
 */

public interface PreloaderIF {

  /**
   * INTERNAL: Preloads the resource pointed to by the given locator.
   *
   * @return Returns a locator that references the preloaded resource.
   */
  public LocatorIF preload(LocatorIF locator) throws IOException;

  /**
   * INTERNAL: Can be used to figure out if it is necessary to preload
   * the resource referenced by the locator.<p>
   */
  public boolean needsPreloading(LocatorIF locator);
  
}





