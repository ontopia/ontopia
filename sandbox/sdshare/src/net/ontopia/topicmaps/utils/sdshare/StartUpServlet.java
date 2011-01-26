
package net.ontopia.topicmaps.utils.sdshare;

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ontopia.utils.StringUtils;
import net.ontopia.utils.PropertyUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.TopicMapFragmentWriterIF;
import net.ontopia.topicmaps.core.events.TopicMapEvents;
import net.ontopia.topicmaps.entry.TopicMaps;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;
import net.ontopia.topicmaps.xml.XTMTopicMapFragmentWriter;
import net.ontopia.topicmaps.utils.rdf.RDFFragmentExporter;

/**
 * PUBLIC: This servlet loads at starts up and reads the
 * configuration.  It then registers event listeners so that the
 * SDshare implementation is notified of any changes.
 */
public class StartUpServlet extends HttpServlet {
  public static Map<String, TopicMapTracker> topicmaps;
  private static Properties properties;
  static Logger log = LoggerFactory.getLogger(StartUpServlet.class.getName());
  protected static final long DEFAULT_EXPIRY_TIME = 86400000; // 24h
  
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
      if (ref == null) {
        log.error("No topic map reference for ID: '" + tmid + "'");
        continue;
      }
      TopicMapTracker tracker = new TopicMapTracker(ref, getExpiryTime());
      File file = new File(System.getProperty("java.io.tmpdir"),
                           tmid + ".dribble");
      try {
        tracker.setDribbleFile(file.getAbsolutePath());
      } catch (IOException e) {
        log.error("Cannot set up dribble file", e);
      }
      TopicMapEvents.addTopicListener(ref, tracker);
      topicmaps.put(tmid, tracker);
      log.debug("Registered topic map '" + tmid + "'");
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
    for (int ix = 0; ix < ids.length; ix++)
      ids[ix] = ids[ix].trim();
    return Arrays.<String>asList(ids);
  }

  // FIXME: should we remove this in favour of something based on hostname?
  public static String getEndpointURL() {
    return properties.getProperty("endpoint");
  }

  private static String HOSTNAME;
  public static String getTopicMapURL(String tmid) {
    return "http://" + getHostName() + "/sdshare/" + tmid;
  }

  private static String getHostName() {
    if (HOSTNAME == null) {
      try {
        HOSTNAME = InetAddress.getLocalHost().getHostName();
      } catch (java.net.UnknownHostException e) {
        HOSTNAME = "localhost";
      }
    }
    return HOSTNAME;
  }

  public static String getTitle() {
    return properties.getProperty("title");
  }

  public static long getExpiryTime() {
    String time = properties.getProperty("expiry");
    if (time == null)
      return DEFAULT_EXPIRY_TIME;
    try {
      return Long.parseLong(time.trim());
    } catch (NumberFormatException e) {
      log.error("expiry property set to unparseable number '" + time + "'");
      return DEFAULT_EXPIRY_TIME;
    }
  }

  /**
   * Returns identifiers for the syntaxes we should produce snapshots
   * and fragments in.
   */
  public static SyntaxIF[] getSyntaxes() {
    String val = properties.getProperty("syntaxes");
    String[] ids;
    
    if (val == null)
      ids = new String[] { "xtm" };
    else {
      ids = val.toLowerCase().split(",");
      for (int ix = 0; ix < ids.length; ix++)
        ids[ix] = ids[ix].trim();
    }

    SyntaxIF[] syntaxes = new SyntaxIF[ids.length];
    for (int ix = 0; ix < syntaxes.length; ix++)
      syntaxes[ix] = getSyntax(ids[ix]);
    return syntaxes;
  }

  // --- INTERNAL

  public static SyntaxIF getSyntax(String id) {
    if (id.equals("xtm"))
      return new XTMSyntax();
    else if (id.equals("rdf"))
      return new RDFSyntax();
    else
      throw new OntopiaRuntimeException("Unknown syntax: '" + id + "'");
  }

  public static class XTMSyntax implements SyntaxIF {
    public String getId() {
      return "xtm";
    }

    public String getMIMEType() {
      return "application/x-tm+xml; version=1.0";
    }

    public TopicMapFragmentWriterIF getFragmentWriter(OutputStream out,
                                                      String encoding)
      throws IOException {
      return new XTMTopicMapFragmentWriter(out, encoding);
    }
  }

  public static class RDFSyntax implements SyntaxIF {
    public String getId() {
      return "rdf";
    }

    public String getMIMEType() {
      return "application/rdf+xml";
    }

    public TopicMapFragmentWriterIF getFragmentWriter(OutputStream out,
                                                      String encoding)
      throws IOException {
      return new RDFFragmentExporter(out, encoding);
    }
  }
}