
package net.ontopia.topicmaps.query.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ComparisonPredicateTests extends AbstractPredicateTest {
  
  public ComparisonPredicateTests(String name) {
    super(name);
  }

  /// setup

  public void tearDown() {    
    closeStore();
  }
  
  /// tests

  public void testGreaterThan() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "V", "WRONG WRONG WRONG");
    addMatch(matches, "V", "Unknown 1");
    addMatch(matches, "V", "Unknown 2");
    addMatch(matches, "V", "Trygve Garshol");
    
    verifyQuery(matches, "select $V from topic-name($T, $BN), value($BN, $V), $V > \"Tr\"?");
  }

  public void testLessThan() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "V", "Asle Skalle");
    addMatch(matches, "V", "Astri England Garshol");
    addMatch(matches, "V", "Bertha England");
    
    verifyQuery(matches, "select $V from topic-name($T, $BN), value($BN, $V), $V < \"Bh\"?");
  }

  public void testLessThanEqual() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "V", "Asle Skalle");
    addMatch(matches, "V", "Astri England Garshol");
    addMatch(matches, "V", "Bertha England");
    addMatch(matches, "V", "Bj\u00F8rg England");
    
    verifyQuery(matches, "select $V from topic-name($T, $BN), value($BN, $V), $V <= \"Bj\u00F8rg England\"?");
  }

  public void testGreaterThanEqual() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "V", "WRONG WRONG WRONG");
    addMatch(matches, "V", "Unknown 1");
    addMatch(matches, "V", "Unknown 2");
    addMatch(matches, "V", "Trygve Garshol");
    
    verifyQuery(matches, "select $V from topic-name($T, $BN), value($BN, $V), $V >= \"Trygve Garshol\"?");
  }

  public void testBetween1() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "V", "Kjellaug Garshol");
    addMatch(matches, "V", "Knut Garshol");
    addMatch(matches, "V", "Lars Marius Garshol");
    addMatch(matches, "V", "Lars Magne Skalle");
    addMatch(matches, "V", "Magnus England");
    addMatch(matches, "V", "May Stenersen");
    
    verifyQuery(matches, "select $V from topic-name($T, $BN), value($BN, $V), $V > \"K\", $V < \"N\"?");
  }

  public void testBetween2() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "V", "Kjellaug Garshol");
    addMatch(matches, "V", "Knut Garshol");
    addMatch(matches, "V", "Lars Marius Garshol");
    addMatch(matches, "V", "Lars Magne Skalle");
    addMatch(matches, "V", "Magnus England");
    addMatch(matches, "V", "May Stenersen");
    verifyQuery(matches, "select $V from topic-name($T, $BN), value($BN, $V), $V >= \"Kjellaug Garshol\", $V <= \"May Stenersen\"?");
  }

  public void testBug2123() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", "Asle Skalle");
		verifyQuery(matches, "select $N from instance-of($T, father), topic-name($T, $TN), value($TN, $N), { age($T, $AGE) }, $AGE > \"11\"?");
  }

}
