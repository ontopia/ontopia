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

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import gnu.trove.map.hash.TObjectIntHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: 
 */
public class CompoundAnalyzer extends AbstractDocumentAnalyzer implements TermAnalyzerIF {

  // Define a logging category.
  private static Logger log = LoggerFactory.getLogger(CompoundAnalyzer.class.getName());
  
  private TermDatabase tdb;
  private TermStemmerIF termStemmer;
  
  private Map<Variant, Followers> followers = new HashMap<Variant, Followers>();

  private int maxLength = 3;

  private double term1ScoreThreshold = 0.02d;
  private double term2ScoreThreshold = 0.02d;
  private int compositeOccsThreshold = 2;

  private double compoundFactor = 2.0d; // 0.6d;
  
  public CompoundAnalyzer() {
    super(1);
  }

  public void setMaxLength(int maxLength) {
    this.maxLength = maxLength;
  }

  public void setTerm1ScoreThreshold(double term1ScoreThreshold) {
    this.term1ScoreThreshold = term1ScoreThreshold;
  }

  public void setTerm2ScoreThreshold(double term2ScoreThreshold) {
    this.term2ScoreThreshold = term2ScoreThreshold;
  }

  public void setCompositeOccurrencesThreshold(int compositeOccsThreshold) {
    this.compositeOccsThreshold = compositeOccsThreshold;
  }
      
  public void setTermStemmer(TermStemmerIF stemmer) {
    this.termStemmer = stemmer;
  }
  
  // --------------------------------------------------------------------------
  // variant followers
  // --------------------------------------------------------------------------
  
  protected void addFollower(Variant variant, Token token) {
    addFollower(variant, token, 1);
  }
  
  protected void addFollower(Variant variant, Token token, int counts) {
    Followers f = followers.get(variant);
    if (f == null) {
      f = new Followers();
      followers.put(variant, f);
    }
    f.addFollower(token, counts);
  }
  
  // --------------------------------------------------------------------------
  // document analyzer
  // --------------------------------------------------------------------------

  @Override
  public void analyzeToken(TextBlock parent, Token token, int index) {
    // ignore non variant tokens
    if (token.getType() == Token.TYPE_VARIANT) {
      List<Token> tokens = parent.getTokens();
      int size = tokens.size();
      if (size-1 > index) {
        Token next = tokens.get(index+1);      
        addFollower(((Variant)token), next);
      }
    }
    
  }
  
  // --------------------------------------------------------------------------
  // term analyzer
  // --------------------------------------------------------------------------

  @Override
  public void analyzeTerm(Term term) {
    addComposites(tdb, term, 2);
  }
  
  @Override
  public void startAnalysis(TermDatabase tdb) {
    this.tdb = tdb;
  }

  @Override
  public void endAnalysis() {
    this.tdb = null;
  }

  public void addComposites(TermDatabase tdb, Term t1, int length) {
    double t1Score = t1.getScore();
    if (t1Score < term1ScoreThreshold) {
      return;
    }

    // loop over all variants and look at their followers
    Variant[] variants = t1.getVariants();
    for (int x=0; x < variants.length; x++) {
      Variant v1 = variants[x];      
      Followers f1 = followers.get(v1);
      if (f1 != null) {
        double limit = f1.getLimit();
        Variant[] followers1 = f1.getFollowers();
        for (int z=0; z < followers1.length; z++) {
          Variant v2 = followers1[z];
          Term t2 = v2.getTerm();

          if (t1.equals(t2)) {
            continue; // ignore repeated terms
          }

          double t2Score = t2.getScore();
          if (t2Score < term2ScoreThreshold) {
            continue;
          }

          // check threshold by term, not by variant
          double compositeScoreTerm = f1.getScore(t2);            
          int compositeOccsTerm = f1.getFollowerOccurrences(t2);
          String composite = v1.getValue() + " " + v2.getValue();
          
          log.debug("k:" + composite + " " + (compositeScoreTerm-limit) + ", " + compositeOccsTerm + "/" + f1.getTotalFollowerOccurences());
          // ignore composites below the thresholds
          if (compositeScoreTerm >= limit && compositeOccsTerm >= compositeOccsThreshold) {

            double compositeScore = f1.getScore(v2);            
            int compositeOccs = f1.getFollowerOccurrences(v2);
            
            // create new composite term
            Variant v3 = tdb.createVariant(composite);
            Term t3 = v3.getTerm();
            if (t3 == null) {
              String stem = (termStemmer != null ? termStemmer.stem(composite) : composite);
              t3 = tdb.getTerm(stem);
              double newScore = (t1Score + (t2Score * compositeScore)) * compoundFactor;
              if (t3 == null) {
                t3 = tdb.createTerm(stem);
                // calculate first time score
                t3.setScore(newScore, "new compound score");
                log.debug("c:" + t3.getStem() + " " + t3.getScore() + ", " + compositeOccs + "\n : (" + t1Score + " + (" + t2Score + " * " + compositeScore + ")) * " + compoundFactor + ")");
              } else {
                // adjust term score
                log.debug("d:" + compositeOccs + " * " + compositeScore);
                t3.addScore(newScore, "compound adjustment");
              }
              
              v3.setTerm(t3);
            }
            t3.addVariant(v3, compositeOccs);

            // register the followers of the new composite
            Followers f2 = followers.get(v2);
            if (f2 != null) {
              Variant[] followers2 = f2.getFollowers();
              for (int y=0; y < followers2.length; y++) {
                Variant v4 = followers2[y];
                addFollower(v3, v4, f2.getFollowerOccurrences(v4));
              }
            }
            // score down individual terms
            log.debug("  b: " + t1.getScore() + " " + t2.getScore());
            double ns1 = ((1.0d * compositeOccs) / t1.getOccurrences());
            double ns2 = ((1.0d * compositeOccs) / t2.getOccurrences());
            if (ns1 < 1.0d) {
              t1.multiplyScore((1.0d - ns1), "compound individiual adjustment");
            }
            if (ns2 < 1.0d) {
              t2.multiplyScore((1.0d - ns2), "compound individiual adjustment");
            }
            log.debug("  a: " + t1.getScore() + " " + t2.getScore());
            
            // find more complex composites
            if (length < maxLength) {
              addComposites(tdb, t3, ++length);
            }
          }
        }
      }
    }
  }
  
  // --------------------------------------------------------------------------
  // debug
  // --------------------------------------------------------------------------

  public void dump(Term t) {
    double termScore =  t.getScore();
    System.out.println("t:"+ t.getPreferredName() + " " + termScore + ", " + t.getOccurrences());
    Object[] variants = t.getVariantsByRank();
    for (int x=0; x < variants.length; x++) {
      Variant v = (Variant)variants[x];
      System.out.println("  v:" + v + ":" + t.getOccurrences(v));
      Followers f = followers.get(v);
      if (f == null) {
        System.out.println("    f:null");
      } else {
        System.out.println("    f:delimiters: " + f.getFollowedByDelimiter());
        
        Object[] followers = f.getFollowersByRank();
        for (int z=0; z < followers.length; z++) {
          Variant next = (Variant)followers[z];
          double nextScore = next.getTerm().getScore();
          int nextOccs = f.getFollowerOccurrences(next);
          double nextCompositeScore = f.getScore(next);
          System.out.println("    f:" + next.getValue() + " " +
                             nextScore + ", " + nextOccs + ", " +
                             nextCompositeScore);
        }
      }
    }
  }
  
  // --------------------------------------------------------------------------
  // nested classes
  // --------------------------------------------------------------------------
  
  private class CompositeScoreComparator implements Comparator<Variant> {
    private Followers f;
    CompositeScoreComparator(Followers f) {
      this.f = f;
    }    
    @Override
    public int compare(Variant v1, Variant v2) {
      return Double.compare(f.getScore(v2), f.getScore(v1));
    }
  }
  
  private class Followers {
    private TObjectIntHashMap<Variant> followers = new TObjectIntHashMap<Variant>();
    private int followedByDelimiter;
    private int totalFollowerOccurrences;

    public void addFollower(Token token, int counts) {
      if (token.getType() == Token.TYPE_VARIANT) {
        Variant variant = (Variant) token;
        if (followers.get(variant) > 0) {
          followers.adjustValue(variant, counts);
        } else {
          followers.put(variant, counts);
        }
        totalFollowerOccurrences += counts;
      } else {
        followedByDelimiter += counts;
      }
    }

    public Variant[] getFollowers() {
      return followers.keys(new Variant[followers.keys().length]);
    }
    
    public Variant[] getFollowersByRank() {
      Variant[] ranked = followers.keys(new Variant[followers.keys().length]);
      Arrays.sort(ranked, new CompositeScoreComparator(this));
      return ranked;
    }
    
    public int getTotalFollowerOccurences() {
      return totalFollowerOccurrences;
    }
    
    public int getFollowerOccurrences(Variant v) {
      return followers.get(v);
    }

    public int getFollowerOccurrences(Term t) {
      int occs = 0;
      Object[] variants = t.getVariants();
      for (int i=0; i < variants.length; i++) {
        Variant variant = (Variant)variants[i];
        if (followers.get(variant) > 0) {
          occs += getFollowerOccurrences(variant);
        }
      }
      return occs;
    }

    public int getFollowedByDelimiter() {
      return followedByDelimiter;
    }

    public double getScore(Variant v) {
      return (1.0d * getFollowerOccurrences(v)) / totalFollowerOccurrences;
    }

    public double getScore(Term t) {
      double score = 0d;
      Object[] variants = t.getVariants();
      for (int i=0; i < variants.length; i++) {
        Variant variant = (Variant)variants[i];
        if (followers.get(variant) > 0) {
          score += getScore(variant);
        }
      }
      return score;
    }


    public double getLimit() {
      return 0.64d - (Math.log(totalFollowerOccurrences) / 15.0); // NOTE: empirical
    }
    
  }
  
}
