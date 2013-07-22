/*
 * #!
 * Ontopia Webed
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

package net.ontopia.topicmaps.webed.impl.actions.tmobject;

import java.util.Collection;
import java.util.Iterator;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.impl.basic.Constants;

/**
 * PUBLIC: Action for deleting objects from a topic map by doing 
 * tolog queries to find them. All objects found, in all columns, are
 * deleted using TMObjectIF.remove().
 *
 * @since 2.0
 */
public class TologDeleteFixed implements ActionIF {

  public void perform(ActionParametersIF params, ActionResponseIF response) {
    QueryProcessorIF processor =
      QueryUtils.getQueryProcessor((TopicMapIF) params.get(1));
    Collection queries = params.getCollection(0);
    
    try {
      // iterate over the queries and run them one by one
      Iterator it = queries.iterator();
      while (it.hasNext()) {
        QueryResultIF result = processor.execute((String) it.next());

        while (result.next()) {
          for (int ix = 0; ix < result.getWidth(); ix++) {
            ((TMObjectIF) result.getValue(ix)).remove();
          }
        }
      
        result.close();
      }
      
      TMObjectIF next = (TMObjectIF) params.get(2);
      if (next != null)
        response.addParameter(Constants.RP_TOPIC_ID, next.getObjectId());        
    } catch (InvalidQueryException e) {
      throw new ActionRuntimeException(e);
    }
  }
  
}
