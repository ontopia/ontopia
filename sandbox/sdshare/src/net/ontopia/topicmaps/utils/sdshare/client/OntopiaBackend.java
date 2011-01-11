
package net.ontopia.topicmaps.utils.sdshare.client;

import java.net.URL;
import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ontopia.utils.StringUtils;
import net.ontopia.utils.CompactHashSet;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.xml.DefaultXMLReaderFactory;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.topicmaps.entry.TopicMaps;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.topicmaps.utils.KeyGenerator;

public class OntopiaBackend implements ClientBackendIF {
  static Logger log = LoggerFactory.getLogger(OntopiaBackend.class.getName());

  // FIXME: need to be able to get a reference here somehow.
  
  public void loadSnapshot(SyncEndpoint endpoint, Snapshot snapshot) {
    TopicMapStoreIF store = null;
    try {
      String url = snapshot.getFeedURI();
      LocatorIF base = URILocator.create(snapshot.getFeed().getPrefix());
      
      XTMTopicMapReader reader = new XTMTopicMapReader(new URL(url).openConnection().getInputStream(), base);
      store = getStore(endpoint.getHandle());
      TopicMapIF tm = store.getTopicMap();
      try {
        reader.importInto(tm);
        store.commit();
      } finally {
        store.abort();
      }
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    } finally {
      if (store != null)
        store.close();
    }
  }

  public void applyFragment(SyncEndpoint endpoint, Fragment fragment) {
    boolean committed = false;
    TopicMapStoreIF store = null;
    try {
      // (2) start a transaction      
      store = getStore(endpoint.getHandle());
      
      // (3) applying the fragment
      log.info("Applying fragment " + fragment);
      try {
        applyFragment(fragment.getParent().getPrefix(),
                      fragment,
                      store.getTopicMap());
      } catch (Exception e) {
        store.abort();
        throw new OntopiaRuntimeException(e);
      }
      
      // (4) commit
      store.commit();
    } finally {
      if (store != null)
        store.close(); // recycle the store
    }
  }

  // ===== INTERNAL IMPLEMENTATION CODE

  private TopicMapStoreIF getStore(String id) {
    return TopicMaps.createStore(id, false);
  }
  
  private void applyFragment(String prefix, Fragment fragment,
                             TopicMapIF topicmap) throws IOException {    
    // FIXME: before issue #3680 is cleared up, we don't know how to
    // interpret multiple SIs on a single fragment. for now we will
    // just assume that all entries have only a single SI.
    // http://projects.topicmapslab.de/issues/3680
    if (fragment.getTopicSIs().size() != 1)
      throw new RuntimeException("Fragment " + fragment.getFragmentURI() +
                                 " had wrong number of TopicSIs: " +
                                 fragment.getTopicSIs().size());

    log.info("TopicSI: " + fragment.getTopicSIs());
    
    // (1) get the fragment
    // FIXME: for now we only support XTM
    String url = fragment.getFragmentURI();
    LocatorIF base = URILocator.create(fragment.getParent().getPrefix());
    XTMTopicMapReader reader = new XTMTopicMapReader(new URL(url).openConnection().getInputStream(), base);
    reader.setFollowTopicRefs(false);
    TopicMapIF tmfragment = reader.read();
    log.info("Prefix: '" + prefix + "'");
    
    // (2) apply it
    LocatorIF si = URILocator.create(fragment.getTopicSIs().iterator().next());
    TopicIF ftopic = tmfragment.getTopicBySubjectIdentifier(si);
    TopicIF ltopic;
    if (ftopic != null)
      ltopic = MergeUtils.findTopic(topicmap, ftopic);
    else
      ltopic = topicmap.getTopicBySubjectIdentifier(si);

    log.info("ftopic: " + ftopic);
    if (ftopic != null) {
      for (LocatorIF iid : ftopic.getItemIdentifiers())
        log.info("ftopic.iid: " + iid);
    }
    log.info("ltopic: " + ltopic);

    // (a) check if we need to create the topic
    if (ltopic == null && ftopic != null)
      // the topic exists in the source, but not in the target, therefore
      // we create it
      ltopic = topicmap.getBuilder().makeTopic();

    // there might not be a fragment topic, which indicates that it's been
    // deleted. in this case we make a blank dummy that will cause everything
    // to be deleted, and the local topic to be deleted at the end.
    if (ftopic == null)
      ftopic = tmfragment.getBuilder().makeTopic();

    // (b) copy across all identifiers
    ltopic = MergeUtils.copyIdentifiers(ltopic, ftopic);

    // (c) merge all topics in the fragment into the target. this is
    // necessary so that all types, scoping topics, and associated
    // topics are actually present in the target when we update the
    // topic characteristics. (however, we can't merge in the current
    // topic, as the procedure for that one is a bit more complex.)
    for (TopicIF oftopic : tmfragment.getTopics())
      if (oftopic != ftopic)
        MergeUtils.mergeInto(topicmap, oftopic);

    // (d) sync the types
    // FIXME: how the hell do we do this?

    // (e) sync the names
    Map<String, ? extends ReifiableIF> keymap = makeKeyMap(ftopic.getTopicNames(), topicmap);
    syncCollection(ftopic, ltopic, keymap, ltopic.getTopicNames(), prefix);

    // (f) sync the occurrences
    keymap = makeKeyMap(ftopic.getOccurrences(), topicmap);
    syncCollection(ftopic, ltopic, keymap, ltopic.getOccurrences(), prefix);
    
    // (g) sync the associations
    keymap = makeKeyMap(getAssociations(ftopic), topicmap);
    syncCollection(ftopic, ltopic, keymap, getAssociations(ltopic), prefix);

    // (h) is the topic deleted
    if (ltopic.getTopicNames().isEmpty() &&
        ltopic.getOccurrences().isEmpty() &&
        ltopic.getRoles().isEmpty())
      ltopic.remove(); // empty topic, therefore remove
  }

  private Map<String, ? extends ReifiableIF>
  makeKeyMap(Collection<? extends ReifiableIF> objects, TopicMapIF othertm) {
    Map<String, ReifiableIF> keymap = new HashMap();
    for (ReifiableIF object : objects)
      keymap.put(KeyGenerator.makeKey(object, othertm), object);
    return keymap;
  }

  private void syncCollection(TopicIF ftopic, TopicIF ltopic,
                              Map<String,? extends ReifiableIF> keymap,
                              Collection<? extends ReifiableIF> lobjects,
                              String prefix) {
    log.info("-----");
    // check all local objects against the fragment
    lobjects = new ArrayList<ReifiableIF>(lobjects); // avoid concmodexc
    for (ReifiableIF lobject : lobjects) {
      String key = KeyGenerator.makeKey(lobject);
      ReifiableIF fobject = keymap.get(key);

      log.info("lobject: " + lobject);
      log.info("fobject: " + fobject);

      if (fobject == null) {
        // the source doesn't have this object. however, if it still has
        // item identifiers (other than ours), we can keep it. we do
        // need to remove any iids starting with the prefix, though.
        pruneItemIdentifiers(lobject, prefix);
        log.info("lobject.iids1: " + lobject.getItemIdentifiers());
        if (lobject.getItemIdentifiers().isEmpty())
          lobject.remove();
      } else {
        // the source has this object. we need to make sure the local
        // copy has the item identifier.
        addItemIdentifier(lobject, prefix);
        log.info("lobject.iids2: " + lobject.getItemIdentifiers());
        keymap.remove(key); // we've seen this one, so cross it off
      }
    }

    // copy the objects in the source which are not in the local copy
    // across to the local copy, adding item identifiers
    for (ReifiableIF fobject : keymap.values()) {
      ReifiableIF lobject = MergeUtils.mergeInto(ltopic, fobject);
      log.info("remaining: " + lobject);
      addItemIdentifier(lobject, prefix);
    }
  }

  private void pruneItemIdentifiers(TMObjectIF object, String prefix) {
    for (LocatorIF iid : object.getItemIdentifiers())
      if (iid.getAddress().startsWith(prefix))
        object.removeItemIdentifier(iid);
  }

  // FIXME: this doesn't really add; it just ensures that the object has
  // one. ie: method is idempotent.
  private void addItemIdentifier(TMObjectIF object, String prefix) {
    for (LocatorIF iid : object.getItemIdentifiers())
      if (iid.getAddress().startsWith(prefix))
        return; // it already has one; we're done

    // it doesn't really matter what the iid is, so long as it is
    // unique and starts with the right prefix. we therefore take what
    // we assume is the shortest path to the goal.
    TopicMapIF topicmap = object.getTopicMap();
    LocatorIF base = URILocator.create(prefix);
    String objectid = object.getObjectId();
    LocatorIF iid = base.resolveAbsolute("#sd" + objectid);
    while (topicmap.getObjectByItemIdentifier(iid) != null)
      iid = base.resolveAbsolute("#sd" + objectid + '-' +
                                 StringUtils.makeRandomId(5));

    object.addItemIdentifier(iid);
  }

  private Collection<ReifiableIF> getAssociations(TopicIF topic) {
    Collection<ReifiableIF> assocs = new ArrayList<ReifiableIF>();
    for (AssociationRoleIF role : topic.getRoles())
      assocs.add(role.getAssociation());
    return assocs;
  }  
  
}