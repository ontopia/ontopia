
package net.ontopia.topicmaps.utils.sdshare.client;

import java.io.IOException;
import org.xml.sax.SAXException;

/**
 * INTERNAL: A front end from which one can get change and snapshot feeds.
 */
public interface ClientFrontendIF {

  public String getHandle();

  public SnapshotFeed getSnapshotFeed() throws IOException, SAXException;

  public FragmentFeed getFragmentFeed(long lastChange)
    throws IOException, SAXException;
  
}