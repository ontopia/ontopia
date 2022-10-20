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

package net.ontopia.topicmaps.query.parser;

import java.util.Map;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

/**
 * INTERNAL: Represents a parsed MERGE statement.
 */
public class MergeStatement extends ModificationStatement {

  @Override
  public int doStaticUpdates(TopicMapIF topicmap, Map arguments)
    throws InvalidQueryException {
    TopicIF topic1 = (TopicIF) getValue(litlist.get(0), arguments);
    TopicIF topic2 = (TopicIF) getValue(litlist.get(1), arguments);
    if (!topic1.equals(topic2)) {
      topic1.merge(topic2);
    }
    return 1;
  }

  @Override
  public int doUpdates(QueryMatches matches)
    throws InvalidQueryException {
    int merges = 0;

    Map parameters = matches.getQueryContext().getParameters();
    Object arg1 = getValue(litlist.get(0), parameters);
    int varix1 = getIndex(arg1, matches);
    Object arg2 = getValue(litlist.get(1), parameters);
    int varix2 = getIndex(arg2, matches);
    
    for (int row = 0; row <= matches.last; row++) {
      if (varix1 != -1) {
        arg1 = matches.data[row][varix1];
      }

      if (varix2 != -1) {
        arg2 = matches.data[row][varix2];
      }

      TopicIF topic1 = (TopicIF) arg1;
      TopicIF topic2 = (TopicIF) arg2;
      if (!topic1.equals(topic2) &&
          topic1.getTopicMap() != null &&
          topic2.getTopicMap() != null) {
        topic1.merge(topic2);
        merges++;
      }
    }

    return merges;
  }

  @Override
  public String toString() {
    String str = "merge " + toStringLitlist();
    if (query != null) {
      str += "\nfrom" + query.toStringFromPart();
    }
    return str;
  }
}