
package net.ontopia.topicmaps.utils;

import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;

/**
 * INTERNAL: Stringifier that returns the object id of the topic that 
 * belongs to this tmobject.
 */

public class ObjectTopicIdStringifier {


  public static String toString(TMObjectIF tmobject) {
    if (tmobject == null) return "null";
    else if (tmobject instanceof TopicNameIF)
      return ((TopicNameIF)tmobject).getTopic().getObjectId();
    else if (tmobject instanceof VariantNameIF)
      return ((VariantNameIF)tmobject).getTopic().getObjectId();
    else if (tmobject instanceof OccurrenceIF)
      return ((OccurrenceIF)tmobject).getTopic().getObjectId();
    else if (tmobject instanceof TopicIF)
      return ((TopicIF)tmobject).getObjectId();
    else return "null";
  }

  public static TopicIF getTopic(TMObjectIF tmobject) {
    if (tmobject == null) return null;
    else if (tmobject instanceof TopicNameIF)
      return ((TopicNameIF)tmobject).getTopic();
    else if (tmobject instanceof VariantNameIF)
      return ((VariantNameIF)tmobject).getTopic();
    else if (tmobject instanceof OccurrenceIF)
      return ((OccurrenceIF)tmobject).getTopic();
    else if (tmobject instanceof TopicIF)
      return ((TopicIF)tmobject);
    else return null;

  }

}





