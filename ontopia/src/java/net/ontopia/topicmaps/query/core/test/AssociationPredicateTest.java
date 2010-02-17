
// $Id: AssociationPredicateTest.java,v 1.5 2005/07/13 08:56:48 grove Exp $

package net.ontopia.topicmaps.query.core.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.ontopia.topicmaps.query.core.InvalidQueryException;

public class AssociationPredicateTest extends AbstractPredicateTest {
  
  public AssociationPredicateTest(String name) {
    super(name);
  }

  public void tearDown() {
    closeStore();
  }

  /// tests
  
  public void testCompletelyOpen() throws InvalidQueryException, IOException {
    load("family2.ltm");

    List matches = new ArrayList();
    Iterator it = topicmap.getAssociations().iterator();
    while (it.hasNext())
      addMatch(matches, "TOPIC", it.next());
    
    verifyQuery(matches, "association($TOPIC)?");
  }

  public void testWithSpecificAssociationFalse()
    throws InvalidQueryException, IOException {
    load("jill.xtm");

    findNothing(OPT_TYPECHECK_OFF +
                "association(jill-ontopia-topic)?");
  }

  public void testWithSpecificAssociationTrue()
    throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList();
    matches.add(new HashMap());
    
    verifyQuery(matches, "association(jill-ontopia-association)?");
  }

  public void testQMOverwriteProblem() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList();
    addMatch(matches, "OBJECT", topicmap);

    // because of the overwrite we used to have, this should lose the TM
    // and only give us the assocs in the result set (which means we get nothing)

    // the logic is this:
    //  1) turn off the optimizer so the = will run first,
    //  2) the two OR branches share the same QM object as input,
    //  3) the association() predicate will remove all non-assocs from it,
    //  4) therefore the topicmap() predicate doesn't see the TM (it's scanning
    //     the same result set), and
    //  5) we get an empty result
    verifyQuery(matches,
                "/* #OPTION: optimizer.reorder = false */ " +
                "$OBJECT = jillstm, " +
                "{ association($OBJECT) | topicmap($OBJECT) }?");
  }
  
  public void testFiltering() throws InvalidQueryException, IOException {
    load("family.ltm");

    findNothing("/* #OPTION: optimizer.reorder = false */ " +
                "$A = 1, association($A)?");
  }
}
