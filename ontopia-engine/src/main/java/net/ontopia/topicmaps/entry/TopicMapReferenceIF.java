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

import java.io.IOException;
import net.ontopia.topicmaps.core.TopicMapStoreIF;

/**
 * PUBLIC: Represents a reference to a topic map. A topic map
 * reference is used to get hold of TopicMapStoreIF instances.<p>
 *
 * A reference is always considered to be open once it has been
 * constructed. It can also be reopened if it has been closed, but not
 * if it has been deleted.<p>
 *
 * The most common TopicMapReferenceIF implementations (such as the
 * one for RDBMS topic maps) are thread-safe.<p>
 */
public interface TopicMapReferenceIF {

  /**
   * PUBLIC: Gets the id of the reference.
   */
  public String getId();
  // source.getId() + "." + ownId
  
  /**
   * INTERNAL: Sets the id of the reference. <b>Warning:</b> Intended
   * for internal use only. The reference should throw an {@link
   * java.lang.UnsupportedOperationException} if it does not support
   * setting the id.<p>
   */
  public void setId(String id);

  /**
   * PUBLIC: Gets the title of the reference.
   */
  public String getTitle();

  /**
   * PUBLIC: Sets the title of the reference.
   */
  public void setTitle(String title);

  /**
   * PUBLIC: Gets the source to which the reference belongs.
   *
   * @since 1.3.2
   */
  public TopicMapSourceIF getSource();

  /**
   * PUBLIC: Sets the source to which the reference belongs. The
   * reference should throw an {@link
   * java.lang.UnsupportedOperationException} if it does not support
   * setting the source.
   *
   * @since 1.3.2
   */
  public void setSource(TopicMapSourceIF source);

  /**
   * PUBLIC: Creates a topic map store that lets you access the
   * referenced topic map.
   *
   * @since 1.3.2
   */
  public TopicMapStoreIF createStore(boolean readonly) throws IOException;

  /**
   * PUBLIC: Returns true if the reference is open.
   *
   * @since 1.3.2
   */
  public boolean isOpen();

  /**
   * PUBLIC: Opens the reference.
   *
   * @since 2.1
   */
  public void open();

  /**
   * PUBLIC: Closes all open stores and the reference itself. Note
   * that topic map stores created through this reference are closed
   * and dereferenced when the reference is closed. The reference
   * can be reopened after it has been closed.
   *
   * @since 1.3.2
   */
  public void close();
  // includes closing store, removing reference 

  /**
   * PUBLIC: Returns true if the topic map has been deleted.
   *
   * @since 2.1
   */
  public boolean isDeleted();
  
  /**
   * PUBLIC: Closes all open stores and deletes the topic map. The
   * reference is closed before the topic map is deleted. The
   * reference cannot be reopened after the topic map has been
   * deleted.
   *
   * @since 1.3.2
   */
  public void delete();
  
  /**
   * EXPERIMENTAL: Closes all open stores and clears the topic map by
   * removing all data. The reference is closed before the topic map
   * is deleted. The reference can be reopened after the topic map has
   * been deleted.
   *
   * @since 2.1.1
   */
  public void clear() throws IOException;

  /**
   * INTERNAL: Callback from the specified TopicMapStoreIF after it
   * has been closed. <b>Warning:</b> Intended for internal use only.
   *
   * @since 1.3.2
   */
  public void storeClosed(TopicMapStoreIF store);
  
}
