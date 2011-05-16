
// $Id: TopicMaps.java,v 1.3 2007/08/24 13:19:12 lars.garshol Exp $

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
    if (ref == null)
      throw new OntopiaRuntimeException("Topic map '" + topicmapId +
                                        "' not found in repository '" +
                                        repositoryId + "'.");
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
    synchronized (repositories) {
      TopicMapRepositoryIF repository = repositories.get(repositoryId);
      if (repository == null) {
        if (repositoryId.startsWith("file:"))
          repository = XMLConfigSource.getRepository(repositoryId.substring("file:".length()));
        else if (repositoryId.startsWith("classpath:"))
          repository = XMLConfigSource.getRepositoryFromClassPath(repositoryId.substring("classpath:".length()));
        else
          throw new IllegalArgumentException("Invalid scheme on repository id: '" + repositoryId + "'. Must be either file: or classpath.");
        repositories.put(repositoryId, repository);
      }
      return repository;
    }
  }
  
}
