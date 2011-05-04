
package net.ontopia.topicmaps.utils.sdshare.client;

import java.io.Reader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Set;
import java.util.Date;
import java.util.TimeZone;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.net.MalformedURLException;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.AttributeList;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ontopia.xml.PrettyPrinter;
import net.ontopia.utils.URIUtils;
import net.ontopia.utils.CompactHashSet;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.xml.XMLReaderFactoryIF;
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
  public static XMLReaderFactoryIF parserfactory =
    new DefaultXMLReaderFactory(); // public so we can modify for testing
  
  static {
    format_wo_tz.setTimeZone(TimeZone.getTimeZone("Z"));    
  }

  // constants
  private static final String NS_ATOM = "http://www.w3.org/2005/Atom";
  private static final String NS_SD = "http://www.egovpt.org/sdshare";

  /**
   * Parses Atom-compatible date-time string and returns value as millisecs
   * since epoch (same as System.currentTimeMillis()).
   */
  public static long parseDateTime(String date) {
    try {
      if (date.endsWith("Z"))
        return format_wo_tz.parse(date).getTime();
      else
        return format_with_tz.parse(date).getTime();
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }
  
  public static FragmentFeed readFragmentFeed(String filename_or_url)
    throws IOException, SAXException {
    return readFragmentFeed(filename_or_url, 0);
  }

  public static FragmentFeed readFragmentFeed(String filename_or_url,
                                              long lastChange)
    throws IOException, SAXException {
    // TODO: we should support if-modified-since
    String uri = URIUtils.getURI(filename_or_url).getExternalForm();

    if (uri.startsWith("http://") && lastChange > 0) {
      // need to add 'since' parameter
      String datetime = format_wo_tz.format(new Date(lastChange));
      if (uri.indexOf('?') == -1)
        uri += "?since=" + datetime;
      else
        uri += "&since=" + datetime;
    }
    
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

  /**
   * PUBLIC: Reads a post feed and returns a FragmentFeed object, with
   * the fragments inlined.
   */
  public static FragmentFeed readPostFeed(Reader in)
    throws IOException, SAXException {
    PostFeedReader handler = new PostFeedReader();
    XMLReader parser = parserfactory.createXMLReader();
    // turning this on so we get the 'xmlns*' attributes
    parser.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
    parser.setContentHandler(handler);
    parser.parse(new InputSource(in));
    return handler.getFragmentFeed();
  }
  
  private static void parseWithHandler(String uri, ContentHandler handler)
    throws IOException, SAXException {
    XMLReader parser = parserfactory.createXMLReader();
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
        if (feedurl != null) // it's null for post feeds
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

      } else if (uri.equals(NS_ATOM) && name.equals("updated"))
        updated = parseDateTime(buf.toString());

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
    protected FragmentFeed feed;
    protected long lastChange;

    // SAX tracking
    protected Set<AtomLink> links; // links in current entry
    protected Set<String> sis;     // current <TopicSI>s
    protected Set<String> sls;     // current <TopicSL>s
    protected Set<String> iis;     // current <TopicII>s
    // content is set by a subclass, never by FragmentFeedReader itself
    protected String content;      // contents of <content>

    public FragmentFeedReader(String feedurl, long lastChange) {
      super(feedurl);
      this.lastChange = lastChange;
      this.feed = new FragmentFeed();
      this.sis = new CompactHashSet();
      this.sls = new CompactHashSet();
      this.iis = new CompactHashSet();
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
        throw new OntopiaRuntimeException(e);
      }
    }

    public void endElement(String uri, String name, String qname) {
      try {
        endElement_(uri, name, qname);
      } catch (Exception e) {
        throw new OntopiaRuntimeException(e);
      }
    }
    
    public void startElement_(String uri, String name, String qname,
                             Attributes atts) {
      super.startElement(uri, name, qname, atts);
      
      if ((uri.equals(NS_SD) && name.equals("ServerSrcLocatorPrefix")) ||
          (uri.equals(NS_SD) && name.equals("TopicSI")) ||
          (uri.equals(NS_SD) && name.equals("TopicII")) ||
          (uri.equals(NS_SD) && name.equals("TopicSL")))
        keep = true;
      
      else if (uri.equals(NS_ATOM) && name.equals("link") && inEntry) {
        String rel = atts.getValue("rel");
        if (rel == null || !rel.equals("alternate"))
          return; // then we don't know what this is
        
        String type = atts.getValue("type");
        String href = atts.getValue("href");
        if (href == null)
          throw new RuntimeException("No href attribute on <link>");
        LocatorIF fraguri;
        if (feedurl == null)
          fraguri = URILocator.create(href); // must be absolute
        else
          fraguri = feedurl.resolveAbsolute(href);

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

      else if (uri.equals(NS_SD) && name.equals("TopicSL"))
        sls.add(buf.toString());

      else if (uri.equals(NS_SD) && name.equals("TopicII"))
        iis.add(buf.toString());
      
      else if (uri.equals(NS_ATOM) && name.equals("entry")) {
        // verify that we've got everything
        if (links.size() < 1 && content == null)
          throw new RuntimeException("Fragment entry had no suitable links " +
                                     "and no content");
        if (updated == -1)
          throw new RuntimeException("Fragment entry had no updated field");
        if (sis.isEmpty() && sls.isEmpty() && iis.isEmpty())
          throw new RuntimeException("Fragment entry had no identity");
        
        // check if this is a new fragment, or if we saw it before
        if (updated > lastChange) {
          log.trace("New fragment, updated: " + updated + ", last change: " +
                    lastChange);
          
          // create new fragment
          Fragment f = new Fragment(links, updated, content);
          if (!sis.isEmpty())
            f.setTopicSIs(sis);
          if (!iis.isEmpty())
            f.setTopicIIs(iis);
          if (!sls.isEmpty())
            f.setTopicSLs(sls);
          feed.addFragment(f);
        } else
          log.info("Found old fragment, updated: " + updated);

        // reset tracking fields
        links = new CompactHashSet();
        sis = new CompactHashSet();
        iis = new CompactHashSet();
        sls = new CompactHashSet();
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

  /**
   * INTERNAL: SAX 2.0 ContentHandler to interpret Atom post feeds.
   */
  private static class PostFeedReader extends FragmentFeedReader {
    private PrettyPrinter pp; // if set means we're inside <content>
    private StringWriter tmp;

    public PostFeedReader() {
      super(null, 0);
    }
    
    public void startElement(String uri, String name, String qname,
                             Attributes atts) {
      if (pp != null)
        pp.startElement(qname, new AttributesAdapter(atts));
      
      else if (uri.equals(NS_ATOM) && name.equals("content")) {
        // FIXME: we should look at the MIME type...
        tmp = new StringWriter();
        pp = new PrettyPrinter(tmp, null);
      } else
        
        super.startElement(uri, name, qname, atts);
    }

    public void characters(char[] ch, int start, int length) {
      if (pp != null)
        pp.characters(ch, start, length);
      else
        super.characters(ch, start, length);
    }

    public void endElement(String uri, String name, String qname) {
      if (uri.equals(NS_ATOM) && name.equals("content")) {
        // the fragment hasn't been created yet, so what do we do?
        content = tmp.toString(); // ready to be stored in Fragment
        log.info("content: '" + content + "'");
        pp = null;
      } else if (pp != null)
        pp.endElement(qname);
      else
        super.endElement(uri, name, qname);
    }
  }

  // --- AttributesAdapter

  /**
   * Wraps an Attributes object to adapt it to the AttributeList interface.
   */
  private static class AttributesAdapter implements AttributeList {
    private Attributes atts;

    public AttributesAdapter(Attributes atts) {
      this.atts = atts;
    }

    public int getLength() {
      return atts.getLength();
    }

    public void addAttribute(String name, String type, String value) {
      throw new UnsupportedOperationException();
    }

    public void clear() {
      throw new UnsupportedOperationException();
    }

    public String getName(int i) {
      return atts.getQName(i);
    }

    public String getType(int i) {
      throw new UnsupportedOperationException();
    }

    public String getType(String name) {
      throw new UnsupportedOperationException();
    }

    public String getValue(int i) {
      return atts.getValue(i);
    }

    public String getValue(String name) {
      return atts.getValue(name);
    }

    public void removeAttribute(String name) {
      throw new UnsupportedOperationException();
    }

    public void removeAttribute(int i) {
      throw new UnsupportedOperationException();
    }
  }
}