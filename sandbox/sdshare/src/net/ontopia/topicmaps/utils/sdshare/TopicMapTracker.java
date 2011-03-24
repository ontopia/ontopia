
package net.ontopia.topicmaps.utils.sdshare;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.io.Writer;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;

import net.ontopia.utils.StringUtils;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.events.TopicMapListenerIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// FIXME: dribble file is unnecessarily big (in bytes)
// it might be better to use a binary format for the dribble file.
// that way it gets smaller, and we don't have to parse it when
// loading.

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
  /**
   * This is the file we use to persist change information.
   */
  private String dribblefile;
  private Writer dribbler;

  static Logger log = LoggerFactory.getLogger(TopicMapTracker.class.getName());
  
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

  public List<ChangedTopic> getChangeFeed(long since) {
    expireOldChanges();

    int pos = binarySearch(since); // find position of this timestamp
    
    // now 'pos' will either point to a change with this timestamp
    // (possibly somewhere in a run of changes with equal timestamps),
    // or to the change on the edge between before and after. since we
    // need to include all changes with times equal to this change we
    // scan upwards until the timestamps are earlier than 'since'
    while (pos > 0 && changes.get(pos).getTimestamp() >= since)
      pos--;
    
    // it's possible that the above gives us a position where we need to
    // run further out, so we try that too, to make sure
    while (pos < changes.size() && changes.get(pos).getTimestamp() < since)
      pos++;

    if (pos == changes.size())
      return Collections.EMPTY_LIST;
    else
      return changes.subList(pos, changes.size());
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

  // note: don't try to call this after listeners have been registered.
  // if you do invariants may be violated, causing confusion.
  public void setDribbleFile(String dribblefile) throws IOException {
    if (!(ref instanceof RDBMSTopicMapReference)) {
      log.info("Topic map is not an RDBMS topic map. Not making dribble file " +
               dribblefile);
      return;
    }
    this.dribblefile = dribblefile;
    loadDribbleFile(); // dribbler is set up in here
  }
  
  private synchronized void modified(ChangedTopic o) {
    expireOldChanges();
    int pos = findDuplicate(o);
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

    if (dribbler != null) {
      try {
        // FIXME: we may not need to write *every* duplicate change to
        // the file. may save some time to skip fast dupes of same ID.
        dribbler.write(o.getSerialization() + "\n");
        // FIXME: flushing all the time makes loading metadata.xtm take
        // 12 secs instead of 9 secs. so there is a considerable cost here.
        // must consider whether this can be improved, and whether it needs
        // to be.
        dribbler.flush(); 
      } catch (IOException e) {
        log.error("Couldn't write to dribble file", e);
        // FIXME: attempt to clean up file handle, then close it if this
        // is not possible.
      }
    }
  }

  private int findDuplicate(ChangedTopic o) {
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
    int high = changes.size() - 1;

    // very often, the topic changed is the same as the previous, because
    // of how the event system works. therefore we cheat by checking the
    // last position first.
    if (changes.get(high).equals(o))
      return high;

    long key = o.getTimestamp();
    int pos = binarySearch(key);
    int low = 0;
    
    // we've now either given up, or found an entry with the same time.
    // however, there can be many entries with the same time, so we need
    // to scan both up and down to find it.    
    int origpos = pos;
    ChangedTopic other = changes.get(pos);
    while (!other.equals(o) && other.getTimestamp() == key && pos > low) {
      pos--;
      other = changes.get(pos);
    }
    if (other.equals(o))
      return pos;

    pos = origpos + 1;
    other = changes.get(pos);
    while (!other.equals(o) && other.getTimestamp() == key && pos < high) {
      pos++;
      other = changes.get(pos);
    }
    if (changes.get(pos).equals(o))
      return pos;
    return -1; // it's not here
  }

  /**
   * Returns the index of a change (any change) with this timestamp.
   */
  private int binarySearch(long key) {
    if (changes.isEmpty())
      return 0;
    
    int low = 0;
    int high = changes.size() - 1;
    int pos = (low + high) / 2;

    ChangedTopic other = changes.get(pos);
    long time = other.getTimestamp();
    while (time != key && low < high) {
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
    }

    return pos;
  }

  private synchronized void expireOldChanges() {
    // is it time to expire yet?
    if (System.currentTimeMillis() - lastExpired < Math.min(100, expirytime))
      return;
    
    // find the position of the first change to keep
    long expireolderthan = System.currentTimeMillis() - expirytime;
    int keepfrom;
    for (keepfrom = 0;
         keepfrom < changes.size() &&
           changes.get(keepfrom).getTimestamp() < expireolderthan;
         keepfrom++)
      ;

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

  private void loadDribbleFile() throws IOException {
    // file format: line-based. two types of record.
    // C objectid timestamp\n
    // D objectid timestamp ("s," <sid>)* ("l," <slo>)* ("i," <iid>)+\n

    // (1) get ready
    long expireolderthan = System.currentTimeMillis() - expirytime;
    
    // (2) read in old changes, while discarding old ones and duplicates
    try {
      BufferedReader reader = new BufferedReader(new FileReader(dribblefile));
      String line = reader.readLine();
      while (line != null) {
        boolean changed = line.charAt(0) == 'C';
        int pos = line.indexOf(" ", 2);
        String objid = line.substring(2, pos);
        long timestamp = 0;

        ChangedTopic change = null;
        if (changed) {
          timestamp = Long.parseLong(line.substring(pos + 1));
          change = new ChangedTopic(objid, timestamp);
        } else {
          int pos2 = line.indexOf(" ", pos + 1);
          if (pos2 != -1) {
            // if the deleted topic had no identity we can't make a
            // change object for it, since there's no way of
            // connecting it to anything else. we can't have included
            // it in any SDshare feed, anyway, so we can safely drop
            // the event. especially as the topic's gone now, anyway.
            timestamp = Long.parseLong(line.substring(pos + 1, pos2));
            String[] ids = StringUtils.split(line.substring(pos2 + 1));

            Collection<LocatorIF> sids = new ArrayList<LocatorIF>();
            Collection<LocatorIF> iids = new ArrayList<LocatorIF>();
            Collection<LocatorIF> slos = new ArrayList<LocatorIF>();
            for (int ix = 0; ix < ids.length; ix++) {
              LocatorIF loc = URILocator.create(ids[ix].substring(1));
              if (ids[ix].charAt(0) == 's')
                sids.add(loc);
              else if (ids[ix].charAt(0) == 'l')
                slos.add(loc);
              else if (ids[ix].charAt(0) == 'i')
                iids.add(loc);
              else
                throw new RuntimeException("Unknown identifier type in '" +
                                           ids[ix] + "'");
            }

            change = new DeletedTopic(objid, timestamp, sids, slos, iids);
          }
        }
        
        if (timestamp > expireolderthan && change != null)
          modified(change);
      
        line = reader.readLine();
      }
      reader.close();
    } catch (FileNotFoundException e) {
      // we assume this means that the file hasn't been created yet, so
      // we just warn, create the file, and carry on
      log.warn("No dribble file found at '" + dribblefile + "', creating one");
    }
    
    // (3) write out clean dribble file
    dribbler = new FileWriter(dribblefile); // *don't* append
    for (ChangedTopic ch : changes)
      dribbler.write(ch.getSerialization() + "\n");
    dribbler.flush(); // commit to disk
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