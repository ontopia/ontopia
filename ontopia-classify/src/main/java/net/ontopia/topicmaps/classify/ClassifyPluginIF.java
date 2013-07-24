/*
 * #!
 * Ontopia Classify
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

package net.ontopia.topicmaps.classify;

import net.ontopia.topicmaps.core.TopicIF;

/**
 * INTERNAL: Interface implemented by code that is able to locate
 * classifiable content for topics.
 */
public interface ClassifyPluginIF {

  /**
   * INTERNAL: Returns true if the plug-in is able to locate
   * classifiable content for the given topic.
   */
  public boolean isClassifiable(TopicIF topic);

  /**
   * INTERNAL: Returns the classifiable content for the given topic.
   */
  public ClassifiableContentIF getClassifiableContent(TopicIF topic);
  
}
