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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.utils.TopicComparators;
import net.ontopia.topicmaps.utils.TopicStringifiers;

/**
 * INTERNAL: A comparator for ordering AssociationRoleIFs
 * alphabetically by role player and role type.
 */
public class AssociationRoleComparator implements Comparator<AssociationRoleIF> {

  // constants
  private static final Function<TopicIF, String> DEF_TOPIC_STRINGIFIER = TopicStringifiers
    .getSortNameStringifier();
  private static final Comparator<TopicIF> DEF_TOPIC_COMPARATOR = TopicComparators
    .getCaseInsensitiveComparator(DEF_TOPIC_STRINGIFIER);
  
  protected Comparator<TopicIF> tc;

  public AssociationRoleComparator() {
    // Empty constructor, used on application startup to initialise a
    // "fast" comparator which will compare association roles using no
    // context.
    tc = DEF_TOPIC_COMPARATOR;
  }

  /**
   * Constructor used to make a comparator which will compare
   * Association Roles using the context provided.
   *
   * @param context The context to select topics in.
   * @param sortTopic The topic representing sort names.
   */
  public AssociationRoleComparator(Collection<TopicIF> context, TopicIF sortTopic) {
    if (context == null)
      context = Collections.emptySet();

    List<TopicIF> sortContext = new ArrayList<TopicIF>(context);
    if (sortTopic != null)
      sortContext.add(sortTopic);
    tc = new TopicComparator(context, sortContext);
  }
  
  /**
   * Compares two AssociationRoleIFs.
   */
  @Override
  public int compare (AssociationRoleIF ar1, AssociationRoleIF ar2){
    // Compare role players
    int result = tc.compare(ar1.getPlayer(), ar2.getPlayer());
    if (result == 0)
      // Compare role types
      return tc.compare(ar1.getType(), ar2.getType());
    else
      return result;
  }
  
}
