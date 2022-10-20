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

package net.ontopia.topicmaps.query.impl.utils;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore;
import net.ontopia.topicmaps.query.core.QueryProcessorFactoryIF;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * PUBLIC: 
 * 
 * @since 5.1
 */
public class TologQueryProcessorFactory implements QueryProcessorFactoryIF {
  private static final Logger log = 
    LoggerFactory.getLogger(TologQueryProcessorFactory.class.getName());
  
  public static final String NAME = "TOLOG";
  
  private static String PROP_IMPLEMENTATION =
    "net.ontopia.topicmaps.query.core.QueryProcessorIF";
  
  @Override
  public String getQueryLanguage() {
    return NAME;
  }

  @Override
  public QueryProcessorIF createQueryProcessor(TopicMapIF topicmap,
      LocatorIF base, Map<String, String> properties) {
    String propval = null;
    int implementation = topicmap.getStore().getImplementation();
    if (implementation == TopicMapStoreIF.RDBMS_IMPLEMENTATION) {
      if (properties != null) {
        propval = (String) properties.get(PROP_IMPLEMENTATION);
      }
      if (propval == null) {
        propval = ((RDBMSTopicMapStore) topicmap
            .getStore()).getProperty(PROP_IMPLEMENTATION);
      }
      log.debug("Query processor setting: '" + propval + "'");
      if (propval != null && propval.equals("rdbms")) {
        log.debug("Creating RDBMS query processor for: " + topicmap);
        if (base == null) {
          return new net.ontopia.topicmaps.query.impl.rdbms.QueryProcessor(
              topicmap);
        } else {
          return new net.ontopia.topicmaps.query.impl.rdbms.QueryProcessor(
              topicmap, base);
        }
      }
    }
    // Otherwise use basic query processor
    if (propval == null || propval.equals("in-memory")) {
      log.debug("Creating basic query processor for: " + topicmap);
      if (base == null) {
        return new net.ontopia.topicmaps.query.impl.basic.QueryProcessor(
            topicmap);
      } else {
        return new net.ontopia.topicmaps.query.impl.basic.QueryProcessor(
            topicmap, base);
      }
    } else {
      throw new OntopiaRuntimeException("Property '" + PROP_IMPLEMENTATION
          + "' contains invalid value: '" + propval + "'");
    }
  }  
}
