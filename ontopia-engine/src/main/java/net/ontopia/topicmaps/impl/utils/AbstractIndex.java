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

package net.ontopia.topicmaps.impl.utils;

import net.ontopia.topicmaps.core.index.IndexIF;

/**
 * INTERNAL: An abstract index class.
 */

public abstract class AbstractIndex implements IndexIF {

  /**
   * INTERNAL: Method used by IndexManagerIF to manage index creation. The idea
   * behind this method is for the index itself to decide whether to create a
   * new instance every time or the same one.
   * 
   * @return Index instance.
   */
  public abstract IndexIF getIndex();

}
