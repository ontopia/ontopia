
package net.ontopia.topicmaps.utils.sdshare.client;

import java.io.IOException;
import org.xml.sax.SAXException;

public class AtomFrontend implements ClientFrontendIF {
  private String handle; // handle (URL|id) of source collection
  private CollectionFeed feed;

  public AtomFrontend(String handle) {
    this.handle = handle;
  }
  
  public String getHandle() {
    return handle;
  }

  public SnapshotFeed getSnapshotFeed() throws IOException, SAXException {
    String feedurl = getFeed().getSnapshotFeed();
    return FeedReaders.readSnapshotFeed(feedurl);
  }

  public FragmentFeed getFragmentFeed(long lastChange)
    throws IOException, SAXException {
    String feedurl = getFeed().getFragmentFeed();
    return FeedReaders.readFragmentFeed(feedurl, lastChange);
  }

  private CollectionFeed getFeed() throws IOException, SAXException {
    if (feed == null)
      feed = FeedReaders.readCollectionFeed(handle);
    return feed;
  }
  
}