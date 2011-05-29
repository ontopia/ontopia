
package net.ontopia.topicmaps.utils.deciders;

import net.ontopia.utils.DeciderIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TypedIF;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * INTERNAL: Decider that allows the user to filter out chosen objects
 * used for testing the filtering of exporters.
 */
public class TMDecider implements DeciderIF {

  public TMDecider() {
  }

  public boolean ok(Object object) {

    // a topic can be disallowed by being named "Disallowed Topic"
    // a typed object can be disallowed by being typed with a topic named
    //   "Disallowed Type", but the typing topic itself will be accepted

    if (object instanceof TopicIF)
      return !isTopicName((TopicIF) object, "Disallowed Topic");

    if (object instanceof TypedIF) {
      TypedIF typed = (TypedIF) object;
      boolean filtered = typed == null ||
                         !isTopicName(typed.getType(), "Disallowed Type");
      if (!filtered)
        return false;
    }

    if (object instanceof VariantNameIF) 
      return !((VariantNameIF) object).getValue().equals("Disallowed Variant");

    if (object instanceof TopicNameIF) 
      return !((TopicNameIF) object).getValue().equals("Disallowed Name");
    
    return true;
  }

  private static boolean isTopicName(TopicIF topic, String value) {
    String v = getTopicName(topic);
    return v != null && v.equals(value);
  }
  
  private static String getTopicName(TopicIF topic) {
    if (topic == null)
      return null;
    
    Iterator it = topic.getTopicNames().iterator();
    if (it.hasNext()) {
      TopicNameIF name = (TopicNameIF) it.next();
      return name.getValue();
    }
    return null;
  }
}
