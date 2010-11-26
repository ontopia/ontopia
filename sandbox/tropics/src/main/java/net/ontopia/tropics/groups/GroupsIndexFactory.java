package net.ontopia.tropics.groups;

import java.util.HashSet;
import java.util.Set;

import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;
import net.ontopia.topicmaps.entry.TopicMaps;
import net.ontopia.tropics.utils.Predicate;
import net.ontopia.tropics.utils.TopicMapUtils;

public class GroupsIndexFactory {
  
  private static final GroupsIndex GROUPS_IDX = new GroupsIndex();
  
  static {
    final Set<String> allTopicMaps = new HashSet<String>();
    
    TopicMapRepositoryIF tmRepository = TopicMaps.getRepository();
    TopicMapUtils tmUtils = new TopicMapUtils();
    tmUtils.iterateTopicMapIds(tmRepository, new Predicate() {      
      public void apply(String value) {
        allTopicMaps.add(value);
      }
    });
    
    GROUPS_IDX.addGroup("group.all", allTopicMaps);
  }
  
  public static GroupsIndex getGroupsIndex() {
    return GROUPS_IDX;
  }
}
