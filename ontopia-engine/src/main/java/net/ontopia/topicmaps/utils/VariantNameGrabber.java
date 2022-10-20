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

package net.ontopia.topicmaps.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;

/**
 * INTERNAL: Grabber that grabs the most highest ranked variant name by
 * scope from a basename.</p>
 *
 * The grabber uses a ScopedIFComparator internally to rank the
 * variant names of the given basename. If the basename has no
 * applicable variant names, null is returned.</p>
 */
public class VariantNameGrabber implements Function<TopicNameIF, VariantNameIF> {

  /**
   * PROTECTED: The comparator used to sort the variant names.
   */
  protected Comparator<? super VariantNameIF> comparator;
 
  /**
   * INTERNAL: Creates a grabber; makes the comparator a ScopedIFComparator
   *         for the given scope.
   *
   * @param scope A scope; a collection of TopicIF objects.
   */
  public VariantNameGrabber(Collection<TopicIF> scope) {
    this.comparator = new ScopedIFComparator<VariantNameIF>(scope);
  }

  /**
   * INTERNAL: Creates a grabber which uses the given comparator.
   *
   * @param comparator The given comparator
   */
  public VariantNameGrabber(Comparator<? super VariantNameIF> comparator) {
    this.comparator = comparator;
  }

  /**
   * INTERNAL: Grabs the most appropriate variant name for the given base
   * name, using the comparator established at creation to compare
   * available variant names.
   *
   * @param basename an object, but must implement TopicNameIF.
   * @return the most applicable variant name, or null.
   * @exception throws OntopiaRuntimeException if the given base name 
   *                   is not a TopicNameIF object.
   */
  @Override
  public VariantNameIF apply(TopicNameIF basename) {
    List<VariantNameIF> variants = new ArrayList<VariantNameIF>(basename.getVariants());

    // If there are no variant names return the base name itself.
    if (variants.isEmpty()) {
      return null;
    }

    // If there are multiple variant names rank them.
    Collections.sort(variants, comparator);
    return variants.get(0);
  }
  
}
