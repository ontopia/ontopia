
package net.ontopia.topicmaps.utils.sdshare;

import java.util.List;
import java.util.ArrayList;
  
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.events.TopicMapListenerIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;

// TESTING: could we build some tests specifically for the tracker?
// I suppose we could.

// FIXME: cost of duplicate suppression is O(n) per event, so O(n^2)
// for n events
// POSSIBLE SOLUTION:
// add a hashmap from object id to ChangedTopic. use this to find the
// modification time, then do a binary search to find the actual
// object in the sorted list. this should be O(log n) (or O(sqrt(n))),
// and thus at least potentially fast enough. however, leery of doing
// this without at least *some* automated tests.

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
      // if the found change is the last one, then just replace
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