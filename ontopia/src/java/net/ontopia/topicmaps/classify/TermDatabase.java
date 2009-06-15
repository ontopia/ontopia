
// $Id: TermDatabase.java,v 1.15 2007/03/14 14:01:38 grove Exp $

package net.ontopia.topicmaps.classify;

import java.util.*;

import net.ontopia.utils.*;

/**
 * INTERNAL: 
 */
public class TermDatabase {
  
  protected Map terms = new HashMap();
  protected Map variants = new HashMap();  
  protected Map delimiter_terms = new HashMap();
  
  TermDatabase() {
  }

  public double getMaxScore() {
    Object[] terms = getTermsByRank();
    return ((Term)terms[0]).getScore();
  }

  public Collection getTerms() {
    return terms.values();
  }

  public Object[] getTermsByRank() {
    Object[] ranked = terms.values().toArray();
    Arrays.sort(ranked, Term.SCORE_COMPARATOR);
    return ranked;
  }

  public int getTermCount() {
    return terms.size();
  }
  
  public Term getTerm(String stem) {
    return (Term)terms.get(stem);
  }
  
  public Term createTerm(String stem) {
    Term term = (Term)terms.get(stem);
    if (term == null) {
      term = new Term(stem);
      terms.put(stem, term);
    }
    return term;
  }

  public Variant getVariant(String variant) {
    return (Variant)variants.get(variant);
  }

  public Variant createVariant(String variant) {
    Variant v = (Variant)variants.get(variant);
    if (v == null) {
      v = new Variant(variant);
      variants.put(variant, v);
    }
    return v;
  }
  
  public Token createDelimiter(String delimiter) {
    Token token = (Token)delimiter_terms.get(delimiter);
    if (token == null) {
      token = new Token(delimiter, Token.TYPE_DELIMITER);
      delimiter_terms.put(delimiter, token);
    }
    return token;
  }
  
  public void mergeTerms(Term t1, Term t2) {
    if (t1 == t2) return;
    t1.merge(t2);
    terms.remove(t2.getStem());
  }
  
  // --------------------------------------------------------------------------
  // debug
  // --------------------------------------------------------------------------

  public void dump() {
    dump(-1);
  }
  
  public void dump(int firstN) {
    // rank terms by score
    Object[] terms = getTermsByRank();
    
    // output top N terms
    int num = (firstN <= 0 ? terms.length : Math.min(terms.length, firstN));
    
    for (int i=0; i < num; i++) {
      Term t = (Term)terms[i];
      System.out.println(Integer.toString(i+1) + ": " + t.getPreferredName() + " " + t.getScore() + ", " + t.getOccurrences());
    }
    System.out.println("Total: " + terms.length + " terms.");
  }
  
}
