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

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.OccurrenceIndexIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.utils.PredicateDrivenCostEstimator;
import net.ontopia.topicmaps.query.impl.utils.PredicateSignature;
import net.ontopia.topicmaps.query.spi.SearchResultIF;
import net.ontopia.topicmaps.query.spi.SearcherIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * EXPERIMENTAL: Java searcher predicate.<p>
 */
public class JavaSearcherPredicate implements BasicPredicateIF {

  protected String name;
  protected TopicMapIF topicmap;
  protected SearcherIF searcher;
  
  public JavaSearcherPredicate(String name, TopicMapIF topicmap,
                               SearcherIF searcher) {
    this.name = name;
    this.topicmap = topicmap;
    this.searcher = searcher;
  }
  
  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getSignature() {
    int valtype = searcher.getValueType();
    switch (valtype) {
    case SearcherIF.STRING_VALUE:
      return "s s! f?";
    case SearcherIF.OBJECT_ID:
    case SearcherIF.ITEM_IDENTIFIER:
      return "x s! f?";
    case SearcherIF.OCCURRENCE_URI:
      return "o s! f?";
    case SearcherIF.SUBJECT_LOCATOR:
    case SearcherIF.SUBJECT_IDENTIFIER:
      return "t s! f?";
    default:
      throw new OntopiaRuntimeException("Unknown searcher value type: " + valtype);
    }
  }

  @Override
  public int getCost(boolean[] boundparams) {
    return PredicateDrivenCostEstimator.FILTER_RESULT; // FIXME: is this right?
  }

  @Override
  public QueryMatches satisfy(QueryMatches matches, Object[] arguments)
    throws InvalidQueryException {

    PredicateSignature sign = PredicateSignature.getSignature(this);
    sign.verifyBound(matches, arguments, this);
    
    int oix = matches.getIndex(arguments[0]);
    int qix = matches.getIndex(arguments[1]);
    int six = (arguments.length > 2 ? matches.getIndex(arguments[2]) : -1);
    
    if (!matches.bound(qix)) {
      throw new InvalidQueryException("Second argument to " + getName() + " must " +
                                      "be bound");
    }

    if (six >= 0 && matches.bound(six)) {
      throw new InvalidQueryException("Third argument to " + getName() + " must " +
                                      "be unbound");
    }
    
    int valtype = searcher.getValueType();

    // find all distinct queries and initialize result maps
    Map queries = new HashMap();
    for (int ix = 0; ix <= matches.last; ix++) {
      Map r = (Map)queries.get(matches.data[ix][qix]);
      if (r == null) {
        r = new HashMap();
        queries.put(matches.data[ix][qix], r);
      }
    }

    // get results for all queries
    Iterator qiter = queries.keySet().iterator();
    while (qiter.hasNext()) {
      String query = (String)qiter.next();
      Map r = (Map)queries.get(query);
      SearchResultIF sr = searcher.getResult(query);
      try {
        while (sr.next()) {
          float score = sr.getScore();
          // need special logic for occurrence uris as there might be
          // multiple occurrences with the same uri.
          if (valtype == SearcherIF.OCCURRENCE_URI) {
            OccurrenceIndexIF occindex = (OccurrenceIndexIF)topicmap
              .getIndex("net.ontopia.topicmaps.core.index.OccurrenceIndexIF");
            Iterator occiter = occindex.getOccurrences((String)sr.getValue()).iterator();
            while (occiter.hasNext()) {
              Object o = occiter.next();
              Collection s = (Collection)r.get(o);
              if (s == null) {
                s = new ArrayList();
                r.put(o, s);
              }
              s.add(score);
            }
          } else {
            Object o = getObject(sr.getValue(), valtype);
            // note: ignore null values
            if (o == null) {
              continue;
            }
            Collection s = (Collection)r.get(o);
            if (s == null) {
              s = new ArrayList();
              r.put(o, s);
            }
            s.add(score);
          }
        }
      } finally {
        sr.close();
      }
    }

    // loop over results to populate and/or filter query matches
    QueryMatches result = new QueryMatches(matches);
    for (int ix = 0; ix <= matches.last; ix++) {
      String query = (String)matches.data[ix][qix];
      Map r = (Map)queries.get(query);

      if (matches.bound(oix)) {
        // filter results
        Object o = matches.data[ix][oix];
        Collection scores = (Collection)r.get(o);
        if (scores == null) {
          continue;
        }
        Iterator siter = scores.iterator();
        while (siter.hasNext()) {
          Object score = siter.next();
          
          Object[] newRow = (Object[]) matches.data[ix].clone();
          //! newRow[oix] = o;
          if (six >= 0) {
            newRow[six] = score;
          }
          
          if (result.last+1 == result.size) {
            result.increaseCapacity();
          }
          result.last++;
          
          // FIXME: is this really safe? or could row sharing give overwrites?
          result.data[result.last] = newRow;          
        }
      } else {
        // query to results
        Iterator oiter = r.keySet().iterator();
        while (oiter.hasNext()) {
          Object o = oiter.next();
          Collection scores = (Collection)r.get(o);
          Iterator siter = scores.iterator();
          while (siter.hasNext()) {
            Object score = siter.next();

            Object[] newRow = (Object[]) matches.data[ix].clone();
            newRow[oix] = o;
            if (six >= 0) {
              newRow[six] = score;
            }
            
            if (result.last+1 == result.size) {
              result.increaseCapacity();
            }
            result.last++;
            
            // FIXME: is this really safe? or could row sharing give overwrites?
            result.data[result.last] = newRow;          
          }
        }
      }
    }

    return result;
  }
  
  protected Object getObject(Object value, int valtype) {
    try {
      switch (valtype) {
      case SearcherIF.STRING_VALUE:
        return value.toString();
      case SearcherIF.OBJECT_VALUE:
        return value;
      case SearcherIF.OBJECT_ID:
        return topicmap.getObjectById((String)value);
      case SearcherIF.SUBJECT_LOCATOR:
        return topicmap.getTopicBySubjectLocator(new URILocator((String)value));
      case SearcherIF.SUBJECT_IDENTIFIER:
        return topicmap.getTopicBySubjectIdentifier(new URILocator((String)value));
      case SearcherIF.ITEM_IDENTIFIER:
        return topicmap.getObjectByItemIdentifier(new URILocator((String)value));
      default:
        throw new OntopiaRuntimeException("Unknown searche value type: " + valtype);
      }
    } catch (URISyntaxException m) {
      throw new OntopiaRuntimeException(m);
    }
  } 
  
}





