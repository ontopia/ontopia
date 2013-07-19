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

/**
 * INTERNAL: We used to have five change types (declared as static
 * ints in ChangelogReaderIF), but reduced that to the current two.
 * However, I don't want to use a boolean for this, and so decided to
 * change over to an enum. 
 */
public enum ChangeType {
  /**
   * Used for both insert and update. The code works out from the data
   * what to do.
   */
  UPDATE,

  /**
   * Used for delete. The code detects this case itself from the data.
   */
  DELETE

  // there used to also be UNKNOWN (not needed any more) and IGNORE
  // (not needed any more). we deliberately omit both.
}