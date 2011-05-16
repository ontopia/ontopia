
// $Id: DynamicSearcherPredicateTest.java,v 1.4 2008/06/12 14:37:21 geir.gronmo Exp $

package net.ontopia.topicmaps.query.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ontopia.topicmaps.query.spi.AbstractSearcher;
import net.ontopia.topicmaps.query.spi.AbstractSearchResult;
import net.ontopia.topicmaps.query.spi.SearcherIF;
import net.ontopia.topicmaps.query.spi.SearchResultIF;

public class DynamicSearcherPredicateTest extends AbstractPredicateTest {

  private static final String DECL = "import \"urn:x-java:net.ontopia.topicmaps.query.core.DynamicSearcherPredicateTest$ExactSearcher\" as fulltext ";
  
  public DynamicSearcherPredicateTest(String name) {
    super(name);
  }

  public void tearDown() {
    closeStore();
  }  

  public void testNoHits1() throws InvalidQueryException, IOException {
    load("fulltext.ltm");
    
    findNothing(DECL + "fulltext:exact($O, \"blah\")?");
  }
  
  public void testNoHits2() throws InvalidQueryException, IOException {
    load("fulltext.ltm");
    
    findNothing(DECL + "fulltext:exact($O, \"blah\", $S)?");
  }
  
  public void testCNoScore() throws InvalidQueryException, IOException {
    load("fulltext.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "O", "c");
    
    verifyQuery(matches,
                DECL + "fulltext:exact($O, \"c\")?");
  }
  
  public void testCScore() throws InvalidQueryException, IOException {
    load("fulltext.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "O", "c", "S", new Float(0.8f));
    
    verifyQuery(matches,
                DECL + "fulltext:exact($O, \"c\", $S)?");
  }
  
  public void testDNoScore() throws InvalidQueryException, IOException {
    load("fulltext.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "O", "d");
    
    verifyQuery(matches,
                DECL + "fulltext:exact($O, \"d\")?");
  }
  
  public void testDScore() throws InvalidQueryException, IOException {
    load("fulltext.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "O", "d", "S", new Float(0.5f));
    addMatch(matches, "O", "d", "S", new Float(0.7f));
    
    verifyQuery(matches,
                DECL + "fulltext:exact($O, \"d\", $S)?");
  }
  
  public void testDorFNoScore() throws InvalidQueryException, IOException {
    load("fulltext.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "O", "d");
    addMatch(matches, "O", "f");
    
    verifyQuery(matches,
                DECL + "{ fulltext:exact($O, \"d\") | fulltext:exact($O, \"f\") }?");
  }
  
  public void testDorFScore() throws InvalidQueryException, IOException {
    load("fulltext.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "O", "d", "S", new Float(0.5f));
    addMatch(matches, "O", "d", "S", new Float(0.7f));
    addMatch(matches, "O", "f", "S", new Float(0.4f));
    
    verifyQuery(matches,
                DECL + "{ fulltext:exact($O, \"d\", $S) | fulltext:exact($O, \"f\", $S) }?");
  }
  
  public void testDorFScoreAndTopics() throws InvalidQueryException, IOException {
    load("fulltext.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("d1"), "O", "d", "S", new Float(0.5f));
    addMatch(matches, "T", getTopicById("d1"), "O", "d", "S", new Float(0.7f));
    addMatch(matches, "T", getTopicById("d2"), "O", "d", "S", new Float(0.5f));
    addMatch(matches, "T", getTopicById("d2"), "O", "d", "S", new Float(0.7f));
    addMatch(matches, "T", getTopicById("f2"), "O", "f", "S", new Float(0.4f));
    
    verifyQuery(matches,
                DECL + "select $T, $O, $S from { fulltext:exact($O, \"d\", $S) | fulltext:exact($O, \"f\", $S) }, topic-name($T, $N), value($N, $O)?");
  }
  
  public void testBandInstancesOfT1() throws InvalidQueryException, IOException {
    load("fulltext.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("c1"), "O", "c", "S", new Float(0.8f));
    
    verifyQuery(matches, DECL +
                "select $T, $O, $S from instance-of($T, t1), topic-name($T, $N), value($N, $O), fulltext:unknown($O, \"c\", $S)?");
  }

  public void testUnknownSubjectLocator()
    throws InvalidQueryException, IOException {
    // tests what happens if a searcher returns a subject locator that does
    // not exist in the topic map being searched. the result should be that
    // the hit is ignored.

    load("fulltext.ltm");
        
    findNothing("import \"urn:x-java:net.ontopia.topicmaps.query.core.DynamicSearcherPredicateTest$UnknownSearcher\" as fulltext " +
                "fulltext:unknown($T, \"don't find anything\")?");    
  }
    
  // -- exact searcher implementation
    
  public static class ExactSearcher extends AbstractSearcher {

    public int getValueType() {
      return SearcherIF.STRING_VALUE;
    }
    
    public SearchResultIF getResult(String query) {
      return new ExactSearchResult(query);
    }
    
    private class ExactSearchResult extends AbstractSearchResult {
      String query;
      int c = 0;

      String[] o = new String[] { "a",  "b",  "c",  "d",  "e",  "d",  "f",  "g" };
      float[] f = new float[] {  1.0f, 0.9f, 0.8f, 0.7f, 0.6f, 0.5f, 0.4f, 0.3f };
      
      ExactSearchResult(String query) {
        this.query = query;
      }
      
      public boolean next() {
        for (int i = c+1; i < o.length; i++) {
          if (query.equals(o[i])) {
            c = i;
            return true;
          }
        }
        return false;
      }
      
      public Object getValue() {
        return o[c];
      }
      
      public float getScore() {
        return f[c];
      }
      
      public void close() {
      }
    };
  }
  
  // -- unknown topics searcher implementation

  public static class UnknownSearcher extends AbstractSearcher {

    public int getValueType() {
      return SearcherIF.SUBJECT_LOCATOR;
    }
    
    public SearchResultIF getResult(String query) {
      return new UnknownSearchResult();
    }
    
    private class UnknownSearchResult extends AbstractSearchResult {
      private boolean has_more = true;
      
      public boolean next() {
        boolean value = has_more;
        has_more = false;
        return value;
      }
      
      public Object getValue() {
        return "http://this.subject.locator.is.not.found/";
      }
      
      public float getScore() {
        return 1.0f;
      }
      
      public void close() {
      }
    };
  }
  
}
