package ontopoly.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import ontopoly.model.PSI;
import ontopoly.model.Topic;
import ontopoly.model.TopicMap;
import ontopoly.model.TopicType;


public class OntopolyUtils {

  private OntopolyUtils() {    
  }
  
  /**
   * Returns the topic type that is the default topic type amongst the topic types of the topic.
   * @param topic
   * @param roleField
   * @return
   */
  public static TopicType getDefaultTopicType(Topic topic) {
    List topicTypes = topic.getTopicTypes();
    int size = topicTypes.size();
    if (size == 1) 
      return (TopicType)topicTypes.get(0);
    else if (size == 0) {
      // HACK: add untyped-topic to list of topic types if no other type exist
      TopicMap tm = topic.getTopicMap();
      return new TopicType(OntopolyModelUtils.getTopicIF(tm, PSI.ON_UNTYPED_TOPIC), tm);
    }

    TopicType ontologyType = null;
    TopicType instanceType = null;
    int ontologyTypeWeight = 0;
    // int instanceTypeWeight = 0;
    for (int i=0; i < size; i++) {
      TopicType topicType = (TopicType)topicTypes.get(i);
      if (topicType.isSystemTopic()) {
        Collection psis = topicType.getTopicIF().getSubjectIdentifiers();
        if (psis.contains(PSI.ON_TOPIC_TYPE)) {
          if (ontologyTypeWeight < 10) {
            ontologyType = topicType;
            ontologyTypeWeight = 10;
          }
        } else if (psis.contains(PSI.ON_ASSOCIATION_TYPE)) {
          if (ontologyTypeWeight < 7) {
            ontologyType = topicType;
            ontologyTypeWeight = 7;
          }
        } else if (psis.contains(PSI.ON_ROLE_TYPE)) {
          if (ontologyTypeWeight < 6) {
            ontologyType = topicType;
            ontologyTypeWeight = 6;
          }
        } else if (psis.contains(PSI.ON_NAME_TYPE)) {
          if (ontologyTypeWeight < 5) {
            ontologyType = topicType;
            ontologyTypeWeight = 5;
          }
        } else if (psis.contains(PSI.ON_OCCURRENCE_TYPE)) {
          if (ontologyTypeWeight < 4) {
            ontologyType = topicType;
            ontologyTypeWeight = 4;
          }
        } else if (psis.contains(PSI.ON_IDENTITY_TYPE)) {
          if (ontologyTypeWeight < 3) {
            ontologyType = topicType;
            ontologyTypeWeight = 3;
          }
        } else if (psis.contains(PSI.ON_SYSTEM_TOPIC)) {
          if (ontologyTypeWeight < 1) {
            ontologyType = topicType;
            ontologyTypeWeight = 1;
          }
        } else {
          if (ontologyTypeWeight < 2) {
            ontologyType = topicType;
            ontologyTypeWeight = 2;
          }
        }
      } else {
        instanceType = topicType;
        // instanceTypeWeight = 1;
      }
    }
    
    if (instanceType == null)
      return ontologyType;
    else
      return instanceType;
  }
  
  public static boolean filterTopicByAdministratorRole(Topic topic) {
    return true;
  }

  public static boolean filterTopicByAnnotationRole(Topic topic) {
    return !topic.isPrivateSystemTopic() || topic.isOntologyType();
  }
  
  public static boolean filterTopicByDefaultRole(Topic topic) {
    return !topic.isPrivateSystemTopic();
  }
  
  public static void filterTopicsByAdministratorRole(Collection<? extends Topic> topics) {
    // none should be excluded
  }
     
  public static void filterTopicsByAnnotationRole(Collection<? extends Topic> topics) {
    // WARNING: collection must be mutable and iterator support .remove()
    Iterator<? extends Topic> iter = topics.iterator();
    while (iter.hasNext()) {
      if (!filterTopicByAnnotationRole(iter.next()))
        iter.remove();
    }
  }
  
  public static void filterTopicsByDefaultRole(Collection<? extends Topic> topics) {
    // WARNING: collection must be mutable and iterator support .remove()
    Iterator iter = topics.iterator();
    while (iter.hasNext()) {
      Topic topic = (Topic)iter.next();
      if (!filterTopicByDefaultRole(topic))
        iter.remove();
    }
  }
  
}
