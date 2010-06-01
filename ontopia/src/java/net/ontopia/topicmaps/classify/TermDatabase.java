
// $Id: TermDatabase.java,v 1.15 2007/03/14 14:01:38 grove Exp $

package net.ontopia.topicmaps.classify;

import java.util.*;

import net.ontopia.utils.*;

/**
 * INTERNAL: 
 */
public class TermDatabase {
  protected Map<String, Term> terms = new HashMap<String, Term>();
  protected Map<String, Variant> variants = new HashMap<String, Variant>();  
  protected Map<String, Token> delimiter_terms = new HashMap<String, Token>();
  
  TermDatabase() {
  }

  public double getMaxScore() {
    Term[] terms = getTermsByRank();
    return terms[0].getScore();
  }

  public Collection<Term> getTerms() {
    return terms.values();
  }

  public Term[] getTermsByRank() {
    Term[] ranked = terms.values().toArray(new Term[] {});
    Arrays.sort(ranked, Term.SCORE_COMPARATOR);
    return ranked;
  }

  public int getTermCount() {
    return terms.size();
  }
  
  public Term getTerm(String stem) {
    return terms.get(stem);
  }
  
  public Term createTerm(String stem) {
    Term term = terms.get(stem);
    if (term == null) {
      term = new Term(stem);
      terms.put(stem, term);
    }
    return term;
  }

  public Variant getVariant(String variant) {
    return variants.get(variant);
  }

  public Variant createVariant(String variant) {
    Variant v = variants.get(variant);
    if (v == null) {
      v = new Variant(variant);
      variants.put(variant, v);
    }
    return v;
  }
  
  public Token createDelimiter(String delimiter) {
    Token token = delimiter_terms.get(delimiter);
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
    Term[] terms = getTermsByRank();
    
    // output top N terms
    int num = (firstN <= 0 ? terms.length : Math.min(terms.length, firstN));
    
    for (int i=0; i < num; i++) {
      Term t = terms[i];
      System.out.println(Integer.toString(i+1) + ": " + t.getPreferredName() + " " + t.getScore() + ", " + t.getOccurrences());
    }
    System.out.println("Total: " + terms.length + " terms.");
  }
  
}
