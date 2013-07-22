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

package net.ontopia.topicmaps.nav.utils.comparators;

import java.util.*;

import net.ontopia.utils.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.infoset.core.*;

/**
 * INTERNAL: A Comparator for ordering locators alphabetically.
 */
public class LocatorComparator implements Comparator {

  protected Comparator tc;
  protected Collection scopes;

  /**
   * Empty constructor, used on application startup to initialise a
   * "fast" comparator which will compare Locators using no context.
   */
  public LocatorComparator() {
    tc = new StringifierComparator();
  }

  /**
   * Constructor used to make a comparator which will compare Locators
   * using the context provided. This implementation doesn't use the
   * context yet.
   */
  public LocatorComparator(Collection context) {
    this.scopes = context;
    tc = new StringifierComparator();
  }
  
  /**
   * Compares two LocatorIFs
   */
  public int compare(Object o1, Object o2) {
    LocatorIF l1, l2;
    try {
      l1 = (LocatorIF) o1;
      l2 = (LocatorIF) o2;
    } catch (ClassCastException e) {
      String msg = "LocatorComparator Error: " +
        "This comparator only compares LocatorIFs";
      throw new OntopiaRuntimeException(msg);
    }
    return tc.compare(l1.getAddress(), l2.getAddress());
  }
}





