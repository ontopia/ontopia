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

package net.ontopia.topicmaps.core;

import java.util.Objects;
import net.ontopia.topicmaps.utils.KeyGenerator;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.topicmaps.xml.InvalidTopicMapException;

/**
 * INTERNAL: Thrown when a reifiable object is reified by a topic that
 * already reifies another reifiable object.</p>
 *
 * Extends ConstraintViolationException but does not provide additional
 * methods; this is because the purpose is just to provide a different
 * exception, to allow API users to handle them differently.</p>
 *
 * @since 5.4.0
 */

public class DuplicateReificationException extends ConstraintViolationException {

  public DuplicateReificationException(String message) {
    super(message);
  }

  /**
   * INTERNAL: Checks reification logic
   *
   * @return true if all operations are done and no more actions are needed
   * @throws InvalidTopicMapException if the reifier already reifies a
   *         different object
   */
  public static boolean check(ReifiableIF reifiable, TopicIF reifier) {
    if (reifier == null) {
      return false;
    }
    
    ReifiableIF existingReified = reifier.getReified();
    if (existingReified != null &&
        !Objects.equals(existingReified, reifiable)) {
      if (existingReified instanceof TopicMapIF) {
        throw new DuplicateReificationException("The topic " + reifier +
           " cannot reify more than one reifiable object. 1: " + existingReified +
           " 2: " + reifiable);
      }
      String key1 = KeyGenerator.makeKey(reifiable);
      String key2 = KeyGenerator.makeKey(existingReified);
      if (!key1.equals(key2)) {
        throw new DuplicateReificationException("The topic " + reifier +
           " cannot reify more than one reifiable object. 1: " + existingReified +
           " 2: " + reifiable);
      }
      MergeUtils.mergeInto(reifiable, existingReified);
    }
    
    TopicIF existingReifier = reifiable.getReifier();
    if (existingReifier != null &&
        !Objects.equals(existingReifier, reifier)) {
      MergeUtils.mergeInto(reifier, existingReifier);
      return true;
    }
    return false;
  }
}
