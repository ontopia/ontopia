
// $Id: QueryProcessorTest.java,v 1.75 2009/04/27 11:00:50 lars.garshol Exp $

package net.ontopia.topicmaps.query.toma;

import java.io.IOException;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

public class QueryResultTest extends AbstractTomaQueryTestCase {
  
  public QueryResultTest(String name) {
    super(name);
  }

  /// context management

  public void setUp() {
    QueryMatches.initialSize = 1;
  }

  public void tearDown() {
    closeStore();
  }

  /// test cases
  
  public void testColumnName() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    String query = "select $M, $F where $M.(mother)<-$A(parenthood)->(father) = $F;";
    QueryResultIF result = processor.execute(query);

    assertTrue("$M not first column", result.getColumnName(0).equals("$M"));
    assertTrue("$F not second column", result.getColumnName(1).equals("$F"));
    
    try {
      result.getColumnName(2);
      fail("accepted non-existent column");
    } catch (IndexOutOfBoundsException e) {
    }

    try {
      result.getColumnName(-1);
      fail("accepted non-existent column");
    } catch (IndexOutOfBoundsException e) {
    }
  }

  public void testColumnNames() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    String query = "select $M, $F where $M.(mother)<-$A(parenthood)->(father) = $F;";
    QueryResultIF result = processor.execute(query);
    String[] cols = result.getColumnNames();

    assertTrue("wrong length of column name array", cols.length == 2);
    assertTrue("$M not first column", cols[0].equals("$M"));
    assertTrue("$F not second column", cols[1].equals("$F"));
  }

  public void testGetIndex() throws InvalidQueryException, IOException{
    load("family.ltm");
    
    String query = "select $M, $F where $M.(mother)<-$A(parenthood)->(father) = $F;";
    QueryResultIF result = processor.execute(query);

    assertTrue("variable $M had bad index",
               result.getIndex("$M") == 0);
    assertTrue("variable $F had bad index",
               result.getIndex("$F") == 1);
    assertTrue("non-existent variable $A was found",
               result.getIndex("$A") == -1);
  }
  
  public void testGetWidthSelect() throws InvalidQueryException, IOException{
    load("family.ltm");
    
    String query = "select $M, $F where $M.(mother)<-$A(parenthood)->(father) = $F;";
    QueryResultIF result = processor.execute(query);
    assertTrue("result had wrong width", result.getWidth() == 2);    
  }
  
  public void testGetValue() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    String query = "select $M where $M.type = father;";
    QueryResultIF result = processor.execute(query);
    
    assertTrue("result had wrong width", result.getWidth() == 1);
    
    try {
      result.getValue(0);
      fail("accepted getValue before calling next");
    } catch (IllegalStateException e) {
    }
    
    assertTrue("query needs to have at least 1 row as a result", result.next());
    
    Object topic = result.getValue(0);
    assertTrue("result must be of type TopicIF", topic instanceof TopicIF);
    
    try {
      result.getValue(2);
      fail("accepted non-existant column for getValue");
    } catch (IndexOutOfBoundsException e) {
    }
  }
  
  public void testGetValues() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    String query = "select $M where $M.type = father;";
    QueryResultIF result = processor.execute(query);
    
    assertTrue("result had wrong width", result.getWidth() == 1);
    
    try {
      result.getValues();
      fail("accepted getValues before calling next");
    } catch (IllegalStateException e) {
    }
    
    assertTrue("query needs to have at least 1 row as a result", result.next());
    
    Object[] values = result.getValues();
    assertTrue("values empty", values != null);
    assertTrue("size of values array not matching", values.length == 1);    
  }
  
  public void testClose() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    String query = "select $M where $M.type = father;";
    QueryResultIF result = processor.execute(query);
    
    // close result set
    result.close();
    
    try {
      result.getWidth();
      fail("accepted getWidth after close");
    } catch (IllegalStateException e) {
    }
  }  
}
