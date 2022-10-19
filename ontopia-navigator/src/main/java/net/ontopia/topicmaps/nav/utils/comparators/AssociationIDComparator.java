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

import java.util.Comparator;
import java.text.Collator;
import net.ontopia.topicmaps.core.AssociationIF;

/**
 * INTERNAL: A Comparator for ordering AssociationIFs after their ID.
 *
 * @since 1.2.5
 */
public class AssociationIDComparator implements Comparator<AssociationIF> {

  protected Comparator<Object> ac;

  /**
   * Default constructor.
   */  
  public AssociationIDComparator() {
    ac = Collator.getInstance();
  }
  
  /**
   * Compares two AssociationIFs.
   */
  @Override
  public int compare(AssociationIF a1, AssociationIF a2) {
    return ac.compare(a1.getObjectId(), a2.getObjectId());
  }
  
}





