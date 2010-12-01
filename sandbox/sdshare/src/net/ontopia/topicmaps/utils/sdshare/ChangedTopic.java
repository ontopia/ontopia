
package net.ontopia.topicmaps.utils.sdshare;

/**
 * INTERNAL: Represents a change to a single topic.
 */
public class ChangedTopic {
  private String objid;
  private long timestamp; // millisecs since standard Java epoch

  public ChangedTopic(String objid) {
    this.objid = objid;
    this.timestamp = System.currentTimeMillis();
  }

  public String getObjectId() {
    return objid;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public boolean isDeleted() {
    return false;
  }

  public int hashCode() {
    return objid.hashCode();
  }

  public boolean equals(Object o) {
    if (o instanceof ChangedTopic)
      return ((ChangedTopic) o).getObjectId().equals(objid);
    return false;
  }

  public String toString() {
    return "[ChangedTopic " + objid + " at " + timestamp + "]";
  }
}
