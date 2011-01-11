
package net.ontopia.topicmaps.utils.sdshare.client;

/**
 * PUBLIC: Represents a collection feed. 
 */
public class CollectionFeed {
  private String fragmentfeed;
  private String snapshotfeed;

  public CollectionFeed() {
  }
  
  public String getFragmentFeed() {
    return fragmentfeed;
  }

  public void setFragmentFeed(String uri) {
    fragmentfeed = uri;
  }
    
  public String getSnapshotFeed() {
    return snapshotfeed;
  }

  public void setSnapshotFeed(String uri) {
    snapshotfeed = uri;
  }
}
