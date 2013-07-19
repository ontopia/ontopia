/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.impl.utils;

import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.utils.ObjectUtils;
import net.ontopia.topicmaps.xml.InvalidTopicMapException;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.topicmaps.utils.KeyGenerator;

/**
 * INTERNAL: Topic map object deletion utilities.
 */
public class ReificationUtils {

  /**
   * INTERNAL: Make the reifier topic reify the reifiable object.
   *
   * @return the topic that ends up being the reifier
   * @throws InvalidTopicMapException if the reifier already reifies a
   *         different object
   */
  public static TopicIF reify(ReifiableIF reifiable, TopicIF reifier) {
    if (reifier == null)
      return null;
    
    ReifiableIF existingReified = reifier.getReified();
    if (existingReified != null &&
        ObjectUtils.different(existingReified, reifiable)) {
      String key1 = KeyGenerator.makeKey(reifiable);
      String key2 = KeyGenerator.makeKey(existingReified);
      if (!key1.equals(key2))
        throw new InvalidTopicMapException("The topic " + reifier +
           " cannot reify more than one reifiable object. 1: " + existingReified +
           " 2: " + reifiable);
      MergeUtils.mergeInto(reifiable, existingReified);
    }
    
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
