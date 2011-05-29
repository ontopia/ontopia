
package net.ontopia.topicmaps.query.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.ontopia.infoset.core.LocatorIF;

public class TopicMapPredicateTest extends AbstractPredicateTest {
  
  public TopicMapPredicateTest(String name) {
    super(name);
  }

  /// tests
  
  public void testCompletelyOpen() throws InvalidQueryException, IOException {
    load("family2.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TOPICMAP", topicmap);
    
    verifyQuery(matches, "topicmap($TOPICMAP)?");
    closeStore();
  }

  public void testWithSpecificTopicMap() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList();
    matches.add(new HashMap());
    
    verifyQuery(matches, "topicmap(jillstm)?");
    closeStore();
  }

  public void testWithSpecificNonTopicMap() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList(); // should not match anything
    verifyQuery(matches, OPT_TYPECHECK_OFF +
                "topicmap(jill-ontopia-association)?");
    closeStore();
  }

  public void testWithCrossJoin() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList(); // should not match anything
    verifyQuery(matches, OPT_TYPECHECK_OFF +
                "topic($NOTHING), topicmap($NOTHING)?");
    closeStore();
  }

  public void testBug2003() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList(); // should not match anything
    LocatorIF loc = (LocatorIF)topicmap.getItemIdentifiers().iterator().next();
    addMatch(matches, "SRCLOC", loc.getAddress());
    verifyQuery(matches, "select $SRCLOC from topicmap($TM), item-identifier($TM, $SRCLOC)?");
    closeStore();
  }
    
  public void testFiltering() throws InvalidQueryException, IOException {
    load("family.ltm");

    findNothing("/* #OPTION: optimizer.reorder = false */ " +
                "$A = 1, topicmap($A)?");
  }
}
