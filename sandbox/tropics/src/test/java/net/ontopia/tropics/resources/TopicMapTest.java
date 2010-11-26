package net.ontopia.tropics.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;

import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.tropics.utils.TMTestUtils;

import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

public class TopicMapTest extends BasicTropicsTest {
    
  @Test
  public void testLovecraftXTM2() {
    ClientResource client = new ClientResource(TMTestUtils.LOVECRAFT_URI);
    
    String response = get(client);
    System.out.println(response);
    TopicMapIF tm = TMTestUtils.readFromXTM(response, TMTestUtils.LOVECRAFT_URI);  
    
    checkContents(tm);
  }

  @Test
  public void testLovecraftJTM() {
    ClientResource client = new ClientResource(TMTestUtils.LOVECRAFT_URI);
    client.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(MediaType.APPLICATION_JSON));
    
    String response = get(client);
    TopicMapIF tm = TMTestUtils.readFromJTM(response, TMTestUtils.LOVECRAFT_URI);       
    
    checkContents(tm);
  }

  @Test
  public void testNonExistantTopicMap() {
    ClientResource client = new ClientResource(TMTestUtils.TOPICMAPS_URI + "poe");

    try {
      client.get();
      fail();
    } catch (ResourceException e) {
      assertEquals(Status.CLIENT_ERROR_NOT_FOUND, e.getStatus());   
    }
  }
    
  private void checkContents(TopicMapIF tm) {
    try {
      assertNotNull(tm.getObjectByItemIdentifier(new URILocator("http://localhost:8182/api/v1/topics/cthulhu")));
      assertNotNull(tm.getObjectByItemIdentifier(new URILocator("http://localhost:8182/api/v1/topics/great_old_one")));
      assertNotNull(tm.getObjectByItemIdentifier(new URILocator("http://localhost:8182/api/v1/topics/rlyeh")));
      assertNotNull(tm.getObjectByItemIdentifier(new URILocator("http://localhost:8182/api/v1/topics/city")));
      assertNotNull(tm.getObjectByItemIdentifier(new URILocator("http://localhost:8182/api/v1/topics/citizen")));
      assertNotNull(tm.getObjectByItemIdentifier(new URILocator("http://localhost:8182/api/v1/topics/lives-in")));
      
      assertEquals(1, tm.getAssociations().size());

      assertNull(tm.getObjectByItemIdentifier(new URILocator("http://localhost:8182/api/v1/topics/indiana_jones")));
    } catch (MalformedURLException e) {
      e.printStackTrace();
      fail();
    }
  }
  
}
