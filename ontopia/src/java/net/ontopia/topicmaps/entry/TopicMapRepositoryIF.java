
// $Id: TopicMapRepositoryIF.java,v 1.11 2007/08/27 15:35:11 geir.gronmo Exp $

package net.ontopia.topicmaps.entry;

import java.util.Collection;

import net.ontopia.topicmaps.core.TopicMapStoreIF;

/**
 * PUBLIC: Class used to provide easy access to references from one or
 * more topic map sources.<p>
 *
 * The repository allows access to its underlying references using
 * reference keys that the repository assigns each reference. It is up
 * to the repository implementation to generate these keys. Each
 * repository decides how to generate its reference keys. Note that
 * the reference key is therefore not neccessarily identical to the
 * reference's id. See the implementations for more information on
 * their key generation policies.<p>
 *
 * The default implementation of TopicMapRepositoryIF is thread-safe.<p>
 *
 * @since 1.3.2
 */
public interface TopicMapRepositoryIF {
  
  /**
   * PUBLIC: Creates a new topic map store for the given topic map
   * id. This method effectively delegates the call to the underlying
   * topic map reference. An exception is thrown if the topic map
   * reference cannot be found or any errors occurs while loading it.
   *
   * @since 3.4
   */
  public TopicMapStoreIF createStore(String refkey, boolean readonly);
  
  /**
   * PUBLIC: Gets a topic map reference by its reference key. Returns
   * null if not found.
   */
  public TopicMapReferenceIF getReferenceByKey(String refkey);

  /**
   * PUBLIC: Gets the key used to identify the reference in the
   * repository.
   */
  public String getReferenceKey(TopicMapReferenceIF ref);
  
  /**
   * PUBLIC: Returns a collection containing all references.
   */
  public Collection getReferences();
  // returns TopicMapReferenceIF objects; unmodifiable
  // no removeReference; do reference.deactivate or reference.delete instead
  
  /**
   * PUBLIC: Returns a collection containing the keys of all references.
   */
  public Collection getReferenceKeys();

  /**
   * PUBLIC: Refreshes all sources and recreates the reference map.
   */
  public void refresh();
  // clear reference Map, refresh all sources, recreate reference Map

  /**
   * PUBLIC: Returns the topic map source that has the given source id.
   */
  public TopicMapSourceIF getSourceById(String source_id);

  /**
   * PUBLIC: Returns an immutable collection containing the
   * TopicMapSourceIFs registered with the topic map repository.
   */
  public Collection getSources();
  // FIXME: Is the collection updated when the repository is updated?
  
  /**
   * PUBLIC: Adds the source to the repository.
   */
  public void addSource(TopicMapSourceIF source);
  // if source already present; do nothing
  // disallow "." in source IDs
  // while (source.getId() is a duplicate) { source.setId(origId + num++); }

  /**
   * PUBLIC: Removes the source from the repository.
   */
  public void removeSource(TopicMapSourceIF source);

  /**
   * PUBLIC: Closes the repository and releases all resources bound by
   * the repository. Closing the repository will also close any open
   * topic map references held by the topic map repository.
   *
   * @since 2.1
   */
  public void close();

}
