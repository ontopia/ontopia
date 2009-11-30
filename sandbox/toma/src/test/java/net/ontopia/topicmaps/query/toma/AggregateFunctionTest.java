package net.ontopia.topicmaps.query.toma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

@SuppressWarnings("unchecked")
public class AggregateFunctionTest extends AbstractTomaQueryTestCase {
  
  public AggregateFunctionTest(String name) {
    super(name);
  }

  /// context management

  public void setUp() {
    QueryMatches.initialSize = 1;
  }

  public void tearDown() {
    closeStore();
  }

  /// checking query structure

  public void testCount() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "COUNT($T)", new Integer(2));
    
    verifyQuery(matches, "select count($t) where $t.type(1) = format;");
  }
  
  public void testSum() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "SUM($T.OC(i'pages'))", new Double(200));
    
    verifyQuery(matches,
        "select sum($t.oc(pages)) where $t.type = standard and exists $t.oc(pages);");
  }

  public void testMin() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "MIN($T.OC(i'pages'))", new Double(80));
    
    verifyQuery(matches,
        "select min($t.oc(pages)) where $t.type = standard and exists $t.oc(pages);");
  }
  
  public void testMax() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "MAX($T.OC(i'pages'))", new Double(120));
    
    verifyQuery(matches,
        "select max($t.oc(pages)) where $t.type = standard and exists $t.oc(pages);");
  }
  
  public void testAvg() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "AVG($T.OC(i'pages'))", new Double(100));
    
    verifyQuery(matches,
        "select avg($t.oc(pages)) where $t.type = standard and exists $t.oc(pages);");
  }

  public void testGroupedAggregation() throws InvalidQueryException,
      IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$P", getTopicById("ontopia"), "COUNT($C)", new Integer(2));
    addMatch(matches, "$P", getTopicById("tinytim"), "COUNT($C)", new Integer(1));
    
    verifyQuery(matches,
        "select $P, count($c) where $p.(project)<-(contributes-to)->(person) = $c;");
  }
  
  public void testCombinedAggregate() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "SUM($T.OC(i'pages'))", new Double(200), "COUNT($T)",
        new Integer(2));

    verifyQuery(
        matches,
        "select sum($t.oc(pages)), count($t) where $t.type = standard and exists $t.oc(pages);");
  }
  
  public void testConcat() throws InvalidQueryException, IOException {
    load("family.ltm");

    // this test creates a permutation of the possible
    // result (as sorting is done after aggregating), and checks if the actual
    // result is within the available matches.
    List matches = new ArrayList();
    addMatch(matches, "CONCAT($T.NAME,',')", "Edvin Garshol,Petter Garshol");
    addMatch(matches, "CONCAT($T.NAME,',')", "Petter Garshol,Edvin Garshol");

    verifyQueryExists(
        matches,
        "select concat($t.name, ',') where $t.type = father and $t.name ~ '(Edvin|Petter).*Garshol';");
  }
}
