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

package net.ontopia.topicmaps.query.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.ontopia.topicmaps.query.spi.AbstractSearchResult;
import net.ontopia.topicmaps.query.spi.AbstractSearcher;
import net.ontopia.topicmaps.query.spi.SearchResultIF;
import net.ontopia.topicmaps.query.spi.SearcherIF;
import org.junit.Test;

public class DynamicSearcherPredicateTest extends AbstractPredicateTest {

  private static final String DECL = "import \"urn:x-java:net.ontopia.topicmaps.query.core.DynamicSearcherPredicateTest$ExactSearcher\" as fulltext ";
  
  @Test
  public void testNoHits1() throws InvalidQueryException, IOException {
    load("fulltext.ltm");
    
    assertFindNothing(DECL + "fulltext:exact($O, \"blah\")?");
  }
  
  @Test
  public void testNoHits2() throws InvalidQueryException, IOException {
    load("fulltext.ltm");
    
    assertFindNothing(DECL + "fulltext:exact($O, \"blah\", $S)?");
  }
  
  @Test
  public void testCNoScore() throws InvalidQueryException, IOException {
    load("fulltext.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "O", "c");
    
    assertQueryMatches(matches,
                DECL + "fulltext:exact($O, \"c\")?");
  }
  
  @Test
  public void testCScore() throws InvalidQueryException, IOException {
    load("fulltext.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "O", "c", "S", 0.8f);
    
    assertQueryMatches(matches,
                DECL + "fulltext:exact($O, \"c\", $S)?");
  }
  
  @Test
  public void testDNoScore() throws InvalidQueryException, IOException {
    load("fulltext.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "O", "d");
    
    assertQueryMatches(matches,
                DECL + "fulltext:exact($O, \"d\")?");
  }
  
  @Test
  public void testDScore() throws InvalidQueryException, IOException {
    load("fulltext.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "O", "d", "S", 0.5f);
    addMatch(matches, "O", "d", "S", 0.7f);
    
    assertQueryMatches(matches,
                DECL + "fulltext:exact($O, \"d\", $S)?");
  }
  
  @Test
  public void testDorFNoScore() throws InvalidQueryException, IOException {
    load("fulltext.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "O", "d");
    addMatch(matches, "O", "f");
    
    assertQueryMatches(matches,
                DECL + "{ fulltext:exact($O, \"d\") | fulltext:exact($O, \"f\") }?");
  }
  
  @Test
  public void testDorFScore() throws InvalidQueryException, IOException {
    load("fulltext.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "O", "d", "S", 0.5f);
    addMatch(matches, "O", "d", "S", 0.7f);
    addMatch(matches, "O", "f", "S", 0.4f);
    
    assertQueryMatches(matches,
                DECL + "{ fulltext:exact($O, \"d\", $S) | fulltext:exact($O, \"f\", $S) }?");
  }
  
  @Test
  public void testDorFScoreAndTopics() throws InvalidQueryException, IOException {
    load("fulltext.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("d1"), "O", "d", "S", 0.5f);
    addMatch(matches, "T", getTopicById("d1"), "O", "d", "S", 0.7f);
    addMatch(matches, "T", getTopicById("d2"), "O", "d", "S", 0.5f);
    addMatch(matches, "T", getTopicById("d2"), "O", "d", "S", 0.7f);
    addMatch(matches, "T", getTopicById("f2"), "O", "f", "S", 0.4f);
    
    assertQueryMatches(matches,
                DECL + "select $T, $O, $S from { fulltext:exact($O, \"d\", $S) | fulltext:exact($O, \"f\", $S) }, topic-name($T, $N), value($N, $O)?");
  }
  
  @Test
  public void testBandInstancesOfT1() throws InvalidQueryException, IOException {
    load("fulltext.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("c1"), "O", "c", "S", 0.8f);
    
    assertQueryMatches(matches, DECL +
                "select $T, $O, $S from instance-of($T, t1), topic-name($T, $N), value($N, $O), fulltext:unknown($O, \"c\", $S)?");
  }

  @Test
  public void testUnknownSubjectLocator()
    throws InvalidQueryException, IOException {
    // tests what happens if a searcher returns a subject locator that does
    // not exist in the topic map being searched. the result should be that
    // the hit is ignored.

    load("fulltext.ltm");
        
    assertFindNothing("import \"urn:x-java:net.ontopia.topicmaps.query.core.DynamicSearcherPredicateTest$UnknownSearcher\" as fulltext " +
                "fulltext:unknown($T, \"don't find anything\")?");    
  }
    
  // -- exact searcher implementation
    
  public static class ExactSearcher extends AbstractSearcher {

    @Override
    public int getValueType() {
      return SearcherIF.STRING_VALUE;
    }
    
    @Override
    public SearchResultIF getResult(String query) {
      return new ExactSearchResult(query);
    }
    
    private class ExactSearchResult extends AbstractSearchResult {
      private String query;
      private int c = 0;

      private String[] o = new String[] { "a",  "b",  "c",  "d",  "e",  "d",  "f",  "g" };
      private float[] f = new float[] {  1.0f, 0.9f, 0.8f, 0.7f, 0.6f, 0.5f, 0.4f, 0.3f };
      
      ExactSearchResult(String query) {
        this.query = query;
      }
      
      @Override
      public boolean next() {
        for (int i = c+1; i < o.length; i++) {
          if (query.equals(o[i])) {
            c = i;
            return true;
          }
        }
        return false;
      }
      
      @Override
      public Object getValue() {
        return o[c];
      }
      
      @Override
      public float getScore() {
        return f[c];
      }
      
      @Override
      public void close() {
        // no-op
      }
    };
  }
  
  // -- unknown topics searcher implementation

  public static class UnknownSearcher extends AbstractSearcher {

    @Override
    public int getValueType() {
      return SearcherIF.SUBJECT_LOCATOR;
    }
    
    @Override
    public SearchResultIF getResult(String query) {
      return new UnknownSearchResult();
    }
    
    private class UnknownSearchResult extends AbstractSearchResult {
      private boolean has_more = true;
      
      @Override
      public boolean next() {
        boolean value = has_more;
        has_more = false;
        return value;
      }
      
      @Override
      public Object getValue() {
        return "http://this.subject.locator.is.not.found/";
      }
      
      @Override
      public float getScore() {
        return 1.0f;
      }
      
      @Override
      public void close() {
        // no-op
      }
    };
  }
  
}
