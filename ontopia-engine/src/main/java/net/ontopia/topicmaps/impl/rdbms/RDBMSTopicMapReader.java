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

package net.ontopia.topicmaps.impl.rdbms;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.utils.MergeUtils;

/**
 * INTERNAL: Topic map reader that reads topic maps from the RDBMS
 * backend connector.
 *
 * @since 1.2.5
 */
public class RDBMSTopicMapReader implements TopicMapReaderIF {
  protected String propfile;
  protected Map properties;
  protected long topicmap_id;

  public RDBMSTopicMapReader(long topicmap_id) {
    this.topicmap_id = topicmap_id;
  }

  public RDBMSTopicMapReader(String propfile, long topicmap_id) {
    this.propfile = propfile;
    this.topicmap_id = topicmap_id;
  }
        
  public RDBMSTopicMapReader(Map properties, long topicmap_id) {
    this.properties = properties;
    this.topicmap_id = topicmap_id;
  }
  
  @Override
  public TopicMapIF read() throws java.io.IOException {
    if (properties != null) {
      return new RDBMSTopicMapStore(properties, topicmap_id).getTopicMap();
    } else if (propfile != null) {
      return new RDBMSTopicMapStore(propfile, topicmap_id).getTopicMap();
    } else {
      return new RDBMSTopicMapStore(topicmap_id).getTopicMap();
    }
  }

  @Override
  public Collection<TopicMapIF> readAll() throws java.io.IOException {
    return (Collection<TopicMapIF>) Collections.singleton(read());
  }        

  /**
   * RDBMSTopicMapReader does not accept any additional properties outside of the specified 
   * properties file.
   * @param properties 
   */
  @Override
  public void setAdditionalProperties(Map<String, Object> properties) {
    // no-op
  }

  @Override
  public void importInto(TopicMapIF topicmap) throws IOException {
    MergeUtils.mergeInto(topicmap, read());
  }
}
