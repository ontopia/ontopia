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

package net.ontopia.topicmaps.utils.deciders;

import java.util.Iterator;
import java.util.function.Predicate;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.topicmaps.core.VariantNameIF;

/**
 * INTERNAL: Decider that allows the user to filter out chosen objects
 * used for testing the filtering of exporters.
 */
public class TMDecider implements Predicate<Object> {

  @Override
  public boolean test(Object object) {

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
    
    Iterator<TopicNameIF> it = topic.getTopicNames().iterator();
    if (it.hasNext()) {
      TopicNameIF name = it.next();
      return name.getValue();
    }
    return null;
  }
}
