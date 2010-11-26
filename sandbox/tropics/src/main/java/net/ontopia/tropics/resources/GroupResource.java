package net.ontopia.tropics.resources;

import java.util.Set;

import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.utils.AssociationBuilder;
import net.ontopia.tropics.groups.GroupsIndex;
import net.ontopia.tropics.groups.GroupsIndexFactory;
import net.ontopia.tropics.utils.TopicCreatorForTopicMapIds;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

public class GroupResource extends BaseResource {
  
  @Get("xtm2|xml")
  public Representation getXTM() throws ResourceException {    
    TopicMapIF tm = getTopicMapGroupFromPath();    
    
    if (tm == null) {
      getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
      return null;
    } 
    
    return new StringRepresentation(TM_UTILS.writeToXTM(tm));
  }
  
  private TopicMapIF getTopicMapGroupFromPath() {
    String hostRef = getRequest().getHostRef().toString();
    String groupId = getRequestAttributes().get("groupId").toString();
        
    // Get contents of requested group.
    GroupsIndex groupsIdx = GroupsIndexFactory.getGroupsIndex();
    Set<String> topicMapIds = groupsIdx.getContents(groupId);
    if (topicMapIds == null) return null;
    
    // Create Topic Map Store for result topic map.
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();    
    store.setBaseAddress(URILocator.create(hostRef + "/api/v1/groups/" + groupId));
    
    // Get result topic map and Topic Creator.
    TopicMapIF tm = store.getTopicMap();
    TopicCreatorForTopicMapIds topicCreator = new TopicCreatorForTopicMapIds(tm, hostRef);
    
    // Add Topic Maps belonging to the group to the result topic map.
    try {
      topicCreator.init();
      for (String topicMapId : topicMapIds) {
        topicCreator.apply(topicMapId);
      }      
    } catch (ConstraintViolationException e) {
      e.printStackTrace();
      return null;
    }
    
    // Add topic map group itself to result topic map.
    createTopicMapGroupType(tm);    
    TopicIF group = createTopicMapGroupInstance(tm, groupId);
    
    TopicMapBuilderIF tmBuilder = tm.getBuilder();
    TopicIF topicmapOccType = (TopicIF) tm.getObjectByItemIdentifier(URILocator.create(hostRef + "/api/v1/topics/topicmap"));
    tmBuilder.makeOccurrence(group, topicmapOccType, hostRef + "/api/v1/topicmaps/" + groupId);

    // Create necessary supporting types for linking group to topic maps
    TopicIF containsAssocType = tmBuilder.makeTopic();
    tmBuilder.makeTopicName(containsAssocType, "contains");
    containsAssocType.addItemIdentifier(URILocator.create(hostRef + "/api/v1/topics/contains"));

    TopicIF containerRoleType = tmBuilder.makeTopic();
    tmBuilder.makeTopicName(containerRoleType, "container");
    containerRoleType.addItemIdentifier(URILocator.create(hostRef + "/api/v1/topics/container"));

    TopicIF itemRoleType = tmBuilder.makeTopic();
    tmBuilder.makeTopicName(itemRoleType, "item");
    itemRoleType.addItemIdentifier(URILocator.create(hostRef + "/api/v1/topics/item"));

    // Link group to the topic maps it contains.
    AssociationBuilder assocBuilder = new AssociationBuilder(containsAssocType, containerRoleType, itemRoleType);
    ClassInstanceIndexIF ciIdx = (ClassInstanceIndexIF) tm.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
    for (TopicIF topicMapTopic : ciIdx.getTopics((TopicIF) tm.getObjectByItemIdentifier(URILocator.create(hostRef + "/api/v1/topics/topicmap")))) {
      assocBuilder.makeAssociation(group, topicMapTopic);
    }
    
    return tm;
  }
}
