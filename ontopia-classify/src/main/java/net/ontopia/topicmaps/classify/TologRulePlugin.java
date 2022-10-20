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
public class TologRulePlugin implements ClassifyPluginIF {

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
      QueryResultIF qr = qp.execute("import \"classify.tl\" as classify classify:is-classifiable(%topic%)?", Collections.singletonMap("topic", topic));
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
    if (topic == null) {
      return null;
    }
    TopicMapIF tm = topic.getTopicMap();
    if (tm == null) {
      return null;
    }
    QueryProcessorIF qp = QueryUtils.getQueryProcessor(tm);
    try {
      QueryResultIF qr = qp.execute("import \"classify.tl\" as classify cl:get-classifiable-content(%topic%, $DOCURI)?", Collections.singletonMap("topic", topic));
      try {
        if (qr.next()) {
          String identifier = (String)qr.getValue(0);
          return ClassifyUtils.getClassifiableContent(identifier);
        } else {
          return null;
        }
      } finally {
        qr.close();
      }
    } catch (Throwable e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
}
