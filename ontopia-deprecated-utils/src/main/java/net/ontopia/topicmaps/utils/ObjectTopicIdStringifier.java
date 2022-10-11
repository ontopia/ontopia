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

import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;

/**
 * INTERNAL: Stringifier that returns the object id of the topic that 
 * belongs to this tmobject.
 */

@Deprecated
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





