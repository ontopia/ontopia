package net.ontopia.topicmaps.query.toma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

@SuppressWarnings("unchecked")
public class FunctionTest extends AbstractTomaQueryTestCase {
  
  public FunctionTest(String name) {
    super(name);
  }

  /// context management

  public void setUp() {
    QueryMatches.initialSize = 1;
  }

  public void tearDown() {
    closeStore();
  }

  /// function tests
  
  public void testLowercase() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("format"));
    
    verifyQuery(matches, "select $t where lowercase($t.name) = 'format';");
  }
  
  public void testUppercase() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("format"));
    
    verifyQuery(matches, "select $t where uppercase($t.name) = 'FORMAT';");
  }  
  
  public void testTitlecase() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("format-for"));
    
    verifyQuery(matches, "select $t where titlecase($t.name) = 'Format For';");
  } 
  
  public void testToNum() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TO_NUM($T.OC(i'mass'))", "2.0");
    
    verifyQuery(matches, "select to_num($t.oc(mass)) where $t.id = xtm-standard;");
  }
  
  public void testSubstr() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "SUBSTR($T.NAME,'1','3')", "XTM");
    addMatch(matches, "SUBSTR($T.NAME,'1','3')", "The");
    
    verifyQuery(matches, "select substr($t.name, 1, 3) where $t.type = format;");
  }
  
  public void testTrim() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TRIM($T.NAME,'BOTH',' Project')", "Ontopia");
    
    verifyQuery(matches, "select trim($t.name, BOTH, ' Project') where $t.type = project;");
  } 

  public void testLength() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "LENGTH(TRIM($T.NAME,'BOTH',' Project'))", "7");
    
    verifyQuery(matches, "select length(trim($t.name, BOTH, ' Project')) where $t.type = project;");
  } 
}
