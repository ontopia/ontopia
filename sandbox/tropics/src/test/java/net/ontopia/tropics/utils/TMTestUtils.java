package net.ontopia.tropics.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Set;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.utils.jtm.JTMTopicMapReader;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;

public class TMTestUtils {
  public static final String BASE_URI = "http://localhost:8182/api/v1/";
  
  public static final String TOPICMAPS_URI  = BASE_URI + "topicmaps/";
  public static final String TOPICS_URI     = BASE_URI + "topics/";
  public static final String GROUPS_URI     = BASE_URI + "groups/";
  public static final String SEARCH_URI     = BASE_URI + "search";
  public static final String QUESTION_URI   = BASE_URI + "question";  
  
  public static final String LOVECRAFT_URI  = TOPICMAPS_URI + "lovecraft";
  public static final String ALL_URI        = GROUPS_URI + "group.all";

  public static final String CTHULHU_TOPIC_URI       = TOPICS_URI + "cthulhu";
  public static final String NYARLATHOTEP_TOPIC_URI  = TOPICS_URI + "nyarlathotep";
  public static final String PUCCINI_TOPIC_URI       = TOPICS_URI + "puccini";
  
  public static final String LOVECRAFT_TM   = "/topicmaps/lovecraft";
  public static final String OPERA_TM       = "/topicmaps/ItalianOpera";
  public static final String ALL_TM         = "/groups/group.all";
  
  public static TopicMapIF readFromXTM(String response, String base_address) {
    TopicMapIF tm = null;
    try {
      XTMTopicMapReader reader = new XTMTopicMapReader(new StringReader(response), new URILocator(base_address));
      tm = reader.read();      
    } catch (MalformedURLException e) {
      e.printStackTrace();
      fail();
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
    
    if (tm == null) fail("topic map is (null)");
    
    return tm;
  }

  public static TopicMapIF readFromJTM(String response, String base_address) {
    TopicMapIF tm = null;
    try {
      JTMTopicMapReader reader = new JTMTopicMapReader(new StringReader(response), new URILocator(base_address));
      tm = reader.read();      
    } catch (MalformedURLException e) {
      e.printStackTrace();
      fail();
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
    
    if (tm == null) fail("topic map is (null)");
    
    return tm;
  }
  
  public static Collection<TopicIF> getLengthCheckedInstances(TopicMapIF tm, TopicIF type, int expectedNrOfInstances) {
    ClassInstanceIndexIF typeIndex = (ClassInstanceIndexIF) tm.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
    Collection<TopicIF> instances = typeIndex.getTopics(type);
    
    assertEquals(expectedNrOfInstances, instances.size());

    return instances;
  }
  
  public static void checkAllInstancesExpected(Collection<TopicIF> instances, Set<LocatorIF> expected) {
    for (TopicIF goo : instances) {
      Collection<LocatorIF> itemIdentifiers = goo.getItemIdentifiers();
      boolean found = false; 
      
      for (LocatorIF itemIdentifier : itemIdentifiers) {
        if (expected.contains(itemIdentifier)) {
          found = true;
          break;
        }
      }
      
      assertTrue(found);
    }
  }

}
