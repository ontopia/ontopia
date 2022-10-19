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
import java.util.List;
import org.junit.Test;

public class ComparisonPredicateTests extends AbstractPredicateTest {
  
  /// tests

  @Test
  public void testGreaterThan() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "V", "WRONG WRONG WRONG");
    addMatch(matches, "V", "Unknown 1");
    addMatch(matches, "V", "Unknown 2");
    addMatch(matches, "V", "Trygve Garshol");
    
    assertQueryMatches(matches, "select $V from topic-name($T, $BN), value($BN, $V), $V > \"Tr\"?");
  }

  @Test
  public void testLessThan() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "V", "Asle Skalle");
    addMatch(matches, "V", "Astri England Garshol");
    addMatch(matches, "V", "Bertha England");
    
    assertQueryMatches(matches, "select $V from topic-name($T, $BN), value($BN, $V), $V < \"Bh\"?");
  }

  @Test
  public void testLessThanEqual() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "V", "Asle Skalle");
    addMatch(matches, "V", "Astri England Garshol");
    addMatch(matches, "V", "Bertha England");
    addMatch(matches, "V", "Bj\u00F8rg England");
    
    assertQueryMatches(matches, "select $V from topic-name($T, $BN), value($BN, $V), $V <= \"Bj\u00F8rg England\"?");
  }

  @Test
  public void testGreaterThanEqual() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "V", "WRONG WRONG WRONG");
    addMatch(matches, "V", "Unknown 1");
    addMatch(matches, "V", "Unknown 2");
    addMatch(matches, "V", "Trygve Garshol");
    
    assertQueryMatches(matches, "select $V from topic-name($T, $BN), value($BN, $V), $V >= \"Trygve Garshol\"?");
  }

  @Test
  public void testBetween1() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "V", "Kjellaug Garshol");
    addMatch(matches, "V", "Knut Garshol");
    addMatch(matches, "V", "Lars Marius Garshol");
    addMatch(matches, "V", "Lars Magne Skalle");
    addMatch(matches, "V", "Magnus England");
    addMatch(matches, "V", "May Stenersen");
    
    assertQueryMatches(matches, "select $V from topic-name($T, $BN), value($BN, $V), $V > \"K\", $V < \"N\"?");
  }

  @Test
  public void testBetween2() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "V", "Kjellaug Garshol");
    addMatch(matches, "V", "Knut Garshol");
    addMatch(matches, "V", "Lars Marius Garshol");
    addMatch(matches, "V", "Lars Magne Skalle");
    addMatch(matches, "V", "Magnus England");
    addMatch(matches, "V", "May Stenersen");
    assertQueryMatches(matches, "select $V from topic-name($T, $BN), value($BN, $V), $V >= \"Kjellaug Garshol\", $V <= \"May Stenersen\"?");
  }

  @Test
  public void testBug2123() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", "Asle Skalle");
		assertQueryMatches(matches, "select $N from instance-of($T, father), topic-name($T, $TN), value($TN, $N), { age($T, $AGE) }, $AGE > \"11\"?");
  }

}
