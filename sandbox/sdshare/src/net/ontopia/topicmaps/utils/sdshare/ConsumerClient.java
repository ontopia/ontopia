
package net.ontopia.topicmaps.utils.sdshare;

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TimeZone;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import net.ontopia.utils.StringUtils;
import net.ontopia.utils.CompactHashSet;
import net.ontopia.xml.DefaultXMLReaderFactory;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.topicmaps.utils.KeyGenerator;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;

/**
 * PUBLIC: An SDshare client which can poll an SDshare fragment feed
 * to update a local topic map.
 */
public class ConsumerClient {
  private long lastChange;
  private long lastCheck;
  private long checkInterval;
  private boolean stopped;
  private TopicMapReferenceIF ref;
  private String feedurl;
  private static SimpleDateFormat format =
    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
  
  static {
    format.setTimeZone(TimeZone.getTimeZone("Z"));    
  }

  // constants
  private static final String NS_ATOM = "http://www.w3.org/2005/Atom";
  private static final String NS_SD = "http://www.egovpt.org/sdshare";

  /**
   * PUBLIC: Creates the client, but does nothing more. 
   */
  public ConsumerClient(TopicMapReferenceIF ref, String feedurl) {
    this.ref = ref;
    this.feedurl = feedurl;
  }
  
  /**
   * PUBLIC: Starts an infinite loop checking the SDshare feed
   * repeatedly with the given time interval between checks.
   * Continues until stopSync() is called. Does <em>not</em> start a
   * new thread; this is left for the API client to do.
   */
  public void startSync() {
    stopped = false;
    while (!stopped) {
      try {
        sync();
      } catch (Exception e) {
        e.printStackTrace(); // FIXME: log properly!
        // wait, then carry on, assuming this was temporary
      }      
      try {
        Thread.sleep(checkInterval);
      } catch (InterruptedException e) {
        // well, whatever
      }
    }
  }
  
  /**
   * PUBLIC: Checks the SDshare feed to see if there are any changes
   * since last sync(), then applies any new changes in the feed,
   * updating the topic map. Starts a transaction on the topic map
   * reference, and commits/rollsback changes afterwards.
   */
  public void sync() throws IOException, SAXException {
    // (1) check the fragments feed
    FragmentFeed feed = getFragmentFeed();
    if (feed.getFragments().isEmpty())
      return; // nothing to do

    boolean committed = false;
    TopicMapStoreIF store = null;
    try {
      // (2) start a transaction      
      store = ref.createStore(false);
      
      // (3) loop over fragments, applying each
      for (Fragment frag : feed.getFragments())
        applyFragment(feed.getPrefix(), frag, store.getTopicMap());
      
      // (4) commit
      store.commit();
      store.close();
    } finally {
      if (store != null) {
        if (!committed)
          store.abort();
        store.close(); // recycle the store
      }
    }
  }

  /**
   * PUBLIC: If the sync loop is running, calling this method makes it
   * stop. If the sync loop is not running, calling this method has no
   * effect.
   */
  public void stopSync() {
    stopped = true;
  }

  /**
   * PUBLIC: Returns the interval between feed checks in the
   * startSync() method, measured in milliseconds.
   */
  public long getCheckInterval() {
    return checkInterval;
  }

  /**
   * PUBLIC: Sets the interval between feed checks in the startSync()
   * method, measured in milliseconds.
   */
  public void setCheckInterval(long interval) {
    checkInterval = interval;
  }

  /**
   * PUBLIC: Reads a collection feed and returns an object representing
   * (the interesting part of) the contents of the feed.
   */
  public static CollectionFeed readCollectionFeed(String uri)
    throws IOException, SAXException {
    CollectionFeedReader handler = new CollectionFeedReader(uri);
    XMLReader parser = new DefaultXMLReaderFactory().createXMLReader();
    parser.setContentHandler(handler);
    parser.parse(uri);
    return handler.getCollectionFeed();
  }

  /**
   * PUBLIC: Reads a snapshot feed and returns a list of the snapshot
   * URIs. The order of the list is the same as in the feed.
   */
  public static List<LocatorIF> readSnapshotFeed(String uri)
    throws IOException, SAXException {
    SnapshotFeedReader handler = new SnapshotFeedReader(uri);
    XMLReader parser = new DefaultXMLReaderFactory().createXMLReader();
    parser.setContentHandler(handler);
    parser.parse(uri);
    return handler.getList();
  }

  public FragmentFeed getFragmentFeed() throws IOException, SAXException {
    // TODO: we should support if-modified-since
    FragmentFeedReader handler = new FragmentFeedReader(feedurl, lastChange);
    XMLReader parser = new DefaultXMLReaderFactory().createXMLReader();
    parser.setContentHandler(handler);
    parser.parse(feedurl);
    return handler.getFragmentFeed();
  }
  
  // --- Actual implementation

  private void applyFragment(LocatorIF oprefix, Fragment fragment,
                             TopicMapIF topicmap) throws IOException {
    String prefix = oprefix.getAddress();
    
    // FIXME: before issue #3680 is cleared up, we don't know how to
    // interpret multiple SIs on a single fragment. for now we will
    // just assume that all entries have only a single SI.
    // http://projects.topicmapslab.de/issues/3680
    if (fragment.getTopicSIs().size() != 1)
      throw new RuntimeException("Fragment " + fragment.getFragmentURI() +
                                 " had wrong number of TopicSIs: " +
                                 fragment.getTopicSIs().size());
    
    // (1) get the fragment
    // FIXME: for now we only support XTM
    XTMTopicMapReader reader = new XTMTopicMapReader(fragment.getFragmentURI());
    TopicMapIF tmfragment = reader.read();
    
    // (2) apply it
    LocatorIF si = fragment.getTopicSIs().iterator().next();
    TopicIF ftopic = tmfragment.getTopicBySubjectIdentifier(si);
    TopicIF ltopic = topicmap.getTopicBySubjectIdentifier(si);

    // FIXME: but what if the topic was deleted?

    // (a) check if we need to create the topic
    if (ltopic == null && ftopic != null)
      // the topic exists in the source, but not in the target, therefore
      // we create it
      ltopic = topicmap.getBuilder().makeTopic();

    // (b) copy across all identifiers
    MergeUtils.copyIdentifiers(ltopic, ftopic);

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

    // (3) update lastChange
    lastChange = fragment.getUpdated();
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
    // check all local objects against the fragment
    for (ReifiableIF lobject : lobjects) {
      String key = KeyGenerator.makeKey(lobject);
      ReifiableIF fobject = keymap.get(key);

      if (fobject == null) {
        // the source doesn't have this object. however, if it still has
        // item identifiers (other than ours), we can keep it. we do
        // need to remove any iids starting with the prefix, though.
        pruneItemIdentifiers(lobject, prefix);
        if (lobject.getItemIdentifiers().isEmpty())
          lobject.remove();
      } else {
        // the source has this object. we need to make sure the local
        // copy has the item identifier.
        addItemIdentifier(lobject, prefix);
        keymap.remove(key); // we've seen this one, so cross it off
      }
    }

    // copy the objects in the source which are not in the local copy
    // across to the local copy, adding item identifiers
    for (ReifiableIF fobject : keymap.values()) {
      ReifiableIF lobject = MergeUtils.mergeInto(ltopic, fobject);
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
    LocatorIF base = topicmap.getStore().getBaseAddress();
    String objectid = object.getObjectId();
    LocatorIF iid = base.resolveAbsolute("#sd" + objectid);
    while (topicmap.getObjectByItemIdentifier(iid) != null)
      iid = base.resolveAbsolute("#sd" + objectid + '-' +
                                 StringUtils.makeRandomId(5));

    object.addItemIdentifier(iid);
  }
  
  // --- Fragment feed ContentHandler

  /**
   * INTERNAL: SAX 2.0 ContentHandler to interpret Atom fragment feeds.
   */
  private static class FragmentFeedReader extends DefaultHandler {
    private LocatorIF feedurl;
    private FragmentFeed feed;
    private long lastChange;

    // tracking
    private boolean keep;       // whether to keep text content
    private StringBuilder buf;  // accumulating buffer
    private boolean inEntry;    // are we inside an entry element?
    private String mimetype;    // mimetype of last fragment link
    private String fraglink;    // href of last fragment link
    private long updated;       // content of last <updated> in <entry>
    private Set<LocatorIF> sis; // current <TopicSI>s

    public FragmentFeedReader(String feedurl, long lastChange) {
      this.feedurl = URILocator.create(feedurl);
      this.lastChange = lastChange;
      this.feed = new FragmentFeed();
      this.buf = new StringBuilder();
      this.sis = new CompactHashSet();
    }

    public FragmentFeed getFragmentFeed() {
      return feed;
    }

    public void startElement(String uri, String name, String qname,
                             Attributes atts) {
      if ((uri.equals(NS_SD) && name.equals("ServerSrcLocatorPrefix")) ||
          (uri.equals(NS_SD) && name.equals("TopicSI")))
        keep = true;
      
      else if (uri.equals(NS_ATOM) && name.equals("entry"))
        inEntry = true; // other book-keeping done in endElement()
        
      else if (uri.equals(NS_ATOM) && name.equals("link") && inEntry) {
        String rel = atts.getValue("rel");
        if (rel == null || !rel.equals("alternate"))
          return; // then we don't know what this is
        
        String type = atts.getValue("type");
        if (!isOKMimeType(type))
          return; // we can't load this, so we're passing on it
        
        String href = atts.getValue("href");
        if (href == null)
          throw new RuntimeException("No href attribute on <link>");

        mimetype = type;
        fraglink = href;

      } else if (uri.equals(NS_ATOM) && name.equals("updated") && inEntry)
        keep = true;
    }

    public void characters(char[] ch, int start, int length) {
      if (keep)
        buf.append(ch, start, length);
    }

    public void endElement(String uri, String name, String qname) {
      if (uri.equals(NS_SD) && name.equals("ServerSrcLocatorPrefix"))
        feed.setPrefix(URILocator.create(buf.toString()));

      else if (uri.equals(NS_SD) && name.equals("TopicSI"))
        sis.add(URILocator.create(buf.toString()));
      
      else if (uri.equals(NS_ATOM) && name.equals("entry")) {
        // verify that we've got everything
        if (mimetype == null || fraglink == null)
          throw new RuntimeException("Fragment entry had no suitable links");
        if (updated == -1)
          throw new RuntimeException("Fragment entry had no updated field");
        if (sis.isEmpty())
          throw new RuntimeException("Fragment entry had no TopicSIs");
        
        // check if this is a new fragment, or if we saw it before
        if (updated < lastChange)
          return; // we've done this one already, so ignore it
        
        // create new fragment
        LocatorIF fraguri = feedurl.resolveAbsolute(fraglink);
        feed.addFragment(new Fragment(fraguri, mimetype, sis, updated));
        
        // reset tracking fields
        mimetype = null;
        fraglink = null;
        updated = -1;
        inEntry = false;
        sis = new CompactHashSet();

      } else if (uri.equals(NS_ATOM) && name.equals("updated") && inEntry) {
        try {
          updated = format.parse(buf.toString()).getTime();
        } catch (ParseException e) {
          throw new RuntimeException(e);
        }
      }

      if (keep) {
        buf.setLength(0); // empty, but reuse buffer
        keep = false;
      }
    }

    private boolean isOKMimeType(String mimetype) {
      // FIXME: implement!
      return true;
    }
  }

  // --- FragmentFeed representation

  /**
   * INTERNAL: Represents the pieces of information we collect when
   * polling a fragment feed. Just a simple data carrier.
   */
  public static class FragmentFeed {
    private LocatorIF prefix;
    private List<Fragment> fragments;

    public FragmentFeed() {
      this.fragments = new ArrayList<Fragment>();
    }
    
    public void setPrefix(LocatorIF prefix) {
      this.prefix = prefix;
    }

    public LocatorIF getPrefix() {
      return prefix;
    }

    public void addFragment(Fragment fragment) {
      fragments.add(fragment);
    }

    public List<Fragment> getFragments() {
      return fragments;
    }
  }

  /**
   * INTERNAL: Represents an individual fragment in a fragment feed.
   * Again just a data carrier.
   */
  public static class Fragment {
    private Set<LocatorIF> topicSIs;
    private LocatorIF fragmenturi;
    private String mimetype;
    private long updated;

    public Fragment(LocatorIF fragmenturi, String mimetype,
                    Set<LocatorIF> topicSIs, long updated) {
      this.fragmenturi = fragmenturi;
      this.mimetype = mimetype;
      this.topicSIs = topicSIs;
      this.updated = updated;
    }

    public LocatorIF getFragmentURI() {
      return fragmenturi;
    }

    public String getMIMEType() {
      return mimetype;
    }

    public Set<LocatorIF> getTopicSIs() {
      return topicSIs;
    }

    public long getUpdated() {
      return updated;
    }
  }

  /**
   * INTERNAL: SAX 2.0 ContentHandler to interpret Atom collection feeds.
   */
  private static class CollectionFeedReader extends DefaultHandler {
    private LocatorIF feedurl;
    private CollectionFeed feed;

    public CollectionFeedReader(String feedurl) {
      this.feedurl = URILocator.create(feedurl);
      this.feed = new CollectionFeed();
    }

    public CollectionFeed getCollectionFeed() {
      return feed;
    }

    public void startElement(String uri, String name, String qname,
                             Attributes atts) {
      if (uri.equals(NS_ATOM) && name.equals("link")) {
        String href = atts.getValue("href");
        if (href == null)
          throw new RuntimeException("No href attribute on <link>");

        String rel = atts.getValue("rel");
        if (rel == null)
          return;
        
        LocatorIF theuri = feedurl.resolveAbsolute(href);
        if (rel.equals("http://www.egovpt.org/sdshare/fragmentsfeed"))
          feed.setFragmentFeed(theuri);
        else if (rel.equals("http://www.egovpt.org/sdshare/snapshotsfeed"))
          feed.setSnapshotFeed(theuri);
      }
    }
  }
  
  /**
   * INTERNAL: Represents a collection feed. Just a data carrier.
   */
  public static class CollectionFeed {
    private LocatorIF fragmentfeed;
    private LocatorIF snapshotfeed;

    public CollectionFeed() {
    }

    public LocatorIF getFragmentFeed() {
      return fragmentfeed;
    }

    public void setFragmentFeed(LocatorIF uri) {
      fragmentfeed = uri;
    }
    
    public LocatorIF getSnapshotFeed() {
      return snapshotfeed;
    }

    public void setSnapshotFeed(LocatorIF uri) {
      snapshotfeed = uri;
    }
  }

  /**
   * INTERNAL: SAX 2.0 ContentHandler to interpret Atom snapshot feeds.
   */
  private static class SnapshotFeedReader extends DefaultHandler {
    private LocatorIF feedurl;
    private List<LocatorIF> snapshots;
    private boolean inEntry;    // are we inside an entry element?

    public SnapshotFeedReader(String feedurl) {
      this.feedurl = URILocator.create(feedurl);
      this.snapshots = new ArrayList<LocatorIF>();
    }

    public List<LocatorIF> getList() {
      return snapshots;
    }

    public void startElement(String uri, String name, String qname,
                             Attributes atts) {
      if (uri.equals(NS_ATOM) && name.equals("entry"))
        inEntry = true; // other book-keeping done in endElement()
        
      else if (uri.equals(NS_ATOM) && name.equals("link") && inEntry) {
        String rel = atts.getValue("rel");
        if (rel == null || !rel.equals("alternate"))
          return; // then we don't know what this is
        
        String href = atts.getValue("href");
        if (href == null)
          throw new RuntimeException("No href attribute on <link>");

        snapshots.add(feedurl.resolveAbsolute(href));
      }
    }

    public void endElement(String uri, String name, String qname) {
      if (uri.equals(NS_ATOM) && name.equals("entry"))
        inEntry = false;
    }
  }
}