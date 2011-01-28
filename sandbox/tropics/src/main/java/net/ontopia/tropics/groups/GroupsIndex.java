package net.ontopia.tropics.groups;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.tropics.utils.TopicMapUtils;


public class GroupsIndex {

  /**
   * Main index, contains for each group which topic maps it contains.
   */
  private final Map<String, Set<String>> index = new HashMap<String, Set<String>>();
  
  /**
   * Reverse of main index, contains for each topic map which group it belongs to.  
   */
  private final Map<String, Set<String>> rIndex = new HashMap<String, Set<String>>(); 
  
  /**
   * Cache for merged topic maps, as merging is an expensive operation.
   */
  private final Map<String, TopicMapIF> tmCache = new HashMap<String, TopicMapIF>();
  
  
  public Collection<String> getGroupIds() {
    return Collections.unmodifiableCollection(index.keySet());
  }

  public Set<String> getContents(String groupId) {
    return Collections.unmodifiableSet(index.get(groupId));
  }
  
  public synchronized void addGroup(String groupId, Set<String> groupItems) {
    if (!index.containsKey(groupId)) {
      index.put(groupId, groupItems);
    }
    
    for (String groupItem : groupItems) {
      Set<String> groupIds = rIndex.get(groupItem); 
      if (groupIds == null) {
        groupIds = new HashSet<String>();
        rIndex.put(groupItem, groupIds);
      }
      groupIds.add(groupId);
    }
  }

  public synchronized TopicMapIF getGroupAsTopicMap(String hostRef, String groupId) {    
    Set<String> groupContents = index.get(groupId);
    if (groupContents == null) return null;
    
    TopicMapIF cachedTm = tmCache.get(groupId);
    if (cachedTm == null) {
      long start = System.currentTimeMillis();
      cachedTm = createMergedTopicMap(hostRef, groupContents);
      System.out.println("Building merged topicmap took " + (System.currentTimeMillis() - start) + " millis.");
      
      tmCache.put(groupId, cachedTm);
    } 
    
    return cachedTm.getTopicMap();
  }

  public void updated(String topicMapId) {
    Set<String> groupIds = rIndex.get(topicMapId);
    if ((groupIds == null) || (groupIds.size() == 0)) return;
        
    for (String groupId : groupIds) {
      tmCache.remove(groupId);
    }
  }
  
  private TopicMapIF createMergedTopicMap(String hostRef, Set<String> topicMapIds) {
    TopicMapUtils tmUtils = new TopicMapUtils();

    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    TopicMapIF tm = store.getTopicMap();
    for (String topicMapId : topicMapIds) {
      try {
        TopicMapIF tmPart = tmUtils.getTopicMap(hostRef, topicMapId);
        MergeUtils.mergeInto(tm, tmPart);
      } catch (IOException e) {
        e.printStackTrace();
        return null;
      }
    }
    store.commit();
    
    return tm;
  }
}
