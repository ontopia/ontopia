
// $Id: QueryProcessorTest.java,v 1.75 2009/04/27 11:00:50 lars.garshol Exp $

package net.ontopia.topicmaps.query.toma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

public class TypeHierarchyTest extends AbstractTomaQueryTestCase {
  
  public TypeHierarchyTest(String name) {
    super(name);
  }

  /// context management

  public void setUp() {
    QueryMatches.initialSize = 1;
  }

  public void tearDown() {
    closeStore();
  }

  /// type hierarchies
  
  public void testTypeLevelZero() throws InvalidQueryException, IOException {
    load("hierarchies.ltm");

    // type(0) gets the topic itself
    List<TopicIF> matches = new ArrayList<TopicIF>();
    addMatch(matches, "$T.TYPE", getTopicById("topicA"));
    
    verifyQuery(matches, "select $t.type(0) where $t = i'topicA';");
  }
  
  public void testNoType() throws InvalidQueryException, IOException {
    load("hierarchies.ltm");

    // topicA does not have a type
    findNothing("select $t.type(1) where $t = i'topicA';");
  }
  
  public void testTypeWithRange() throws InvalidQueryException, IOException {
    load("hierarchies.ltm");

    // topicC has topicB, which has topicA as type
    List<TopicIF> matches = new ArrayList<TopicIF>();
    addMatch(matches, "$T.TYPE", getTopicById("topicA"));
    addMatch(matches, "$T.TYPE", getTopicById("topicB"));
    
    verifyQuery(matches, "select $t.type(1..*) where $t = i'topicC';");
  }  

  public void testTypeWithRange2() throws InvalidQueryException, IOException {
    load("hierarchies.ltm");

    // topicC has topicB, which has topicA as type
    List<TopicIF> matches = new ArrayList<TopicIF>();
    addMatch(matches, "$T.TYPE", getTopicById("topicA"));
    addMatch(matches, "$T.TYPE", getTopicById("topicB"));
    addMatch(matches, "$T.TYPE", getTopicById("topicC"));
    
    verifyQuery(matches, "select $t.type(1..3) where $t = i'topicD';");
  }
  
  public void testTypeWithStar() throws InvalidQueryException, IOException {
    load("hierarchies.ltm");

    // topicC has topicB, which has topicA as type
    List<TopicIF> matches = new ArrayList<TopicIF>();
    addMatch(matches, "$T.TYPE", getTopicById("topicA"));
    addMatch(matches, "$T.TYPE", getTopicById("topicB"));
    
    verifyQuery(matches, "select $t.type(*) where $t = i'topicB';");
  }

  /// instance hierarchies
  
  public void testInstanceLevelZero() throws InvalidQueryException, IOException {
    load("hierarchies.ltm");

    // instance(0) gets the topic itself
    List<TopicIF> matches = new ArrayList<TopicIF>();
    addMatch(matches, "$T.INSTANCE", getTopicById("topicA"));
    
    verifyQuery(matches, "select $t.instance(0) where $t = i'topicA';");
  }
  
  public void testNoInstance() throws InvalidQueryException, IOException {
    load("hierarchies.ltm");

    // topicD does not have an instance
    findNothing("select $t.instance(1) where $t = i'topicD';");
  }
  
  public void testInstanceWithRange() throws InvalidQueryException, IOException {
    load("hierarchies.ltm");

    List<TopicIF> matches = new ArrayList<TopicIF>();
    addMatch(matches, "$T.INSTANCE", getTopicById("topicC"));
    addMatch(matches, "$T.INSTANCE", getTopicById("topicD"));
    
    verifyQuery(matches, "select $t.instance(1..*) where $t = i'topicB';");
  }  

  public void testInstanceWithRange2() throws InvalidQueryException, IOException {
    load("hierarchies.ltm");

    // topicC has topicB, which has topicA as type
    List<TopicIF> matches = new ArrayList<TopicIF>();
    addMatch(matches, "$T.INSTANCE", getTopicById("topicB"));
    addMatch(matches, "$T.INSTANCE", getTopicById("topicC"));
    addMatch(matches, "$T.INSTANCE", getTopicById("topicD"));
    
    verifyQuery(matches, "select $t.instance(1..3) where $t = i'topicA';");
  }
  
  public void testInstanceWithStar() throws InvalidQueryException, IOException {
    load("hierarchies.ltm");

    // topicC has topicB, which has topicA as type
    List<TopicIF> matches = new ArrayList<TopicIF>();
    addMatch(matches, "$T.INSTANCE", getTopicById("topicC"));
    addMatch(matches, "$T.INSTANCE", getTopicById("topicD"));
    
    verifyQuery(matches, "select $t.instance(*) where $t = i'topicC';");
  }  

  /// supertype-subtype hierarchies
  
  public void testSubTypeLevelZero() throws InvalidQueryException, IOException {
    load("hierarchies.ltm");

    // sub(0) gets the type itself
    List<TopicIF> matches = new ArrayList<TopicIF>();
    addMatch(matches, "$T.SUB", getTopicById("typeA"));
    
    verifyQuery(matches, "select $t.sub(0) where $t = i'typeA';");
  }
  
  public void testNoSubType() throws InvalidQueryException, IOException {
    load("hierarchies.ltm");

    // typeD does not have a sub type
    findNothing("select $t.sub(1) where $t = i'typeD';");
  }
  
  public void testSubTypeWithRange() throws InvalidQueryException, IOException {
    load("hierarchies.ltm");

    List<TopicIF> matches = new ArrayList<TopicIF>();
    addMatch(matches, "$T.SUB", getTopicById("typeC"));
    addMatch(matches, "$T.SUB", getTopicById("typeD"));
    
    verifyQuery(matches, "select $t.sub(1..*) where $t = i'typeB';");
  }  

  public void testSubTypeWithRange2() throws InvalidQueryException, IOException {
    load("hierarchies.ltm");

    List<TopicIF> matches = new ArrayList<TopicIF>();
    addMatch(matches, "$T.SUB", getTopicById("typeB"));
    addMatch(matches, "$T.SUB", getTopicById("typeC"));
    addMatch(matches, "$T.SUB", getTopicById("typeD"));
    
    verifyQuery(matches, "select $t.sub(1..3) where $t = i'typeA';");
  }
  
  public void testSubTypeWithStar() throws InvalidQueryException, IOException {
    load("hierarchies.ltm");

    List<TopicIF> matches = new ArrayList<TopicIF>();
    addMatch(matches, "$T.SUB", getTopicById("typeC"));
    addMatch(matches, "$T.SUB", getTopicById("typeD"));
    
    verifyQuery(matches, "select $t.sub(*) where $t = i'typeC';");
  }

  // super type tests
  
  public void testSuperTypeLevelZero() throws InvalidQueryException, IOException {
    load("hierarchies.ltm");

    // super(0) gets the type itself
    List<TopicIF> matches = new ArrayList<TopicIF>();
    addMatch(matches, "$T.SUPER", getTopicById("typeD"));
    
    verifyQuery(matches, "select $t.super(0) where $t = i'typeD';");
  }
  
  public void testNoSuperType() throws InvalidQueryException, IOException {
    load("hierarchies.ltm");

    // typeA does not have a super type
    findNothing("select $t.super(1) where $t = i'typeA';");
  }
  
  public void testSuperTypeWithRange() throws InvalidQueryException, IOException {
    load("hierarchies.ltm");

    List<TopicIF> matches = new ArrayList<TopicIF>();
    addMatch(matches, "$T.SUPER", getTopicById("typeA"));
    addMatch(matches, "$T.SUPER", getTopicById("typeB"));
    
    verifyQuery(matches, "select $t.super(1..*) where $t = i'typeC';");
  }  

  public void testSuperTypeWithRange2() throws InvalidQueryException, IOException {
    load("hierarchies.ltm");

    List<TopicIF> matches = new ArrayList<TopicIF>();
    addMatch(matches, "$T.SUPER", getTopicById("typeA"));
    addMatch(matches, "$T.SUPER", getTopicById("typeB"));
    addMatch(matches, "$T.SUPER", getTopicById("typeC"));
    
    verifyQuery(matches, "select $t.super(1..3) where $t = i'typeD';");
  }
  
  public void testSuperTypeWithStar() throws InvalidQueryException, IOException {
    load("hierarchies.ltm");

    List<TopicIF> matches = new ArrayList<TopicIF>();
    addMatch(matches, "$T.SUPER", getTopicById("typeA"));
    addMatch(matches, "$T.SUPER", getTopicById("typeB"));
    
    verifyQuery(matches, "select $t.super(*) where $t = i'typeB';");
  }
  
}
