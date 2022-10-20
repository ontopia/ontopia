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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.index.NameIndexIF;
import net.ontopia.topicmaps.core.index.OccurrenceIndexIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;
import net.ontopia.topicmaps.query.impl.utils.Prefetcher;
import net.ontopia.topicmaps.query.parser.Variable;

/**
 * INTERNAL: Implements the 'value' predicate.
 */
public class ValuePredicate implements BasicPredicateIF {
  protected TopicMapIF topicmap;
  protected NameIndexIF nameindex;
  protected OccurrenceIndexIF occindex;

  // used to see what types the object parameter may have
  private static final int NO_TYPES  = 0;
  private static final int NAME_TYPE = 1;
  private static final int OCC_TYPE  = 2;
  private static final int ALL_TYPES = 3;
  
  public ValuePredicate(TopicMapIF topicmap) {
    this.topicmap = topicmap;
    this.nameindex = (NameIndexIF) topicmap
      .getIndex("net.ontopia.topicmaps.core.index.NameIndexIF");
    this.occindex = (OccurrenceIndexIF) topicmap
      .getIndex("net.ontopia.topicmaps.core.index.OccurrenceIndexIF");
  }
  
  @Override
  public String getName() {
    return "value";
  }
  
  @Override
  public String getSignature() {
    return "bov s";
  }
  
  @Override
  public int getCost(boolean[] boundparams) {
    if (boundparams[0] && boundparams[1]) {
      return PredicateDrivenCostEstimator.FILTER_RESULT;
    } else if (boundparams[0] && !boundparams[1]) {
      return PredicateDrivenCostEstimator.SINGLE_RESULT;
    } else if (!boundparams[0] && boundparams[1]) {
      return PredicateDrivenCostEstimator.SMALL_RESULT;
    } else {
      return PredicateDrivenCostEstimator.WHOLE_TM_RESULT;
    }
  }

  @Override
  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    int objix = matches.getIndex(arguments[0]);
    int valueix = matches.getIndex(arguments[1]);

    if (matches.bound(objix) && !matches.bound(valueix)) {
      prefetchValue(matches, arguments, objix);
      return PredicateUtils.objectToOne(matches, objix, valueix,
                                        TMObjectIF.class,
                                        PredicateUtils.OBJECT_TO_VALUE);

    } else if (!matches.bound(objix) && matches.bound(valueix)) {
      return lookupObjects(matches, objix, valueix);

    } else if (matches.bound(objix) && matches.bound(valueix)) {
      prefetchValue(matches, arguments, objix);
      return PredicateUtils.filter(matches, objix, valueix,
                                   TMObjectIF.class, String.class,
                                   PredicateUtils.FILTER_VALUE);
      
    } else {
      prefetchValue(matches, arguments, objix);
      return PredicateUtils.collectionToOne(matches, getObjects(), objix, valueix,
                                            PredicateUtils.GENERATE_VALUE);
    }
  }
  
  protected void prefetchValue(QueryMatches matches, Object[] arguments, int objix) {
    
    if (arguments[0] instanceof Variable) {
      String varname = ((Variable)arguments[0]).getName();
      Object[] types = matches.getQueryContext().getVariableTypes(varname);
      if (types != null) {
	for (int i=0; i < types.length; i++) {
	  if (TopicNameIF.class.equals(types[i])) {
	    Prefetcher.prefetch(topicmap, matches, objix,
				Prefetcher.TopicNameIF, 
				Prefetcher.TopicNameIF_value, false);
	  } else if (OccurrenceIF.class.equals(types[i])) {
	    Prefetcher.prefetch(topicmap, matches, objix,
				Prefetcher.OccurrenceIF, 
				Prefetcher.OccurrenceIF_value, false);
	  } else if (VariantNameIF.class.equals(types[i])) {
	    Prefetcher.prefetch(topicmap, matches, objix,
				Prefetcher.VariantNameIF, 
				Prefetcher.VariantNameIF_value, false);
	  }
	}
      }
    }
  }

  private QueryMatches lookupObjects(QueryMatches matches,
                                     int objix, int valueix) {
    int objtypes = getObjectTypes(matches, objix);
    
    QueryMatches result = new QueryMatches(matches);
    for (int ix = 0; ix <= matches.last; ix++) {
      if (!(matches.data[ix][valueix] instanceof String)) {
        continue;
      }

      String value = (String) matches.data[ix][valueix];
      Collection objects = new ArrayList();
      if ((objtypes & NAME_TYPE) != 0) {
        objects.addAll(nameindex.getTopicNames(value));
        objects.addAll(filterVariants(nameindex.getVariants(value)));
      }
      if ((objtypes & OCC_TYPE) != 0) {
        objects.addAll(filterOccurrences(occindex.getOccurrences(value)));
      }

      if (objects.isEmpty()) {
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

  // --- Helpers

  private int getObjectTypes(QueryMatches matches, int objix) {
    QueryContext context = matches.getQueryContext();
    String varname = ((Variable) matches.getColumnDefinition(objix)).getName();
    Object[] vartypes = context.getVariableTypes(varname);
    if (vartypes == null) {
      return ALL_TYPES;
    }

    int types = NO_TYPES;
    for (int ix = 0; ix < vartypes.length; ix++) {
      if (vartypes[ix].equals(TopicNameIF.class) ||
          vartypes[ix].equals(VariantNameIF.class)) {
        types |= NAME_TYPE;
      } else if (vartypes[ix].equals(OccurrenceIF.class)) {
        types |= OCC_TYPE;
      }
    }

    return types;
  }

  protected Object[] getObjects() {
    TopicIF[] topics = (TopicIF[]) topicmap.getTopics().toArray(new TopicIF[] {});
    Collection objects = new ArrayList(topics.length * 3);
    
    for (int tix = 0; tix < topics.length; tix++) {
      Collection bns = topics[tix].getTopicNames();
      objects.addAll(bns);
      objects.addAll(filterOccurrences(topics[tix].getOccurrences()));
      
      Iterator it = bns.iterator();
      while (it.hasNext()) {
        TopicNameIF bn = (TopicNameIF) it.next();
        objects.addAll(filterVariants(bn.getVariants()));
      }
    }

    return objects.toArray();
  }

  private static Collection filterOccurrences(Collection occs) {
    Collection result = new ArrayList(occs.size());
    Iterator iter = occs.iterator();
    while (iter.hasNext()) {
      OccurrenceIF occ = (OccurrenceIF) iter.next();
      if (!Objects.equals(occ.getDataType(), DataTypes.TYPE_URI)) {
        result.add(occ);
      }
    }
    return result;
  }

  private static Collection filterVariants(Collection vns) {
    Collection result = new ArrayList(vns.size());
    Iterator iter = vns.iterator();
    while (iter.hasNext()) {
      VariantNameIF vn = (VariantNameIF) iter.next();
      if (!Objects.equals(vn.getDataType(), DataTypes.TYPE_URI)) {
        result.add(vn);
      }
    }
    return result;
  }
  
}
