package net.ontopia.tropics.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.tropics.utils.TMTestUtils;
import net.ontopia.tropics.utils.URIUtils;

import org.junit.Test;
import org.restlet.resource.ClientResource;

public class TopicTest extends BasicTropicsTest {
  
  @SuppressWarnings("serial")
  @Test
  public void testCthulhuTopic() throws UnsupportedEncodingException, MalformedURLException {
    Map<String, String> params = new HashMap<String, String>() {{
      put("tms-include", TMTestUtils.LOVECRAFT_TM);      
    }};
    
    ClientResource client = new ClientResource(URIUtils.buildURI(TMTestUtils.CTHULHU_TOPIC_URI, params));
    String response = get(client);
    
    TopicMapIF tm = TMTestUtils.readFromXTM(response, TMTestUtils.TOPICS_URI);
    assertNotNull(tm.getObjectByItemIdentifier(new URILocator("http://localhost:8182/api/v1/topics/cthulhu")));
    assertNotNull(tm.getObjectByItemIdentifier(new URILocator("http://localhost:8182/api/v1/topics/great_old_one")));
    assertNotNull(tm.getObjectByItemIdentifier(new URILocator("http://localhost:8182/api/v1/topics/rlyeh")));
    assertNotNull(tm.getObjectByItemIdentifier(new URILocator("http://localhost:8182/api/v1/topics/citizen")));
    assertNotNull(tm.getObjectByItemIdentifier(new URILocator("http://localhost:8182/api/v1/topics/city")));
    assertNotNull(tm.getObjectByItemIdentifier(new URILocator("http://localhost:8182/api/v1/topics/lives-in")));
    
    assertNull(tm.getObjectByItemIdentifier(new URILocator("http://localhost:8182/api/v1/topics/nyarlathotep")));
    assertNull(tm.getObjectByItemIdentifier(new URILocator("http://localhost:8182/api/v1/topics/indiana_jones")));
  }
  
  @SuppressWarnings("serial")
  @Test
  public void testNyarlathotepTopic() throws UnsupportedEncodingException, MalformedURLException {
    Map<String, String> params = new HashMap<String, String>() {{
      put("tms-include", TMTestUtils.LOVECRAFT_TM);      
    }};
    
    ClientResource client = new ClientResource(URIUtils.buildURI(TMTestUtils.NYARLATHOTEP_TOPIC_URI, params));
    String response = get(client);
    
    TopicMapIF tm = TMTestUtils.readFromXTM(response, TMTestUtils.TOPICS_URI);
    assertEquals(3, tm.getTopics().size());
    assertNotNull(tm.getObjectByItemIdentifier(new URILocator("http://localhost:8182/api/v1/topics/nyarlathotep")));
    assertNotNull(tm.getObjectByItemIdentifier(new URILocator("http://localhost:8182/api/v1/topics/god")));
    
    assertNull(tm.getObjectByItemIdentifier(new URILocator("http://localhost:8182/api/v1/topics/cthulhu")));
    assertNull(tm.getObjectByItemIdentifier(new URILocator("http://localhost:8182/api/v1/topics/indiana_jones")));
  }

  @SuppressWarnings("serial")
  @Test
  public void testPucciniTopic() throws UnsupportedEncodingException, MalformedURLException {
    Map<String, String> params = new HashMap<String, String>() {{
      put("tms-include", TMTestUtils.OPERA_TM);      
    }};
    
    ClientResource client = new ClientResource(URIUtils.buildURI(TMTestUtils.PUCCINI_TOPIC_URI, params));
    String response = get(client);
    
    TopicMapIF tm = TMTestUtils.readFromXTM(response, TMTestUtils.TOPICS_URI);
    assertEquals(51, tm.getTopics().size());
    assertEquals(18, tm.getAssociations().size());
    
    TopicIF puccini = (TopicIF) tm.getObjectByItemIdentifier(new URILocator("http://localhost:8182/api/v1/topics/puccini")); 
    assertNotNull(puccini);
    
    Set<String> expectedNames = new HashSet<String>(Arrays.asList("Puccini, Giacomo", "Giacomo Puccini", "Puccini"));
    for (TopicNameIF name : puccini.getTopicNames()) {
      assertTrue(expectedNames.contains(name.getValue()));
    }
    
    assertNull(tm.getObjectByItemIdentifier(new URILocator("http://localhost:8182/api/v1/topics/cthulhu")));
    assertNull(tm.getObjectByItemIdentifier(new URILocator("http://localhost:8182/api/v1/topics/indiana_jones")));
  }

  @SuppressWarnings("serial")
  @Test
  public void testUpdateWelshLanguageTopic() throws IOException {
    Map<String, String> params = new HashMap<String, String>() {{
      put("tms-include", TMTestUtils.OPERA_TM);      
    }};
        
    ClientResource client = new ClientResource(URIUtils.buildURI(TMTestUtils.WELSH_TOPIC_URI, params));
    
    String response = get(client);

    TopicMapIF tm = TMTestUtils.readFromXTM(response, TMTestUtils.TOPICS_URI);
    TopicIF topic = (TopicIF) tm.getObjectByItemIdentifier(URILocator.create(URIUtils.buildURI(TMTestUtils.WELSH_TOPIC_URI, null)));
    
    List<TopicNameIF> names = new ArrayList<TopicNameIF>(topic.getTopicNames());
    assertEquals(5, names.size());
    String newName = "Waals";
    for (TopicNameIF name : names) {
      if (name.getScope().size() == 0) {        
        name.setValue(newName);
      }
    } 
    
    String content = TMTestUtils.writeToXTM(tm);
    put(client, content);

    response = get(client);
    tm = TMTestUtils.readFromXTM(response, TMTestUtils.TOPICS_URI);
    
    topic = (TopicIF) tm.getObjectByItemIdentifier(URILocator.create(URIUtils.buildURI(TMTestUtils.WELSH_TOPIC_URI, null)));    
    assertNotNull(topic);
    
    names = new ArrayList<TopicNameIF>(topic.getTopicNames());
    assertEquals(5, names.size());
    for (TopicNameIF name : names) {
      if (name.getScope().size() == 0) {        
        assertEquals(newName, name.getValue());
      }
    } 
  }
}
