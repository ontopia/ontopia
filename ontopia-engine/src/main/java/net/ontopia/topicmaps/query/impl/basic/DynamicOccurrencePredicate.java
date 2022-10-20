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
import java.util.Objects;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.core.index.OccurrenceIndexIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;
import net.ontopia.topicmaps.query.impl.utils.PredicateOptions;
import net.ontopia.topicmaps.query.impl.utils.Prefetcher;

/**
 * INTERNAL: Implements occurrence predicates.
 */
public class DynamicOccurrencePredicate extends AbstractDynamicPredicate {
  protected TopicMapIF topicmap;
  protected ClassInstanceIndexIF index;
  protected OccurrenceIndexIF occindex;

  public DynamicOccurrencePredicate(TopicMapIF topicmap, LocatorIF base, TopicIF type) {
    super(type, base);
    this.topicmap = topicmap;
    
    index = (ClassInstanceIndexIF) topicmap.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
    occindex = (OccurrenceIndexIF) topicmap.getIndex("net.ontopia.topicmaps.core.index.OccurrenceIndexIF");
  }

  @Override
  public String getSignature() {
    return "t s z?"; // third arg is PredicateOptions, inserted by optimizer
  }
  
  @Override
  public int getCost(boolean[] boundparams) {
    if (boundparams[0] && boundparams[1]) {
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    } else if (boundparams[0] && !boundparams[1]) {
      return PredicateDrivenCostEstimator.SMALL_RESULT;
    } else if (!boundparams[0] && boundparams[1]) {
      return PredicateDrivenCostEstimator.SMALL_RESULT;
    } else {
      return PredicateDrivenCostEstimator.BIG_RESULT;
    }
  }

  @Override
  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    PredicateOptions options = null;
    boolean prefix_search = (arguments.length == 3); // inserted by optimizer
    if (prefix_search) {
      options = (PredicateOptions) arguments[2];
    }
    
    QueryMatches result = new QueryMatches(matches);
    int topicix = result.getIndex(arguments[0]);
    int valueix = result.getIndex(arguments[1]);

    if (matches.bound(valueix) && !matches.bound(topicix)) {
      satisfyWithBoundString(matches, result, topicix, valueix, prefix_search,
              options);
    } else if (matches.bound(topicix) && !matches.bound(valueix)) {
      satisfyWithBoundTopic(matches, result, topicix, valueix);
    } else if (!matches.bound(topicix) && !matches.bound(valueix)) {
      satisfyWithAllUnbound(matches, result, topicix, valueix, prefix_search,
              options);
    } else {
      satisfyWithAllBound(matches, result, topicix, valueix);
    }

    return result;
  }

  private void satisfyWithBoundTopic(QueryMatches matches, QueryMatches result,
                                     int topicix, int valueix) {

    Prefetcher.prefetch(topicmap, matches, topicix,
			Prefetcher.TopicIF, 
			Prefetcher.TopicIF_occurrences, false);

    for (int ix = 0; ix <= matches.last; ix++) {
      Object object = matches.data[ix][topicix];
      if (!(object instanceof TopicIF)) {
        continue; // no match for this row
      }
      
      TopicIF topic = (TopicIF) object;

      Iterator it = topic.getOccurrences().iterator();
      while (it.hasNext()) {
        OccurrenceIF occ = (OccurrenceIF) it.next();
        if (!type.equals(occ.getType())) {
          continue;
        }

        String value = occ.getValue();
        if (result.last+1 == result.size) {
          result.increaseCapacity();
        }
        result.last++;
      
        Object[] newRow = (Object[]) matches.data[ix].clone();
        newRow[valueix] = value;
        result.data[result.last] = newRow;
      }
    }
  }

  private void satisfyWithAllBound(QueryMatches matches, QueryMatches result,
                                   int topicix, int valueix) {

    for (int ix = 0; ix <= matches.last; ix++) {
      TopicIF topic = (TopicIF) matches.data[ix][topicix];
      String value = (String) matches.data[ix][valueix];

      Iterator it = topic.getOccurrences().iterator();
      while (it.hasNext()) {
        OccurrenceIF occ = (OccurrenceIF) it.next();
        if (!type.equals(occ.getType())) {
          continue;
        }

        String occval = occ.getValue();

        if (!Objects.equals(value, occval)) {
          continue;
        }
        
        if (result.last+1 == result.size) {
          result.increaseCapacity();
        }
        result.last++;
      
        result.data[result.last] = (Object[]) matches.data[ix].clone();
      }
    }
  }
  
  private void satisfyWithAllUnbound(QueryMatches matches, QueryMatches result,
                                     int topicix, int valueix, boolean prefix_search,
                                     PredicateOptions options) {

    OccurrenceIF[] occs = new OccurrenceIF[0];

    if (prefix_search) {
      occs =  (OccurrenceIF[]) occindex.getOccurrencesByPrefix(options.getValue()).toArray(occs);
    } else {
      occs = (OccurrenceIF[]) index.getOccurrences(type).toArray(occs);
    }

    Prefetcher.prefetch(topicmap, occs, 
			Prefetcher.OccurrenceIF, 
			Prefetcher.OccurrenceIF_topic, true);
    Prefetcher.prefetch(topicmap, occs, 
			Prefetcher.OccurrenceIF, 
			Prefetcher.OccurrenceIF_value, false);
    
    int occs_length = occs.length;

    // loop over all existing matches
    for (int ix = 0; ix <= matches.last; ix++) {

      // for each, fill in the set of occurrences of this type
      for (int occix = 0; occix < occs_length; occix++) {
        OccurrenceIF occ = occs[occix];

        // prefix_search is by prefix, not type, so need to check type here
        if (!occ.getType().equals(type)) {
          continue; // right prefix, wrong type
        }
        
        String value = occ.getValue();
        if (result.last+1 == result.size) {
          result.increaseCapacity();
        }
        result.last++;
      
        Object[] newRow = (Object[]) matches.data[ix].clone();
        newRow[valueix] = value;
        newRow[topicix] = occ.getTopic();
        result.data[result.last] = newRow;
      }
    }
  }

  private void satisfyWithBoundString(QueryMatches matches, QueryMatches result,
                                      int topicix, int valueix, boolean prefix_search,
                                      PredicateOptions options) {
    
    // loop over all existing matches
    for (int ix = 0; ix <= matches.last; ix++) {
      Object object = matches.data[ix][valueix];
      if (!(object instanceof String)) {
        continue; // no match for this row
      }
      
      String value = (String) matches.data[ix][valueix];
      if (prefix_search) {
        value = options.getValue();
      }

      // find all occurrences with this string value
      Object[] occs;
      if (prefix_search) {
        occs = occindex.getOccurrencesByPrefix(value).toArray();
      } else {
        occs = occindex.getOccurrences(value).toArray();
      }      
      addTo(result, occs, type, matches.data[ix], topicix);
    }   
  }

  private void addTo(QueryMatches result, Object[] occs, TopicIF type,
                     Object[] oldrow, int topicix) {
    for (int oix = 0; oix < occs.length; oix++) {
      OccurrenceIF occ = (OccurrenceIF) occs[oix];
      if (occ.getType().equals(type)) {
        // ok, it's a match: add it
        if (result.last+1 == result.size) {
          result.increaseCapacity();
        }
        result.last++;
      
        Object[] newRow = (Object[]) oldrow.clone();
        newRow[topicix] = occ.getTopic();
        result.data[result.last] = newRow;
      }
    }
  }
  
}
