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

package net.ontopia.topicmaps.utils.ctm;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;

/**
 * Common interface for generating both literal values and topics.
 */
public interface ValueGeneratorIF {

  /**
   * Returns true if this generator produces a topic.
   */
  boolean isTopic();
  
  String getLiteral();
  
  LocatorIF getDatatype();

  /**
   * Returns a locator if the literal is a locator. Otherwise it
   * throws an exception.
   */
  LocatorIF getLocator();
  
  ValueGeneratorIF copy();

  TopicIF getTopic();
  
}
