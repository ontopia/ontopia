
// $Id: BaseLocatorPredicateTest.java,v 1.6 2008/01/11 13:29:34 geir.gronmo Exp $

package net.ontopia.topicmaps.query.core.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.ontopia.topicmaps.query.core.InvalidQueryException;

public class BaseLocatorPredicateTest extends AbstractPredicateTest {
  
  public BaseLocatorPredicateTest(String name) {
    super(name);
  }

  public void tearDown() {
    closeStore();
  }
  
  /// tests
  
  public void testCompletelyOpen() throws InvalidQueryException, IOException {
    load("family2.ltm");

    List matches = new ArrayList();
    addMatch(matches, "BASE", topicmap.getStore().getBaseAddress().getAddress());
    
    verifyQuery(matches, "base-locator($BASE)?");
  }
  
  public void testCompletelyOpenNoValue() throws InvalidQueryException, IOException {
    makeEmpty();

    // NOTE: have to skip rdbms test, because rdbms topic map store
    // gets base locator implicitly.
    if (topicmap.getStore().getBaseAddress() == null) {
      List matches = new ArrayList();    
      verifyQuery(matches, "base-locator($BASE)?");
    }
  }

  public void testWithSpecificValueFalse() throws InvalidQueryException, IOException {
    load("jill.xtm");

    findNothing(OPT_TYPECHECK_OFF +
                "base-locator(jillstm)?");
  }

  public void testWithSpecificValueFalse2() throws InvalidQueryException, IOException{
    load("jill.xtm");

    List matches = new ArrayList();    
    verifyQuery(matches, "base-locator(\"jillstm\")?");
  }

  public void testWithSpecificValueFalse3() throws InvalidQueryException, IOException{
    makeEmpty();

    List matches = new ArrayList();    
    verifyQuery(matches, "base-locator(\"file://tst.xtm\")?");
  }
  
  public void testWithSpecificValueTrue() throws InvalidQueryException, IOException {
    load("jill.xtm");

    String base = topicmap.getStore().getBaseAddress().getAddress();
    List matches = new ArrayList();
    matches.add(new HashMap());
    
    verifyQuery(matches, "base-locator(\"" + base + "\")?");
  }
  
}
