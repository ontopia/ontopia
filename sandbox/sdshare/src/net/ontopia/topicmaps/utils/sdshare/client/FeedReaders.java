
package net.ontopia.topicmaps.utils.sdshare.client;

import java.io.IOException;
import java.util.Set;
import java.util.TimeZone;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.net.MalformedURLException;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ontopia.utils.URIUtils;
import net.ontopia.utils.CompactHashSet;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.xml.DefaultXMLReaderFactory;

/**
 * PUBLIC: Utility methods for loading various kinds of SDshare feeds.
 */
public class FeedReaders {
  static Logger log = LoggerFactory.getLogger(FeedReaders.class.getName());
  private static SimpleDateFormat format_wo_tz =
    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
  private static SimpleDateFormat format_with_tz =
    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
  
  static {
    format_wo_tz.setTimeZone(TimeZone.getTimeZone("Z"));    
  }

  // constants
  private static final String NS_ATOM = "http://www.w3.org/2005/Atom";
  private static final String NS_SD = "http://www.egovpt.org/sdshare";

  public static FragmentFeed readFragmentFeed(String filename_or_url)
    throws IOException, SAXException {
    return readFragmentFeed(filename_or_url, 0);
  }

  public static FragmentFeed readFragmentFeed(String filename_or_url,
                                              long lastChange)
    throws IOException, SAXException {
    // TODO: we should support if-modified-since
    String uri = URIUtils.getURI(filename_or_url).getExternalForm();
    FragmentFeedReader handler = new FragmentFeedReader(uri, lastChange);
    parseWithHandler(uri, handler);
    return handler.getFragmentFeed();
  }
  
  /**
   * PUBLIC: Reads a collection feed and returns an object representing
   * (the interesting part of) the contents of the feed.
   */
  public static CollectionFeed readCollectionFeed(String filename_or_url)
    throws IOException, SAXException {
    String uri = URIUtils.getURI(filename_or_url).getExternalForm();
    CollectionFeedReader handler = new CollectionFeedReader(uri);
    parseWithHandler(uri, handler);
    return handler.getCollectionFeed();
  }

  /**
   * PUBLIC: Reads a snapshot feed and returns a list of the
   * snapshots. The order of the list is the same as in the feed.
   */
  public static SnapshotFeed readSnapshotFeed(String filename_or_url)
    throws IOException, SAXException {
    String uri = URIUtils.getURI(filename_or_url).getExternalForm();
    SnapshotFeedReader handler = new SnapshotFeedReader(uri);
    parseWithHandler(uri, handler);
    return handler.getFeed();
  }

  private static void parseWithHandler(String uri, ContentHandler handler)
    throws IOException, SAXException {
    XMLReader parser = new DefaultXMLReaderFactory().createXMLReader();
    parser.setContentHandler(handler);
    parser.parse(uri);
  }
  
  // --- Abstract ContentHandler
  // can track element contents, and does track the "updated" element

  private abstract static class AbstractFeedReader extends DefaultHandler {
    protected LocatorIF feedurl;

    // tracking
    protected boolean keep;       // whether to keep text content
    protected StringBuilder buf;  // accumulating buffer
    protected boolean inEntry;
    protected long updated;       // content of last <updated>

    public AbstractFeedReader(String feedurl) {
      try {
        this.feedurl = new URILocator(feedurl);
      } catch (MalformedURLException e) {
        throw new OntopiaRuntimeException(e);
      }
      this.buf = new StringBuilder();
    }

    public void startElement(String uri, String name, String qname,
                             Attributes atts) {
      if (uri.equals(NS_ATOM) && name.equals("entry"))
        inEntry = true; // other book-keeping done in endElement()
        
      else if (uri.equals(NS_ATOM) && name.equals("updated"))
        keep = true;
    }

    public void characters(char[] ch, int start, int length) {
      if (keep)
        buf.append(ch, start, length);
    }

    public void endElement(String uri, String name, String qname) {
      if (uri.equals(NS_ATOM) && name.equals("entry")) {
        inEntry = false;
        updated = -1;

      } else if (uri.equals(NS_ATOM) && name.equals("updated")) {
        try {
          String date = buf.toString();
          if (date.endsWith("Z"))
            updated = format_wo_tz.parse(date).getTime();
          else
            updated = format_with_tz.parse(date).getTime();
        } catch (ParseException e) {
          throw new RuntimeException(e);
        }
      }

      if (keep) {
        buf.setLength(0); // empty, but reuse buffer
        keep = false;
      }
    }
  }
  
  // --- Fragment feed ContentHandler

  /**
   * INTERNAL: SAX 2.0 ContentHandler to interpret Atom fragment feeds.
   */
  private static class FragmentFeedReader extends AbstractFeedReader {
    private FragmentFeed feed;
    private long lastChange;

    // SAX tracking
    private Set<AtomLink> links; // links in current entry
    private Set<String> sis;     // current <TopicSI>s

    public FragmentFeedReader(String feedurl, long lastChange) {
      super(feedurl);
      this.lastChange = lastChange;
      this.feed = new FragmentFeed();
      this.sis = new CompactHashSet();
      this.links = new CompactHashSet();
    }

    public FragmentFeed getFragmentFeed() {
      return feed;
    }

    public void startElement(String uri, String name, String qname,
                             Attributes atts) {
      try {
        startElement_(uri, name, qname, atts);
      } catch (Exception e) {
        e.printStackTrace();
        throw new OntopiaRuntimeException(e);
      }
    }

    public void endElement(String uri, String name, String qname) {
      try {
        endElement_(uri, name, qname);
      } catch (Exception e) {
        e.printStackTrace();
        throw new OntopiaRuntimeException(e);
      }
    }
    
    public void startElement_(String uri, String name, String qname,
                             Attributes atts) {
      super.startElement(uri, name, qname, atts);
      
      if ((uri.equals(NS_SD) && name.equals("ServerSrcLocatorPrefix")) ||
          (uri.equals(NS_SD) && name.equals("TopicSI")))
        keep = true;
      
      else if (uri.equals(NS_ATOM) && name.equals("link") && inEntry) {
        String rel = atts.getValue("rel");
        if (rel == null || !rel.equals("alternate"))
          return; // then we don't know what this is
        
        String type = atts.getValue("type");
        String href = atts.getValue("href");
        if (href == null)
          throw new RuntimeException("No href attribute on <link>");
        LocatorIF fraguri = feedurl.resolveAbsolute(href);

        MIMEType mimetype = null;
        if (type != null)
          mimetype = new MIMEType(type);

        links.add(new AtomLink(mimetype, fraguri.getExternalForm()));
      }
    }

    public void endElement_(String uri, String name, String qname) {     
      if (uri.equals(NS_SD) && name.equals("ServerSrcLocatorPrefix"))
        feed.setPrefix(buf.toString());

      else if (uri.equals(NS_SD) && name.equals("TopicSI"))
        sis.add(buf.toString());
      
      else if (uri.equals(NS_ATOM) && name.equals("entry")) {
        // verify that we've got everything
        if (links.size() < 1)
          throw new RuntimeException("Fragment entry had no suitable links");
        if (updated == -1)
          throw new RuntimeException("Fragment entry had no updated field");
        if (sis.isEmpty())
          throw new RuntimeException("Fragment entry had no TopicSIs");
        
        // check if this is a new fragment, or if we saw it before
        if (updated > lastChange) {
          log.info("New fragment, updated: " + updated + ", last change: " +
                   lastChange);
          
          // create new fragment
          feed.addFragment(new Fragment(links, sis, updated));      
        } else
          log.info("Found old fragment, updated: " + updated);

        // reset tracking fields
        links = new CompactHashSet();
        sis = new CompactHashSet();        
      }

      super.endElement(uri, name, qname);
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
          feed.setFragmentFeed(theuri.getExternalForm());
        else if (rel.equals("http://www.egovpt.org/sdshare/snapshotsfeed"))
          feed.setSnapshotFeed(theuri.getExternalForm());
      }
    }
  }
  
  /**
   * INTERNAL: SAX 2.0 ContentHandler to interpret Atom snapshot feeds.
   */
  private static class SnapshotFeedReader extends AbstractFeedReader {
    private SnapshotFeed feed;
    private Snapshot current; // INV: null outside of <entry> elements

    public SnapshotFeedReader(String feedurl) {
      super(feedurl);
      this.feed = new SnapshotFeed();
    }

    public SnapshotFeed getFeed() {
      return feed;
    }

    public void startElement(String uri, String name, String qname,
                             Attributes atts) {
      super.startElement(uri, name, qname, atts);
      
      if (uri.equals(NS_SD) && name.equals("ServerSrcLocatorPrefix"))
        keep = true;
      
      else if (uri.equals(NS_ATOM) && name.equals("entry"))
        current = new Snapshot(feed); // other book-keeping done in endElement()
        
      else if (uri.equals(NS_ATOM) && name.equals("link") && inEntry) {
        String rel = atts.getValue("rel");
        if (rel == null || !rel.equals("alternate"))
          return; // then we don't know what this is
        
        String href = atts.getValue("href");
        if (href == null)
          throw new RuntimeException("No href attribute on <link>");

        current.setSnapshotURI(feedurl.resolveAbsolute(href).getExternalForm());
      }
    }

    public void endElement(String uri, String name, String qname) {
      if (uri.equals(NS_ATOM) && name.equals("entry")) {
        feed.addSnapshot(current);
        current = null;
      } else if (uri.equals(NS_SD) && name.equals("ServerSrcLocatorPrefix"))
        feed.setPrefix(buf.toString());

      super.endElement(uri, name, qname);

      if (uri.equals(NS_ATOM) && name.equals("updated") && inEntry)
        current.setUpdated(updated);
    }
  }
}