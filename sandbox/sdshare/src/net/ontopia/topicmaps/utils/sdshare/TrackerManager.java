
package net.ontopia.topicmaps.utils.sdshare;

import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.events.TopicMapEvents;
import net.ontopia.topicmaps.entry.TopicMaps;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;

/**
 * INTERNAL: Class which manages registrations of TopicMapTrackers, to
 * ensure that the client and server don't register different trackers
 * for the same topic map. The StartUpClient in the server calls this
 * class, as does the OntopiaFrontend in the client.
 */
public class TrackerManager {
  static Logger log = LoggerFactory.getLogger(TrackerManager.class.getName());
  private static Map<String, TopicMapTracker> topicmaps;
  private static long expiry_time;
  protected static final long DEFAULT_EXPIRY_TIME = 86400000; // 24h

  static {
    topicmaps = new HashMap();
    expiry_time = DEFAULT_EXPIRY_TIME;
  }

  public static void setExpiryTime(long new_expiry_time) {
    expiry_time = new_expiry_time;
  }

  public static TopicMapTracker getTracker(String tmid) {
    return topicmaps.get(tmid);
  }

  public static TopicMapTracker registerTracker(String tmid) {
    if (topicmaps.containsKey(tmid))
      return topicmaps.get(tmid);
    
    TopicMapRepositoryIF rep = TopicMaps.getRepository();
    TopicMapReferenceIF ref = rep.getReferenceByKey(tmid);
    if (ref == null) {
      log.error("No such topic map '" + tmid + "'");
      throw new OntopiaRuntimeException("No such topic map");
    }
      
    TopicMapTracker tracker = new TopicMapTracker(ref, expiry_time);
    File file = new File(System.getProperty("java.io.tmpdir"),
                         tmid + ".dribble");
    try {
      tracker.setDribbleFile(file.getAbsolutePath());
    } catch (IOException e) {
      log.warn("Cannot set up dribble file", e);
    }
    TopicMapEvents.addTopicListener(ref, tracker);
    topicmaps.put(tmid, tracker);

    log.debug("Registered topic map '" + tmid + "' in TrackerManager.class " +
              System.identityHashCode(TrackerManager.class) +
              " with tracker " + tracker);

    return tracker;
  }
  
}