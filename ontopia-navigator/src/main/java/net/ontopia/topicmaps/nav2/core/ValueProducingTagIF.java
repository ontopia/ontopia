/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav2.core;

import java.util.Collection;
import javax.servlet.jsp.JspException;

/**
 * INTERNAL: Implemented by tags whose functionality is such that they
 * process a single input collection to produce their output value.
 */
public interface ValueProducingTagIF {

  /**
   * INTERNAL: Process the input collection and return the output
   * collection.  This collection will afterwards typically be passed
   * up to the parent tag, which should be a value-accepting tag.
   *
   * @see net.ontopia.topicmaps.nav2.taglibs.value.BaseValueProducingTag
   */
  public Collection process(Collection inputCollection) throws JspException;
  
}
