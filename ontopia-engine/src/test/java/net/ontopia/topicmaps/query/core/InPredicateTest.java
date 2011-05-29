
package net.ontopia.topicmaps.query.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Ignore;

@Ignore //disabled: EXPERIMENTAL predicate
public class InPredicateTest extends AbstractPredicateTest {
	
  protected final String IMPORT_EXPERIMENTAL = "import \"http://psi.ontopia.net/tolog/experimental/\" as exp ";
  
  public InPredicateTest(String name) {
    super(name);
  }

  /// tests

  public void testHumanInList() throws InvalidQueryException, IOException {
    load("family2.ltm");

    List matches = new ArrayList();
    addMatch(matches, "HUMAN", getTopicById("dan"));
    addMatch(matches, "HUMAN", getTopicById("sharon"));
    addMatch(matches, "HUMAN", getTopicById("spencer"));
    
    verifyQuery(matches, IMPORT_EXPERIMENTAL + "instance-of($HUMAN, human), exp:in($HUMAN, dan, sharon, spencer)?");
    closeStore();
  }

  public void testFemaleInList() throws InvalidQueryException, IOException {
    load("family2.ltm");

    List matches = new ArrayList();
    addMatch(matches, "FEMALE", getTopicById("sharon"));
    
    verifyQuery(matches, IMPORT_EXPERIMENTAL + "instance-of($FEMALE, female), exp:in($FEMALE, dan, sharon, spencer)?");
    closeStore();
  }

  public void testMaleInList() throws InvalidQueryException, IOException {
    load("family2.ltm");

    List matches = new ArrayList();
    addMatch(matches, "MALE", getTopicById("dan"));
    addMatch(matches, "MALE", getTopicById("spencer"));
    
    verifyQuery(matches, IMPORT_EXPERIMENTAL + "instance-of($MALE, male), exp:in($MALE, dan, sharon, spencer)?");
    closeStore();
  }

  public void testHumanNotInList() throws InvalidQueryException, IOException {
    load("family2.ltm");

    List matches = new ArrayList();
    addMatch(matches, "HUMAN", getTopicById("alan"));
    addMatch(matches, "HUMAN", getTopicById("peter"));
    addMatch(matches, "HUMAN", getTopicById("andy"));
    addMatch(matches, "HUMAN", getTopicById("philip"));
    addMatch(matches, "HUMAN", getTopicById("bruce"));
    addMatch(matches, "HUMAN", getTopicById("clyde"));
    addMatch(matches, "HUMAN", getTopicById("james"));
    
    verifyQuery(matches, IMPORT_EXPERIMENTAL + "instance-of($HUMAN, male), not(exp:in($HUMAN, dan, sharon, spencer))?");
    closeStore();
  }
  
}
