
package net.ontopia.topicmaps.utils.sdshare.client;

import java.io.IOException;
import org.xml.sax.SAXException;

/**
 * PUBLIC: Represents information about an SDshare source to
 * synchronize from.
 */
public class SyncSource {
  private String url; // URL of collection feed
  private CollectionFeed feed;
  private int checkInterval; // in seconds!
  /**
   * The time (on this machine) of the last time we checked this source.
   * In milliseconds since epoch.
   */
  private long lastCheck;
  /**
   * The timestamp on the last change processed from this source, as given
   * by the server. In milliseconds since epoch.
   */
  private long lastChange;
  
  public SyncSource(String url, int checkInterval) {
    this.url = url;
    this.checkInterval = checkInterval;
  }
  
  public String getURL() { // of collection feed
    return url;
  }

  public String getSnapshotFeedURL() throws IOException, SAXException {
    return getFeed().getSnapshotFeed();
  }

  public String getFragmentFeedURL() throws IOException, SAXException {
    return getFeed().getFragmentFeed();
  }

  private CollectionFeed getFeed() throws IOException, SAXException {
    if (feed == null)
      feed = FeedReaders.readCollectionFeed(url);
    return feed;
  }

  /**
   * Returns the timestamp of the last change processed from this
   * source, as given in the Atom feed.
   */
  public long getLastChange() {
    return this.lastChange;
  }

  /**
   * Updates the timestamp of the last change *iff* this timestamp is
   * later than the latest one seen so far. The rationale is that we
   * are not absolutely sure what order fragments are returned in.
   */
  public void setLastChange(long lastChange) {
    if (lastChange > this.lastChange)
      this.lastChange = lastChange;
  }

  /**
   * Called to let the source know that it is now updated.
   */
  public void updated() {
    this.lastCheck = System.currentTimeMillis();
  }

  /**
   * Returns true iff the current time >= time of last check + check
   * interval.
   */
  public boolean isTimeToCheck() {
    return System.currentTimeMillis() >= lastCheck + (checkInterval * 1000);
  }
}
