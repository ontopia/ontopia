
package net.ontopia.topicmaps.query.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.AbstractQueryTest;

public class FlatQueryResultIteratorTest extends AbstractQueryTest {
  
  public FlatQueryResultIteratorTest(String name) {
    super(name);
  }

  /// tests

  public void testNoResults() throws InvalidQueryException, IOException {
    load("family.ltm");
    verifyResult(Collections.EMPTY_SET, "instance-of($PA, mother)?");    
    closeStore();   
  }

  public void testSingleRowOfResults() throws InvalidQueryException, IOException {
    load("family.ltm");

    List result = new ArrayList();
    result.add(getTopicById("edvin"));
    result.add(getTopicById("kfg"));
    result.add(getTopicById("petter"));
    result.add(getTopicById("asle"));
    result.add(getTopicById("magnus"));
    result.add(getTopicById("unknown2"));
    
    verifyResult(result, "instance-of($PA, father)?");    
    closeStore();      
  }

  public void testTwoRowsOfResults() throws InvalidQueryException, IOException {
    load("family.ltm");

    List result = new ArrayList();
    result.add(getTopicById("bjorg"));
    result.add(getTopicById("gerd"));
    result.add(getTopicById("lmg"));
    result.add(getTopicById("silje"));
    result.add(getTopicById("astri"));
    result.add(getTopicById("lms"));
    
    verifyResult(result, "parenthood(bertha : mother, $MOTHER : child), " +
                         "parenthood($MOTHER : mother, $CHILD : child) " +
                         "order by $MOTHER?");
    closeStore();      
  }

  /// internal

  protected void verifyResult(Collection result, String query)
    throws InvalidQueryException {
    
    Iterator it = new FlatQueryResultIterator(processor.execute(query));
    while (it.hasNext()) {
      Object value = it.next();
      assertTrue("value not found in expected results: " + value,
                 result.contains(value));
      result.remove(value);
    }

    assertTrue("expected values not found: " + result, result.isEmpty());
  }  
  
}
