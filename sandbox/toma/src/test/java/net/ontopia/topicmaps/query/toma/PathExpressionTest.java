package net.ontopia.topicmaps.query.toma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

@SuppressWarnings("unchecked")
public class PathExpressionTest extends AbstractTomaQueryTestCase {
  
  public PathExpressionTest(String name) {
    super(name);
  }

  /// context management

  public void setUp() {
    QueryMatches.initialSize = 1;
  }

  public void tearDown() {
    closeStore();
  }

  /// id path expressions
  
  public void testID() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T.ID", getTopicById("ontopia").getItemIdentifiers().iterator().next());
    
    verifyQuery(matches, "select $t.id where $t.id = 'ontopia';");
  }  
  
  /// name path expressions
  
  public void testName() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T.NAME", getTopicById("ontopia").getTopicNames().iterator().next());
    
    verifyQuery(matches, "select $t.name where $t.name = 'Ontopia Project';");
  }

  /// variant path expressions
  
  public void testVariant() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    Collection<TopicNameIF> topicNames = getTopicById("ltm").getTopicNames();
    for (Object o : topicNames) {
      TopicNameIF name = (TopicNameIF) o;
      for (Object var : name.getVariants()) {
        addMatch(matches, "$T.NAME.VAR", var);
      }
    }
    
    verifyQuery(matches, "select $t.name.var where $t.name = 'The linear topic map notation';");
  }

  public void testVariantWithScope() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("ltm"));
    
    verifyQuery(matches, "select $t where exists $t.name.var@german;");
  }
  
  /// occurrence path expressions
  
  public void testOccurrence() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T.OC", getTopicById("ontopia").getOccurrences().iterator().next());
    
    verifyQuery(matches, "select $t.oc where $t = i'ontopia';");
  }
  
  public void testTypedOccurrence() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("ltm"));
    addMatch(matches, "$T", getTopicById("topic-maps"));
    
    verifyQuery(matches, "select $t where exists $t.oc(specification);");
  }  

  /// subject indicator path expressions
  
  public void testSubjectIndicator() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T.SI", getTopicById("ltm").getSubjectIdentifiers().iterator().next());
    
    verifyQuery(matches, "select $t.si where $t = i'ltm';");
  }  

  /// scope path expressions
  
  public void testScope() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("topic-maps"));
    
    verifyQuery(matches, "select $t where exists $t.name.sc;");
  }  

  /// player path expressions
  
  public void testPlayer() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$A.PLAYER", getTopicById("topic-maps"));
    addMatch(matches, "$A.PLAYER", getTopicById("ltm"));
    addMatch(matches, "$A.PLAYER", getTopicById("xtm"));
    
    verifyQuery(matches, "select distinct $a.player where exists $a(format-for)->($$);");
  }  

  /// role path expressions
  
  public void testRole() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$A.ROLE", getTopicById("format"));
    addMatch(matches, "$A.ROLE", getTopicById("standard"));
    
    verifyQuery(matches, "select distinct $a.role where exists $a(format-for)->($$);");
  }
  
  /// reifier path expressions
  
  public void testReifier() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$A.REIFIER", getTopicById("xtm-standard"));
    
    verifyQuery(matches, "select $a.reifier where $a(format-for)->(format) = i'xtm';");
  }
  
  /// type path expressions
  
  public void testType() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T.TYPE", getTopicById("format"));
    
    verifyQuery(matches, "select $t.type(1) where $t = i'xtm';");
  }
  
  /// instance path expressions
  
  public void testInstance() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T.INSTANCE", getTopicById("xtm"));
    addMatch(matches, "$T.INSTANCE", getTopicById("ltm"));
    
    verifyQuery(matches, "select $t.instance(1) where $t = i'format';");
  }  
}
