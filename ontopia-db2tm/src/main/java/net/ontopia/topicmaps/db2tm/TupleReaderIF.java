/*
 * #!
 * Ontopia DB2TM
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

package net.ontopia.topicmaps.db2tm;


import net.ontopia.utils.*;

/**
 * INTERNAL: A tuple reader is an iterator-like interface for looping
 * through the tuples from a given relation.
 */
public interface TupleReaderIF {

  /**
   * INTERNAL: Returns the next tuple. Method will return null when
   * there are no more tuples.
   */
  public String[] readNext();

  // NOTE: next method intended for further performance improvements
  //! public boolean readNext(String[] tuple, int offset, int length);

  /**
   * INTERNAL: Releases all resources held by the tuple reader. This
   * method should be called when done with the tuple reader.
   */
  public void close();

}
