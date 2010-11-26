package net.ontopia.tropics.resources;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;

import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.AssociationWalker;
import net.ontopia.tropics.utils.TMTestUtils;

import org.junit.Test;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

public class GroupTest extends BasicTropicsTest {

  @Test
  public void testNonExistingGroup() {
    ClientResource clientResource = new ClientResource(TMTestUtils.GROUPS_URI + "electronics");    
    
    try {
      clientResource.get();
    } catch (ResourceException e) {
      return;
    }
    
    fail();
  }
  
  @Test
  public void testExistingGroup() {
    ClientResource clientResource = new ClientResource(TMTestUtils.ALL_URI);    
    
    String response = null;
    try {
      response = get(clientResource);
    } catch (ResourceException e) {
      e.printStackTrace();
      fail();
    }
    
    System.out.println(response);
    
    TopicMapIF tm = TMTestUtils.readFromXTM(response, TMTestUtils.TOPICMAPS_URI);
    
    TopicIF italianOperaTopic = (TopicIF) tm.getObjectByItemIdentifier(URILocator.create("http://localhost:8182/api/v1/topicmaps/ItalianOpera"));
    assertNotNull(italianOperaTopic);
    
    TopicIF topicMapType = (TopicIF) tm.getObjectByItemIdentifier(URILocator.create("http://localhost:8182/api/v1/topics/topicmap"));
    assertNotNull(topicMapType);

    Collection<TopicIF> apisTypes = italianOperaTopic.getTypes();
    assertTrue(apisTypes.contains(topicMapType));
    
    assertNotNull(tm.getObjectByItemIdentifier(URILocator.create("http://localhost:8182/api/v1/topicmaps/lovecraft")));
    
    TopicIF allGroupTopic = (TopicIF) tm.getObjectByItemIdentifier(URILocator.create("http://localhost:8182/api/v1/groups/group.all"));
    assertNotNull(allGroupTopic);
    Collection<OccurrenceIF> occurrences = allGroupTopic.getOccurrences();
    boolean found = false;
    for (OccurrenceIF occurrence : occurrences) {
      if (occurrence.getValue().equals("http://localhost:8182/api/v1/topicmaps/group.all")) found = true;
    }
    assertTrue(found);
    
    TopicIF groupType = (TopicIF) tm.getObjectByItemIdentifier(URILocator.create("http://localhost:8182/api/v1/topics/topicmapgroup"));
    assertNotNull(groupType);

    Collection<TopicIF> allGroupTypes = allGroupTopic.getTypes();
    assertTrue(allGroupTypes.contains(groupType));
    
    TopicIF containsAssocType = (TopicIF) tm.getObjectByItemIdentifier(URILocator.create("http://localhost:8182/api/v1/topics/contains"));
    TopicIF leftRoleType = (TopicIF) tm.getObjectByItemIdentifier(URILocator.create("http://localhost:8182/api/v1/topics/container"));
    TopicIF rightRoleType = (TopicIF) tm.getObjectByItemIdentifier(URILocator.create("http://localhost:8182/api/v1/topics/item"));
    assertTrue(new AssociationWalker(containsAssocType, leftRoleType, rightRoleType).isAssociated(allGroupTopic, italianOperaTopic));
  }
}
