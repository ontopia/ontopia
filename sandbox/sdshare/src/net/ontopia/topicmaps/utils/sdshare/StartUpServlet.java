
package net.ontopia.topicmaps.utils.sdshare;

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ontopia.utils.StringUtils;
import net.ontopia.utils.PropertyUtils;
import net.ontopia.topicmaps.core.events.TopicMapEvents;
import net.ontopia.topicmaps.entry.TopicMaps;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;

/**
 * PUBLIC: This servlet loads at starts up and reads the
 * configuration.  It then registers event listeners so that the
 * SDshare implementation is notified of any changes.
 */
public class StartUpServlet extends HttpServlet {
  public static Map<String, TopicMapTracker> topicmaps;
  private static Properties properties;
  static Logger log = LoggerFactory.getLogger(StartUpServlet.class.getName());
  
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    log.info("Starting SDshare setup servlet");
    
    // (1) load config
    try {
      properties = PropertyUtils.loadPropertiesFromClassPath("sdshare.properties");
      if (properties == null) {
        log.error("Could not find sdshare.properties!");
        throw new ServletException("Could not find sdshare.properties!");
      } else
        log.debug("Loaded " + properties.size() + " SDshare properties");
    } catch (IOException e) {
      throw new ServletException(e);
    }
    List<String> tmids = getTopicMapIds();

    // (2) register event listeners
    topicmaps = new HashMap<String, TopicMapTracker>();
    TopicMapRepositoryIF rep = TopicMaps.getRepository();
    for (String tmid : tmids) {
      TopicMapReferenceIF ref = rep.getReferenceByKey(tmid);
      // FIXME: complain and skip if not there
      TopicMapTracker tracker = new TopicMapTracker(ref);
      TopicMapEvents.addTopicListener(ref, tracker);
      topicmaps.put(tmid, tracker);
    }
    log.debug("SDshare setup servlet initialized");
  }

  public static List<String> getTopicMapIds() {
    String tmids = properties.getProperty("topicmaps");
    if (tmids == null) {
      log.error("No topic maps configured for SDshare");
      tmids = "";
    }
    String[] ids = StringUtils.split(tmids, ",");
    return Arrays.<String>asList(ids);
  }

  public static String getEndpointURL() {
    return properties.getProperty("endpoint");
  }
}