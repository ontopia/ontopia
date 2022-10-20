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

import java.util.Map;
import java.io.IOException;
import net.ontopia.persistence.proxy.StorageIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * PUBLIC: The RDBMS TopicMapStoreFactoryIF implementation.
 */

public class RDBMSStoreFactory implements TopicMapStoreFactoryIF {

  // FIXME: Complete constructors
  protected long topicmap_id = -1;
  protected String propfile;
  protected Map properties;
  protected StorageIF storage;

  public RDBMSStoreFactory() {
  }
  
  public RDBMSStoreFactory(long topicmap_id) {
    this.topicmap_id = topicmap_id;
  }

  public RDBMSStoreFactory(String propfile) {
    this.propfile = propfile;
  }

  public RDBMSStoreFactory(String propfile, long topicmap_id) {
    this.propfile = propfile;
    this.topicmap_id = topicmap_id;
  }

  public RDBMSStoreFactory(Map properties) {
    this.properties = properties;
  }

  public RDBMSStoreFactory(Map properties, long topicmap_id) {
    this.properties = properties;
    this.topicmap_id = topicmap_id;
  }

  public RDBMSStoreFactory(StorageIF storage) {
    this.storage = storage;
  }

  public RDBMSStoreFactory(StorageIF storage, long topicmap_id) {
    this.storage = storage;
    this.topicmap_id = topicmap_id;
  }
  
  @Override
  public TopicMapStoreIF createStore() {
    try {
      if (topicmap_id < 0) {
        if (storage != null) {
          return new RDBMSTopicMapStore(storage);
        } else if (propfile != null) {
          return new RDBMSTopicMapStore(propfile);
        } else if (properties != null) {
          return new RDBMSTopicMapStore(properties);
        } else {
          return new RDBMSTopicMapStore();
        }
      } else {
        if (storage != null) {
          return new RDBMSTopicMapStore(storage, topicmap_id);
        } else if (propfile != null) {
          return new RDBMSTopicMapStore(propfile, topicmap_id);
        } else if (properties != null) {
          return new RDBMSTopicMapStore(properties, topicmap_id);
        } else {
          return new RDBMSTopicMapStore(topicmap_id);
        }
      }
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
}
