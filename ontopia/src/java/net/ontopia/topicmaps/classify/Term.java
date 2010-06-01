
// $Id: Term.java,v 1.23 2007/07/13 06:21:21 geir.gronmo Exp $

package net.ontopia.topicmaps.classify;

import java.util.*;

import net.ontopia.utils.*;
import gnu.trove.TObjectIntHashMap;
import gnu.trove.TObjectIntIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
  
/**
 * INTERNAL: 
 */
public class Term {
  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(Term.class.getName());
  
  protected String stem;  
  protected double score = 1.0d;
  protected int totalOccurrences;
  
  // key: variant, value: int
  protected TObjectIntHashMap<Variant> variants = new TObjectIntHashMap<Variant>(); 
  
  Term(String stem) {
    this.stem = stem;
  }

  public String getStem() {
    return stem;
  }
  
  public double getScore() {
    return score;
  }
  
  public void setScore(double score, String reason) {
    if (score <= 0.0d)
      throw new RuntimeException("Score is not nillable: " + score + " term: " + this);
    log.debug(">" + stem + "< =" + score + ", " + reason);
    this.score = score;    
  }

  public void addScore(double ascore, String reason) {
    this.score += ascore;
    log.debug(">" + stem + "< +" + ascore + "=" + score + ", " + reason);
  }

  public void multiplyScore(double factor, String reason) {
    this.score = score * factor;
    log.debug(">" + stem + "< *" + factor + "=" + score + ", " + reason);
  }

  public void divideScore(double factor, String reason) {
    this.score = score / factor;
    log.debug(">" + stem + "< /" + factor + "=" + score + ", " + reason);
  }
  
  public Variant[] getVariants() {
    return variants.keys(new Variant[] {});
  }

  public Variant[] getVariantsByRank() {
    Variant[] ranked = getVariants();
    Arrays.sort(ranked, new VariantComparator());
    return ranked;
  }

  public double getScore(Variant v) {
    return (1.0d * getOccurrences(v)) / totalOccurrences;
}

  public int getOccurrences() {
    return totalOccurrences;
  }
  
  public int getOccurrences(Variant variant) {
    return variants.get(variant);
  }

  public String getPreferredName() {
    if (variants.isEmpty())
      return getStem();
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
  
  public void addVariant(Variant variant) {
    addVariant(variant, 1);
  }
  
  public void addVariant(Variant variant, int occurrences) {
    if (variants.get(variant) > 0)
      variants.increment(variant);
    else
      variants.put(variant, occurrences);
    totalOccurrences += occurrences;
  }
  
  public void merge(Term other) {
    if (other == this) return;
    
    this.score = this.score + other.score;    
    this.totalOccurrences = this.totalOccurrences + other.totalOccurrences;

    TObjectIntIterator<Variant> iter = other.variants.iterator();
    while (iter.hasNext()) {
      iter.advance();
      Variant key = iter.key();
      int value = iter.value();
      if (this.variants.containsKey(key))
        this.variants.adjustValue(key, value);
      else
        this.variants.put(key, value);
      key.replaceTerm(this);
    }
  }
  
  public String toString() {
    return '\'' + getStem() + "\'" + getScore() + ":" + (variants.isEmpty() ? "" : Arrays.asList(variants.keys()).toString());
  }
  
  public static Comparator SCORE_COMPARATOR =
    new Comparator() {
      public int compare(Object o1, Object o2) {
        Term t1 = (Term)o1;
        Term t2 = (Term)o2;
        return ObjectUtils.compare(t2.getScore(), t1.getScore()); // NOTE: reverse order
      }
    };
  
  private class VariantComparator implements Comparator {    
    public int compare(Object o1, Object o2) {
      Variant v1 = (Variant)o1;
      Variant v2 = (Variant)o2;
      int c = ObjectUtils.compare(getOccurrences(v2), getOccurrences(v1)); // NOTE: reverse order
      if (c != 0) return c;
      return v1.getValue().compareTo(v2.getValue());
    }
  };
  
}
