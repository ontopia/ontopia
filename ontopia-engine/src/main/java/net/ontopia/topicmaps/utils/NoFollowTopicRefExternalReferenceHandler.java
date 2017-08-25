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

package net.ontopia.topicmaps.utils;

import net.ontopia.topicmaps.xml.ExternalReferenceHandlerIF;
import net.ontopia.infoset.core.LocatorIF;

/**
 * PUBLIC: An implementation of ExternalReferenceHandlerIF that
 * prevents the traversal of external topic references. External topic
 * maps will be resolved.<p>
 *
 * @since 3.2
 */

public class NoFollowTopicRefExternalReferenceHandler
  implements ExternalReferenceHandlerIF {

  /**
   * PUBLIC: External topic maps are resolved.
   */
  @Override
  public LocatorIF externalTopicMap(LocatorIF loc) {
    return loc;
  }

  /**
   * PUBLIC: External topics are not resolved.
   */
  @Override
  public LocatorIF externalTopic(LocatorIF loc) {
    return null;
  }
}
