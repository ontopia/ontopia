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
import java.util.Iterator;
import java.util.List;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import org.junit.Test;

public class DatatypePredicateTest extends AbstractPredicateTest {
  private static final String XTM_URITYPE = "http://www.w3.org/2001/XMLSchema#anyURI";
  private static final String XTM_STRINGTYPE = "http://www.w3.org/2001/XMLSchema#string";
  
  /// tests

  @Test
  public void testWithSpecificOcc() throws InvalidQueryException, IOException {
    load("int-occs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "DT", XTM_STRINGTYPE);
    
    assertQueryMatches(matches,
                "select $DT from " +
                "  occurrence(topic1, $OCC), " +
                "  datatype($OCC, $DT)?");
  }  

  @Test
  public void testWithSpecificOcc2() throws InvalidQueryException, IOException {
    load("ext-occs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "DT", XTM_URITYPE);
    
    assertQueryMatches(matches,
                "select $DT from " +
                "  occurrence(topic1, $OCC), " +
                "  datatype($OCC, $DT)?");
  }

  @Test
  public void testWithSpecificWrongType()
    throws InvalidQueryException, IOException {
    load("int-occs.ltm");
                
    assertFindNothing("$A = 2, " +
                "datatype($A, $OCC)?");
  }    

  @Test
  public void testWithSpecificWrongType2()
    throws InvalidQueryException, IOException {
    load("int-occs.ltm");
                
    assertGetParseError("datatype(2, $OCC)?");
  }    
  
  @Test
  public void testFilterWithSpecificType()
    throws InvalidQueryException, IOException {
    load("uc-literature.xtm");

    List matches = new ArrayList();
    TopicIF paper = getTopicById("d-topicmaps-color");
    Iterator it = paper.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occ = (OccurrenceIF) it.next();
      if (occ.getLocator() == null) {
        addMatch(matches, "OCC", occ);
      }
    }
    
    assertQueryMatches(matches,
                "occurrence(d-topicmaps-color, $OCC), " +
                "datatype($OCC, \"http://www.w3.org/2001/XMLSchema#string\")?");
  }

  @Test
  public void testFilterWithSpecificType2()
    throws InvalidQueryException, IOException {
    load("uc-literature.xtm");

    List matches = new ArrayList();
    TopicIF paper = getTopicById("d-topicmaps-color");
    Iterator it = paper.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occ = (OccurrenceIF) it.next();
      if (occ.getLocator() != null) {
        addMatch(matches, "OCC", occ);
      }
    }
    
    assertQueryMatches(matches,
                "occurrence(d-topicmaps-color, $OCC), " +
                "datatype($OCC, \"http://www.w3.org/2001/XMLSchema#anyURI\")?");
  }
  
  @Test
  public void testFilterByWrongType() throws InvalidQueryException, IOException {
    load("uc-literature.xtm");
                
    assertFindNothing("$DT = 2, " +
                "occurrence(d-topicmaps-color, $OCC), " +
                "datatype($OCC, $DT)?");
  }  

  @Test
  public void testFilterByWrongType2()
    throws InvalidQueryException, IOException {
    load("uc-literature.xtm");
                
    assertGetParseError("occurrence(d-topicmaps-color, $OCC), " +
                  "datatype($OCC, 2)?");
  }  
  
  @Test
  public void testLookupByType()
    throws InvalidQueryException, IOException {
    load("int-occs.ltm");

    List matches = getByType(XTM_STRINGTYPE);   
    assertQueryMatches(matches,
                "datatype($OCC, \"http://www.w3.org/2001/XMLSchema#string\")?");
  }

  @Test
  public void testLookupByType2()
    throws InvalidQueryException, IOException {
    load("int-occs.ltm");

    assertFindNothing("datatype($OCC, \"http://www.w3.org/2001/XMLSchema#anyURI\")?");
  }

  @Test
  public void testLookupByType3()
    throws InvalidQueryException, IOException {
    load("ext-occs.ltm");

    assertFindNothing("datatype($OCC, \"http://www.w3.org/2001/XMLSchema#string\")?");
  }

  @Test
  public void testLookupByType4()
    throws InvalidQueryException, IOException {
    load("ext-occs.ltm");

    List matches = getByType(XTM_URITYPE);
    assertQueryMatches(matches,
                "datatype($OCC, \"http://www.w3.org/2001/XMLSchema#anyURI\")?");
  }

  @Test
  public void testLookupByWrongType() throws InvalidQueryException, IOException {
    load("ext-occs.ltm");

    assertFindNothing("$DT = 2, " +
                "datatype($OCC, $DT)?");
  }

  @Test
  public void testLookupByWrongType2()
    throws InvalidQueryException, IOException {
    load("ext-occs.ltm");

    assertGetParseError("datatype($OCC, 2)?");
  }
  
  @Test
  public void testProduceAll()
    throws InvalidQueryException, IOException {
    load("int-occs.ltm");

    assertQueryMatches(getAll(), "datatype($OCC, $DT)?");
  }

  @Test
  public void testProduceAll2() throws InvalidQueryException, IOException {
    load("ext-occs.ltm");

    assertQueryMatches(getAll(), "datatype($OCC, $DT)?");
  }
  
  // MISSING TESTS:
  //  - variant names

  /// utilities

  private List getByType(String datatype) {
    List matches = new ArrayList();
    Iterator it = topicmap.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      Iterator it2 = topic.getOccurrences().iterator();
      while (it2.hasNext()) {
        OccurrenceIF occ = (OccurrenceIF) it2.next();
        if (occ.getDataType().getAddress().equals(datatype)) {
          addMatch(matches, "OCC", occ);
        }
      }
    }
    return matches;
  }

  private List getAll() {
    List matches = new ArrayList();
    Iterator it = topicmap.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      Iterator it2 = topic.getOccurrences().iterator();
      while (it2.hasNext()) {
        OccurrenceIF occ = (OccurrenceIF) it2.next();
        addMatch(matches, "OCC", occ, "DT", occ.getDataType().getAddress());
      }
    }
    return matches;
  }
}
