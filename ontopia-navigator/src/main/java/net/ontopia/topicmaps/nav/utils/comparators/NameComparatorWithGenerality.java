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
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.NameIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.TopicIF;

/**
 * INTERNAL: A Comparator for ordering TopicNameIFs and VariantNameIFs
 * first after their generality (determined by the number of themes in
 * their scopes, which means that the name in the unconstrained scope
 * would always appear first) and second alphabetically
 * (case-independent).
 */
public class NameComparatorWithGenerality extends NameComparator {

  /**
   * Empty constructor, used on application startup to initialise a
   * "fast" comparator which will compare TopicNameIFs and
   * VariantNameIFs using no context.
   */
  public NameComparatorWithGenerality() {
    super();
  }

  /**
   * Constructor used to make a comparator which will compare
   * TopicNameIFs and VariantNameIFs using the context provided.
   */
  public NameComparatorWithGenerality(Collection<TopicIF> context) {
    super(context);
  }

  /**
   * INTERNAL: helper method which gets out of the object the
   * base name or variant name value. If it's a basename try to
   * retrieve the sort variant name for it. The resulting string
   * contains first the number of themes and after that the name.
   */
  @Override
  protected String getName(NameIF obj) {
    String value = null;
    
    // --- first try if it's a base name
    if (obj instanceof TopicNameIF) {
      TopicNameIF basename = (TopicNameIF) obj;

      // try to get sort variant name for this base name
      initSortNameGrabber( basename );
      VariantNameIF sortVariant = sortNameGrabber.apply( basename );
      if (sortVariant != null) {
        if (sortVariant.getValue() != null) {
          value = sortVariant.getValue();
        } else {
          value = sortVariant.getLocator().getAddress();
        }
      }
      else {
        value = basename.getValue();
      }
      // order in first instance after the generality
      value = basename.getScope().size() + value;

    } else if (obj instanceof VariantNameIF) {
      // --- ...second try if it's a variant name
      VariantNameIF variant = (VariantNameIF) obj;
      if (variant.getValue() != null) {
        value = variant.getValue();
      } else {
        value = variant.getLocator().getAddress();
      }
      // order in first instance after the generality
      value = variant.getScope().size() + value;
    } else {
      throw new OntopiaRuntimeException("NameComparator Error: This comparator only compares " +
                                        "TopicNameIFs and VariantNameIFs. Got " + obj);
    }
    
    return value;
  }
  
}





