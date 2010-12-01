
package net.ontopia.topicmaps.utils.sdshare;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
  
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.events.TopicMapListenerIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;

// FIXME: list of changes is not persistent
// one way to solve this is by a dribble list which is loaded and
// processed in the normal way on startup. however, this could cause
// delays at startup if the list is long. might be worth trying,
// though. must also rewrite the dribble file on startup.

/**
 * INTERNAL: Event listener class which maintains a list of changed
 * topics, ready to be output into the fragment feed.
 */
public class TopicMapTracker implements TopicMapListenerIF {
  private TopicMapReferenceIF ref;
  private List<ChangedTopic> changes;
  private Map<String, ChangedTopic> idmap;
  private long lastExpired; // how long (in ms) since we last expired changes
  /**
   * Number of milliseconds changes stay in changes list before they
   * expire.
   */
  private long expirytime;

  public TopicMapTracker(TopicMapReferenceIF ref) {
    this(ref, StartUpServlet.DEFAULT_EXPIRY_TIME);
  }
  
  public TopicMapTracker(TopicMapReferenceIF ref, long expirytime) {
    this.ref = ref;
    this.changes = new ArrayList();
    this.idmap = new HashMap();
    this.expirytime = expirytime;
  }

  public String getTopicMapId() {
    return ref.getId();
  }

  public TopicMapReferenceIF getReference() {
    return ref;
  }
  
  public List<ChangedTopic> getChangeFeed() {
    expireOldChanges();
    return changes;
  }

  public long getLastChanged() {
    if (changes.isEmpty())
      return 0; // ie: it hasn't ever changed
    else
      return changes.get(changes.size() - 1).getTimestamp();
  }

  // needed for test
  public void setExpiryTime(long expirytime) {
    this.expirytime = expirytime;
  }
  
  private synchronized void modified(ChangedTopic o) {
    try {
      expireOldChanges();
    } catch (Exception e) {
      e.printStackTrace();
    }
    int pos = findDuplicate(o);
    //System.out.println("Change to: " + o.getObjectId() + " at " + pos);
    if (pos == -1)
      changes.add(o);
    else if (pos == (changes.size() - 1))
      // if the found change is the last one, then just replace
      changes.set(pos, o);
    else {
      changes.remove(pos);
      changes.add(o);
    }

    idmap.put(o.getObjectId(), o);
  }

  private int findDuplicate(ChangedTopic o) {
    //return changes.lastIndexOf(o);

    // we find the duplicate by looking up the previous change in the idmap
    // and finding its timestamp. we then use the timestamp to do a binary
    // search of the list of changes to locate it there.

    // a different approach might be to store the list index in each change
    // so that we can find it directly. this requires more memory, however,
    // and means we have to reindex when discarding changes. leaving it as
    // a possibility for later.
    
    ChangedTopic original = idmap.get(o.getObjectId());
    if (original == null)
      return -1; // it's not in the list
    return binarySearch(original);
  }

  private int binarySearch(ChangedTopic o) {
    //System.out.println("===== Seeking " + o);
    long key = o.getTimestamp();
    int low = 0;
    int high = changes.size() - 1;
    int pos = (low + high) / 2;

    // very often, the topic changed is the same as the previous, because
    // of how the event system works. therefore we cheat by checking the
    // last position first.
    if (changes.get(high).equals(o)) {
      //System.out.println("It was the last one");
      return high;
    }

    // ok, it wasn't there, so we really do need to search
    //System.out.println("[" + low + " ... " + pos + " ... " + high + "]");

    ChangedTopic other = changes.get(pos);
    long time = other.getTimestamp();
    while (time != key && low < high) {
      //System.out.println("failed: "  + other);
      
      int oldpos = pos;
      if (key < time) { // we need to go towards the start
        high = pos - 1; // must be before this one
        pos = (low + pos) / 2;
      } else { // we need to go further back
        low = pos + 1; // must be further out
        pos = (high + pos) / 2;        
      }
      if (pos == oldpos)
        pos++; // don't stay in same pos, go higher

      other = changes.get(pos);
      time = other.getTimestamp();      
      //System.out.println("[" + low + " ... " + pos + " ... " + high + "]");
    }

    //System.out.println("pos " + pos + " holds "  + other);
    
    // we've now either given up, or found an entry with the same time.
    // however, there can be many entries with the same time, so we need
    // to scan both up and down to find it.    
    int origpos = pos;

    // other is already set correctly from loop above
    while (!other.equals(o) && other.getTimestamp() == key && pos > low) {
      pos--;
      other = changes.get(pos);
      //System.out.println("pos " + pos + " holds " + other);
    }
    if (other.equals(o)) {
      //System.out.println("FOUND at " + pos);
      return pos;
    }
    pos = origpos + 1;
    other = changes.get(pos);
    while (!other.equals(o) && other.getTimestamp() == key && pos < high) {
      //System.out.println("pos " + pos + " holds " + other);
      pos++;
      other = changes.get(pos);
    }
    //System.out.println("FINAL: pos " + pos + " holds "  + other);
    if (changes.get(pos).equals(o))
      return pos;
    return -1; // it's not here
  }

  private synchronized void expireOldChanges() {
    // is it time to expire yet?
    //System.out.println("time since last expiry: " + (System.currentTimeMillis() - lastExpired));
    if (System.currentTimeMillis() - lastExpired < Math.min(100, expirytime))
      return;
    
    // find the position of the first change to keep
    long expireolderthan = System.currentTimeMillis() - expirytime;
    //System.out.println("expiring changes older than " + expireolderthan);
    int keepfrom;
    for (keepfrom = 0;
         keepfrom < changes.size() &&
           changes.get(keepfrom).getTimestamp() < expireolderthan;
         keepfrom++)
      ;
    //System.out.println("keeping everything from position " + keepfrom +
    //                   " to " + changes.size() + "; " +
    //                   ((keepfrom < changes.size()) ? changes.get(keepfrom) : null));

    // can we efficiently remove a subrange of the array? no, we can't.
    // List doesn't have a method for it. ArrayList does, but it's protected.
    // therefore we do it brute-force, and hope this is efficient enough.
    if (keepfrom > 0) {
      if (keepfrom == changes.size())
        changes.clear(); // throw away everything
      else
        changes = new ArrayList(changes.subList(keepfrom, changes.size()));
    }
    
    // update timestamp
    lastExpired = System.currentTimeMillis();
  }
  
  // --- TopicMapListenerIF implementation

  public void objectAdded(TMObjectIF snapshot) {
    modified(new ChangedTopic(snapshot.getObjectId()));
  }

  public void objectModified(TMObjectIF snapshot) {
    modified(new ChangedTopic(snapshot.getObjectId()));
  }

  public void objectRemoved(TMObjectIF snapshot) {
    modified(new DeletedTopic(snapshot));
  }
  
}