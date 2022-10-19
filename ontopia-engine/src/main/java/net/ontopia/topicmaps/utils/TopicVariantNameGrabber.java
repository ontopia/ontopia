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
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;

/**
 * INTERNAL: Grabber that grabs the most highest ranked variant name by
 * scope from a topic, ignoring the scope of the base names.</p>
 *
 * The grabber uses a ScopedIFComparator internally to rank all the
 * variant names of the given topic. If the topic has no applicable
 * variant names, null is returned.</p>
 *
 * @since 2.0.3
 */
public class TopicVariantNameGrabber implements Function<TopicIF, VariantNameIF> {

  /**
   * PROTECTED: The comparator used to sort the variant names.
   */
  protected Comparator<? super VariantNameIF> comparator;
 
  /**
   * INTERNAL: Creates a grabber.
   *
   * @param scope A scope; a collection of TopicIF objects.
   */
  public TopicVariantNameGrabber(Collection<TopicIF> scope) {
    this.comparator = new ScopedIFComparator<VariantNameIF>(scope);
  }

  /**
   * INTERNAL: Creates a grabber which uses the given comparator.
   *
   * @param comparator The given comparator
   */
  public TopicVariantNameGrabber(Comparator<? super VariantNameIF> comparator) {
    this.comparator = comparator;
  }

  /**
   * INTERNAL: Grabs the most appropriate variant name for the given
   * topic name, using the comparator established at creation to
   * compare available variant names.
   *
   * @param topic an object, but must implement TopicIF
   * @return the most applicable variant name, or null.
   * @exception throws OntopiaRuntimeException if the given topic
   *                   is not a TopicIF object.
   */
  @Override
  public VariantNameIF apply(TopicIF topic) {
    List<VariantNameIF> variants = new ArrayList<VariantNameIF>();

    Iterator<TopicNameIF> it = topic.getTopicNames().iterator();
    while (it.hasNext()) {
      TopicNameIF basename = it.next();
      variants.addAll(basename.getVariants());
    }
    
    // If there are no variant names return null
    if (variants.isEmpty())
      return null;

    // If there is multiple variant names rank them.
    Collections.sort(variants, comparator);
    return variants.get(0);
  }
  
}
