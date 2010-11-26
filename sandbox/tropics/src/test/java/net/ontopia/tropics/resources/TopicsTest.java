package net.ontopia.tropics.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.tropics.utils.TMTestUtils;
import net.ontopia.tropics.utils.URIUtils;

import org.junit.Test;
import org.restlet.resource.ClientResource;

public class TopicsTest extends BasicTropicsTest {

  @SuppressWarnings("serial")
  @Test
  public void testGetGreatOldOnes() throws UnsupportedEncodingException, MalformedURLException {
    Map<String, String> params = new HashMap<String, String>() {{
      put("has-type", "/topics/great_old_one");
      put("tms-include", TMTestUtils.LOVECRAFT_TM);      
    }};
    
    ClientResource client = new ClientResource(URIUtils.buildURI(TMTestUtils.TOPICS_URI, params));
    String response = get(client);

    TopicMapIF tm = TMTestUtils.readFromXTM(response, TMTestUtils.TOPICS_URI);
    assertEquals(8, tm.getTopics().size());
    
    TopicIF gooType = (TopicIF) tm.getObjectByItemIdentifier(new URILocator("http://localhost:8182/api/v1/topics/great_old_one"));
    assertNotNull(gooType);
    
    Collection<TopicIF> gooInstances = TMTestUtils.getLengthCheckedInstances(tm, gooType, 2);
    
    Set<LocatorIF> expectedGOOs = new HashSet<LocatorIF>(
        Arrays.asList(new URILocator("http://localhost:8182/api/v1/topics/cthulhu"),
                      new URILocator("http://localhost:8182/api/v1/topics/yig")));
    
    TMTestUtils.checkAllInstancesExpected(gooInstances, expectedGOOs);    
  }
  
}
