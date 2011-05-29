
package net.ontopia.infoset.core;

import java.io.File;
import net.ontopia.utils.URIUtils;

/**
 * PUBLIC: Utilities for working with LocatorIFs.
 *
 * @since 3.4.1
 */
public class Locators {

  private Locators() {
    // do not call me
  }
  
  /**
   * PUBLIC: Return a locator created from a uri.
   */
  public static LocatorIF getURILocator(String uri) {
    return URIUtils.getURILocator(uri);
  }
  
  /**
   * PUBLIC: Given a File object, produce a corresponding URILocator
   * that uses the URL scheme.
   */
  public static LocatorIF getURILocator(File file) {
    return URIUtils.getFileURI(file);
  }
  
}
