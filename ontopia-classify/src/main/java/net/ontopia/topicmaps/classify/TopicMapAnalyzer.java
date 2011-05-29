
package net.ontopia.topicmaps.classify;

import java.util.*;

import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.topicmaps.query.core.*;
import net.ontopia.topicmaps.query.utils.*;

/**
 * INTERNAL: 
 */
public class TopicMapAnalyzer implements TermAnalyzerIF {

  TermDatabase tdb;
  
  TopicMapIF topicmap;

  QueryProcessorIF qp;
  ParsedQueryIF pq_byName;
  
  Collection ctypes;
  List ctypes_sorted;
  Map atypes;
  
  Collection atopics = new HashSet();
  Map smap = new HashMap(); // key: string, value: variant
  Map vtopics = new HashMap(); // key: string, value: collection of topics

  double matchFactor = 4.0d;
  
  public TopicMapAnalyzer(TopicMapIF topicmap) {
    this.topicmap = topicmap;
    try {
      this.qp = QueryUtils.getQueryProcessor(topicmap);
      this.pq_byName = qp.parse("select $T from topic-name($T, $N), value($N, %VALUE%)?");

      this.ctypes = new HashSet();
      this.ctypes_sorted = new ArrayList();
      this.atypes = new HashMap();
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
        if (ctypes.add(ctype))
          ctypes_sorted.add(ctype);

        AssociationType at = (AssociationType)atypes.get(atype);
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
            Iterator iter = topic.getTypes().iterator();
            while (iter.hasNext()) {
              if (ctypes.contains(iter.next())) {
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
            Collection matching = (Collection)vtopics.get(value);
            if (matching == null) {
              matching = new HashSet();
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
      if (foundMatches > 0)
        term.multiplyScore(matchFactor, "found in topic map");
      
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  public void startAnalysis(TermDatabase tdb) {
    this.tdb = tdb;
  }

  public void endAnalysis() {
    // merge terms that are synonyms
    Iterator iter = atopics.iterator();
    while (iter.hasNext()) {
      TopicIF topic = (TopicIF)iter.next();

      Term term = null;      
      Iterator niter = topic.getTopicNames().iterator();
      while (niter.hasNext()) {
        TopicNameIF bname = (TopicNameIF)niter.next();
        term = createTerm(term, bname.getValue());

        Iterator viter = bname.getVariants().iterator();
        while (viter.hasNext()) {
          VariantNameIF vname = (VariantNameIF)viter.next();
          term = createTerm(term, vname.getValue());
        }
      }
    }
    
    // boost score by topic type
    // store information about which terms map to which topics
    this.tdb = null;
  }

  private Term createTerm(Term term, String value) {
    if (value == null) return term;
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
    public Collection ctypes = new HashSet();
    
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
      if (hasCandidates)
        return (ascore >= 0 ? ascore : uscore);
      else
        return (uscore >= 0 ? uscore : ascore);
    }
    
  }
  

  // -- public methods
  
  public Collection getTopics(Variant variant) {
    Collection result = (Collection)vtopics.get(variant.getValue());
    return (result == null ? Collections.EMPTY_SET : result);
  }

  public Collection getCandidateTypes() {
    return ctypes_sorted;
  }

  public Collection getAssociationTypes() {
    return atypes.values();
  }
  
}
