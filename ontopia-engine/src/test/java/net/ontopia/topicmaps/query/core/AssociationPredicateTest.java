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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;

public class AssociationPredicateTest extends AbstractPredicateTest {
  
  /// tests
  
  @Test
  public void testCompletelyOpen() throws InvalidQueryException, IOException {
    load("family2.ltm");

    List matches = new ArrayList();
    Iterator it = topicmap.getAssociations().iterator();
    while (it.hasNext()) {
      addMatch(matches, "TOPIC", it.next());
    }
    
    assertQueryMatches(matches, "association($TOPIC)?");
  }

  @Test
  public void testWithSpecificAssociationFalse()
    throws InvalidQueryException, IOException {
    load("jill.xtm");

    assertFindNothing(OPT_TYPECHECK_OFF +
                "association(jill-ontopia-topic)?");
  }

  @Test
  public void testWithSpecificAssociationTrue()
    throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList();
    matches.add(new HashMap());
    
    assertQueryMatches(matches, "association(jill-ontopia-association)?");
  }

  @Test
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
    assertQueryMatches(matches,
                "/* #OPTION: optimizer.reorder = false */ " +
                "$OBJECT = jillstm, " +
                "{ association($OBJECT) | topicmap($OBJECT) }?");
  }
  
  @Test
  public void testFiltering() throws InvalidQueryException, IOException {
    load("family.ltm");

    assertFindNothing("/* #OPTION: optimizer.reorder = false */ " +
                "$A = 1, association($A)?");
  }
}
