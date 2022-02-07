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

import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.utils.SameStoreFactory;

/**
 * INTERNAL: A topic map reference that wraps a TopicMapStoreIF
 * object. The reference always returns the same store instance.<p>
 *
 * If the reference has been opened, ie. used to create stores, and
 * the dereferenceOnClose flag is true, it cannot be reopened. If the
 * flag is false, it can be reopened if the store can be reopened.<p>
 *
 * @deprecated Class is now superseded by StoreFactoryReference.
 */

@Deprecated
public class DefaultTopicMapReference extends StoreFactoryReference {
  
  public DefaultTopicMapReference(String id, String title, 
                                  TopicMapStoreIF store) {
    super(id, title, new SameStoreFactory(store));
  }
  
}
