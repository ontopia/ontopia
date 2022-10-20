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

import java.io.IOException;

import net.ontopia.infoset.fulltext.core.SearchResultIF;
import net.ontopia.infoset.fulltext.core.SearcherIF;
import net.ontopia.infoset.fulltext.topicmaps.TopicMapSearchResult;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.PredicateSignature;
import net.ontopia.topicmaps.query.impl.utils.Prefetcher;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.OntopiaUnsupportedException;
import net.ontopia.utils.StringUtils;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: Implements the 'value-like' predicate.
 */
public class ValueLikePredicate implements BasicPredicateIF {
  protected TopicMapIF topicmap;
  protected SearcherIF searcher;

  public ValueLikePredicate(TopicMapIF topicmap) {
    this.topicmap = topicmap;
  }
  
  @Override
  public String getName() {
    return "value-like";
  }

  @Override
  public String getSignature() {
    return "bov s! f?";
  }
  
  @Override
  public int getCost(boolean[] boundparams) {
    if (!boundparams[1]) {
      // cannot run predicate before we have the query
      return PredicateDrivenCostEstimator.INFINITE_RESULT;
    } else {
      // this is not true, but we want to run the full-text as early as
      // possible, to avoid running it many times. 
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    }
  }

  @Override
  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    QueryMatches result = new QueryMatches(matches);
    int topicix = result.getIndex(arguments[0]);
    int valueix = result.getIndex(arguments[1]);
    int scoreix = (arguments.length > 2 ? result.getIndex(arguments[2]) : -1);

    PredicateSignature sign = PredicateSignature.getSignature(this);
    sign.verifyBound(matches, arguments, this);
    
    if (matches.bound(topicix)) {
      throw new InvalidQueryException("First argument to " + getName() + " must " +
                                      "be unbound");
    } else if (scoreix >= 0 && matches.bound(scoreix)) {
      throw new InvalidQueryException("Third argument to " + getName() + " must " +
                                      "be unbound");
    } else {
      satisfyWithAllUnbound(matches, result, topicix, valueix, scoreix);
    }
    
    return result;
  }

  private void satisfyWithAllUnbound(QueryMatches matches, QueryMatches result,
                                     int topicix, int valueix, int scoreix) {

    // loop over all existing matches
    String previous = StringUtils.VERY_UNLIKELY_STRING;
    TopicMapSearchResult ftresult = null;
    for (int ix = 0; ix <= matches.last; ix++) {

      // loop over all matches for this value
      String value = (String) matches.data[ix][valueix];
      if ("".equals(value)) {
        continue;
      } else if (!previous.equals(value)) {
        ftresult = search(value);
        previous = value;
      }
        
      int length = ftresult.size();
      for (int i=0; i < length; i++) {
        
        if (result.last+1 == result.size) {
          result.increaseCapacity();
        }
        result.last++;
        
        Object[] newRow = (Object[]) matches.data[ix].clone();
        newRow[topicix] = ftresult.get(i);
        if (scoreix >= 0) {
          newRow[scoreix] = ftresult.getScore(i);
        }
        result.data[result.last] = newRow;
      }
    }
    
  }

  private TopicMapSearchResult search(String value) {
    try {
      // Get hold of fulltext index.
      if (this.searcher == null) {
        this.searcher = getSearcher(topicmap);
      }

      // search
      SearchResultIF result = this.searcher.search(value);
      
      // prefetch identities
      Prefetcher.prefetch(topicmap, result, "object_id");

      // return result
      return new TopicMapSearchResult(topicmap, result);
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  // FIXME: this is totally unacceptable
  private SearcherIF getSearcher(TopicMapIF topicmap) {

    try {
      return (SearcherIF)topicmap
        .getIndex("net.ontopia.infoset.fulltext.core.SearcherIF");
    } catch (OntopiaUnsupportedException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
}
