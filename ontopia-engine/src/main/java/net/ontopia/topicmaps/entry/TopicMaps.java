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

package net.ontopia.topicmaps.entry;

import java.util.HashMap;
import java.util.Map;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * PUBLIC: The primary access point to your topic maps. The topic map
 * repository definition will be loaded from the CLASSPATH. The
 * default topic map repository will be loaded from the resource
 * 'tm-sources.xml'.
 *
 * @since 3.4
 */
public class TopicMaps {

  protected static final String DEFAULT_REPOSITORY = "classpath:tm-sources.xml";  
  
  protected static final Map<String, TopicMapRepositoryIF> repositories = new HashMap<String, TopicMapRepositoryIF>();

  /**
   * PUBLIC: Returns a new TopicMapStoreIF from the default
   * repository. Remember to close the store once you're done with it.
   */
  public static TopicMapStoreIF createStore(String topicmapId, boolean readOnly) {
    return createStore(topicmapId, readOnly, DEFAULT_REPOSITORY);
  }
  
  /**
   * PUBLIC: Returns a new TopicMapStoreIF from the given
   * repository. Remember to close the store once you're done with it.
   */
  public static TopicMapStoreIF createStore(String topicmapId, boolean readOnly,
                                            String repositoryId) {
    TopicMapRepositoryIF repository = getRepository(repositoryId);
    TopicMapReferenceIF ref = repository.getReferenceByKey(topicmapId);
    if (ref == null) {
      throw new OntopiaRuntimeException("Topic map '" + topicmapId +
                                        "' not found in repository '" +
                                        repositoryId + "'.");
    }
    try {
      return ref.createStore(readOnly);
    } catch (java.io.IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  /**
   * PUBLIC: Returns the default topic maps repository.
   */
  public static TopicMapRepositoryIF getRepository() {
    return getRepository(DEFAULT_REPOSITORY);
  }
  
  /**
   * PUBLIC: Returns the default topic maps repository.
   */
  public static TopicMapRepositoryIF getRepository(String repositoryId) {
      return getRepository(repositoryId, null);
  }

  public static TopicMapRepositoryIF getRepository(String repositoryId, Map<String, String> environ) {
    synchronized (repositories) {
      TopicMapRepositoryIF repository = repositories.get(repositoryId);
      if (repository == null) {
        if (repositoryId.startsWith("file:")) {
          repository = XMLConfigSource.getRepository(repositoryId.substring("file:".length()), environ);
        } else if (repositoryId.startsWith("classpath:")) {
          repository = XMLConfigSource.getRepositoryFromClassPath(repositoryId.substring("classpath:".length()), environ);
        } else {
          throw new IllegalArgumentException("Invalid scheme on repository id: '" + repositoryId + "'. Must be either file: or classpath.");
        }
        repositories.put(repositoryId, repository);
      }
      return repository;
    }
  }

  public static void forget(TopicMapRepositoryIF repositoryIF) {
    String match = null;
    for (String key : repositories.keySet()) {
      if (repositories.get(key).equals(repositoryIF)) {
        match = key;
        break;
      }
    }
    if (match != null) {
      repositories.remove(match);
    }
  }
  
}
