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

public class DatatypePredicateTest extends AbstractPredicateTest {
  private static final String XTM_URITYPE = "http://www.w3.org/2001/XMLSchema#anyURI";
  private static final String XTM_STRINGTYPE = "http://www.w3.org/2001/XMLSchema#string";
  
  public DatatypePredicateTest(String name) {
    super(name);
  }

  @Override
  public void tearDown() {
    closeStore();
  }

  /// tests

  public void testWithSpecificOcc() throws InvalidQueryException, IOException {
    load("int-occs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "DT", XTM_STRINGTYPE);
    
    verifyQuery(matches,
                "select $DT from " +
                "  occurrence(topic1, $OCC), " +
                "  datatype($OCC, $DT)?");
  }  

  public void testWithSpecificOcc2() throws InvalidQueryException, IOException {
    load("ext-occs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "DT", XTM_URITYPE);
    
    verifyQuery(matches,
                "select $DT from " +
                "  occurrence(topic1, $OCC), " +
                "  datatype($OCC, $DT)?");
  }

  public void testWithSpecificWrongType()
    throws InvalidQueryException, IOException {
    load("int-occs.ltm");
                
    findNothing("$A = 2, " +
                "datatype($A, $OCC)?");
  }    

  public void testWithSpecificWrongType2()
    throws InvalidQueryException, IOException {
    load("int-occs.ltm");
                
    getParseError("datatype(2, $OCC)?");
  }    
  
  public void testFilterWithSpecificType()
    throws InvalidQueryException, IOException {
    load("uc-literature.xtm");

    List matches = new ArrayList();
    TopicIF paper = getTopicById("d-topicmaps-color");
    Iterator it = paper.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occ = (OccurrenceIF) it.next();
      if (occ.getLocator() == null)
        addMatch(matches, "OCC", occ);
    }
    
    verifyQuery(matches,
                "occurrence(d-topicmaps-color, $OCC), " +
                "datatype($OCC, \"http://www.w3.org/2001/XMLSchema#string\")?");
  }

  public void testFilterWithSpecificType2()
    throws InvalidQueryException, IOException {
    load("uc-literature.xtm");

    List matches = new ArrayList();
    TopicIF paper = getTopicById("d-topicmaps-color");
    Iterator it = paper.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occ = (OccurrenceIF) it.next();
      if (occ.getLocator() != null)
        addMatch(matches, "OCC", occ);
    }
    
    verifyQuery(matches,
                "occurrence(d-topicmaps-color, $OCC), " +
                "datatype($OCC, \"http://www.w3.org/2001/XMLSchema#anyURI\")?");
  }
  
  public void testFilterByWrongType() throws InvalidQueryException, IOException {
    load("uc-literature.xtm");
                
    findNothing("$DT = 2, " +
                "occurrence(d-topicmaps-color, $OCC), " +
                "datatype($OCC, $DT)?");
  }  

  public void testFilterByWrongType2()
    throws InvalidQueryException, IOException {
    load("uc-literature.xtm");
                
    getParseError("occurrence(d-topicmaps-color, $OCC), " +
                  "datatype($OCC, 2)?");
  }  
  
  public void testLookupByType()
    throws InvalidQueryException, IOException {
    load("int-occs.ltm");

    List matches = getByType(XTM_STRINGTYPE);   
    verifyQuery(matches,
                "datatype($OCC, \"http://www.w3.org/2001/XMLSchema#string\")?");
  }

  public void testLookupByType2()
    throws InvalidQueryException, IOException {
    load("int-occs.ltm");

    findNothing("datatype($OCC, \"http://www.w3.org/2001/XMLSchema#anyURI\")?");
  }

  public void testLookupByType3()
    throws InvalidQueryException, IOException {
    load("ext-occs.ltm");

    findNothing("datatype($OCC, \"http://www.w3.org/2001/XMLSchema#string\")?");
  }

  public void testLookupByType4()
    throws InvalidQueryException, IOException {
    load("ext-occs.ltm");

    List matches = getByType(XTM_URITYPE);
    verifyQuery(matches,
                "datatype($OCC, \"http://www.w3.org/2001/XMLSchema#anyURI\")?");
  }

  public void testLookupByWrongType() throws InvalidQueryException, IOException {
    load("ext-occs.ltm");

    findNothing("$DT = 2, " +
                "datatype($OCC, $DT)?");
  }

  public void testLookupByWrongType2()
    throws InvalidQueryException, IOException {
    load("ext-occs.ltm");

    getParseError("datatype($OCC, 2)?");
  }
  
  public void testProduceAll()
    throws InvalidQueryException, IOException {
    load("int-occs.ltm");

    verifyQuery(getAll(), "datatype($OCC, $DT)?");
  }

  public void testProduceAll2() throws InvalidQueryException, IOException {
    load("ext-occs.ltm");

    verifyQuery(getAll(), "datatype($OCC, $DT)?");
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
        if (occ.getDataType().getAddress().equals(datatype))
          addMatch(matches, "OCC", occ);
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
