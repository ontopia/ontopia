
// $Id: StringModuleTest.java,v 1.21 2009/04/27 11:00:50 lars.garshol Exp $

package net.ontopia.topicmaps.query.core.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ontopia.topicmaps.query.core.InvalidQueryException;

public class StringModuleTest extends AbstractPredicateTest {
  
  public StringModuleTest(String name) {
    super(name);
  }

  public void tearDown() {
    closeStore();
  }

  /// tests

  // --- concat -------------------------------------------------------
  
  public void testConcatOpen1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "COMBO", "user-gdm");
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $COMBO from " +
                "instance-of($USER, user), " +
                "topic-name($USER, $TN), value($TN, $VALUE), " +
                "scope($TN, username), " +
                "str:concat($COMBO, \"user-\", $VALUE)?");
  }

  public void testConcatClosed1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "USER", getTopicById("gdm"));
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $USER from " +
                "instance-of($USER, user), " +
                "topic-name($USER, $TN), value($TN, $VALUE), " +
                "scope($TN, username), " +
                "str:concat(\"user-gdm\", \"user-\", $VALUE)?");
  }
  
  public void testConcatOpen2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "NAME", "Ontopia AS");
    
    verifyQuery(matches, 
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:concat($NAME, \"Ontopia\", \" AS\")?");
  }
  
  public void testConcatOpen2NotMatch() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    
    verifyQuery(matches, 
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "$NAME = \"OntopiaAS\", " +
                "str:concat($NAME, \"Ontopia\", \" AS\")?");
  }
  
  public void testConcatClosed2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:concat(\"Ontopia AS\", \"Ontopia\", \" AS\")?");
  }
  
  public void testConcatClosed2NotMatch() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:concat(\"OntopiaAS\", \"Ontopia\", \" AS\")?");
  }
  
  public void testConcatEmptyFirst() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "NAME", " AS");
    
    verifyQuery(matches, 
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:concat($NAME, \"\", \" AS\")?");
  }
  
  public void testConcatEmptySecond() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "NAME", "Ontopia");
    
    verifyQuery(matches, 
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:concat($NAME, \"Ontopia\", \"\")?");
  }
  
  public void testConcatEmptyBoth() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "NAME", "");
    
    verifyQuery(matches, 
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:concat($NAME, \"\", \"\")?");
  }
  
  public void testConcatErrorInteger1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:concat(1, \"Ontopia\", \" AS\")?");
      fail("Should have failed on the first argument to the 'concat' " +
           "parameter being an integer and not a string.");
    } catch (InvalidQueryException e) {
    }
  }
  
  public void testConcatErrorInteger2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:concat(\"Ontopia AS\", 2, \" AS\")?");
      fail("Should have failed on the second argument to the 'concat' " +
           "parameter being an integer and not a string.");
    } catch (InvalidQueryException e) {
    }
  }
  
  public void testConcatErrorInteger3() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:concat(\"Ontopia AS\", \"Ontopia\", 3)?");
      fail("Should have failed on the third argument to the 'concat' " +
           "parameter being an integer and not a string.");
    } catch (InvalidQueryException e) {
    }
  }
  
  public void testConcatErrorUnbound2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:concat(\"Ontopia AS\", $PART1, \" AS\")?");
      fail("Should have failed on the second parameter to 'concat' not being" +
           "bound.");
    } catch (InvalidQueryException e) {
    }
  }
  
  public void testConcatErrorUnbound3() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:concat(\"Ontopia AS\", \"Ontopia\", $PART2)?");
      fail("Should have failed on the third parameter to 'concat' not being" +
           "bound.");
    } catch (InvalidQueryException e) {
    }
  }
  
  
  // --- contains --------------------------------------------------------
  
  public void testContainsClosed() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("horse"));
    addMatch(matches, "TOPIC", getTopicById("white-horse"));
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $TOPIC from " +
                "instance-of($TOPIC, bbtopic), " +
                "topic-name($TOPIC, $TN), value($TN, $VALUE), " +
                "str:contains($VALUE, \"Horse\")?");
  }

  public void testContainsStart() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:contains(\"Nevertheless\", \"Never\")?");
  }

  public void testContainsMiddle() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:contains(\"Nevertheless\", \"the\")?");
  }

  public void testContainsEnd() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:contains(\"Nevertheless\", \"less\")?");
  }

  public void testContainsMore() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:contains(\"Never\", \"Nevertheless\")?");
  }
  
  public void testContainsNot() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:contains(\"Nevertheless\", \"foobar\")?");
  }
  
  public void testContainsErrorInteger1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:contains(3, \" AS\")?");
      fail("Should have failed on the second parameter to 'contains' being " +
           "an integer instead of a string");
    } catch (InvalidQueryException e) {
    }
  }
  
  public void testContainsErrorInteger2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:contains(\"Ontopia\", 7)?");
      fail("Should have failed on the second parameter to 'contains' being " +
           "an integer instead of a string.");
    } catch (InvalidQueryException e) {
    }
  }
  
  public void testContainsErrorUnbound1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:contains($CONTAINER, \" AS\")?");
      fail("Should have failed on the second parameter to 'contains' not " +
           "being bound.");
    } catch (InvalidQueryException e) {
    }
  }
  
  public void testContainsErrorUnbound2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:contains(\"Ontopia AS\", $CONTAINED)?");
      fail("Should have failed on the second parameter to 'contains' not " +
           "being bound.");
    } catch (InvalidQueryException e) {
    }
  }
  
  // --- ends-with -------------------------------------------------------
  
  public void testEndsWithStart() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:ends-with(\"Nevertheless\", \"Never\")?");
  }

  public void testEndsWithMiddle() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:ends-with(\"Nevertheless\", \"the\")?");
  }

  public void testEndsWithEnd() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:ends-with(\"Nevertheless\", \"less\")?");
  }

  public void testEndsWithAll() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:ends-with(\"Nevertheless\", \"Nevertheless\")?");
  }

  public void testEndsWithMore() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:ends-with(\"Never\", \"Nevertheless\")?");
  }
  
  public void testEndsWith() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "NAME", "Ontopia AS");
    
    verifyQuery(matches, 
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "$NAME = \"Ontopia AS\", " +
                "str:ends-with($NAME, \"AS\")?");
  }

  public void testEndsWithErrorInteger1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:ends-with(3, \" AS\")?");
      fail("Should have failed on the second parameter to 'ends-with' being " +
           "an integer instead of a string");
    } catch (InvalidQueryException e) {
    }
  }
  
  public void testEndsWithErrorInteger2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:ends-with(\"Ontopia\", 7)?");
      fail("Should have failed on the second parameter to 'ends-with' being " +
           "an integer instead of a string.");
    } catch (InvalidQueryException e) {
    }
  }
  
  public void testEndsWithErrorUnbound1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:ends-with($CONTAINER, \" AS\")?");
      fail("Should have failed on the second parameter to 'ends-with' not " +
           "being bound.");
    } catch (InvalidQueryException e) {
    }
  }
  
  public void testEndsWithErrorUnbound2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:ends-with(\"Ontopia AS\", $CONTAINED)?");
      fail("Should have failed on the second parameter to 'ends-with' not " +
           "being bound.");
    } catch (InvalidQueryException e) {
    }
  }
  
  // --- index-of -------------------------------------------------------
  
  public void testIndexOfSpecExample() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "POS", new Integer(25));

    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of($POS, \"The first occurrence of 'the' " +
                "in the sentence.\", \"the\")?"); 
  }
  
  public void testIndexOfStart() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of(0, \"Nevertheless\", \"Never\")?");
  }

  public void testIndexOfMid() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of(5, \"Nevertheless\", \"the\")?");
  }

  public void testIndexOfEnd() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of(8, \"Nevertheless\", \"less\")?");
  }

  public void testIndexOfStart1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of(1, \"Nevertheless\", \"Never\")?");
  }

  public void testIndexOfMid1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of(10, \"Nevertheless\", \"the\")?");
  }

  public void testIndexOfEnd1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of(3, \"Nevertheless\", \"less\")?");
  }

  public void testIndexOfStart2() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", new Integer(0));

    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of($N, \"Nevertheless\", \"Never\")?");
  }

  public void testIndexOfMid2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", new Integer(5));

    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of($N, \"Nevertheless\", \"the\")?");
  }

  public void testIndexOfEnd2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", new Integer(8));

    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of($N, \"Nevertheless\", \"less\")?");
  }

  public void testIndexOfSecond() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", new Integer(2));

    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of($N, \"Door or window?\", \"or\")?");
  }

  public void testIndexOfThird() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", new Integer(2));

    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of($N, \"Thiss iss vassviss?\", \"iss\")?");
  }

  public void testIndexOfSequence() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", new Integer(1));

    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of($N, \"Booooo?\", \"oo\")?");
  }

  public void testIndexOfSequence1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", new Integer(1));

    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of($N, \"Booooom?\", \"oo\")?");
  }

  public void testIndexOfMore() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:index-of($N, \"Never\", \"Nevertheless\")?");
  }

  public void testIndexErrorUnbound1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "CONTAINS", "Booom");

    try {
      verifyQuery(matches,
                  "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:index-of(1, $CONTAINS, \"oo\")?");
      fail("'index-of' should have failed with the first parameter undbound.");
    } catch (InvalidQueryException e) {
    }
  }

  public void testIndexErrorUnbound2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "CONTAINS", "Booom");

    try {
      verifyQuery(matches,
                  "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:index-of(1, $CONTAINS, \"oo\")?");
      fail("'index-of' should have failed with the second parameter undbound.");
    } catch (InvalidQueryException e) {
    }
  }

  public void testIndexErrorString1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:index-of(\"1\", \"Booooom\", \"oo\")?");
      fail("'index-of' should have failed with the first parameter being a " +
            "string instead of an integer.");
    } catch (InvalidQueryException e) {
    }
  }

  public void testIndexErrorString2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:index-of(1, 0, \"oo\")?");
      fail("'index-of' should have failed with the second parameter being an " +
            "integer instead of a string.");
    } catch (InvalidQueryException e) {
    }
  }

  public void testIndexErrorString3() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:index-of(1, \"Booooom\", 3)?");
      fail("'index-of' should have failed with the third parameter being an " +
            "integer instead of a string.");
    } catch (InvalidQueryException e) {
    }
  }

  // --- last-index-of -------------------------------------------------------
  
  public void testLastIndexOfStart() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of(0, \"Nevertheless\", \"Never\")?");
  }

  public void testLastIndexOfMid() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of(5, \"Nevertheless\", \"the\")?");
  }

  public void testLastIndexOfEnd() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of(8, \"Nevertheless\", \"less\")?");
  }

  public void testLastIndexOfStart1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of(1, \"Nevertheless\", \"Never\")?");
  }

  public void testLastIndexOfMid1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of(10, \"Nevertheless\", \"the\")?");
  }

  public void testLastIndexOfEnd1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of(3, \"Nevertheless\", \"less\")?");
  }

  public void testLastIndexOfStart2() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", new Integer(0));

    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of($N, \"Nevertheless\", \"Never\")?");
  }

  public void testLastIndexOfMid2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", new Integer(5));

    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of($N, \"Nevertheless\", \"the\")?");
  }

  public void testLastIndexOfEnd2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", new Integer(8));

    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of($N, \"Nevertheless\", \"less\")?");
  }

  public void testLastIndexOfSecond() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", new Integer(5));

    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of($N, \"Door or window?\", \"or\")?");
  }

  public void testLastIndexOfThird() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", new Integer(15));

    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of($N, \"Thiss iss vassviss?\", \"iss\")?");
  }

  public void testLastIndexOfSequence() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", new Integer(4));

    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of($N, \"Booooo?\", \"oo\")?");
  }

  public void testLastIndexOfSequence1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", new Integer(4));

    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of($N, \"Booooom?\", \"oo\")?");
  }

  public void testLastIndexOfMore() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:last-index-of($N, \"Never\", \"Nevertheless\")?");
  }
    
  
  public void testLastIndexErrorUnbound1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "CONTAINS", "Booom");
  
    try {
      verifyQuery(matches,
                  "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:index-of(1, $CONTAINS, \"oo\")?");
      fail("'last-index-of' should have failed with the first parameter " +
            "undbound.");
    } catch (InvalidQueryException e) {
    }
  }
  
  public void testLastIndexErrorUnbound2() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
  
    List matches = new ArrayList();
    addMatch(matches, "CONTAINS", "Booom");
  
    try {
      verifyQuery(matches,
                  "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:last-index-of(1, $CONTAINS, \"oo\")?");
      fail("'last-index-of' should have failed with the second parameter " +
            "undbound.");
    } catch (InvalidQueryException e) {
    }
  }
  
  public void testLastIndexErrorString1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
  
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:last-index-of(\"1\", \"Booooom\", \"oo\")?");
      fail("'last-index-of' should have failed with the first parameter " +
            "being a string instead of an integer.");
    } catch (InvalidQueryException e) {
    }
  }
  
  public void testLastIndexErrorString2() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
  
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:last-index-of(1, 0, \"oo\")?");
      fail("'last-index-of' should have failed with the second parameter " +
            "being an integer instead of a string.");
    } catch (InvalidQueryException e) {
    }
  }
  
  public void testLastIndexErrorString3() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
  
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:last-index-of(1, \"Booooom\", 3)?");
      fail("'last-index-of' should have failed with the third parameter " +
            "being an integer instead of a string.");
    } catch (InvalidQueryException e) {
    }
  }
  
  // --- length -------------------------------------------------------
  
  public void testLengthOpen() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "USER", getTopicById("gdm"),    "LENGTH", 
        new Integer(12));
    addMatch(matches, "USER", getTopicById("larsga"), "LENGTH", 
        new Integer(19));
    addMatch(matches, "USER", getTopicById("grove"),  "LENGTH", 
        new Integer(15));
    addMatch(matches, "USER", getTopicById("steve"),  "LENGTH", 
        new Integer(12));
    addMatch(matches, "USER", getTopicById("sylvia"), "LENGTH", 
        new Integer(13));
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $USER, $LENGTH from " +
                "instance-of($USER, user), " +
                "topic-name($USER, $TN), value($TN, $VALUE), " +
                "not(scope($TN, $ANYTHING)), " +
                "str:length($VALUE, $LENGTH)?");
  }

  public void testLengthClosed() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "USER", getTopicById("gdm"));
    addMatch(matches, "USER", getTopicById("steve"));
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $USER from " +
                "instance-of($USER, user), " +
                "topic-name($USER, $TN), value($TN, $VALUE), " +
                "not(scope($TN, $ANYTHING)), " +
                "str:length($VALUE, 12)?");
  }

  public void testLengthZero() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:length(\"\", 0)?");
  }

  public void testLengthOne() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:length(\".\", 1)?");
  }

  public void testLengthOneWrong0() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    verifyQuery(matches, "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:length(\".\", 0)?");
  }

  public void testLengthOneWrong4() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:length(\".\", 4)?");
  }

  public void testLengthMany() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:length(\"Ontopia\", 7)?");
  }

  public void testLengthZeroBound() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "L", new Integer(0));

    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:length(\"\", $L)?");
  }

  public void testLengthOneBound() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "L", new Integer(1));

    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:length(\".\", $L)?");
  }

  public void testLengthManyBound() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "L", new Integer(7));

    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:length(\"Ontopia\", $L)?");
  }

  public void testLengthErrorInteger1() throws InvalidQueryException,
      IOException {
  load("bb-test.ltm");
  
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:length(1, 1)?");
      fail("'length' should have failed with the first parameter " +
            "being an integer instead of a string.");
    } catch (InvalidQueryException e) {
    }
  }

  public void testLengthErrorString2() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:length(\"1\", \"1\")?");
      fail("'length' should have failed with the second parameter " +
            "being a string instead of an integer.");
    } catch (InvalidQueryException e) {
    }
  }

  public void testBug2120() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    try {
      List matches = new ArrayList();
      addMatch(matches, "VAL", "topic1", "LEN", new Integer(6));
      addMatch(matches, "VAL", "topic2", "LEN", new Integer(6));
      addMatch(matches, "VAL", "topic3", "LEN", new Integer(6));
      addMatch(matches, "VAL", "topic4", "LEN", new Integer(6));
      verifyQuery(matches,
                  "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "select $VAL, $LEN from " +
                  "  value($OCC, $VAL), " +
                  "  str:length($VAL, $LEN) " +
                  "order by $LEN desc, $VAL?");
    } catch (InvalidQueryException e) {
    }
  }

  // --- starts-with --------------------------------------------------
  
  public void testStartsWithStart() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:starts-with(\"Nevertheless\", \"Never\")?");
  }

  public void testStartsWithMiddle() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:starts-with(\"Nevertheless\", \"the\")?");
  }

  public void testStartsWithEnd() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:starts-with(\"Nevertheless\", \"less\")?");
  }

  public void testStartsWithAll() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:starts-with(\"Nevertheless\", \"Nevertheless\")?");
  }

  public void testStartsWithMore() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList(); // false
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:starts-with(\"Never\", \"Nevertheless\")?");
  }

  public void testStartsWithFilter() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "CLASS", getTopicById("k7a"));
    addMatch(matches, "CLASS", getTopicById("k7b"));
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $CLASS from " +
                "instance-of($CLASS, klasse), " +
                "topic-name($CLASS, $TN), value($TN, $VALUE), " +
                "str:starts-with($VALUE, \"7\")?");
  }

  public void testStartsWithOpt1() throws InvalidQueryException, IOException {
    load("int-occs-2.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("topic2"));
    addMatch(matches, "T", getTopicById("topic5"));

    // uses the optimization
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $T from " +
                "  description($T, $DESC), " +
                "  str:starts-with($DESC, \"topic2\")?");
  }

  public void testStartsWithOpt2() throws InvalidQueryException, IOException {
    load("int-occs-2.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("topic2"), "DESC", "topic23");
    addMatch(matches, "T", getTopicById("topic5"), "DESC", "topic22");
    
    // uses the optimization
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "description($T, $DESC), " +
                "str:starts-with($DESC, \"topic2\")?");
  }

  public void testStartsWithOpt2FindNothing() throws InvalidQueryException, 
      IOException {
    load("int-occs-2.ltm");
    
    // uses the optimization
    findNothing("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "description($T, $DESC), " +
                "str:starts-with($DESC, \"tupic2\")?");
  }
  
  public void testStartsWithOpt3() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("topic1"));
    
    // uses the optimization
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $T from " +
                "description($T, $DESC), " +
                "str:starts-with($DESC, \"topic1\")?");
  }  

  public void testStartsWithOpt4() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("topic1"));
    
    // does not use the optimization
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $T from " +
                "description($T, $DESC), " +
                "str:length($DESC, 6), " +
                "str:starts-with($DESC, \"topic1\")?");
  }

  public void testStartsWithOpt5() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("topic2"));
    addMatch(matches, "T", getTopicById("topic3"));
    addMatch(matches, "T", getTopicById("topic4"));
    
    // uses the optimization
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $T from " +
                "description($T, $DESC), " +
                "not(str:starts-with($DESC, \"topic1\"))?");
  }

  public void testStartsWithOpt6() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("topic1"));
    
    // should not use the optimization
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $T from " +
                "  $T = topic1, " +
                "  description($T, $DESC), " +
                "  str:starts-with($DESC, \"topic\")?");
  }  

  public void testStartsWithOpt7() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("topic1"));
    
    // should not use the optimization
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $T from " +
                "  description(topic1, $DESC), " +
                "  description($T, $DESC), " +
                "  str:starts-with($DESC, \"topic\")?");
  }  

  public void testStartsWithOpt7b() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("topic1"));
    
    // the point here is that the reordering optimizer will move the
    // second description before the first, causing failure if the
    // optimizations occur in the wrong order
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $T from " +
                "  description($T, $DESC), " +
                "  description(topic1, $DESC), " +
                "  str:starts-with($DESC, \"topic\")?");
  }  

  public void testStartsWithOptURI() throws InvalidQueryException, IOException {
    load("opera.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("puccini"));
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $T from " +
                "sound-clip($T, $URI), " +
                "str:starts-with($URI, \"http://www.puccini.it\")?");
  }

  public void testStartsWithOptURI2() throws InvalidQueryException, IOException {
    load("opera.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("puccini"),
                      "URI", "http://www.puccini.it/files/vocepucc.wav");
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $T, $URI from " +
                "sound-clip($T, $URI), " +
                "str:starts-with($URI, \"http://www.pucc\")?");
  }

  public void testStartsWithBoth() throws InvalidQueryException, IOException {
    load("int-occs-2.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("topic3"),
                      "V", "topic3");
    addMatch(matches, "T", getTopicById("topic6"),
                      "V", "topic3://woohoo/");
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $T, $V from " +
                "description($T, $V), " +
                "str:starts-with($V, \"topic3\")?");
  }

  public void testStartsWithBoth2() throws InvalidQueryException, IOException {
    load("int-occs-2.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("topic3"));
    addMatch(matches, "T", getTopicById("topic6"));
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $T from " +
                "description($T, $V), " +
                "str:starts-with($V, \"topic3\")?");
  }
  
  public void testStartsWithErrorInteger1() throws InvalidQueryException, 
      IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:starts-with(3, \" AS\")?");
      fail("Should have failed on the second parameter to 'starts-with' " +
           "being an integer instead of a string");
    } catch (InvalidQueryException e) {
    }
  }
  
  public void testStartsWithErrorInteger2() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:starts-with(\"Ontopia\", 7)?");
      fail("Should have failed on the second parameter to 'starts-with' " +
           "being an integer instead of a string.");
    } catch (InvalidQueryException e) {
    }
  }
  
  public void testStartsWithErrorUnbound1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:starts-with($CONTAINER, \" AS\")?");
      fail("Should have failed on the second parameter to 'starts-with' not " +
           "being bound.");
    } catch (InvalidQueryException e) {
    }
  }
  
  public void testStartsWithErrorUnbound2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:starts-with(\"Ontopia AS\", $CONTAINED)?");
      fail("Should have failed on the second parameter to 'starts-with' not " +
           "being bound.");
    } catch (InvalidQueryException e) {
    }
  }
  
  // --- substring ----------------------------------------------------

  public void testSubstringStartMid() throws InvalidQueryException, 
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "2007-");
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring($OUT, \"2007-03-12\", 0, 5)?");

  }

  public void testSubstringBoundStartMid() throws InvalidQueryException, 
      IOException {
    load("bb-test.ltm");
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
               "str:substring(\"2007-\", \"2007-03-12\", 0, 5)?");
  }

  public void testSubstringStartEnd() throws InvalidQueryException, 
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "2007-03-12");
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring($OUT, \"2007-03-12\", 0, 10)?");
  }

  public void testSubstringBoundStartEnd() throws InvalidQueryException, 
      IOException {
    load("bb-test.ltm");
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
               "str:substring(\"2007-03-12\", \"2007-03-12\", 0, 10)?");
  }

  public void testSubstringStartOmitEnd() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "2007-03-12");
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring($OUT, \"2007-03-12\", 0)?");
  }

  public void testSubstringBoundStartOmitEnd() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");

    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring(\"2007-03-12\", \"2007-03-12\", 0)?");
}

  public void testSubstringMidMid() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "7-0");
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring($OUT, \"2007-03-12\", 3, 6)?");
  }

  public void testSubstringBoundMidMid() throws InvalidQueryException, 
      IOException {
    load("bb-test.ltm");
    
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring(\"7-0\", \"2007-03-12\", 3, 6)?");
  }

  public void testSubstringMidEnd() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "-03-12");
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring($OUT, \"2007-03-12\", 4, 10)?");
  }

  public void testSubstringBoundMidEnd() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring(\"-03-12\", \"2007-03-12\", 4, 10)?");
  }

  public void testSubstringMidOmitEnd() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "-03-12");
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring($OUT, \"2007-03-12\", 4)?");
  }

  public void testSubstringBoundMidOmitEnd() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");

    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring(\"-03-12\", \"2007-03-12\", 4)?");
  }

  public void testSubstringErrorNegativeRange() throws InvalidQueryException, 
      IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "OUT", "-03-12");

    try {
      verifyQuery(matches,
        "import \"http://psi.ontopia.net/tolog/string/\" as str " +
        "str:substring($OUT, \"2007-03-12\", 4, 3)?");
      fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("The 3rd and 4th parameters to"))
        fail("Wrong error message: \"" + e.getMessage() + "\"");
    }
  }

  public void testSubstringErrorBoundNegativeRange() 
      throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring(\"-03-12\", \"2007-03-12\", 4, 3)?");
      fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("The 3rd and 4th parameters to"))
        fail("Wrong error message: \"" + e.getMessage() + "\"");
    }
  }

  public void testSubstringErrorUnbound2() 
      throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();

    try {
      verifyQuery(matches,
                  "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring(\"-03-12\", $ARG2, 4)?");
      fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("Variable $ARG2 not bound in predicate"))
        fail("Wrong error message: \"" + e.getMessage() + "\"");
    }
  }

  public void testSubstringErrorUnbound3() 
      throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring(\"-03-12\", \"2007-03-12\", $ARG3)?");
      fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("Variable $ARG3 not bound in predicate"))
        fail("Wrong error message: \"" + e.getMessage() + "\"");
    }
  }
  
    public void testSubstringErrorUnbound4() 
        throws InvalidQueryException, IOException {
      load("bb-test.ltm");
      
      try {
        verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                    "str:substring(\"-03-12\", \"2007-03-12\", 4, $ARG4)?");
        fail("Expected InvalidQueryException, but got no Exception at all.");
      } catch (InvalidQueryException e) {
        if (!e.getMessage().startsWith("Variable $ARG4 not bound in predicate"))
          fail("Wrong error message: \"" + e.getMessage() + "\"");
      }
  }
  
  public void testSubstringErrorInteger1() 
      throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring(10, \"2007-03-12\", 4, 10)?");
      fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("Predicate substring received class " +
                                     "java.lang.Integer as argument 1, " +
                                     "but requires a string"))
        fail("Wrong error message: \"" + e.getMessage() + "\"");
    }
  }
  
  public void testSubstringErrorInteger2() 
      throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring(\"-03-12\", 20, 4, 10)?");
      fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("Predicate substring received class " +
                                     "java.lang.Integer as argument 2, but " +
                                     "requires a string"))
        fail("Wrong error message: \"" + e.getMessage() + "\"");
    }
  }

  public void testSubstringErrorString3() 
      throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring(\"-03-12\", \"2007-03-12\", \"4\", 10)?");
      fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("Predicate substring received a string " +
                                     "as argument 3, but requires class " +
                                     "java.lang.Integer"))
        fail("Wrong error message: \"" + e.getMessage() + "\"");
    }
  }

  public void testSubstringErrorString4() 
      throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring(\"-03-12\", \"2007-03-12\", 4, \"10\")?");
      fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("Predicate substring received a string " +
                                     "as argument 4, but requires class " +
                                     "java.lang.Integer"))
        fail("Wrong error message: \"" + e.getMessage() + "\"");
    }
  }

  // --- substring-after ----------------------------------------------

  public void testSubstringAfterFirstChar() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "007-03-12");
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-after($OUT, \"2007-03-12\", \"2\")?");
  }

  public void testSubstringAfterMidChar() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "03-12");
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-after($OUT, \"2007-03-12\", \"-\")?");
  }

  public void testSubstringAfterMidTwoChars() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "03-12");
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-after($OUT, \"2007-03-12\", \"7-\")?");
  }

  public void testSubstringAfterFirstTwoChars() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "07-03-12");
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-after($OUT, \"2007-03-12\", \"20\")?");
  }

  public void testSubstringAfterFirstChar1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-after(\"007-03-12\", \"2007-03-12\", \"2\")?");
  }

  public void testSubstringAfterMidChar1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-after(\"03-12\", \"2007-03-12\", \"-\")?");
  }

  public void testSubstringAfterMidTwoChars1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-after(\"03-12\", \"2007-03-12\", \"7-\")?");
  }

  public void testSubstringAfterFirstTwoChars1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-after(\"07-03-12\", \"2007-03-12\", \"20\")?");
  }

  public void testSubstringAfterErrorInteger1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring-after(1, \"20\", \"07-03-12\")?");
      fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("Predicate substring-after received " +
                                     "class java.lang.Integer as argument " +
                                     "1, but requires a string"))
        fail("Wrong error message: \"" + e.getMessage() + "\"");
    }
  }

  public void testSubstringAfterErrorInteger2() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring-after(\"2007-03-12\", 2, \"07-03-12\")?");
      fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("Predicate substring-after received " +
                                     "class java.lang.Integer as argument " +
                                     "2, but requires a string"))
        fail("Wrong error message: \"" + e.getMessage() + "\"");
    }
  }

  public void testSubstringAfterErrorInteger3() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring-after(\"2007-03-12\", \"20\", 3)?");
      fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("Predicate substring-after received " +
                                     "class java.lang.Integer as argument " +
                                     "3, but requires a string"))
        fail("Wrong error message: \"" + e.getMessage() + "\"");
    }
  }

  public void testSubstringAfterErrorUnbound2() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();

    try {
      verifyQuery(matches,
                  "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring-after(\"07-03-12\", $ARG2, \"2007-03-12\")?");
      fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith(""))
        fail("Wrong error message: \"" + e.getMessage() + "\"");
    }
  }
    
      public void testSubstringAfterErrorUnbound3() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();

    try {
      verifyQuery(matches,
                  "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring-after(\"2007-03-12\", \"20\", $ARG3)?");
      fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith(""))
        fail("Wrong error message: \"" + e.getMessage() + "\"");
    }
  }

  // --- substring-before ----------------------------------------------

  public void testSubstringBeforeFirstChar() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "");
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-before($OUT, \"2007-03-12\", \"2\")?");
  }

  public void testSubstringBeforeMidChar() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "2007");
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-before($OUT, \"2007-03-12\", \"-\")?");
  }

  public void testSubstringBeforeMidTwoChars() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "200");
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-before($OUT, \"2007-03-12\", \"7-\")?");
  }

  public void testSubstringBeforeFirstTwoChars() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "");
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-before($OUT, \"2007-03-12\", \"20\")?");
  }

  public void testSubstringBeforeFirstChar1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-before(\"\", \"2007-03-12\", \"2\")?");
  }

  public void testSubstringBeforeMidChar1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-before(\"2007\", \"2007-03-12\", \"-\")?");
  }

  public void testSubstringBeforeMidTwoChars1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-before(\"200\", \"2007-03-12\", \"7-\")?");
  }

  public void testSubstringBeforeFirstTwoChars1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-before(\"\", \"2007-03-12\", \"20\")?");
  }

  public void testSubstringBeforeErrorInteger1() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring-before(1, \"20\", \"07-03-12\")?");
      fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("Predicate substring-before received " +
                                     "class java.lang.Integer as argument " +
                                     "1, but requires a string"))
        fail("Wrong error message: \"" + e.getMessage() + "\"");
    }
  }
  
  public void testSubstringBeforeErrorInteger2() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring-before(\"2007-03-12\", 2, \"07-03-12\")?");
      fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("Predicate substring-before received " +
                                     "class java.lang.Integer as argument " +
                                     "2, but requires a string"))
        fail("Wrong error message: \"" + e.getMessage() + "\"");
    }
  }
  
  public void testSubstringBeforeErrorInteger3() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    try {
      verifyQuery("import \"http://psi.ontopia.net/tolog/string/\" as str " +
                  "str:substring-before(\"2007-03-12\", \"20\", 3)?");
      fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith("Predicate substring-before " +
                                     "received class java.lang.Integer as " +
                                     "argument 3, but requires a string"))
        fail("Wrong error message: \"" + e.getMessage() + "\"");
    }
  }
  
  public void testSubstringBeforeErrorUnbound2() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    
    try {
      verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-before(\"07-03-12\", $ARG2, \"2007-03-12\")?");
      fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith(""))
        fail("Wrong error message: \"" + e.getMessage() + "\"");
    }
  }
  
  public void testSubstringBeforeErrorUnbound3() throws InvalidQueryException,
      IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    
    try {
      verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "str:substring-before(\"2007-03-12\", \"20\", $ARG3)?");
      fail("Expected InvalidQueryException, but got no Exception at all.");
    } catch (InvalidQueryException e) {
      if (!e.getMessage().startsWith(""))
        fail("Wrong error message: \"" + e.getMessage() + "\"");
    }
  }

  // --- translate -------------------------------------------------------
  
  public void testTranslatePreserve() throws InvalidQueryException,
      IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "addis abeba (12)");
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $OUT from " +
                "  str:translate($OUT, " +
                "                \"Addis Abeba (12)\", " +
                "                \"ABCDEFGHIJKLMNOPQRSTUVXYZ\", " +
                "                \"abcdefghijklmnopqrstuvxyz\", \"\")?");
  }
  
  public void testTranslatePreserveDigits() throws InvalidQueryException,
      IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "addisabeba12");
    
    verifyQuery(matches,
               "import \"http://psi.ontopia.net/tolog/string/\" as str " +
               "select $OUT from " +
               "  str:translate($OUT, " +
               "                \"Addis Abeba (12)\", " +
               "                \"ABCDEFGHIJKLMNOPQRSTUVXYZ\", " +
               "                \"abcdefghijklmnopqrstuvxyz\", \" ()\")?");
  }

  public void testTranslateDelete() throws InvalidQueryException,
      IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "aa");
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $OUT from " +
                "  str:translate($OUT, " +
                "                \"Addis Abeba (12)\", " +
                "                \"ABCDEFGHIJKLMNOPQRSTUVXYZ\", " +
                "                \"abcdefghijklmnopqrstuvxyz\")?");
  }

  public void testTranslateDelete2() throws InvalidQueryException,
      IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "addisabeba");
    
    verifyQuery(matches,
           "import \"http://psi.ontopia.net/tolog/string/\" as str " +
           "select $OUT from " +
           "  str:translate($OUT, " +
           "    \"Addis Abeba (12)\", " +
           "    \"ABCDEFGHIJKLMNOPQRSTUVXYZabcdefghijklmnopqrstuvxyz\", " +
           "    \"abcdefghijklmnopqrstuvxyzabcdefghijklmnopqrstuvxyz\")?");
  }

  public void testTranslateDelete3() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "addis abeba ");
    
    verifyQuery(matches,
          "import \"http://psi.ontopia.net/tolog/string/\" as str " +
          "select $OUT from " +
          "  str:translate($OUT, " +
          "    \"Addis Abeba (12)\", " +
          "    \"ABCDEFGHIJKLMNOPQRSTUVXYZabcdefghijklmnopqrstuvxyz \", " +
          "    \"abcdefghijklmnopqrstuvxyzabcdefghijklmnopqrstuvxyz \")?");
  }
  
  public void testTranslatePreserveExtraChars() throws InvalidQueryException,
      IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "addis abeba (12)");
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $OUT from " +
                "  str:translate($OUT, " +
                "                \"Addis Abeba (12)\", " +
                "                \"ABCDEFGHIJKLMNOPQRSTUVXYZ \", " +
                "                \"abcdefghijklmnopqrstuvxyz\", \"\")?");
  }
  
  public void testTranslateDeleteExtraChars() throws InvalidQueryException,
      IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "OUT", "a a ");
    
    verifyQuery(matches,
                "import \"http://psi.ontopia.net/tolog/string/\" as str " +
                "select $OUT from " +
                "  str:translate($OUT, " +
                "                \"Addis Abeba (12)\", " +
                "                \"ABCDEFGHIJKLMNOPQRSTUVXYZ \", " +
                "                \"abcdefghijklmnopqrstuvxyz\")?");
  }
}
