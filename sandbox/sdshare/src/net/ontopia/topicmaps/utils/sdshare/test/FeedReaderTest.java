
package net.ontopia.topicmaps.utils.sdshare.test;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.Iterator;
import org.xml.sax.SAXException;

import net.ontopia.topicmaps.test.AbstractTopicMapTestCase;
import net.ontopia.topicmaps.utils.sdshare.client.*;

public class FeedReaderTest extends AbstractTopicMapTestCase {
  
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

    assertEquals("wrong MIME type",
                 rdflink.getMIMEType().toString(),
                 "application/rdf+xml");
    assertEquals("wrong MIME type",
                 xtmlink.getMIMEType().toString(),
                 "application/x-tm+xml; version=1.0");
    assertEquals("wrong link",
                 rdflink.getUri(),
                 "application/rdf+xml");
    assertEquals("wrong MIME type",
                 xtmlink.getUri(),
                 "application/x-tm+xml; version=1.0");
  }
  
}