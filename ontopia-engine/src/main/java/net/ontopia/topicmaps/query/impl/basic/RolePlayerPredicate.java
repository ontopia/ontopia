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

import java.util.Iterator;

import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.PredicateOptions;
import net.ontopia.topicmaps.query.impl.utils.Prefetcher;
import net.ontopia.topicmaps.query.parser.Parameter;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: Implements the 'role-player' predicate.
 */
public class RolePlayerPredicate implements BasicPredicateIF {
  protected TopicMapIF topicmap;

  public RolePlayerPredicate(TopicMapIF topicmap) {
    this.topicmap = topicmap;
  }
  
  @Override
  public String getName() {
    return "role-player";
  }

  @Override
  public String getSignature() {
    return "r t z?"; // predicateoptions inserted by optimizer
  }
  
  @Override
  public int getCost(boolean[] boundparams) {
    if (boundparams[0] && boundparams[1]) {
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    } else if (boundparams[0] && !boundparams[1]) {
      return PredicateDrivenCostEstimator.SINGLE_RESULT;
    } else if (!boundparams[0] && boundparams[1]) {
      return PredicateDrivenCostEstimator.MEDIUM_RESULT;
    } else {
      return PredicateDrivenCostEstimator.WHOLE_TM_RESULT;
    }
  }

  @Override
  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    int roleix = matches.getIndex(arguments[0]);
    int topicix = matches.getIndex(arguments[1]);

    if (!matches.bound(roleix) && matches.bound(topicix)) {
      Object typecolumn = null;
      if (arguments.length > 2) {
        typecolumn = ((PredicateOptions) arguments[2]).getColumn();
        // translate parameter into value
        // this isn't too pretty; we ought to be able to hide this
        if (typecolumn instanceof Parameter) {
          typecolumn = matches.getQueryContext().
            getParameterValue(((Parameter) typecolumn).getName());
        }
      }
      return topicToRole(matches, arguments, typecolumn);

    } else if (matches.bound(roleix) && !matches.bound(topicix)) {

      Prefetcher.prefetch(topicmap, matches, roleix,
                          Prefetcher.AssociationRoleIF, 
                          Prefetcher.AssociationRoleIF_player, false);

      return PredicateUtils.objectToOne(matches, roleix, topicix,
                                        AssociationRoleIF.class,
                                        PredicateUtils.ROLE_TO_PLAYER);
    } else if (matches.bound(roleix) && matches.bound(topicix)) {

      Prefetcher.prefetch(topicmap, matches, roleix,
                          Prefetcher.AssociationRoleIF, 
                          Prefetcher.AssociationRoleIF_player, false);

      return PredicateUtils.filter(matches, roleix, topicix,
                                   AssociationRoleIF.class, TopicIF.class,
                                   PredicateUtils.FILTER_ROLE_PLAYER);
    } else {

      Prefetcher.prefetch(topicmap, matches, topicix, 
                          Prefetcher.TopicIF, 
                          Prefetcher.TopicIF_roles, false);

      return PredicateUtils.generateFromCollection(matches, topicix, roleix,
                                                   topicmap.getTopics(),
                                                   PredicateUtils.GENERATE_ROLE_PLAYER);
    }
  }
  
  // internal

  private QueryMatches topicToRole(QueryMatches matches, Object[] arguments,
                                   Object typecolumn) {
    int typeix = -1;
    if (typecolumn != null) {
      typeix = matches.getIndex(typecolumn);
      // will be -1 if typecolumn is a TopicIF constant
    }
    
    QueryMatches result = new QueryMatches(matches);
    int roleix = result.getIndex(arguments[0]);
    int topicix = result.getIndex(arguments[1]);

    if (typeix == -1) {
      Prefetcher.prefetch(topicmap, matches, topicix, 
                          Prefetcher.TopicIF, 
                          Prefetcher.TopicIF_roles, false);
    }

    TopicIF roletype = null;
    if (typeix == -1) {
      roletype = (TopicIF) typecolumn; // fixed role type
    }
      
    for (int ix = 0; ix <= matches.last; ix++) {
      TopicIF topic = (TopicIF) matches.data[ix][topicix];

      Iterator it;
      if (typecolumn != null) {
        // used when optimizer has told us the type
        if (typeix != -1) {
          roletype = (TopicIF) matches.data[ix][typeix]; // dynamic role type
        }

        it = topic.getRolesByType(roletype).iterator();
      } else {
        it = topic.getRoles().iterator();
      }

      while (it.hasNext()) {
        AssociationRoleIF role = (AssociationRoleIF) it.next();
        
        if (result.last+1 == result.size) {
          result.increaseCapacity();
        }
        result.last++;
      
        Object[] newRow = (Object[]) matches.data[ix].clone();
        newRow[roleix] = role;
        result.data[result.last] = newRow;
      }
    }
    
    return result;
  }
  
}
