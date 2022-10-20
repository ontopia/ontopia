/*
 * #!
 * Ontopia Content Store
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

package net.ontopia.infoset.content;

import java.util.Map;
import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * INTERNAL: Utility methods for accessing the content store.
 */
public class ContentStoreUtils {

  /**
   * Returns a content store for content stored in the given topic map.
   * @param topicmap The topic map
   * @param properties String properties for configuring the store.
   */
  public static ContentStoreIF getContentStore(TopicMapIF topicmap, Map<?, ?> properties) {
    if (topicmap instanceof net.ontopia.topicmaps.impl.rdbms.TopicMap) {
      return JDBCContentStore.getInstance(topicmap);
    } else {
      return InMemoryContentStore.getInstance(topicmap);
    }
  }
  
}
