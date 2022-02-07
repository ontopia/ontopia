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

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;
import net.ontopia.utils.CollectionUtils;
import net.ontopia.topicmaps.core.NameIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.utils.ScopedIFComparator;
import net.ontopia.topicmaps.utils.IntersectionOfContextDecider;

/**
 * INTERNAL: Grabber that grabs the most appropriate basename from a
 * topic and then the most appropriate variant name, if one can be
 * found. If no better variant name can be found, the base name is
 * used. This class is much used for grabbing display and sort names.
 */
public class ContextNameGrabber implements Function<TopicIF, NameIF> {

  protected Predicate<VariantNameIF> within;
  protected Comparator<TopicNameIF> bnComparator;
  protected Comparator<VariantNameIF> vnComparator;
 
  /**
   * INTERNAL: Creates a grabber; makes the comparators ScopedIFComparator
   *         for the given scopes.
   *
   * @param baseNameContext basename scope;
   *        a collection of TopicIF objects.
   * @param variantNameContext variantname scope;
   *        a collection of TopicIF objects.
   */
  public ContextNameGrabber(Collection<TopicIF> baseNameContext,
                            Collection<TopicIF> variantNameContext) {
    this.within = new IntersectionOfContextDecider<VariantNameIF>(variantNameContext);
    this.bnComparator = new ScopedIFComparator<TopicNameIF>(baseNameContext);
    this.vnComparator = new ScopedIFComparator<VariantNameIF>(variantNameContext);
  }

  /**
   * INTERNAL: Grabs the most appropriate base name for the given topic,
   * using the comparator established at creation to compare available
   * base names and if a sort variant is available it will be used.
   *
   * @param topic A topic; formally an Object, but must implement TopicIF.
   * @return object of class TopicNameIF or VariantNameIF
   * @exception throws OntopiaRuntimeException if the given topic is
   * not a TopicIF object.
   */
  @Override
  public NameIF apply(TopicIF mytopic) {
    // --- pick out best basename
    Collection<TopicNameIF> basenames = mytopic.getTopicNames();
    int basenames_size = basenames.size();
    if (basenames_size == 0)
      return null;

    TopicNameIF bestTopicName;
    if (basenames_size == 1)
      // Pull out the only basename
      bestTopicName = CollectionUtils.getFirstElement(basenames);
    else {
      // Sort list of basenames
      TopicNameIF[] mybasenames = basenames.toArray(new TopicNameIF[basenames.size()]);
      Arrays.sort(mybasenames, bnComparator);
      // Pull out the first basename
      bestTopicName = mybasenames[0];
    }
    
    // --- pick out best variant name
    Collection<VariantNameIF> variantnames = bestTopicName.getVariants();
    int variantnames_size = variantnames.size();
    // If there is no variant name return bestTopicName
    if (variantnames_size == 0)
      return bestTopicName;
    
    // If there is multiple basenames rank them.
    VariantNameIF[] myvariantnames = variantnames.toArray(new VariantNameIF[variantnames.size()]);
    if (variantnames_size > 1)
      Arrays.sort(myvariantnames, vnComparator);
    
    // Test that first variant is within scope
    if (within.test(myvariantnames[0]))
      return myvariantnames[0];
    else
      return bestTopicName;
  }

}
