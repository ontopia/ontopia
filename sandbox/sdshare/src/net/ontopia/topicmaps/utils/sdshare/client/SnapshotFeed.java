
package net.ontopia.topicmaps.utils.sdshare.client;

import java.util.List;
import java.util.ArrayList;

/**
 * PUBLIC: Represents snapshot feeds.
 */
public class SnapshotFeed {
  private List<Snapshot> snapshots;
  private String prefix;

  public SnapshotFeed() {
    this.snapshots = new ArrayList<Snapshot>();
  }
    
  public List<Snapshot> getSnapshots() {
    return snapshots;
  }

  public void addSnapshot(Snapshot snapshot) {
    snapshots.add(snapshot);
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }
}
