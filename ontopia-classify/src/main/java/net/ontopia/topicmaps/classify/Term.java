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

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.Arrays;
import java.util.Comparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
  
/**
 * PUBLIC: Represents a concept which occurs in the classified
 * content.  A term can have many variants, all of which can be found
 * from this object. It also has a score, indicating the importance of
 * the term within the content.
 */
public class Term {
  // Define a logging category.
  private static Logger log = LoggerFactory.getLogger(Term.class.getName());
  
  protected String stem;  
  protected double score = 1.0d;
  protected int totalOccurrences;
  
  protected TObjectIntHashMap<Variant> variants = new TObjectIntHashMap<Variant>(); 
  
  Term(String stem) {
    this.stem = stem;
  }

  /**
   * PUBLIC: Returns the stem common to all variants of the term.
   * Often, the stem does not actually occur in the content.
   */
  public String getStem() {
    return stem;
  }
  
  /**
   * PUBLIC: Returns the term's score, a number in the range 0-1,
   * indicating its importance within the content.   
   */
  public double getScore() {
    return score;
  }

  /**
   * PUBLIC: Returns all variant spellings of this term within the
   * content.
   */
  public Variant[] getVariants() {
    return variants.keys(new Variant[] {});
  }

  /**
   * PUBLIC: Returns all variant spellings of this term within the
   * content, with the most important first.
   */
  public Variant[] getVariantsByRank() {
    Variant[] ranked = getVariants();
    Arrays.sort(ranked, new VariantComparator());
    return ranked;
  }

  /**
   * PUBLIC: Returns the number of times the term occurred within the
   * classified content.
   */
  public int getOccurrences() {
    return totalOccurrences;
  }

  /**
   * PUBLIC: Returns the preferred variant of the term. This is a form
   * of the term which actually occurred in the classified content.
   */
  public String getPreferredName() {
    if (variants.isEmpty()) {
      return getStem();
    }
    Variant maxKey = null;
    int maxValue = -1;
    TObjectIntIterator<Variant> iter = variants.iterator();
    while (iter.hasNext()) {
      iter.advance();
      int thisValue = iter.value();
      Variant thisKey = iter.key();
      // select variant with most occurrences, or lowest lexical value if equal for predictability
      if ((thisValue > maxValue) ||
          ((thisValue == maxValue) && (thisKey.getValue().compareTo(maxKey.getValue()) < 0))) {
        maxValue = thisValue;
        maxKey = thisKey;
      }
    }
    return maxKey.getValue();
  }
  
  protected double getScore(Variant v) {
    return (1.0d * getOccurrences(v)) / totalOccurrences;
  }
  
  protected int getOccurrences(Variant variant) {
    return variants.get(variant);
  }
  
  protected void setScore(double score, String reason) {
    if (score <= 0.0d) {
      throw new RuntimeException("Score is not nillable: " + score + " term: " + this);
    }
    log.debug(">" + stem + "< =" + score + ", " + reason);
    this.score = score;    
  }

  protected void addScore(double ascore, String reason) {
    this.score += ascore;
    log.debug(">" + stem + "< +" + ascore + "=" + score + ", " + reason);
  }

  protected void multiplyScore(double factor, String reason) {
    this.score = score * factor;
    log.debug(">" + stem + "< *" + factor + "=" + score + ", " + reason);
  }

  protected void divideScore(double factor, String reason) {
    this.score = score / factor;
    log.debug(">" + stem + "< /" + factor + "=" + score + ", " + reason);
  }
  
  protected void addVariant(Variant variant) {
    addVariant(variant, 1);
  }
  
  protected void addVariant(Variant variant, int occurrences) {
    if (variants.get(variant) > 0) {
      variants.increment(variant);
    } else {
      variants.put(variant, occurrences);
    }
    totalOccurrences += occurrences;
  }
  
  protected void merge(Term other) {
    if (other == this) {
      return;
    }
    
    this.score = this.score + other.score;    
    this.totalOccurrences = this.totalOccurrences + other.totalOccurrences;

    TObjectIntIterator<Variant> iter = other.variants.iterator();
    while (iter.hasNext()) {
      iter.advance();
      Variant key = iter.key();
      int value = iter.value();
      if (this.variants.containsKey(key)) {
        this.variants.adjustValue(key, value);
      } else {
        this.variants.put(key, value);
      }
      key.replaceTerm(this);
    }
  }
  
  @Override
  public String toString() {
    return '\'' + getStem() + "\'" + getScore() + ":" + (variants.isEmpty() ? "" : Arrays.asList(variants.keys()).toString());
  }
  
  protected static Comparator<Term> SCORE_COMPARATOR =
    new Comparator<Term>() {
      @Override
      public int compare(Term t1, Term t2) {
        return Double.compare(t2.getScore(), t1.getScore()); // NOTE: reverse order
      }
    };
  
  private class VariantComparator implements Comparator<Variant> {    
    @Override
    public int compare(Variant v1, Variant v2) {
      int c = Integer.compare(getOccurrences(v2), getOccurrences(v1)); // NOTE: reverse order
      if (c != 0) {
        return c;
      }
      return v1.getValue().compareTo(v2.getValue());
    }
  }
  
}
