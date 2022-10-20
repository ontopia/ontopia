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

package net.ontopia.topicmaps.query.impl.basic;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.Prefetcher;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;
 
/**
 * INTERNAL: Implements the 'association-role' predicate.
 */
public class AssociationRolePredicate implements BasicPredicateIF {
  protected TopicMapIF topicmap;
 
  public AssociationRolePredicate(TopicMapIF topicmap) {
    this.topicmap = topicmap;
  }
  
  @Override
  public String getName() {
    return "association-role";
  }

  @Override
  public String getSignature() {
    return "a r";
  }
  
  @Override
  public int getCost(boolean[] boundparams) {
    if (boundparams[0] && boundparams[1]) {
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    } else if (boundparams[0] && !boundparams[1]) {
      return PredicateDrivenCostEstimator.SMALL_RESULT;
    } else if (!boundparams[0] && boundparams[1]) {
      return PredicateDrivenCostEstimator.SINGLE_RESULT;
    } else {
      return PredicateDrivenCostEstimator.WHOLE_TM_RESULT;
    }
  }

  @Override
  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    int associx = matches.getIndex(arguments[0]);
    int roleix = matches.getIndex(arguments[1]);

    if (!matches.bound(associx) && matches.bound(roleix)) {
      Prefetcher.prefetch(topicmap, matches, roleix, 
			  Prefetcher.AssociationRoleIF, 
			  Prefetcher.AssociationRoleIF_association, false);
      return PredicateUtils.objectToOne(matches, roleix, associx,
                                        AssociationRoleIF.class,
                                        PredicateUtils.ROLE_TO_ASSOCIATION);
    } else if (matches.bound(associx) && !matches.bound(roleix)) {
      Prefetcher.prefetch(topicmap, matches, associx, 
			  Prefetcher.AssociationIF, 
			  Prefetcher.AssociationIF_roles, false);
      return PredicateUtils.objectToMany(matches, associx, roleix,
                                         AssociationIF.class,
                                         PredicateUtils.ASSOCIATION_TO_ROLE, null);
    } else if (matches.bound(associx) && matches.bound(roleix)) {
      Prefetcher.prefetch(topicmap, matches, roleix, 
			  Prefetcher.AssociationRoleIF, 
			  Prefetcher.AssociationRoleIF_association, false);
      return PredicateUtils.filter(matches, roleix, associx, 
                                   AssociationRoleIF.class, AssociationIF.class,
                                   PredicateUtils.FILTER_ASSOCIATION_ROLE);
    } else {
      // ISSUE: prefetch A.roles?
      return PredicateUtils.generateFromCollection(matches, associx, roleix,
                                                   topicmap.getAssociations(),
                                                   PredicateUtils.GENERATE_ROLES);
    }
  }
  
}
