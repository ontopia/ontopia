
// $Id: TopicComparators.java,v 1.15 2008/06/12 14:37:24 geir.gronmo Exp $

package net.ontopia.topicmaps.utils;

import java.util.*;
import net.ontopia.utils.*;

/**
 * INTERNAL: A collection of topic related comparators.
 */
public class TopicComparators {

  private TopicComparators() {
    // don't call me
  }
  
  // Compare the stringified sortnames of the occurrence types
  protected static Comparator tyc = getTypedIFComparator(Collections.EMPTY_SET);

  public static Comparator getTopicNameComparator(Collection scope) {
    return new StringifierComparator(TopicStringifiers.getTopicNameStringifier(scope));
  }
  
  public static Comparator getTypedIFComparator() {
    return tyc;
  }

  public static Comparator getTypedIFComparator(Collection scope) {
    return new TypedIFComparator(new StringifierComparator(TopicStringifiers.getTopicNameStringifier(scope)));
  }

  public static Comparator getCaseInsensitiveComparator(StringifierIF stringifier) {
    //! return new StringifierComparator(new GrabberStringifier(new GrabberGrabber(new StringifierGrabber(stringifier), new UpperCaseGrabber())));
    // NOTE: 1.3.4 - Replaced above with new and faster comparator:
    return new CaseInsensitiveStringifierComparator(stringifier);
  }

  /**
   * INTERNAL: Case in-sensitive string comparator that is able to
   * handle null values.
   */ 
  public static class CaseInsensitiveStringifierComparator implements Comparator {
    protected StringifierIF stringifier;
    public CaseInsensitiveStringifierComparator(StringifierIF stringifier) {
      this.stringifier = stringifier;
    }
    public int compare(Object obj1, Object obj2) {
      String str1 = stringifier.toString(obj1);
      String str2 = stringifier.toString(obj2);      

      if (str1 == null)
        return (str2 == null ? 0 : 1);
      else
        if (str2 == null)
          return -1;
        else
          return str1.compareToIgnoreCase(str2);
    }
  }
}
