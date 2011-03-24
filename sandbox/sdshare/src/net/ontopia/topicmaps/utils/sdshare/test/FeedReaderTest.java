
package net.ontopia.topicmaps.utils.sdshare.test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

import org.xml.sax.XMLReader;
import org.xml.sax.DTDHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;

import net.ontopia.topicmaps.test.AbstractTopicMapTestCase;
import net.ontopia.topicmaps.utils.sdshare.client.*;
import net.ontopia.xml.XMLReaderFactoryIF;

public class FeedReaderTest extends AbstractTopicMapTestCase {
  private static final SimpleDateFormat format_wo_tz =
    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
  static {
    format_wo_tz.setTimeZone(TimeZone.getTimeZone("Z"));    
  }
  
  public FeedReaderTest(String name) {
    super(name);
  }

  // ===== UTILITIES

  public FragmentFeed readFragmentFeed(String file)
    throws IOException, SAXException {
    file = resolveFileName("sdshare" + File.separator + "feeds" +
                           File.separator + file);
    return FeedReaders.readFragmentFeed(file);
  }

  public FragmentFeed readPostFeed(String file)
    throws IOException, SAXException {
    file = resolveFileName("sdshare" + File.separator + "feeds" +
                           File.separator + file);
    return FeedReaders.readPostFeed(new FileReader(file));
  }

  public void doSinceTest(String baseurl, long thetime, String finalurl)
    throws IOException, SAXException {
    FakeXMLReader our = new FakeXMLReader();
    XMLReaderFactoryIF orig = FeedReaders.parserfactory;
    FeedReaders.parserfactory = our;

    FeedReaders.readFragmentFeed(baseurl, thetime);

    FeedReaders.parserfactory = orig;    
    assertEquals("wrong URI used to retrieve feed", finalurl, our.uri);
  }
  
  // ===== TESTS

  public void testEmptyFragmentFeed() throws Exception {
    FragmentFeed feed = readFragmentFeed("fragment-empty.xml");
    assertTrue("fragments found in empty feed", feed.getFragments().isEmpty());
    assertEquals("incorrect server prefix",
                 "file:/Users/larsga/data/topicmaps/beer.xtm",
                 feed.getPrefix());
  }

  public void testEmptyFragmentFeed2() throws Exception {
    FragmentFeed feed = readFragmentFeed("fragment-empty-2.xml");
    assertTrue("fragments found in empty feed", feed.getFragments().isEmpty());
    assertEquals("incorrect server prefix",
                 "file:/Users/larsga/data/topicmaps/beer.xtm",
                 feed.getPrefix());
  }

  public void testFragmentFeed1() throws Exception {
    FragmentFeed feed = readFragmentFeed("fragment-1.xml");
    assertEquals("wrong number of fragments",
                 feed.getFragments().size(), 1);
    assertEquals("incorrect server prefix",
                 "file:/Users/larsga/data/topicmaps/beer.xtm",
                 feed.getPrefix());

    Fragment fragment = feed.getFragments().iterator().next();
    Set<AtomLink> links = fragment.getLinks();
    assertEquals("wrong number of links", links.size(), 2);

    Iterator<AtomLink> it = links.iterator();
    AtomLink rdflink = it.next();
    AtomLink xtmlink = it.next();

    if (!rdflink.getMIMEType().getType().equals("application/rdf+xml")) {
      AtomLink tmp = rdflink;
      rdflink = xtmlink;
      xtmlink = tmp;
    }

    MIMEType xtm = xtmlink.getMIMEType();
    assertEquals("wrong MIME type",
                 rdflink.getMIMEType().toString(),
                 "application/rdf+xml");
    assertEquals("wrong MIME type", xtm.toString(),
                 "application/x-tm+xml; version=1.0");
    assertTrue("wrong link: " + xtmlink.getUri(),
               xtmlink.getUri().endsWith("&syntax=xtm"));
    assertTrue("wrong link: " + rdflink.getUri(),
               rdflink.getUri().endsWith("&syntax=rdf"));

    assertEquals("wrong MIME main type", xtm.getMainType(), "application");
    assertEquals("wrong MIME subtype", xtm.getSubType(), "x-tm+xml");
    assertEquals("wrong MIME version", xtm.getVersion(), "1.0");
  }

  public void testPostFeed1() throws Exception {
    FragmentFeed feed = readPostFeed("push-1.xml");
    assertEquals("wrong number of fragments",
                 feed.getFragments().size(), 1);
    assertEquals("incorrect server prefix",
                 "file:/Users/larsga/data/topicmaps/beer.xtm",
                 feed.getPrefix());

    Fragment fragment = feed.getFragments().iterator().next();
    Set<AtomLink> links = fragment.getLinks();
    assertEquals("wrong number of links", links.size(), 0);

    assertTrue("no content in fragment", fragment.getContent() != null);

    // FIXME: should test contents of fragment, too
  }

  public void testFragmentSince() throws Exception {
    String timestring = "2011-03-24T10:04:02Z";
    long thetime = format_wo_tz.parse(timestring).getTime();
    String baseurl = "http://www.example.org/sdshare/fragments";
    doSinceTest(baseurl, thetime, baseurl + "?since=" + timestring);
  }

  public void testFragmentSinceFile() throws Exception {
    String timestring = "2011-03-24T10:04:02Z";
    long thetime = format_wo_tz.parse(timestring).getTime();
    String baseurl = "file://Users/larsga/sdshare/fragments.xml";
    doSinceTest(baseurl, thetime, baseurl);
  }

  public void testFragmentSinceNoTime() throws Exception {
    String baseurl = "http://www.example.org/sdshare/fragments";
    doSinceTest(baseurl, 0, baseurl);
  }

  public void testFragmentSinceParamsAlready() throws Exception {
    String timestring = "2011-03-24T10:04:02Z";
    long thetime = format_wo_tz.parse(timestring).getTime();
    String baseurl = "http://www.example.org/sdshare/fragments?tm=x.xtm";
    doSinceTest(baseurl, thetime, baseurl + "&since=" + timestring);
  }
  
  // ===== FAKE XML READER =====

  /**
   * This class exists only so we can pick up the URI passed to the parser.
   */
  class FakeXMLReader implements XMLReader, XMLReaderFactoryIF {
    private String uri;

    // XMLReader implementation
    
    public ContentHandler getContentHandler() { return null; }
    public void setContentHandler(ContentHandler c) {}
    public DTDHandler getDTDHandler() { return null; }
    public void setDTDHandler(DTDHandler d) {}
    public EntityResolver getEntityResolver() { return null; }
    public void setEntityResolver(EntityResolver e) {}
    public ErrorHandler getErrorHandler() { return null; }
    public void setErrorHandler(ErrorHandler e) {}
    public boolean getFeature(String name) { return false; }
    public void setFeature(String name, boolean v) {}
    public Object getProperty(String name) { return null; }
    public void setProperty(String name, Object v) {}
    public void parse(InputSource src) {}
    
    public void parse(String uri) {
      this.uri = uri;
    }

    // XMLReaderFactoryIF implementation

    public XMLReader createXMLReader() {
      return this;
    }
  }
}