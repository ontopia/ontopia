/*
 * #!
 * Ontopia Classify
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

package net.ontopia.topicmaps.classify;

import java.util.Collections;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: 
 */
public class DefaultPlugin implements ClassifyPluginIF {

  @Override
  public boolean isClassifiable(TopicIF topic) {
    if (topic == null) {
      return false;
    }
    TopicMapIF tm = topic.getTopicMap();
    if (tm == null) {
      return false;
    }
    QueryProcessorIF qp = QueryUtils.getQueryProcessor(tm);
    try {
      QueryResultIF qr = qp.execute("using cl for i\"http://psi.ontopia.net/classify/\" " +
                                    "select $CTYPE, $AT from " +
                                    "instance-of(%TOPIC%, $CTYPE), " +
                                    // "cl:classification-type($AT : cl:classified-association-type, $CTYPE : cl:classified-topic-type)," +
                                    "type($A, $CT), subject-identifier($CT, \"http://psi.ontopia.net/classify/classification-type\"), " +
                                    "association-role($A, $R1), type($R1, $CAT), subject-identifier($RT1, \"http://psi.ontopia.net/classify/classified-association-type\"), " +
                                    "association-role($A, $R2), type($R2, $CTT), subject-identifier($RT2, \"http://psi.ontopia.net/classify/classified-topic-type\")," +
                                    "role-player($R1, $AT), role-player($R2, $CTYPE) " + 
                                    "?", Collections.singletonMap("TOPIC", topic));
      try {
        return qr.next();
      } finally {
        qr.close();
      }    
    } catch (Throwable e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  @Override
  public ClassifiableContentIF getClassifiableContent(TopicIF topic) {
    return null;
  }
  
}
