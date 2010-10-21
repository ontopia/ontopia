
package net.ontopia.topicmaps.utils.sdshare;

import java.util.List;
import java.util.ArrayList;
  
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.events.TopicMapListenerIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;

// FIXME: list of changes grows without bound
// FIXME: list of changes is not persistent

/**
 * INTERNAL: Event listener class which maintains a list of changed
 * topics, ready to be output into the fragment feed.
 */
public class TopicMapTracker implements TopicMapListenerIF {
  private TopicMapReferenceIF ref;
  private List<ChangedTopic> changes;

  public TopicMapTracker(TopicMapReferenceIF ref) {
    this.ref = ref;
    this.changes = new ArrayList();
  }

  public String getTopicMapId() {
    return ref.getId();
  }

  public TopicMapReferenceIF getReference() {
    return ref;
  }
  
  public List<ChangedTopic> getChangeFeed() {
    return changes;
  }

  public long getLastChanged() {
    if (changes.isEmpty())
      return 0; // ie: it hasn't ever changed
    else
      return changes.get(changes.size() - 1).getTimestamp();
  }
  
  private synchronized void modified(TMObjectIF snapshot, boolean deleted) {
    ChangedTopic o;
    if (deleted)
      o = new DeletedTopic(snapshot);
    else
      o = new ChangedTopic(snapshot.getObjectId());

    int pos = changes.lastIndexOf(o);
    if (pos == -1)
      changes.add(o);
    else if (pos == (changes.size() - 1))
      changes.set(pos, o);
    else {
      changes.remove(pos);
      changes.add(o);
    }
  }
  
  // --- TopicMapListenerIF implementation

  public void objectAdded(TMObjectIF snapshot) {
    modified(snapshot, false);
  }

  public void objectModified(TMObjectIF snapshot) {
    modified(snapshot, false);
  }

  public void objectRemoved(TMObjectIF snapshot) {
    modified(snapshot, true);
  }
}