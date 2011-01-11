
package net.ontopia.topicmaps.utils.sdshare.test;

import java.io.File;
import java.io.IOException;
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
  
}