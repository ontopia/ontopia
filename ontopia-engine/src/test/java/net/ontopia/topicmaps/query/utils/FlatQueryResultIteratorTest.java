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

package net.ontopia.topicmaps.query.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.ontopia.topicmaps.query.core.AbstractQueryTest;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import org.junit.Assert;
import org.junit.Test;

public class FlatQueryResultIteratorTest extends AbstractQueryTest {
  
  /// tests

  @Test
  public void testNoResults() throws InvalidQueryException, IOException {
    load("family.ltm");
    assertResult(Collections.EMPTY_SET, "instance-of($PA, mother)?");    
    closeStore();   
  }

  @Test
  public void testSingleRowOfResults() throws InvalidQueryException, IOException {
    load("family.ltm");

    List result = new ArrayList();
    result.add(getTopicById("edvin"));
    result.add(getTopicById("kfg"));
    result.add(getTopicById("petter"));
    result.add(getTopicById("asle"));
    result.add(getTopicById("magnus"));
    result.add(getTopicById("unknown2"));
    
    assertResult(result, "instance-of($PA, father)?");    
    closeStore();      
  }

  @Test
  public void testTwoRowsOfResults() throws InvalidQueryException, IOException {
    load("family.ltm");

    List result = new ArrayList();
    result.add(getTopicById("bjorg"));
    result.add(getTopicById("gerd"));
    result.add(getTopicById("lmg"));
    result.add(getTopicById("silje"));
    result.add(getTopicById("astri"));
    result.add(getTopicById("lms"));
    
    assertResult(result, "parenthood(bertha : mother, $MOTHER : child), " +
                         "parenthood($MOTHER : mother, $CHILD : child) " +
                         "order by $MOTHER?");
    closeStore();      
  }

  /// internal

  protected void assertResult(Collection result, String query)
    throws InvalidQueryException {
    
    Iterator it = new FlatQueryResultIterator(processor.execute(query));
    while (it.hasNext()) {
      Object value = it.next();
      Assert.assertTrue("value not found in expected results: " + value,
                 result.contains(value));
      result.remove(value);
    }

    Assert.assertTrue("expected values not found: " + result, result.isEmpty());
  }  
  
}
