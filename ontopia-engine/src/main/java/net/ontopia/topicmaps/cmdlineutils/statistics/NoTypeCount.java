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

package net.ontopia.topicmaps.cmdlineutils.statistics;

import java.util.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.*;


/**
 * 
 */

public class NoTypeCount {

  private Collection returntopics, returnoccurs, returnassocs;
  private TopicMapIF tm;
  
  public NoTypeCount(TopicMapIF tm) {
    this.tm = tm;
    returntopics = new ArrayList();
    returnoccurs = new ArrayList();
    returnassocs = new ArrayList();
  }

  /**
   * Traverses the topicmap and finds the topics, associations and
   * occurrences that has no types.
   */

  public void traverse() throws NullPointerException{
    //Checks all topics in the Topic Map, and for each topic
    //checks it's occurrences.
    Collection topics = tm.getTopics();
    Iterator it = topics.iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF)it.next();
      Collection topictypes = topic.getTypes();
      Collection occurrences = topic.getOccurrences();

      if (topictypes.size() == 0) {
        returntopics.add(topic);
      }
      //The topics occurrences.
      Iterator itr = occurrences.iterator();
      while (itr.hasNext()) {
        OccurrenceIF o = (OccurrenceIF)itr.next();
        TopicIF t = o.getType();
        if (t == null) {
          returnoccurs.add(o);
        }
      }
    }

    //Checks all the associations in the Topic Map.
    Collection assocs = tm.getAssociations();
    it = assocs.iterator();
    while (it.hasNext()) {
      AssociationIF a = (AssociationIF)it.next();
      TopicIF t  = a.getType();
      if (t == null) {
        returnassocs.add(a);
      }
    }
  }//end NoTypeCount


  /**
   * Returns a Collection of Topics which have no type.
   */
  public Collection getNoTypeTopics() {
    return returntopics;
  }

  /**
   * Returns a Collection of Occurrences which have no type.
   */
  public Collection getNoTypeOccurrences() {
    return returnoccurs;
  }

  /**
   * Returns a Collection of Associations which have no type.
   */
  public Collection getNoTypeAssociations() {
    return returnassocs;
  }


}//end class





