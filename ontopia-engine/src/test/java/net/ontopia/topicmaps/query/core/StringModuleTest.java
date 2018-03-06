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
import org.junit.Assert;
import org.junit.Test;

public class StringModuleTest extends AbstractPredicateTest {
  
  /// tests

  // --- concat -------------------------------------------------------
  
  @Test
  public void testConcatOpen1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "COMBO", "user-gdm");
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $COMBO from " +
                "instance-of($USER, user), " +
                "topic-name($USER, $TN), value($TN, $VALUE), " +
                "scope($TN, username), " +
                "str:concat($COMBO, \"user-\", $VALUE)?");
  }

  @Test
  public void testConcatClosed1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "USER", getTopicById("gdm"));
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $USER from " +
                "instance-of($USER, user), " +
                "topic-name($USER, $TN), value($TN, $VALUE), " +
                "scope($TN, username), " +
                "str:concat(\"user-gdm\", \"user-\", $VALUE)?");
  }
  
  @Test
  public void testConcatOpen2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "NAME", "Ontopia AS");
    
    assertQueryMatches(matches, 
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:concat($NAME, \"Ontopia\", \" AS\")?");
  }
  
  @Test
  public void testConcatOpen2NotMatch() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    
    assertQueryMatches(matches, 
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "$NAME = \"OntopiaAS\", " +
                "str:concat($NAME, \"Ontopia\", \" AS\")?");
  }
  
  @Test
  public void testConcatClosed2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:concat(\"Ontopia AS\", \"Ontopia\", \" AS\")?");
  }
  
  @Test
  public void testConcatClosed2NotMatch() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:concat(\"OntopiaAS\", \"Ontopia\", \" AS\")?");
  }
  
  @Test
  public void testConcatEmptyFirst() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "NAME", " AS");
    
    assertQueryMatches(matches, 
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:concat($NAME, \"\", \" AS\")?");
  }
  
  @Test
  public void testConcatEmptySecond() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "NAME", "Ontopia");
    
    assertQueryMatches(matches, 
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:concat($NAME, \"Ontopia\", \"\")?");
  }
  
  @Test
  public void testConcatEmptyBoth() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "NAME", "");
    
    assertQueryMatches(matches, 
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:concat($NAME, \"\", \"\")?");
  }
  
  @Test
  public void testConcatErrorInteger1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:concat(1, \"Ontopia\", \" AS\")?");
      Assert.fail("Should have Assert.failed on the first argument to the 'concat' " +
           "parameter being an integer and not a string.");
    } catch (InvalidQueryException e) {
    }
  }
  
  @Test
  public void testConcatErrorInteger2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:concat(\"Ontopia AS\", 2, \" AS\")?");
      Assert.fail("Should have Assert.failed on the second argument to the 'concat' " +
           "parameter being an integer and not a string.");
    } catch (InvalidQueryException e) {
    }
  }
  
  @Test
  public void testConcatErrorInteger3() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:concat(\"Ontopia AS\", \"Ontopia\", 3)?");
      Assert.fail("Should have Assert.failed on the third argument to the 'concat' " +
           "parameter being an integer and not a string.");
    } catch (InvalidQueryException e) {
    }
  }
  
  @Test
  public void testConcatErrorUnbound2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:concat(\"Ontopia AS\", $PART1, \" AS\")?");
      Assert.fail("Should have Assert.failed on the second parameter to 'concat' not being" +
           "bound.");
    } catch (InvalidQueryException e) {
    }
  }
  
  @Test
  public void testConcatErrorUnbound3() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:concat(\"Ontopia AS\", \"Ontopia\", $PART2)?");
      Assert.fail("Should have Assert.failed on the third parameter to 'concat' not being" +
           "bound.");
    } catch (InvalidQueryException e) {
    }
  }
  
  
  // --- contains --------------------------------------------------------
  
  @Test
  public void testContainsClosed() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("horse"));
    addMatch(matches, "TOPIC", getTopicById("white-horse"));
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $TOPIC from " +
                "instance-of($TOPIC, bbtopic), " +
                "topic-name($TOPIC, $TN), value($TN, $VALUE), " +
                "str:contains($VALUE, \"Horse\")?");
  }

  @Test
  public void testContainsStart() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:contains(\"Nevertheless\", \"Never\")?");
  }

  @Test
  public void testContainsMiddle() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:contains(\"Nevertheless\", \"the\")?");
  }

  @Test
  public void testContainsEnd() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:contains(\"Nevertheless\", \"less\")?");
  }

  @Test
  public void testContainsMore() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:contains(\"Never\", \"Nevertheless\")?");
  }
  
  @Test
  public void testContainsNot() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:contains(\"Nevertheless\", \"foobar\")?");
  }
  
  @Test
  public void testContainsErrorInteger1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:contains(3, \" AS\")?");
      Assert.fail("Should have Assert.failed on the second parameter to 'contains' being " +
           "an integer instead of a string");
    } catch (InvalidQueryException e) {
    }
  }
  
  @Test
  public void testContainsErrorInteger2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:contains(\"Ontopia\", 7)?");
      Assert.fail("Should have Assert.failed on the second parameter to 'contains' being " +
           "an integer instead of a string.");
    } catch (InvalidQueryException e) {
    }
  }
  
  @Test
  public void testContainsErrorUnbound1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:contains($CONTAINER, \" AS\")?");
      Assert.fail("Should have Assert.failed on the second parameter to 'contains' not " +
           "being bound.");
    } catch (InvalidQueryException e) {
    }
  }
  
  @Test
  public void testContainsErrorUnbound2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:contains(\"Ontopia AS\", $CONTAINED)?");
      Assert.fail("Should have Assert.failed on the second parameter to 'contains' not " +
           "being bound.");
    } catch (InvalidQueryException e) {
    }
  }
  
  // --- ends-with -------------------------------------------------------
  
  @Test
  public void testEndsWithStart() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:ends-with(\"Nevertheless\", \"Never\")?");
  }

  @Test
  public void testEndsWithMiddle() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:ends-with(\"Nevertheless\", \"the\")?");
  }

  @Test
  public void testEndsWithEnd() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:ends-with(\"Nevertheless\", \"less\")?");
  }

  @Test
  public void testEndsWithAll() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:ends-with(\"Nevertheless\", \"Nevertheless\")?");
  }

  @Test
  public void testEndsWithMore() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:ends-with(\"Never\", \"Nevertheless\")?");
  }
  
  @Test
  public void testEndsWith() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "NAME", "Ontopia AS");
    
    assertQueryMatches(matches, 
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "$NAME = \"Ontopia AS\", " +
                "str:ends-with($NAME, \"AS\")?");
  }

  @Test
  public void testEndsWithErrorInteger1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:ends-with(3, \" AS\")?");
      Assert.fail("Should have Assert.failed on the second parameter to 'ends-with' being " +
           "an integer instead of a string");
    } catch (InvalidQueryException e) {
    }
  }
  
  @Test
  public void testEndsWithErrorInteger2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:ends-with(\"Ontopia\", 7)?");
      Assert.fail("Should have Assert.failed on the second parameter to 'ends-with' being " +
           "an integer instead of a string.");
    } catch (InvalidQueryException e) {
    }
  }
  
  @Test
  public void testEndsWithErrorUnbound1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:ends-with($CONTAINER, \" AS\")?");
      Assert.fail("Should have Assert.failed on the second parameter to 'ends-with' not " +
           "being bound.");
    } catch (InvalidQueryException e) {
    }
  }
  
  @Test
  public void testEndsWithErrorUnbound2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:ends-with(\"Ontopia AS\", $CONTAINED)?");
      Assert.fail("Should have Assert.failed on the second parameter to 'ends-with' not " +
           "being bound.");
    } catch (InvalidQueryException e) {
    }
  }
  
  // --- index-of -------------------------------------------------------
  
  @Test
  public void testIndexOfSpecExample() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "POS", 25);

    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of($POS, \"The first occurrence of 'the' " +
                "in the sentence.\", \"the\")?"); 
  }
  
  @Test
  public void testIndexOfStart() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of(0, \"Nevertheless\", \"Never\")?");
  }

  @Test
  public void testIndexOfMid() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of(5, \"Nevertheless\", \"the\")?");
  }

  @Test
  public void testIndexOfEnd() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of(8, \"Nevertheless\", \"less\")?");
  }

  @Test
  public void testIndexOfStart1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of(1, \"Nevertheless\", \"Never\")?");
  }

  @Test
  public void testIndexOfMid1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of(10, \"Nevertheless\", \"the\")?");
  }

  @Test
  public void testIndexOfEnd1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of(3, \"Nevertheless\", \"less\")?");
  }

  @Test
  public void testIndexOfStart2() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", 0);

    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of($N, \"Nevertheless\", \"Never\")?");
  }

  @Test
  public void testIndexOfMid2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", 5);

    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of($N, \"Nevertheless\", \"the\")?");
  }

  @Test
  public void testIndexOfEnd2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", 8);

    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of($N, \"Nevertheless\", \"less\")?");
  }

  @Test
  public void testIndexOfSecond() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", 2);

    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of($N, \"Door or window?\", \"or\")?");
  }

  @Test
  public void testIndexOfThird() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", 2);

    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of($N, \"Thiss iss vassviss?\", \"iss\")?");
  }

  @Test
  public void testIndexOfSequence() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", 1);

    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of($N, \"Booooo?\", \"oo\")?");
  }

  @Test
  public void testIndexOfSequence1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", 1);

    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of($N, \"Booooom?\", \"oo\")?");
  }

  @Test
  public void testIndexOfMore() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of($N, \"Never\", \"Nevertheless\")?");
  }

  @Test
  public void testIndexErrorUnbound1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "CONTAINS", "Booom");

    try {
      assertQueryMatches(matches,
                  "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:index-of(1, $CONTAINS, \"oo\")?");
      Assert.fail("'index-of' should have Assert.failed with the first parameter undbound.");
    } catch (InvalidQueryException e) {
    }
  }

  @Test
  public void testIndexErrorUnbound2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "CONTAINS", "Booom");

    try {
      assertQueryMatches(matches,
                  "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:index-of(1, $CONTAINS, \"oo\")?");
      Assert.fail("'index-of' should have Assert.failed with the second parameter undbound.");
    } catch (InvalidQueryException e) {
    }
  }

  @Test
  public void testIndexErrorString1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:index-of(\"1\", \"Booooom\", \"oo\")?");
      Assert.fail("'index-of' should have Assert.failed with the first parameter being a " +
            "string instead of an integer.");
    } catch (InvalidQueryException e) {
    }
  }

  @Test
  public void testIndexErrorString2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:index-of(1, 0, \"oo\")?");
      Assert.fail("'index-of' should have Assert.failed with the second parameter being an " +
            "integer instead of a string.");
    } catch (InvalidQueryException e) {
    }
  }

  @Test
  public void testIndexErrorString3() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:index-of(1, \"Booooom\", 3)?");
      Assert.fail("'index-of' should have Assert.failed with the third parameter being an " +
            "integer instead of a string.");
    } catch (InvalidQueryException e) {
    }
  }

  // --- last-index-of -------------------------------------------------------
  
  @Test
  public void testLastIndexOfStart() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of(0, \"Nevertheless\", \"Never\")?");
  }

  @Test
  public void testLastIndexOfMid() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of(5, \"Nevertheless\", \"the\")?");
  }

  @Test
  public void testLastIndexOfEnd() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of(8, \"Nevertheless\", \"less\")?");
  }

  @Test
  public void testLastIndexOfStart1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of(1, \"Nevertheless\", \"Never\")?");
  }

  @Test
  public void testLastIndexOfMid1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of(10, \"Nevertheless\", \"the\")?");
  }

  @Test
  public void testLastIndexOfEnd1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of(3, \"Nevertheless\", \"less\")?");
  }

  @Test
  public void testLastIndexOfStart2() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", 0);

    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of($N, \"Nevertheless\", \"Never\")?");
  }

  @Test
  public void testLastIndexOfMid2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", 5);

    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of($N, \"Nevertheless\", \"the\")?");
  }

  @Test
  public void testLastIndexOfEnd2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", 8);

    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of($N, \"Nevertheless\", \"less\")?");
  }

  @Test
  public void testLastIndexOfSecond() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", 5);

    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of($N, \"Door or window?\", \"or\")?");
  }

  @Test
  public void testLastIndexOfThird() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", 15);

    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of($N, \"Thiss iss vassviss?\", \"iss\")?");
  }

  @Test
  public void testLastIndexOfSequence() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", 4);

    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of($N, \"Booooo?\", \"oo\")?");
  }

  @Test
  public void testLastIndexOfSequence1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", 4);

    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of($N, \"Booooom?\", \"oo\")?");
  }

  @Test
  public void testLastIndexOfMore() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of($N, \"Never\", \"Nevertheless\")?");
  }
    
  
  @Test
  public void testLastIndexErrorUnbound1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "CONTAINS", "Booom");
  
    try {
      assertQueryMatches(matches,
                  "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:index-of(1, $CONTAINS, \"oo\")?");
      Assert.fail("'last-index-of' should have Assert.failed with the first parameter " +
            "undbound.");
    } catch (InvalidQueryException e) {
    }
  }
  
  @Test
  public void testLastIndexErrorUnbound2() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
  
    List matches = new ArrayList();
    addMatch(matches, "CONTAINS", "Booom");
  
    try {
      assertQueryMatches(matches,
                  "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:last-index-of(1, $CONTAINS, \"oo\")?");
      Assert.fail("'last-index-of' should have Assert.failed with the second parameter " +
            "undbound.");
    } catch (InvalidQueryException e) {
    }
  }
  
  @Test
  public void testLastIndexErrorString1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
  
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:last-index-of(\"1\", \"Booooom\", \"oo\")?");
      Assert.fail("'last-index-of' should have Assert.failed with the first parameter " +
            "being a string instead of an integer.");
    } catch (InvalidQueryException e) {
    }
  }
  
  @Test
  public void testLastIndexErrorString2() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
  
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:last-index-of(1, 0, \"oo\")?");
      Assert.fail("'last-index-of' should have Assert.failed with the second parameter " +
            "being an integer instead of a string.");
    } catch (InvalidQueryException e) {
    }
  }
  
  @Test
  public void testLastIndexErrorString3() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
  
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:last-index-of(1, \"Booooom\", 3)?");
      Assert.fail("'last-index-of' should have Assert.failed with the third parameter " +
            "being an integer instead of a string.");
    } catch (InvalidQueryException e) {
    }
  }
  
  // --- length -------------------------------------------------------
  
  @Test
  public void testLengthOpen() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "USER", getTopicById("gdm"),    "LENGTH", 12);
    addMatch(matches, "USER", getTopicById("larsga"), "LENGTH", 19);
    addMatch(matches, "USER", getTopicById("grove"),  "LENGTH", 15);
    addMatch(matches, "USER", getTopicById("steve"),  "LENGTH", 12);
    addMatch(matches, "USER", getTopicById("sylvia"), "LENGTH", 13);
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $USER, $LENGTH from " +
                "instance-of($USER, user), " +
                "topic-name($USER, $TN), value($TN, $VALUE), " +
                "not(scope($TN, $ANYTHING)), " +
                "str:length($VALUE, $LENGTH)?");
  }

  @Test
  public void testLengthClosed() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "USER", getTopicById("gdm"));
    addMatch(matches, "USER", getTopicById("steve"));
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $USER from " +
                "instance-of($USER, user), " +
                "topic-name($USER, $TN), value($TN, $VALUE), " +
                "not(scope($TN, $ANYTHING)), " +
                "str:length($VALUE, 12)?");
  }

  @Test
  public void testLengthZero() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:length(\"\", 0)?");
  }

  @Test
  public void testLengthOne() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:length(\".\", 1)?");
  }

  @Test
  public void testLengthOneWrong0() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    assertQueryMatches(matches, "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:length(\".\", 0)?");
  }

  @Test
  public void testLengthOneWrong4() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:length(\".\", 4)?");
  }

  @Test
  public void testLengthMany() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:length(\"Ontopia\", 7)?");
  }

  @Test
  public void testLengthZeroBound() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "L", 0);

    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:length(\"\", $L)?");
  }

  @Test
  public void testLengthOneBound() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "L", 1);

    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:length(\".\", $L)?");
  }

  @Test
  public void testLengthManyBound() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "L", 7);

    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:length(\"Ontopia\", $L)?");
  }

  @Test
  public void testLengthErrorInteger1() throws InvalidQueryException,
      IOException {
  load("bb-test.ltm");
  
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:length(1, 1)?");
      Assert.fail("'length' should have Assert.failed with the first parameter " +
            "being an integer instead of a string.");
    } catch (InvalidQueryException e) {
    }
  }

  @Test
  public void testLengthErrorString2() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:length(\"1\", \"1\")?");
      Assert.fail("'length' should have Assert.failed with the second parameter " +
            "being a string instead of an integer.");
    } catch (InvalidQueryException e) {
    }
  }

  @Test
  public void testBug2120() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    try {
      List matches = new ArrayList();
      addMatch(matches, "VAL", "topic1", "LEN", 6);
      addMatch(matches, "VAL", "topic2", "LEN", 6);
      addMatch(matches, "VAL", "topic3", "LEN", 6);
      addMatch(matches, "VAL", "topic4", "LEN", 6);
      assertQueryMatches(matches,
                  "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "select $VAL, $LEN from " +
                  "  value($OCC, $VAL), " +
                  "  str:length($VAL, $LEN) " +
                  "order by $LEN desc, $VAL?");
    } catch (InvalidQueryException e) {
    }
  }

  // --- starts-with --------------------------------------------------
  
  @Test
  public void testStartsWithStart() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:starts-with(\"Nevertheless\", \"Never\")?");
  }

  @Test
  public void testStartsWithMiddle() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:starts-with(\"Nevertheless\", \"the\")?");
  }

  @Test
  public void testStartsWithEnd() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:starts-with(\"Nevertheless\", \"less\")?");
  }

  @Test
  public void testStartsWithAll() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:starts-with(\"Nevertheless\", \"Nevertheless\")?");
  }

  @Test
  public void testStartsWithMore() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:starts-with(\"Never\", \"Nevertheless\")?");
  }

  @Test
  public void testStartsWithFilter() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "CLASS", getTopicById("k7a"));
    addMatch(matches, "CLASS", getTopicById("k7b"));
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $CLASS from " +
                "instance-of($CLASS, klasse), " +
                "topic-name($CLASS, $TN), value($TN, $VALUE), " +
                "str:starts-with($VALUE, \"7\")?");
  }

  @Test
  public void testStartsWithOpt1() throws InvalidQueryException, IOException {
    load("int-occs-2.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("topic2"));
    addMatch(matches, "T", getTopicById("topic5"));

    // uses the optimization
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $T from " +
                "  description($T, $DESC), " +
                "  str:starts-with($DESC, \"topic2\")?");
  }

  @Test
  public void testStartsWithOpt2() throws InvalidQueryException, IOException {
    load("int-occs-2.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("topic2"), "DESC", "topic23");
    addMatch(matches, "T", getTopicById("topic5"), "DESC", "topic22");
    
    // uses the optimization
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "description($T, $DESC), " +
                "str:starts-with($DESC, \"topic2\")?");
  }

  @Test
  public void testStartsWithOpt2FindNothing() throws InvalidQueryException, 
      IOException {
    load("int-occs-2.ltm");
    
    // uses the optimization
    assertFindNothing("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "description($T, $DESC), " +
                "str:starts-with($DESC, \"tupic2\")?");
  }
  
  @Test
  public void testStartsWithOpt3() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("topic1"));
    
    // uses the optimization
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $T from " +
                "description($T, $DESC), " +
                "str:starts-with($DESC, \"topic1\")?");
  }  

  @Test
  public void testStartsWithOpt4() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("topic1"));
    
    // does not use the optimization
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $T from " +
                "description($T, $DESC), " +
                "str:length($DESC, 6), " +
                "str:starts-with($DESC, \"topic1\")?");
  }

  @Test
  public void testStartsWithOpt5() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("topic2"));
    addMatch(matches, "T", getTopicById("topic3"));
    addMatch(matches, "T", getTopicById("topic4"));
    
    // uses the optimization
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $T from " +
                "description($T, $DESC), " +
                "not(str:starts-with($DESC, \"topic1\"))?");
  }

  @Test
  public void testStartsWithOpt6() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("topic1"));
    
    // should not use the optimization
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $T from " +
                "  $T = topic1, " +
                "  description($T, $DESC), " +
                "  str:starts-with($DESC, \"topic\")?");
  }  

  @Test
  public void testStartsWithOpt7() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("topic1"));
    
    // should not use the optimization
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $T from " +
                "  description(topic1, $DESC), " +
                "  description($T, $DESC), " +
                "  str:starts-with($DESC, \"topic\")?");
  }  

  @Test
  public void testStartsWithOpt7b() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("topic1"));
    
    // the point here is that the reordering optimizer will move the
    // second description before the first, causing Assert.failure if the
    // optimizations occur in the wrong order
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $T from " +
                "  description($T, $DESC), " +
                "  description(topic1, $DESC), " +
                "  str:starts-with($DESC, \"topic\")?");
  }  

  @Test
  public void testStartsWithOptURI() throws InvalidQueryException, IOException {
    load("opera.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("puccini"));
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $T from " +
                "sound-clip($T, $URI), " +
                "str:starts-with($URI, \"http://www.puccini.it\")?");
  }

  @Test
  public void testStartsWithOptURI2() throws InvalidQueryException, IOException {
    load("opera.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("puccini"),
                      "URI", "http://www.puccini.it/files/vocepucc.wav");
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $T, $URI from " +
                "sound-clip($T, $URI), " +
                "str:starts-with($URI, \"http://www.pucc\")?");
  }

  @Test
  public void testStartsWithBoth() throws InvalidQueryException, IOException {
    load("int-occs-2.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("topic3"),
                      "V", "topic3");
    addMatch(matches, "T", getTopicById("topic6"),
                      "V", "topic3://woohoo");
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $T, $V from " +
                "description($T, $V), " +
                "str:starts-with($V, \"topic3\")?");
  }

  @Test
  public void testStartsWithBoth2() throws InvalidQueryException, IOException {
    load("int-occs-2.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("topic3"));
    addMatch(matches, "T", getTopicById("topic6"));
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $T from " +
                "description($T, $V), " +
                "str:starts-with($V, \"topic3\")?");
  }
  
  @Test
  public void testStartsWithErrorInteger1() throws InvalidQueryException, 
      IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:starts-with(3, \" AS\")?");
      Assert.fail("Should have Assert.failed on the second parameter to 'starts-with' " +
           "being an integer instead of a string");
    } catch (InvalidQueryException e) {
    }
  }
  
  @Test
  public void testStartsWithErrorInteger2() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:starts-with(\"Ontopia\", 7)?");
      Assert.fail("Should have Assert.failed on the second parameter to 'starts-with' " +
           "being an integer instead of a string.");
    } catch (InvalidQueryException e) {
    }
  }
  
  @Test
  public void testStartsWithErrorUnbound1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:starts-with($CONTAINER, \" AS\")?");
      Assert.fail("Should have Assert.failed on the second parameter to 'starts-with' not " +
           "being bound.");
    } catch (InvalidQueryException e) {
    }
  }
  
  @Test
  public void testStartsWithErrorUnbound2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:starts-with(\"Ontopia AS\", $CONTAINED)?");
      Assert.fail("Should have Assert.failed on the second parameter to 'starts-with' not " +
           "being bound.");
    } catch (InvalidQueryException e) {
    }
  }
  
  // --- substring ----------------------------------------------------

  @Test
  public void testSubstringStartMid() throws InvalidQueryException, 
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "2007-");
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring($OUT, \"2007-03-12\", 0, 5)?");

  }

  @Test
  public void testSubstringBoundStartMid() throws InvalidQueryException, 
      IOException {
    load("bb-test.ltm");
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
               "str:substring(\"2007-\", \"2007-03-12\", 0, 5)?");
  }

  @Test
  public void testSubstringStartEnd() throws InvalidQueryException, 
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "2007-03-12");
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring($OUT, \"2007-03-12\", 0, 10)?");
  }

  @Test
  public void testSubstringBoundStartEnd() throws InvalidQueryException, 
      IOException {
    load("bb-test.ltm");
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
               "str:substring(\"2007-03-12\", \"2007-03-12\", 0, 10)?");
  }

  @Test
  public void testSubstringStartOmitEnd() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "2007-03-12");
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring($OUT, \"2007-03-12\", 0)?");
  }

  @Test
  public void testSubstringBoundStartOmitEnd() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");

    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring(\"2007-03-12\", \"2007-03-12\", 0)?");
}

  @Test
  public void testSubstringMidMid() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "7-0");
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring($OUT, \"2007-03-12\", 3, 6)?");
  }

  @Test
  public void testSubstringBoundMidMid() throws InvalidQueryException, 
      IOException {
    load("bb-test.ltm");
    
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring(\"7-0\", \"2007-03-12\", 3, 6)?");
  }

  @Test
  public void testSubstringMidEnd() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "-03-12");
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring($OUT, \"2007-03-12\", 4, 10)?");
  }

  @Test
  public void testSubstringBoundMidEnd() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring(\"-03-12\", \"2007-03-12\", 4, 10)?");
  }

  @Test
  public void testSubstringMidOmitEnd() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "-03-12");
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring($OUT, \"2007-03-12\", 4)?");
  }

  @Test
  public void testSubstringBoundMidOmitEnd() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");

    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring(\"-03-12\", \"2007-03-12\", 4)?");
  }

  @Test
  public void testSubstringErrorNegativeRange() throws InvalidQueryException, 
      IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "OUT", "-03-12");

    try {
      assertQueryMatches(matches,
        "import \"http://psi.ontopia.net/tolog/string/\" as str " +
        "str:substring($OUT, \"2007-03-12\", 4, 3)?");
      Assert.fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("The 3rd and 4th parameters to")) {
        Assert.fail("Wrong error message: \"" + e.getMessage() + "\"");
      }
    }
  }

  @Test
  public void testSubstringErrorBoundNegativeRange() 
      throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring(\"-03-12\", \"2007-03-12\", 4, 3)?");
      Assert.fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("The 3rd and 4th parameters to")) {
        Assert.fail("Wrong error message: \"" + e.getMessage() + "\"");
      }
    }
  }

  @Test
  public void testSubstringErrorUnbound2() 
      throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();

    try {
      assertQueryMatches(matches,
                  "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring(\"-03-12\", $ARG2, 4)?");
      Assert.fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("Variable $ARG2 not bound in predicate")) {
        Assert.fail("Wrong error message: \"" + e.getMessage() + "\"");
      }
    }
  }

  @Test
  public void testSubstringErrorUnbound3() 
      throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring(\"-03-12\", \"2007-03-12\", $ARG3)?");
      Assert.fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("Variable $ARG3 not bound in predicate")) {
        Assert.fail("Wrong error message: \"" + e.getMessage() + "\"");
      }
    }
  }
  
    @Test
  public void testSubstringErrorUnbound4() 
        throws InvalidQueryException, IOException {
      load("bb-test.ltm");
      
      try {
        assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                    "str:substring(\"-03-12\", \"2007-03-12\", 4, $ARG4)?");
        Assert.fail("Expected InvalidQueryException, but got no Exception at all.");
      } catch (InvalidQueryException e) {
        if (!e.getMessage().startsWith("Variable $ARG4 not bound in predicate")) {
          Assert.fail("Wrong error message: \"" + e.getMessage() + "\"");
        }
      }
  }
  
  @Test
  public void testSubstringErrorInteger1() 
      throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring(10, \"2007-03-12\", 4, 10)?");
      Assert.fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("Predicate substring received class " +
                                     "java.lang.Integer as argument 1, " +
                                     "but requires a string")) {
        Assert.fail("Wrong error message: \"" + e.getMessage() + "\"");
      }
    }
  }
  
  @Test
  public void testSubstringErrorInteger2() 
      throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring(\"-03-12\", 20, 4, 10)?");
      Assert.fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("Predicate substring received class " +
                                     "java.lang.Integer as argument 2, but " +
                                     "requires a string")) {
        Assert.fail("Wrong error message: \"" + e.getMessage() + "\"");
      }
    }
  }

  @Test
  public void testSubstringErrorString3() 
      throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring(\"-03-12\", \"2007-03-12\", \"4\", 10)?");
      Assert.fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("Predicate substring received a string " +
                                     "as argument 3, but requires class " +
                                     "java.lang.Integer")) {
        Assert.fail("Wrong error message: \"" + e.getMessage() + "\"");
      }
    }
  }

  @Test
  public void testSubstringErrorString4() 
      throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring(\"-03-12\", \"2007-03-12\", 4, \"10\")?");
      Assert.fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("Predicate substring received a string " +
                                     "as argument 4, but requires class " +
                                     "java.lang.Integer")) {
        Assert.fail("Wrong error message: \"" + e.getMessage() + "\"");
      }
    }
  }

  // --- substring-after ----------------------------------------------

  @Test
  public void testSubstringAfterFirstChar() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "007-03-12");
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-after($OUT, \"2007-03-12\", \"2\")?");
  }

  @Test
  public void testSubstringAfterMidChar() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "03-12");
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-after($OUT, \"2007-03-12\", \"-\")?");
  }

  @Test
  public void testSubstringAfterMidTwoChars() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "03-12");
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-after($OUT, \"2007-03-12\", \"7-\")?");
  }

  @Test
  public void testSubstringAfterFirstTwoChars() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "07-03-12");
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-after($OUT, \"2007-03-12\", \"20\")?");
  }

  @Test
  public void testSubstringAfterFirstChar1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-after(\"007-03-12\", \"2007-03-12\", \"2\")?");
  }

  @Test
  public void testSubstringAfterMidChar1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-after(\"03-12\", \"2007-03-12\", \"-\")?");
  }

  @Test
  public void testSubstringAfterMidTwoChars1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-after(\"03-12\", \"2007-03-12\", \"7-\")?");
  }

  @Test
  public void testSubstringAfterFirstTwoChars1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-after(\"07-03-12\", \"2007-03-12\", \"20\")?");
  }

  @Test
  public void testSubstringAfterErrorInteger1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring-after(1, \"20\", \"07-03-12\")?");
      Assert.fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("Predicate substring-after received " +
                                     "class java.lang.Integer as argument " +
                                     "1, but requires a string")) {
        Assert.fail("Wrong error message: \"" + e.getMessage() + "\"");
      }
    }
  }

  @Test
  public void testSubstringAfterErrorInteger2() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring-after(\"2007-03-12\", 2, \"07-03-12\")?");
      Assert.fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("Predicate substring-after received " +
                                     "class java.lang.Integer as argument " +
                                     "2, but requires a string")) {
        Assert.fail("Wrong error message: \"" + e.getMessage() + "\"");
      }
    }
  }

  @Test
  public void testSubstringAfterErrorInteger3() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring-after(\"2007-03-12\", \"20\", 3)?");
      Assert.fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("Predicate substring-after received " +
                                     "class java.lang.Integer as argument " +
                                     "3, but requires a string")) {
        Assert.fail("Wrong error message: \"" + e.getMessage() + "\"");
      }
    }
  }

  @Test
  public void testSubstringAfterErrorUnbound2() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();

    try {
      assertQueryMatches(matches,
                  "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring-after(\"07-03-12\", $ARG2, \"2007-03-12\")?");
      Assert.fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("")) {
        Assert.fail("Wrong error message: \"" + e.getMessage() + "\"");
      }
    }
  }
    
      @Test
  public void testSubstringAfterErrorUnbound3() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();

    try {
      assertQueryMatches(matches,
                  "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring-after(\"2007-03-12\", \"20\", $ARG3)?");
      Assert.fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("")) {
        Assert.fail("Wrong error message: \"" + e.getMessage() + "\"");
      }
    }
  }

  // --- substring-before ----------------------------------------------

  @Test
  public void testSubstringBeforeFirstChar() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "");
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-before($OUT, \"2007-03-12\", \"2\")?");
  }

  @Test
  public void testSubstringBeforeMidChar() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "2007");
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-before($OUT, \"2007-03-12\", \"-\")?");
  }

  @Test
  public void testSubstringBeforeMidTwoChars() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "200");
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-before($OUT, \"2007-03-12\", \"7-\")?");
  }

  @Test
  public void testSubstringBeforeFirstTwoChars() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "");
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-before($OUT, \"2007-03-12\", \"20\")?");
  }

  @Test
  public void testSubstringBeforeFirstChar1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-before(\"\", \"2007-03-12\", \"2\")?");
  }

  @Test
  public void testSubstringBeforeMidChar1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-before(\"2007\", \"2007-03-12\", \"-\")?");
  }

  @Test
  public void testSubstringBeforeMidTwoChars1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-before(\"200\", \"2007-03-12\", \"7-\")?");
  }

  @Test
  public void testSubstringBeforeFirstTwoChars1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-before(\"\", \"2007-03-12\", \"20\")?");
  }

  @Test
  public void testSubstringBeforeErrorInteger1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring-before(1, \"20\", \"07-03-12\")?");
      Assert.fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("Predicate substring-before received " +
                                     "class java.lang.Integer as argument " +
                                     "1, but requires a string")) {
        Assert.fail("Wrong error message: \"" + e.getMessage() + "\"");
      }
    }
  }
  
  @Test
  public void testSubstringBeforeErrorInteger2() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring-before(\"2007-03-12\", 2, \"07-03-12\")?");
      Assert.fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("Predicate substring-before received " +
                                     "class java.lang.Integer as argument " +
                                     "2, but requires a string")) {
        Assert.fail("Wrong error message: \"" + e.getMessage() + "\"");
      }
    }
  }
  
  @Test
  public void testSubstringBeforeErrorInteger3() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    try {
      assertQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring-before(\"2007-03-12\", \"20\", 3)?");
      Assert.fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("Predicate substring-before " +
                                     "received class java.lang.Integer as " +
                                     "argument 3, but requires a string")) {
        Assert.fail("Wrong error message: \"" + e.getMessage() + "\"");
      }
    }
  }
  
  @Test
  public void testSubstringBeforeErrorUnbound2() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    
    try {
      assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-before(\"07-03-12\", $ARG2, \"2007-03-12\")?");
      Assert.fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("")) {
        Assert.fail("Wrong error message: \"" + e.getMessage() + "\"");
      }
    }
  }
  
  @Test
  public void testSubstringBeforeErrorUnbound3() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    
    try {
      assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-before(\"2007-03-12\", \"20\", $ARG3)?");
      Assert.fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("")) {
        Assert.fail("Wrong error message: \"" + e.getMessage() + "\"");
      }
    }
  }

  // --- translate -------------------------------------------------------
  
  @Test
  public void testTranslatePreserve() throws InvalidQueryException,
      IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "addis abeba (12)");
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $OUT from " +
                "  str:translate($OUT, " +
                "                \"Addis Abeba (12)\", " +
                "                \"ABCDEFGHIJKLMNOPQRSTUVXYZ\", " +
                "                \"abcdefghijklmnopqrstuvxyz\", \"\")?");
  }
  
  @Test
  public void testTranslatePreserveDigits() throws InvalidQueryException,
      IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "addisabeba12");
    
    assertQueryMatches(matches,
               "import \"http://psi.ontopia.net/tolog/string/\" as str " +
               "select $OUT from " +
               "  str:translate($OUT, " +
               "                \"Addis Abeba (12)\", " +
               "                \"ABCDEFGHIJKLMNOPQRSTUVXYZ\", " +
               "                \"abcdefghijklmnopqrstuvxyz\", \" ()\")?");
  }

  @Test
  public void testTranslateDelete() throws InvalidQueryException,
      IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "aa");
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $OUT from " +
                "  str:translate($OUT, " +
                "                \"Addis Abeba (12)\", " +
                "                \"ABCDEFGHIJKLMNOPQRSTUVXYZ\", " +
                "                \"abcdefghijklmnopqrstuvxyz\")?");
  }

  @Test
  public void testTranslateDelete2() throws InvalidQueryException,
      IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "addisabeba");
    
    assertQueryMatches(matches,
           "import \"http://psi.ontopia.net/tolog/string/\" as str " +
           "select $OUT from " +
           "  str:translate($OUT, " +
           "    \"Addis Abeba (12)\", " +
           "    \"ABCDEFGHIJKLMNOPQRSTUVXYZabcdefghijklmnopqrstuvxyz\", " +
           "    \"abcdefghijklmnopqrstuvxyzabcdefghijklmnopqrstuvxyz\")?");
  }

  @Test
  public void testTranslateDelete3() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "addis abeba ");
    
    assertQueryMatches(matches,
          "import \"http://psi.ontopia.net/tolog/string/\" as str " +
          "select $OUT from " +
          "  str:translate($OUT, " +
          "    \"Addis Abeba (12)\", " +
          "    \"ABCDEFGHIJKLMNOPQRSTUVXYZabcdefghijklmnopqrstuvxyz \", " +
          "    \"abcdefghijklmnopqrstuvxyzabcdefghijklmnopqrstuvxyz \")?");
  }
  
  @Test
  public void testTranslatePreserveExtraChars() throws InvalidQueryException,
      IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "addis abeba (12)");
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $OUT from " +
                "  str:translate($OUT, " +
                "                \"Addis Abeba (12)\", " +
                "                \"ABCDEFGHIJKLMNOPQRSTUVXYZ \", " +
                "                \"abcdefghijklmnopqrstuvxyz\", \"\")?");
  }
  
  @Test
  public void testTranslateDeleteExtraChars() throws InvalidQueryException,
      IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "a a ");
    
    assertQueryMatches(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $OUT from " +
                "  str:translate($OUT, " +
                "                \"Addis Abeba (12)\", " +
                "                \"ABCDEFGHIJKLMNOPQRSTUVXYZ \", " +
                "                \"abcdefghijklmnopqrstuvxyz\")?");
  }
}
