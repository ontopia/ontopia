package net.ontopia.tropics.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.tropics.utils.TMTestUtils;

import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.resource.ClientResource;

public class TopicMapsTest extends BasicTropicsTest {

  @Test
  public void testTopicMapsAsXTM() {
    ClientResource clientResource = new ClientResource(TMTestUtils.TOPICMAPS_URI);
    clientResource.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(MediaType.APPLICATION_XML));

    String response = get(clientResource);
    System.out.println(response);
    TopicMapIF tm = TMTestUtils.readFromXTM(response, TMTestUtils.TOPICMAPS_URI);

    checkContents(tm);
  }

  private void checkContents(TopicMapIF tm) {
    assertNotNull(tm.getObjectByItemIdentifier(URILocator.create("http://localhost:8182/api/v1/topicmaps/ItalianOpera")));
    assertNotNull(tm.getObjectByItemIdentifier(URILocator.create("http://localhost:8182/api/v1/topicmaps/lovecraft")));
    assertNotNull(tm.getObjectByItemIdentifier(URILocator.create("http://localhost:8182/api/v1/groups/group.all")));
      
    TopicIF lovecraft_topicmap = (TopicIF) tm.getObjectByItemIdentifier(URILocator.create("http://localhost:8182/api/v1/topicmaps/lovecraft"));
    List<TopicIF> types = new ArrayList<TopicIF>(lovecraft_topicmap.getTypes());
    assertEquals(1, types.size());
    Set<LocatorIF> itemIdentifiers = new HashSet<LocatorIF>(types.get(0).getItemIdentifiers());
    assertTrue(itemIdentifiers.contains(URILocator.create("http://localhost:8182/api/v1/topics/topicmap")));

    assertNull(tm.getObjectByItemIdentifier(URILocator.create("http://localhost:8182/api/v1/topicmaps/crystal_skull")));
  }
}
