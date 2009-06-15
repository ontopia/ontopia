
// $Id: ReificationUtils.java,v 1.4 2008/06/13 10:25:27 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.utils;

import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.utils.ObjectUtils;
import net.ontopia.topicmaps.xml.InvalidTopicMapException;

/**
 * INTERNAL: Topic map object deletion utilities.
 */

public class ReificationUtils {

	/**
	 * INTERNAL:Make the reifier reify the reifiable topic.
	 *
	 * @returns the topic that ends up being the reifier
	 */
  public static TopicIF reify(ReifiableIF reifiable, TopicIF reifier) {
    if (reifier == null)
      return null;
    
    ReifiableIF existingReified = reifier.getReified();
    if (existingReified != null &&
        ObjectUtils.different(existingReified, reifiable))
      throw new InvalidTopicMapException("The topic " + reifier +
       " cannot reify more than one reifiable object. 1: " + existingReified +
       " 2: " + reifiable);
    
    TopicIF existingReifier = reifiable.getReifier();
    if (existingReifier != null &&
        ObjectUtils.different(existingReifier, reifier)) {
      MergeUtils.mergeInto(existingReifier, reifier);
			return existingReifier;
    } else {
      reifiable.setReifier(reifier);
			return reifier;
		}

  }
}
