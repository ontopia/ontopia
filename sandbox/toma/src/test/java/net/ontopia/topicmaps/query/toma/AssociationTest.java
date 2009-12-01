package net.ontopia.topicmaps.query.toma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

@SuppressWarnings("unchecked")
public class AssociationTest extends AbstractTomaQueryTestCase {
  
  public AssociationTest(String name) {
    super(name);
  }

  /// context management

  public void setUp() {
    QueryMatches.initialSize = 1;
  }

  public void tearDown() {
    closeStore();
  }

  /// association tests
  
  public void testSimpleAssociation() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("xtm"));
    addMatch(matches, "$T", getTopicById("ltm"));
    
    verifyQuery(matches, "select $t where (format-for)->(format) = $t;");
  }

  public void testAssociationWithTwoRoles() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("tn"));
    addMatch(matches, "$T", getTopicById("lmg"));
    
    verifyQuery(matches, "select $t where ontopia.(project)<-(contributes-to)->(person) = $t;");
  }

  public void testAssociationWithScope() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("tn"));
    
    verifyQuery(matches, "select $t where ontopia.(project)<-(contributes-to)@(year2009)->(person) = $t;");
  }
  
  public void testAssociationPlayer() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$A.PLAYER", getTopicById("tn"));
    addMatch(matches, "$A.PLAYER", getTopicById("lmg"));
    addMatch(matches, "$A.PLAYER", getTopicById("ontopia"));
    addMatch(matches, "$A.PLAYER", getTopicById("ontopia"));
    addMatch(matches, "$A.PLAYER", getTopicById("lh"));
    addMatch(matches, "$A.PLAYER", getTopicById("tinytim"));
    
    verifyQuery(matches, "select $a.player where exists $a(contributes-to)->($$);");
  }
  
  public void testDistinctAssociationPlayer() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$A.PLAYER", getTopicById("tn"));
    addMatch(matches, "$A.PLAYER", getTopicById("lmg"));
    addMatch(matches, "$A.PLAYER", getTopicById("lh"));
    addMatch(matches, "$A.PLAYER", getTopicById("tinytim"));
    addMatch(matches, "$A.PLAYER", getTopicById("ontopia"));
    
    verifyQuery(matches, "select distinct $a.player where exists $a(contributes-to)->($$);");
  }

  public void testChainedAssociation() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("ltm-standard"));
    addMatch(matches, "$T", getTopicById("xtm-standard"));
    
    // get all the standards that are implemented by the project, tn contributes to
    verifyQuery(
        matches,
        "select $t where tn.($$)<-$a(contributes-to)->(project).($$)<-(implements)->(standard) = $t;");
  }
  
  public void testTypeAssignment() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$ATYPE", getTopicById("contributes-to"), "$ROLE",
        getTopicById("person"), "$PLAYER", getTopicById("tn"));
    
    verifyQuery(
        matches,
        "select $atype, $role, $player where exists ($atype)->($role)[$player] and $player = tn;");
  }
  
}
