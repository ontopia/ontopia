/*
 * #!
 * Ontopia Classify
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

package net.ontopia.topicmaps.classify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.query.core.ParsedQueryIF;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: 
 */
public class TopicMapAnalyzer implements TermAnalyzerIF {

  private TermDatabase tdb;
  
  private ParsedQueryIF pq_byName;
  
  private Collection<TopicIF> ctypes;
  private List<TopicIF> ctypes_sorted;
  private Map<TopicIF, AssociationType> atypes;
  
  private Collection<TopicIF> atopics = new HashSet<TopicIF>();
  private Map<String, Variant> smap = new HashMap<String, Variant>();
  private Map<String, Collection<TopicIF>> vtopics = new HashMap<String, Collection<TopicIF>>();

  private double matchFactor = 4.0d;
  
  public TopicMapAnalyzer(TopicMapIF topicmap) {
    try {
      QueryProcessorIF qp = QueryUtils.getQueryProcessor(topicmap);
      this.pq_byName = qp.parse("select $T from topic-name($T, $N), value($N, %VALUE%)?");

      this.ctypes = new HashSet<TopicIF>();
      this.ctypes_sorted = new ArrayList<TopicIF>();
      this.atypes = new HashMap<TopicIF, AssociationType>();
      QueryResultIF qr = qp.execute("/* #OPTION: optimizer.reorder = false */ " +
                                    "using on for i\"http://psi.ontopia.net/ontology/\" " +
                                    "using cl for i\"http://psi.ontopia.net/classify/\" " +
                                    "using xtm for i\"http://www.topicmaps.org/xtm/1.0/core.xtm#\" " +
                                    "descendant-of($ANC, $DES) :- " +
                                    " { xtm:superclass-subclass($ANC : xtm:superclass, $DES : xtm:subclass) " +
                                    " | xtm:superclass-subclass($ANC : xtm:superclass, $MID : xtm:subclass), descendant-of($MID, $DES) }. " +
                                    "has-role-field($PT, $AT, $RT) :- " +
                                    " { on:has-field($AT : on:field, $PT : on:topic-type, $RT : on:role-type) " + 
                                    " | xtm:superclass-subclass($PT : xtm:subclass, $XT : xtm:superclass), has-role-field($XT, $AT, $RT) }. " +
                                    
                                    "select $CTYPE, $AT, $PRT, $CRT, $ASCORE, $USCORE from " +

                                    "subject-identifier($CT, \"http://psi.ontopia.net/classify/classification-type\"), type($A, $CT), " +
                                    "association-role($A, $R1), type($R1, $CAT), subject-identifier($CAT, \"http://psi.ontopia.net/classify/classified-association-type\"), " +
                                    "association-role($A, $R2), type($R2, $CTT), subject-identifier($CTT, \"http://psi.ontopia.net/classify/classified-topic-type\"), " +
                                    "role-player($R1, $AT), role-player($R2, $PTYPE), " + 
                                    "{ occurrence($AT, $O1), type($O1, $OT1), subject-identifier($OT1, \"http://psi.ontopia.net/classify/score-threshold-with-candidates\"), value($O1, $ASCORE)}, " +
                                    "{ occurrence($AT, $O2), type($O2, $OT2), subject-identifier($OT2, \"http://psi.ontopia.net/classify/score-threshold\"), value($O2, $USCORE)}, " +
                                    "has-role-field($PTYPE, $AT, $PRT), " + 
                                    "has-role-field($CTYPE, $AT, $CRT), " + 
                                    "$PRT /= $CRT, topic($CTYPE)" +
                                    "order by $CTYPE?");
      while (qr.next()) {
        TopicIF ctype = (TopicIF)qr.getValue(0);
        TopicIF atype = (TopicIF)qr.getValue(1);
        TopicIF prtype = (TopicIF)qr.getValue(2);
        TopicIF crtype = (TopicIF)qr.getValue(3);
        String asc = (String)qr.getValue(4);
        String usc = (String)qr.getValue(5);
        if (ctypes.add(ctype)) {
          ctypes_sorted.add(ctype);
        }

        AssociationType at = atypes.get(atype);
        if (at == null) {
          double ascore = -1.0d;
          if (asc != null) {
            try {
              ascore = Double.parseDouble(asc);
            } catch (NumberFormatException e) {
            }
          }
          double uscore = -1.0d;
          if (usc != null) {
            try {
              uscore = Double.parseDouble(usc);
            } catch (NumberFormatException e) {
            }
          }
          //! System.out.println("SC: " + atype + " " +  asc + " : " + score);
          at = new AssociationType(atype, prtype, crtype, ascore, uscore);
          atypes.put(atype, at);
        }
        at.addCandidateType(ctype);
        //! System.out.println("s: " + ctype);        
      }
      
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  @Override
  public void analyzeTerm(Term term) {
    try {
      int foundMatches = 0;
      
      // look up term by name
      //! System.out.println("t> '" + term.getStem() + "'");
      Object[] variants = term.getVariants();
      for (int i=0; i < variants.length; i++) {
        Variant variant = (Variant)variants[i];
        //! System.out.println(" v> '" + variant.getValue() + "'");
        QueryResultIF qr = pq_byName.execute(Collections.singletonMap("VALUE", variant.getValue()));
        try {
          while (qr.next()) {
            TopicIF topic = (TopicIF)qr.getValue(0);
            
            // ignore topic if topic type is unknown
            boolean validType = false;
            for (TopicIF type : topic.getTypes()) {
              if (ctypes.contains(type)) {
                validType = true;
                break;
              }
            }
            if (!validType) {
              //! System.out.println(" e> " + term.getStem() + " " + topic + " " + topic.getTypes());
              continue;
            }
            
            String value = variant.getValue(); 
            smap.put(value, variant);
            Collection<TopicIF> matching = vtopics.get(value);
            if (matching == null) {
              matching = new HashSet<TopicIF>();
              vtopics.put(value, matching);
            }
            matching.add(topic);
            atopics.add(topic);
            foundMatches++;
          }
        } finally {
          qr.close();
        }
      }

      // adjust score if term found in topic map
      if (foundMatches > 0) {
        term.multiplyScore(matchFactor, "found in topic map");
      }
      
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  @Override
  public void startAnalysis(TermDatabase tdb) {
    this.tdb = tdb;
  }

  @Override
  public void endAnalysis() {
    // merge terms that are synonyms
    for (TopicIF topic : atopics) {
      Term term = null;      
      for (TopicNameIF bname : topic.getTopicNames()) {
        term = createTerm(term, bname.getValue());
        for (VariantNameIF vname : bname.getVariants()) {
          term = createTerm(term, vname.getValue());
        }
      }
    }
    
    // boost score by topic type
    // store information about which terms map to which topics
    this.tdb = null;
  }

  private Term createTerm(Term term, String value) {
    if (value == null) {
      return term;
    }
    Variant variant = tdb.getVariant(value);
    if (variant != null) {
      if (term == null) {
        return variant.getTerm();
      } else {
        tdb.mergeTerms(term, variant.getTerm());
      }
    }
    return term;
  }

  // -- inner classes

  public static class AssociationType {

    public TopicIF atype;
    public TopicIF prtype;
    public TopicIF crtype;
    public double ascore;
    public double uscore;
    public Collection<TopicIF> ctypes = new HashSet<TopicIF>();
    
    AssociationType(TopicIF atype, TopicIF prtype, TopicIF crtype, double ascore, double uscore) {
      this.atype = atype;
      this.prtype = prtype;
      this.crtype = crtype;
      this.ascore = ascore;
      this.uscore = uscore;
    }

    public void addCandidateType(TopicIF ctype) {
      this.ctypes.add(ctype);
    }

    public String getKey() {
      return atype.getObjectId() + ":" + prtype.getObjectId() + ":" + crtype.getObjectId();
    }

    public String getAssociationTypeId() {
      return atype.getObjectId();
    }

    public String getContentRoleTypeId() {
      return prtype.getObjectId();
    }

    public String getTopicRoleTypeId() {
      return crtype.getObjectId();
    }
    
    public String getName() {
      return TopicStringifiers.toString(atype, prtype);
    }

    public double getScoreThreshold(boolean hasCandidates) {
      //! System.out.println("HS: " + hasCandidates + " " + atype + " " + ascore + " vs " + uscore);
      if (hasCandidates) {
        return (ascore >= 0 ? ascore : uscore);
      } else {
        return (uscore >= 0 ? uscore : ascore);
      }
    }
    
  }
  

  // -- public methods
  
  public Collection<TopicIF> getTopics(Variant variant) {
    Collection<TopicIF> result = vtopics.get(variant.getValue());
    return (result == null ? new HashSet<TopicIF>() : result);
  }

  public Collection<TopicIF> getCandidateTypes() {
    return ctypes_sorted;
  }

  public Collection<AssociationType> getAssociationTypes() {
    return atypes.values();
  }
  
}
