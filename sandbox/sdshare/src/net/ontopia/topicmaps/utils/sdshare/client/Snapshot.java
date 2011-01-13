
package net.ontopia.topicmaps.utils.sdshare.client;

/**
 * PUBLIC: Represents an entry in a snapshot feed.
 */
public class Snapshot {
  private String uri;
  private long timestamp;
  private SnapshotFeed feed;

  public Snapshot(SnapshotFeed feed) {
    this.feed = feed;
  }

  public String getSnapshotURI() {
    return uri;
  }

  public void setSnapshotURI(String uri) {
    this.uri = uri;
  }

  public long getUpdated() {
    return timestamp;
  }

  public void setUpdated(long timestamp) {
    this.timestamp = timestamp;
  }

  public SnapshotFeed getFeed() {
    return feed;
  }
}
