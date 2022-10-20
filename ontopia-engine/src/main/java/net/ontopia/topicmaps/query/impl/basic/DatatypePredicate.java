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

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;

/**
 * INTERNAL: Implements the 'datatype' predicate.
 */
public class DatatypePredicate implements BasicPredicateIF {
  protected TopicMapIF topicmap;

  public DatatypePredicate(TopicMapIF topicmap) {
    this.topicmap = topicmap;
  }
  
  @Override
  public String getName() {
    return "datatype";
  }
  
  @Override
  public String getSignature() {
    return "ov s";
  }
  
  @Override
  public int getCost(boolean[] boundparams) {
    if (boundparams[0] && boundparams[1]) {
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    } else if (boundparams[0] && !boundparams[1]) {
      return PredicateDrivenCostEstimator.SINGLE_RESULT;
    } else if (!boundparams[0] && boundparams[1]) {
      // FIXME: can do better based on knowledge of datatypes here?
      return PredicateDrivenCostEstimator.BIG_RESULT;
    } else {
      return PredicateDrivenCostEstimator.WHOLE_TM_RESULT;
    }
  }

  @Override
  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    int objix = matches.getIndex(arguments[0]);
    int typeix = matches.getIndex(arguments[1]);

    if (matches.bound(objix) && !matches.bound(typeix)) {
      return PredicateUtils.objectToOne(matches, objix, typeix,
                                        TMObjectIF.class,
                                        PredicateUtils.OBJECT_TO_DATATYPE);

    } else if (matches.bound(objix) && matches.bound(typeix)) {
      return PredicateUtils.filter(matches, objix, typeix,
                                   TMObjectIF.class, String.class,
                                   PredicateUtils.FILTER_DATATYPE);
      
    } else if (!matches.bound(objix) && !matches.bound(typeix)) {
      return PredicateUtils.collectionToOne(matches, getObjects(),
                                            objix, typeix,
                                            PredicateUtils.GENERATE_DATATYPE);

    } else {
      return lookupObjects(matches, objix, typeix, makeTypeIndex());
    }
  }

  private Object[] getObjects() {
    TopicIF[] topics = (TopicIF[]) topicmap.getTopics().toArray(new TopicIF[] {});
    Collection objects = new ArrayList(topics.length * 3);
    
    for (int tix = 0; tix < topics.length; tix++) {
      objects.addAll(topics[tix].getOccurrences());
      
      Collection bns = topics[tix].getTopicNames();
      Iterator it = bns.iterator();
      while (it.hasNext()) {
        TopicNameIF bn = (TopicNameIF) it.next();
        objects.addAll(bn.getVariants());
      }
    }

    return objects.toArray();
  }

  private Map makeTypeIndex() {
    Map index = new HashMap();
    Iterator it = topicmap.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();

      Iterator it2 = topic.getTopicNames().iterator();
      while (it2.hasNext()) {
        TopicNameIF bn = (TopicNameIF) it2.next();

        Iterator it3 = bn.getVariants().iterator();
        while (it3.hasNext()) {
          VariantNameIF vn = (VariantNameIF) it3.next();
          add(index, vn, vn.getDataType());
        }
      }

      it2 = topic.getOccurrences().iterator();
      while (it2.hasNext()) {
        OccurrenceIF occ = (OccurrenceIF) it2.next();
        add(index, occ, occ.getDataType());
      }
    }
    return index;
  }

  private void add(Map index, Object object, LocatorIF datatype) {
    List objects = (List) index.get(datatype.getAddress());
    if (objects == null) {
      objects = new ArrayList();
      index.put(datatype.getAddress(), objects);
    }
    objects.add(object);
  }

  private QueryMatches lookupObjects(QueryMatches matches,
                                     int objix, int typeix,
                                     Map index) {
    QueryMatches result = new QueryMatches(matches);
    for (int ix = 0; ix <= matches.last; ix++) {
      if (!(matches.data[ix][typeix] instanceof String)) {
        continue;
      }

      String typeuri = (String) matches.data[ix][typeix];
      List objects = (List) index.get(typeuri);
      if (objects == null || objects.isEmpty()) {
        continue;
      }
      
      while (result.last + objects.size() >= result.size) {
        result.increaseCapacity();
      }

      Object[] values = objects.toArray();
      for (int pos = 0; pos < values.length; pos++) {
        Object[] newRow = (Object[]) matches.data[ix].clone();
        newRow[objix] = values[pos];
        result.data[++result.last] = newRow;
      }
    }

    return result;
  }    
}
