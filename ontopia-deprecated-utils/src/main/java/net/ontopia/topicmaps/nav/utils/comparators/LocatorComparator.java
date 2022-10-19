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

import java.util.Collection;
import java.util.Comparator;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.utils.StringifierComparator;

/**
 * INTERNAL: A Comparator for ordering locators alphabetically.
 */
@Deprecated
public class LocatorComparator implements Comparator<LocatorIF> {

  protected Comparator<String> tc;
  protected Collection<TopicIF> scopes;

  /**
   * Empty constructor, used on application startup to initialise a
   * "fast" comparator which will compare Locators using no context.
   */
  public LocatorComparator() {
    tc = new StringifierComparator<String>();
  }

  /**
   * Constructor used to make a comparator which will compare Locators
   * using the context provided. This implementation doesn't use the
   * context yet.
   */
  public LocatorComparator(Collection<TopicIF> context) {
    this.scopes = context;
    tc = new StringifierComparator<String>();
  }
  
  /**
   * Compares two LocatorIFs
   */
  @Override
  public int compare(LocatorIF l1, LocatorIF l2) {
    return tc.compare(l1.getAddress(), l2.getAddress());
  }
}





