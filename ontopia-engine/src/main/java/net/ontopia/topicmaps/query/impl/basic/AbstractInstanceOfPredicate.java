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

import java.util.Collection;
import java.util.Iterator;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.Prefetcher;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: Common superclass for the two instance-of predicates.
 */
public abstract class AbstractInstanceOfPredicate implements BasicPredicateIF {
  protected TopicMapIF topicmap;
  protected ClassInstanceIndexIF index;
  
  public AbstractInstanceOfPredicate(TopicMapIF topicmap) {
    this.topicmap = topicmap;
    index = (ClassInstanceIndexIF) topicmap.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
  }

  @Override
  public String getSignature() {
    return "t t";
  }

  @Override
  public int getCost(boolean[] boundparams) {
    if (boundparams[0] && boundparams[1]) {
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    } else if (boundparams[0] && !boundparams[1]) {
      return PredicateDrivenCostEstimator.SMALL_RESULT;
    } else if (!boundparams[0] && boundparams[1]) {
      return PredicateDrivenCostEstimator.BIG_RESULT;
    } else {
      return PredicateDrivenCostEstimator.WHOLE_TM_RESULT;
    }
  }
  
  @Override
  public QueryMatches satisfy(QueryMatches result, Object[] arguments)
    throws InvalidQueryException {
    
    start();        
    int instix = result.getIndex(arguments[0]);
    int classix = result.getIndex(arguments[1]);
    
    if (result.data[0][instix] != null) {
      if (result.data[0][classix] != null) {
        // (instance, class)
        return verifyPairs(result, instix, classix);
      } else {
        // (instance, $CLASS)
        return fillInClasses(result, instix, classix);
      }
    } else {
      if (result.data[0][classix] != null) {
        // ($INSTANCE, class)
        return fillInInstances(result, instix, classix);
      } else {
        // ($INSTANCE, $CLASS)
        return generateAllPairs(result, instix, classix);
      }
    }
  }

  // --- Data interface

  /**
   * INTERNAL: Called before the evaluation of a new query, to allow
   * resetting of internal caches etc.
   */
  protected abstract void start();

  /**
   * INTERNAL: Should return all applicable types of the given topic.
   * In 'instance-of' this includes the supertypes of the topic's types.
   */
  protected abstract Collection getClasses(TopicIF instance);

  /**
   * INTERNAL: Should return all instances of the topic, as seen by the
   * specific predicate.
   */
  protected abstract Collection getInstances(TopicIF klass);


  /**
   * INTERNAL: Should return all topic types, as seen by the specific
   * predicate.
   */
  protected abstract Collection getTypes();

  /**
   * INTERNAL: Should return all supertypes of the given class,
   * including the class itself. In 'direct-instance-of' this should
   * just be the class itself.
   */
  protected abstract Collection getSupertypes(TopicIF type);

  /**
   * INTERNAL: Should return all instances of the topic, as seen by the
   * specific predicate.
   */
  protected Collection getDirectInstances(TopicIF classes) {
    return index.getTopics(classes);
  }
  
  // --- Implementations of the various cases

  private QueryMatches fillInInstances(QueryMatches matches,
                                       int instix, int classix) {
    QueryMatches result = new QueryMatches(matches);
    for (int ix = 0; ix <= matches.last; ix++) {
      if (matches.data[ix][classix] instanceof TopicIF) {
        TopicIF klass = (TopicIF) matches.data[ix][classix];
        addTo(result, getInstances(klass), instix, matches.data[ix]);
      }
    }
    return result;
  }
  
  private QueryMatches fillInClasses(QueryMatches matches,
                                     int instix, int classix) {
    QueryMatches result = new QueryMatches(matches);
    for (int ix = 0; ix <= matches.last; ix++) {
      if (matches.data[ix][instix] instanceof TopicIF) {
        TopicIF instance = (TopicIF) matches.data[ix][instix];
        addTo(result, getClasses(instance), classix, matches.data[ix]);
      }
    }
    return result;
  }
    
  private QueryMatches generateAllPairs(QueryMatches matches,
                                        int instix, int classix) {
    QueryMatches result = new QueryMatches(matches);
    Iterator it = getTypes().iterator();

    while (it.hasNext()) {
      TopicIF klass = (TopicIF) it.next();
      Object[] instances = getDirectInstances(klass).toArray();
      Object[] supers = getSupertypes(klass).toArray();
      int instances_length = instances.length;
      int supers_length = supers.length;
      
      for (int kix = 0; kix < supers_length; kix++) {
        for (int ix = 0; ix < instances_length; ix++) {
          for (int i = 0; i <= matches.last; i++) {
            if (result.last+1 == result.size) {
              result.increaseCapacity();
            }
            result.last++;

            Object[] row = (Object[]) matches.data[i].clone();
            row[classix] = supers[kix];
            row[instix] = instances[ix];
            result.data[result.last] = row;
          }
        }
      }
    }
    
    return result;
  }
  
  private QueryMatches verifyPairs(QueryMatches matches,
                                   int instix, int classix) {

    Prefetcher.prefetch(topicmap, matches, instix, 
			Prefetcher.TopicIF, 
			Prefetcher.TopicIF_types, false);

    QueryMatches result = new QueryMatches(matches);
    for (int ix = 0; ix <= matches.last; ix++) {
      if (!(matches.data[ix][instix] instanceof TopicIF)) {
        continue;
      }
      
      TopicIF inst = (TopicIF) matches.data[ix][instix];
      Object klass = matches.data[ix][classix];

      if (!getClasses(inst).contains(klass)) {
        continue; // not a match, so skip it
      }
      
      if (result.last+1 == result.size) {
        result.increaseCapacity();
      }
      result.last++;

      // FIXME: is this really safe? or could row sharing give overwrites?
      result.data[result.last] = matches.data[ix];
    }

    return result;
  }

  // --- Helpers

  /**
   * INTERNAL: Given a collection of values, and a column to fill them
   * into, and an existing row, that row is duplicated with new values
   * for the column for each element of the collection.
   */
  private void addTo(QueryMatches result,
                     Collection theValues, int colix, Object[] row) {
    
    Object[] values = theValues.toArray();
    int length = values.length;
    for (int ix = 0; ix < length; ix++) {
      if (result.last+1 == result.size) {
        result.increaseCapacity();
      }
      result.last++;
      
      Object[] newRow = (Object[]) row.clone();
      newRow[colix] = values[ix];
      result.data[result.last] = newRow;
    }
  }
  
}
