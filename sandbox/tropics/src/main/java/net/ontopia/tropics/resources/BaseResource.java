package net.ontopia.tropics.resources;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;

import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.utils.TopicMapSynchronizer;
import net.ontopia.tropics.TropicsApplicationV1;
import net.ontopia.tropics.groups.GroupsIndexFactory;
import net.ontopia.tropics.utils.TopicMapUtils;

import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

public class BaseResource extends ServerResource {
  protected static final TopicMapUtils TM_UTILS = new TopicMapUtils();
    
  protected TopicMapIF createTopicMapForTopics(TopicMapIF tm, Reference baseRef, Collection<TopicIF> topics) {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    
    try {
      store.setBaseAddress(new URILocator(baseRef.toString()));
    } catch (MalformedURLException e) {
      e.printStackTrace();
      return null;
    }
    
    TopicMapIF resultTM = store.getTopicMap();
    
    for (TopicIF topic : topics) {
      TopicMapSynchronizer.update(resultTM, topic);
    }
      
    return resultTM;
  }
  
  protected TopicMapIF getTopicMapFromParameter(String topicMapInclude) throws ResourceException {
    if (!topicMapInclude.startsWith("/topicmaps/")) return null;
    
    String topicMapId = topicMapInclude.substring(topicMapInclude.lastIndexOf('/') + 1);

    if (topicMapId.startsWith("group.")) {
      return getTopicMapGroup(topicMapId);
    } else {
      try {
        return TM_UTILS.getTopicMap(getRequest().getHostRef().toString(), topicMapId);
      } catch (IOException e) {
        throw new ResourceException(e);
      }
    }
  }

  protected String getTopicMapId() {
    return getRequestAttributes().get("topicmapId").toString();
  }

  protected TopicMapIF getTopicMapFromPath() throws ResourceException {
    String topicMapId = getTopicMapId();
    
    if (topicMapId.startsWith("group.")) {
      return getTopicMapGroup(topicMapId);
    } else {
      try {
        return TM_UTILS.getTopicMap(getRequest().getHostRef().toString(), topicMapId);
      } catch (IOException e) {
        throw new ResourceException(e);
      }
    }
  }
  
  private TopicMapIF getTopicMapGroup(String groupId) {
    return GroupsIndexFactory.getGroupsIndex().getGroupAsTopicMap(getRequest().getHostRef().toString(), groupId);
  }

  protected TopicMapRepositoryIF getTopicMapRepository() {  
    return ((TropicsApplicationV1) getApplication()).getTopicMapRepository();  
  }

  protected TopicIF createTopicMapGroupType(TopicMapIF tm) {
    TopicMapBuilderIF tmBuilder = tm.getBuilder();
    
    TopicIF groupType = tmBuilder.makeTopic();
    tmBuilder.makeTopicName(groupType, "Group");
    groupType.addItemIdentifier(URILocator.create(getRequest().getHostRef() + "/api/v1/topics/topicmapgroup"));
    
    return groupType;
  }  
  
  protected TopicIF createTopicMapGroupInstance(TopicMapIF tm, String groupId) {
    TopicMapBuilderIF tmBuilder = tm.getBuilder();
    
    TopicIF groupType = (TopicIF) tm.getObjectByItemIdentifier(URILocator.create(getRequest().getHostRef() + "/api/v1/topics/topicmapgroup"));
    TopicIF group = tmBuilder.makeTopic(groupType);
    tmBuilder.makeTopicName(group, groupId + " group");
    group.addItemIdentifier(URILocator.create(getRequest().getHostRef() + "/api/v1/groups/" + groupId));

    return group;
  }
}
