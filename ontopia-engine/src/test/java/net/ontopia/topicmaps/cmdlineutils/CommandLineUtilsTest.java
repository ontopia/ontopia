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

package net.ontopia.topicmaps.cmdlineutils;

import java.util.Collection;
import java.util.HashMap;
import net.ontopia.topicmaps.cmdlineutils.sanity.AssociationSanity;
import net.ontopia.topicmaps.cmdlineutils.sanity.DuplicateNames;
import net.ontopia.topicmaps.cmdlineutils.sanity.DuplicateOccurrences;
import net.ontopia.topicmaps.cmdlineutils.sanity.NoNames;
import net.ontopia.topicmaps.cmdlineutils.statistics.NoTypeCount;
import net.ontopia.topicmaps.cmdlineutils.statistics.TopicAssocDep;
import net.ontopia.topicmaps.cmdlineutils.statistics.TopicCounter;
import net.ontopia.topicmaps.core.TopicMapIF;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class CommandLineUtilsTest {

  public static TopicMapIF tm;


  @Before
  public abstract void setUp();

  @After
  public abstract void tearDown();

  /**
   * **********************************************
   * methods used to test the statistics printer.
   * **********************************************
   */

  @Test
  public void testTopicCounter() {
    TopicCounter test = new TopicCounter(tm);
    

    try {
      test.count();
    } catch (NullPointerException e) {
      Assert.fail("Cought an \"unexpected\" null pointer exception");
    }
    
    Assert.assertTrue(test != null);

    //Type checking

    Assert.assertTrue("check (getTopicTypes)", test.getTopicTypes() instanceof HashMap); 
    Assert.assertTrue("check (getAssociationTypes)", 
           test.getAssociationTypes() instanceof HashMap); 
    Assert.assertTrue("check (getOccurrenceTypes)", 
           test.getOccurrenceTypes() instanceof HashMap); 

    //Test the "normal" operations using a spesial designed topicmap.

    Assert.assertTrue("checking (getnumberOfTopics)", 
           test.getNumberOfTopics() == tm.getTopics().size());
    //note: comparing with previous version:
    //      +1 because names must have a type (thus a typing topic)
    Assert.assertEquals(15, test.getNumberOfTopics());

    Assert.assertTrue("check (getNumberOfAssociations)", 
           test.getNumberOfAssociations() == tm.getAssociations().size());
    Assert.assertEquals(3, test.getNumberOfAssociations());
    
    Assert.assertEquals(3, test.getNumberOfOccurrences());  

    Assert.assertTrue("Variable numberOfOccurrences not null", 
           test.getNumberOfOccurrences() != 0);
    Assert.assertTrue("Variable numberOfAssociations not null", 
           test.getNumberOfAssociations() != 0);
    Assert.assertTrue("Variable numberOfOcurrences not null", 
           test.getNumberOfOccurrences() != 0);


    //Test the "irregular" operations.
    
    test = new TopicCounter(null);

    try {
      test.count();
      Assert.fail("Should raise a NullPointerException");
    } catch (NullPointerException e){
      //Test succesfull
    }

    Assert.assertEquals(0, test.getNumberOfTopics());
    Assert.assertEquals(0, test.getNumberOfAssociations());
    Assert.assertEquals(0, test.getNumberOfOccurrences());

    
  }

  @Test
  public void testTopicAssocDep() {

    TopicAssocDep test = new TopicAssocDep(tm);

    //Type checking
    
    Assert.assertTrue("check type (getAssociations()) with 'foo'", 
           test.getAssociations() instanceof Collection);

    try {
      test = new TopicAssocDep(null);
      // test.getAssociationDependencies(); // traverse() is now executed in constructor
      Assert.fail("Should raise a NullPointerException");
    } catch (NullPointerException e) {
      //Test succesfull
    }

  }

  @Test
  public void testNoTypeCount() {
    
    NoTypeCount test = new NoTypeCount(tm);
    test.traverse();
    //Type checking.
    Assert.assertTrue("check type (getNoTypeTopics)", 
           test.getNoTypeTopics() instanceof Collection);

    // note: comparing with previous version:
    //       +1 because names must have a type since 5.1.0 and this type topic has no type
    Assert.assertEquals(11, test.getNoTypeTopics().size());

    Assert.assertTrue("check type (getNoTypeAssociations)", 
           test.getNoTypeAssociations() instanceof Collection);

    Assert.assertEquals(0, test.getNoTypeAssociations().size());

    Assert.assertTrue("check type (getNoTypeOccurrences)", 
           test.getNoTypeOccurrences() instanceof Collection);

    Assert.assertEquals(0, test.getNoTypeOccurrences().size());

    
    //set test.tm = null
    test = new NoTypeCount(null);
    try {
      test.traverse();
      Assert.fail("Should raise a NullPointerException");
    } catch (NullPointerException e) {
      //Test succesfull
    }

  }


  /**
   * *******************************************
   * methods used to test the sanity checker.
   * *******************************************
   */

  @Test
  public void testDuplicateAssociations() {
    
    AssociationSanity test = new AssociationSanity(tm);

    test.traverse();

    Assert.assertTrue("type checking (getDuplicateAssociations)", 
           test.getDuplicateAssociations() instanceof HashMap);
    Assert.assertTrue("type checking (getNumberOfDuplicates)", 
           test.getNumberOfDuplicates() instanceof HashMap);
    
    Assert.assertEquals(1, test.getDuplicateAssociations().size());
  }

  @Test
  public void testNoNameTopics() {
    
    NoNames test = new NoNames(tm);

    test.findNoNameTopics();


    //Type checking

    Assert.assertTrue("type checking (getNoNameTopics)", 
           test.getNoNameTopics() instanceof Collection);

    Assert.assertTrue("type checking (getNoCharacteristics)", 
           test.getNoCharacteristics() instanceof Collection);

    Assert.assertTrue("type checking (getNoNameUnconstrained)", 
           test.getNoNameUnconstrained() instanceof Collection);


    Assert.assertEquals(2, test.getNoNameTopics().size());
    Assert.assertEquals(2, test.getNoCharacteristics().size());
    //! Assert.assertEquals(8, test.getNoNameUnconstrained().size());

  }

  @Test
  public void testDuplicateOccurrences() {
    
    DuplicateOccurrences test = new DuplicateOccurrences(tm);
    
    Assert.assertTrue("type checking (getDuplicateOccurrences)",
           test.getDuplicateOccurrences() instanceof Collection);
    
    Assert.assertEquals(1, test.getDuplicateOccurrences().size());

  }

  @Test
  public void testDuplicateNames() {

    DuplicateNames test = new DuplicateNames(tm);

    Assert.assertTrue("type checking (getDuplicateNames)",
           test.getDuplicatedNames() instanceof Collection);

    Assert.assertEquals(0, test.getDuplicatedNames().size());

  }
}
