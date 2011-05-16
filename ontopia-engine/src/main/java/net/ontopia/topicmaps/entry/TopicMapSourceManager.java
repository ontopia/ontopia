
// $Id: TopicMapSourceManager.java,v 1.36 2007/08/30 09:44:01 geir.gronmo Exp $

package net.ontopia.topicmaps.entry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.ontopia.utils.CompactHashSet;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.TopicMapStoreIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: This is the primary implementation of the
 * TopicMapRepositoryIF interface. This class also implements the
 * TopicMapSourceIF interface making it a topic map source that
 * behaves as a facade for the results of multiple topic map
 * sources.<p>
 * 
 * The reference keys used are the reference ids retrieved from
 * <code>reference.getId()</code>. An exception will be thrown if
 * there are duplicate reference keys.<p>
 * 
 * The sources that are added to the repository must have unique
 * ids. If the source id is not specified, the source cannot be looked
 * up by id.<p>
 */

public class TopicMapSourceManager implements TopicMapRepositoryIF {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(TopicMapSourceManager.class.getName());

  protected Set<TopicMapSourceIF> sources = new CompactHashSet<TopicMapSourceIF>();

  protected Map<String, TopicMapSourceIF> smap = new HashMap<String, TopicMapSourceIF>();

  protected boolean refreshed = false;

  protected Map<String, TopicMapReferenceIF> keyrefs = new HashMap<String, TopicMapReferenceIF>(); // key: ref

  protected Map<TopicMapReferenceIF, String> refkeys = new HashMap<TopicMapReferenceIF, String>(); // ref: key

  public TopicMapSourceManager() {
    super();
  }

  public TopicMapSourceManager(TopicMapSourceIF source) {
    addSource(source);
  }

  public TopicMapSourceManager(Collection<TopicMapSourceIF> sources) {
    Iterator<TopicMapSourceIF> iter = sources.iterator();
    while (iter.hasNext())
      addSource(iter.next());
  }

  public synchronized Collection<TopicMapReferenceIF> getReferences() {
    if (!refreshed)
      refresh();
    return keyrefs.values();
  }

  public synchronized Collection<String> getReferenceKeys() {
    if (!refreshed)
      refresh();
    return keyrefs.keySet();
  }

  public synchronized TopicMapReferenceIF getReferenceByKey(String key) {
    if (!refreshed)
      refresh();
    return keyrefs.get(key);
  }

  public synchronized String getReferenceKey(TopicMapReferenceIF ref) {
    return refkeys.get(ref);
  }

  public TopicMapStoreIF createStore(String refkey, boolean readonly) {
    TopicMapReferenceIF ref = getReferenceByKey(refkey);
    if (ref == null)
      throw new OntopiaRuntimeException("Topic map reference '" + refkey + "' not found.");
    try {
      return ref.createStore(readonly);
    } catch (java.io.IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  public synchronized TopicMapSourceIF getSourceById(String source_id) {
    return smap.get(source_id);
  }

  public synchronized Collection<TopicMapSourceIF> getSources() {
    return Collections.unmodifiableCollection(sources);
  }

  public synchronized void addSource(TopicMapSourceIF source) {
    // Add source to set of sources
    if (sources.add(source)) {
      refreshed = false;
      // Add to source id map, if source has id.
      String id = source.getId();
      if (id != null) {
        if (!smap.containsKey(id))
          smap.put(id, source);
        else
          throw new OntopiaRuntimeException("Source with id already exists: "
              + id);
      }
    }
  }

  public synchronized void removeSource(TopicMapSourceIF source) {
    // Remove source from set of sources
    if (sources.remove(source)) {
      refreshed = false;
      // remove from source id map if, if source has id.
      String id = source.getId();
      if (id != null && smap.containsKey(id))
        smap.remove(id);
    }
  }

  public synchronized void refresh() {
    // Clear reference map
    refreshed = false;
    keyrefs.clear();
    refkeys.clear();

    // Refresh sources and rebuild reference map
    Iterator<TopicMapSourceIF> siter = sources.iterator();
    while (siter.hasNext()) {
      TopicMapSourceIF source = siter.next();
      try {
        // Refresh source
        source.refresh();
        Iterator<TopicMapReferenceIF> riter = source.getReferences().iterator();
        while (riter.hasNext()) {
          TopicMapReferenceIF ref = riter.next();
          // Create reference key and update reference map
          String refkey = createReferenceKey(ref);
          // ! System.out.println("KEY: " + refkey + " <= " + ref);
          keyrefs.put(refkey, ref);
          refkeys.put(ref, refkey);
        }
      } catch (Throwable t) {
        log.error("Could not refresh topic map source " + source + ". Ignoring.", t);
      }
    }
    refreshed = true;
  }

  /**
   * INTERNAL: Creates a unique id. This method is used by the refresh method to
   * generate unique ids for topic map references.
   */
  protected String createReferenceKey(TopicMapReferenceIF ref) {
    // Complain if source id contains periods
    // ! String srcid = source.getId();
    // ! if (srcid.indexOf('.') != -1)
    // ! throw new OntopiaRuntimeException("Source id cannot contain periods." +
    // srcid);
    TopicMapSourceIF source = ref.getSource();
    if (source == null)
      throw new OntopiaRuntimeException(
          "The reference is not attached to a source: " + ref);

    // Complain if key already used
    String refkey = ref.getId();
    if (keyrefs.containsKey(refkey))
      throw new OntopiaRuntimeException("Duplicate reference keys: " + refkey);

    return refkey;
  }

  public synchronized void close() {
    Iterator<TopicMapReferenceIF> iter = refkeys.keySet().iterator();
    while (iter.hasNext()) {
      TopicMapReferenceIF ref = iter.next();
      try {
        if (ref.isOpen())
          ref.close();
      } catch (Exception e) {
        log.warn("Problems occurred when closing reference " + ref, e);
      }
    }
  }

  // -- legacy methods

  /**
   * INTERNAL: Gets the reference that has the given id.
   * 
   * @deprecated replaced by getReferenceByKey(String)
   */
  public TopicMapReferenceIF getReferenceById(String reference_id) {
    return getReferenceByKey(reference_id);
  }

  /**
   * INTERNAL: Returns true if the manager manages a reference with the given id.
   * 
   * @deprecated use 'getReferenceByKey(key) != null' instead
   */
  public boolean hasId(String reference_id) {
    return getReferenceByKey(reference_id) != null;
  }

  /**
   * INTERNAL: Gets the ids of the references managed by this manager.
   * 
   * @deprecated replaced by getReferenceKeys()
   */
  public Collection<String> getIds() {
    return getReferenceKeys();
  }

}
