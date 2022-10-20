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

package net.ontopia.topicmaps.utils;

import java.util.Collection;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;

/**
 * INTERNAL: Comparator that first sorts by type then by scope,
 * where untyped base names are ordered before typed ones.
 *
 * @since 3.0
 */
public class TopicNameComparator extends ScopedIFComparator<TopicNameIF> {
  
  TopicNameComparator(Collection<TopicIF> scope) {
    super(scope);
  }
  
  @Override
  public int compare(TopicNameIF o1, TopicNameIF o2) {
    TopicMapIF tm = o1.getTopicMap();

    TopicIF t1 = o1.getType();
    TopicIF t2 = o2.getType();
    
    // check for default type
    TopicIF untypedname = tm.getTopicBySubjectIdentifier(PSI.getSAMNameType());
    if (untypedname == null) {
      t1 = null;
      t2 = null;
    } else {
      if (untypedname.equals(t1)) {
        t1 = null;
      }
      if (untypedname.equals(t2)) {
        t2 = null;
      }
    }

    // untyped should sort before typed
    if (t1 == null) {
      if (t2 != null) {
        return -1;
      }
    } else {
      if (t2 == null) {
        return 1;
      }
    }
    
    return super.compare(o1, o2);
  }

}
