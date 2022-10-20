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

import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Collection;
import java.util.Objects;

/**
 * PUBLIC: A collection of terms representing the result of
 * classifying a piece of content. The terms have scores indicating
 * their importance within the content, and variants, indicating
 * different spellings for the same term within the content.
 *
 * <p>Use SimpleClassifier to create TermDatabase objects.
 */
public class TermDatabase {
  protected Map<String, Term> terms;
  protected Map<String, Variant> variants;
  protected Map<String, Token> delimiter_terms;
  
  TermDatabase() {
    this.terms = new HashMap<String, Term>();
    this.variants = new HashMap<String, Variant>();
    this.delimiter_terms = new HashMap<String, Token>();
  }

  /**
   * PUBLIC: Returns all terms found in the classified content.
   */
  public Collection<Term> getTerms() {
    return terms.values();
  }

  /**
   * PUBLIC: Returns all terms found in the classified content sorted
   * by score.
   */
  public Term[] getTermsByRank() {
    Term[] ranked = terms.values().toArray(new Term[] {});
    Arrays.sort(ranked, Term.SCORE_COMPARATOR);
    return ranked;
  }

  /**
   * PUBLIC: Returns the number of terms in the database.
   */
  public int getTermCount() {
    return terms.size();
  }
  
  /**
   * PUBLIC: Looks up a particular term by its stem. Returns null if
   * no term is found.
   */
  public Term getTerm(String stem) {
    return terms.get(stem);
  }
  
  /**
   * PUBLIC: Looks up a particular variant by its string representation.
   * Returns null if no variant is found.
   */
  public Variant getVariant(String variant) {
    return variants.get(variant);
  }
  
  // --------------------------------------------------------------------------
  // package internal
  // --------------------------------------------------------------------------
  
  protected Token createDelimiter(String delimiter) {
    Token token = delimiter_terms.get(delimiter);
    if (token == null) {
      token = new Token(delimiter, Token.TYPE_DELIMITER);
      delimiter_terms.put(delimiter, token);
    }
    return token;
  }
  
  protected void mergeTerms(Term t1, Term t2) {
    if (Objects.equals(t1, t2)) {
      return;
    }
    t1.merge(t2);
    terms.remove(t2.getStem());
  }

  protected Term createTerm(String stem) {
    Term term = terms.get(stem);
    if (term == null) {
      term = new Term(stem);
      terms.put(stem, term);
    }
    return term;
  }
  
  protected double getMaxScore() {
    Term[] terms = getTermsByRank();
    if (terms.length == 0) {
      return 0;
    } else {
      return terms[0].getScore();
    }
  }

  protected Variant createVariant(String variant) {
    Variant v = variants.get(variant);
    if (v == null) {
      v = new Variant(variant);
      variants.put(variant, v);
    }
    return v;
  }
  
  // --------------------------------------------------------------------------
  // debug
  // --------------------------------------------------------------------------

  /**
   * INTERNAL: Writes the contents of the term database out to
   * System.out for debugging purposes.
   */
  public void dump() {
    dump(-1);
  }
  
  /**
   * INTERNAL: Writes the contents of the term database out to
   * System.out for debugging purposes.
   * @param firstN how many terms to output   
   */
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
