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

package net.ontopia.topicmaps.nav2.impl.basic;

import java.util.Comparator;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Specialized comparator for AssocInfoStorage objects.
 */
public class AssocInfoStorageComparator implements Comparator {

  /**
   * INTERNAL: Compares two AssocInfoStorage objects.
   */
  @Override
  public int compare(Object o1, Object o2) {
    String value1, value2;

    try {
      value1 = ((AssocInfoStorage) o1).getSortName();
      value2 = ((AssocInfoStorage) o2).getSortName();

    } catch (ClassCastException e) {
      String msg = "AssocInfoStorageComparator Error: " +
        "This comparator only compares AssocInfoStorage objects.";
      throw new OntopiaRuntimeException(msg);
    }

    if (value1 == null) {
      return 1;
    }
    if (value2 == null) {
      return -1;
    }
    
    return value1.compareToIgnoreCase(value2);
  }
  
}





