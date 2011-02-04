
package net.ontopia.topicmaps.utils.sdshare.client;

import java.util.Set;
import java.io.IOException;
import java.io.StringWriter;
import org.xml.sax.SAXException;

import net.ontopia.utils.CompactHashSet;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.entry.TopicMaps;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;
import net.ontopia.topicmaps.xml.XTMTopicMapFragmentWriter;
import net.ontopia.topicmaps.utils.sdshare.ChangedTopic;
import net.ontopia.topicmaps.utils.sdshare.DeletedTopic;
import net.ontopia.topicmaps.utils.sdshare.TrackerManager;
import net.ontopia.topicmaps.utils.sdshare.TopicMapTracker;

/**
 * INTERNAL: Frontend which gets snapshots and changes directly from
 * the Ontopia API.
 */
public class OntopiaFrontend implements ClientFrontendIF {
  private String handle; // handle (URL|id) of source collection
  private TopicMapTracker tracker;

  public OntopiaFrontend(String handle) {
    this.handle = handle;
    this.tracker = TrackerManager.registerTracker(handle);
  }
  
  public String getHandle() {
    return handle;
  }

  public SnapshotFeed getSnapshotFeed() throws IOException, SAXException {
    throw new UnsupportedOperationException(); // FIXME: implement!
  }

  public FragmentFeed getFragmentFeed(long lastChange)
    throws IOException, SAXException {
    FragmentFeed feed = new FragmentFeed();
    // FIXME: what's the prefix?
    // FIXME: presumably we need a title and all that jazz, too?

    TopicMapRepositoryIF rep = TopicMaps.getRepository();
    TopicMapReferenceIF ref = rep.getReferenceByKey(handle);
    TopicMapIF topicmap = ref.createStore(true).getTopicMap();
    
    for (ChangedTopic topic : tracker.getChangeFeed()) {
      if (topic.getTimestamp() < lastChange)
        break; // we've seen all the new changes, so stop

      Set<String> sis = new CompactHashSet();
      String fragment;
      if (topic instanceof DeletedTopic) {
        for (LocatorIF si : ((DeletedTopic) topic).getSubjectIdentifiers())
          sis.add(si.getExternalForm());
        fragment = makeFragment(null);
      } else {
        TopicIF rtopic = (TopicIF) topicmap.getObjectById(topic.getObjectId());
        for (LocatorIF si : rtopic.getSubjectIdentifiers())
          sis.add(si.getExternalForm());
        fragment = makeFragment(rtopic);
      }

      // FIXME: do we really need to serialize the fragment? could we produce
      // it on demand instead?
      feed.addFragment(new Fragment(null, sis, topic.getTimestamp(), fragment));
    }

    return feed;
  }

  private String makeFragment(TopicIF topic) {
    StringWriter out = new StringWriter();
    try {
      XTMTopicMapFragmentWriter writer = new XTMTopicMapFragmentWriter(out);
      writer.startTopicMap();
      if (topic != null)
        writer.exportTopic(topic);
      writer.endTopicMap();
    } catch (IOException e) {
      // should be impossible, but rethrowing just in case
      throw new OntopiaRuntimeException(e);
    }
    return out.toString();
  }
}