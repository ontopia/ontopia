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

package net.ontopia.topicmaps.core.index;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.GenericLocator;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import org.apache.commons.collections4.IteratorUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class OccurrenceIndexTest extends AbstractIndexTest {
  protected OccurrenceIndexIF ix;
  
  @Override
  @Before
  public void setUp() throws Exception {
    ix = (OccurrenceIndexIF)super.setUp("OccurrenceIndexIF");
  }

  @Test
  public void testOccurrenceIndex() {
    // STATE 1: No Occurrence locators defined
    TopicIF topic = builder.makeTopic();
    TopicIF otype = builder.makeTopic();
		String value0 = "dummy0";
		String value1 = "dummy";
    OccurrenceIF occ = builder.makeOccurrence(topic, otype, value0);

    Assert.assertTrue("Index of occurrences by value is not empty.", 
							 ix.getOccurrences(value1, DataTypes.TYPE_STRING).isEmpty());

    Assert.assertTrue("Index of occurrences by value is not empty.", 
							 ix.getOccurrences(value1).isEmpty());

    // STATE 2: Occurrence value added
    occ.setValue(value1);
    
    Assert.assertTrue("Index of occurrences by value does not contain test value.",
           ix.getOccurrences(value1, DataTypes.TYPE_STRING).contains(occ));
    Assert.assertTrue("Index of occurrences by value does not contain test value.",
           ix.getOccurrences(value1).contains(occ));

    // STATE 3: Duplicate occurrence value added
    builder.makeOccurrence(topic, otype, value1);

    Assert.assertTrue("second occurrence not found by value",
           ix.getOccurrences(value1, DataTypes.TYPE_STRING).size() == 2);
    Assert.assertTrue("second occurrence not found by value",
           ix.getOccurrences(value1).size() == 2);

    // STATE 4: Change first occurrence value
    String value2 = "dummy2";
    occ.setValue(value2);

    Assert.assertTrue("list of occurrences not updated",
           ix.getOccurrences(value1, DataTypes.TYPE_STRING).size() == 1);
    Assert.assertTrue("list of occurrences not updated",
           ix.getOccurrences(value1).size() == 1);

    Assert.assertTrue("first occurrence not found by new value",
           ix.getOccurrences(value2, DataTypes.TYPE_STRING).size() == 1);
    Assert.assertTrue("first occurrence not found by new value",
           ix.getOccurrences(value2).size() == 1);

  }

  @Test
  public void testOccurrenceIndexByType() {
    // STATE 1: no occurrence values defined
    TopicIF topic = builder.makeTopic();
    TopicIF otype1 = builder.makeTopic();
    TopicIF otype2 = builder.makeTopic();
    String value0 = "dummy0";
    String value1 = "dummy";
    OccurrenceIF occ = builder.makeOccurrence(topic, otype1, value0);
    Assert.assertTrue("Index of occurrences by value and type is not empty.",
      ix.getOccurrences(value1, otype1).isEmpty());
    Assert.assertTrue("Index of occurrences by value and type is not empty.",
      ix.getOccurrences(value1, DataTypes.TYPE_STRING, otype1).isEmpty());

    // STATE 2: Occurrence value added
    occ.setValue(value1);
    Assert.assertTrue("Index of occurrences by value and type does not contain test value.",
      ix.getOccurrences(value1, otype1).contains(occ));
    Assert.assertTrue("Index of occurrences by value and type does not contain test value.",
      ix.getOccurrences(value1, DataTypes.TYPE_STRING, otype1).contains(occ));

    // STATE 3: Duplicate occurrence value added
    OccurrenceIF occ2 = builder.makeOccurrence(topic, otype1, value1);
    Assert.assertTrue("second occurrence not found by value",
      ix.getOccurrences(value1, otype1).size() == 2);
    Assert.assertTrue("second occurrence not found by value",
      ix.getOccurrences(value1, DataTypes.TYPE_STRING, otype1).size() == 2);

    // STATE 4: Change first occurrence value
    String value2 = "dummy2";
    occ.setValue(value2);
    Assert.assertTrue("list of occurrences not updated",
      ix.getOccurrences(value1, otype1).size() == 1);
    Assert.assertTrue("list of occurrences not updated",
      ix.getOccurrences(value1, DataTypes.TYPE_STRING, otype1).size() == 1);
    Assert.assertTrue("first occurrence not found by new value",
      ix.getOccurrences(value2, otype1).size() == 1);
    Assert.assertTrue("first occurrence not found by new value",
      ix.getOccurrences(value2, DataTypes.TYPE_STRING, otype1).size() == 1);

    // STATE 5: Change occurrence types
    Assert.assertTrue("Index of occurrences by value and type is not empty for original type",
      ix.getOccurrences(value2, otype2).isEmpty());
    Assert.assertTrue("Index of occurrences by value and type is not empty for original type",
      ix.getOccurrences(value2, DataTypes.TYPE_STRING, otype2).isEmpty());
    occ.setType(otype2);
    Assert.assertFalse("Index of occurrences by value and type does not detect changed type",
      ix.getOccurrences(value2, otype2).isEmpty());
    Assert.assertFalse("Index of occurrences by value and type does not detect changed type",
      ix.getOccurrences(value2, DataTypes.TYPE_STRING, otype2).isEmpty());
    Assert.assertTrue("Index of occurrences by value and type does not detect aborted type",
      ix.getOccurrences(value2, otype1).isEmpty());
    Assert.assertTrue("Index of occurrences by value and type does not detect aborted type",
      ix.getOccurrences(value2, DataTypes.TYPE_STRING, otype1).isEmpty());

    // STATE 6: Change second occurrence type
    occ2.setType(otype2);
    Assert.assertFalse("Index of occurrences by value and type contains occurrence with wrong value",
      ix.getOccurrences(value2, otype2).contains(occ2));
    Assert.assertFalse("Index of occurrences by value and type contains occurrence with wrong value",
      ix.getOccurrences(value2, DataTypes.TYPE_STRING, otype2).contains(occ2));
  }

  @Test
  public void testOccurrenceIndexByPrefix() {
    // STATE 1: no occurrence values defined
    TopicIF topic = builder.makeTopic();
    TopicIF otype = builder.makeTopic();

    Assert.assertTrue("Index of occurrences by value is not empty.", 
							 ix.getOccurrencesByPrefix("a", DataTypes.TYPE_STRING).isEmpty());
    Assert.assertTrue("Index of occurrences by value is not empty.", 
							 ix.getOccurrencesByPrefix("a").isEmpty());

    // STATE 2: adding occurrences
    builder.makeOccurrence(topic, otype, "a");
    builder.makeOccurrence(topic, otype, "ab");
    OccurrenceIF oabc = builder.makeOccurrence(topic, otype, "abc");
    builder.makeOccurrence(topic, otype, "abcde");
    builder.makeOccurrence(topic, otype, "ac");
    builder.makeOccurrence(topic, otype, "acde");
    builder.makeOccurrence(topic, otype, "y");

    //! Assert.assertTrue("Occurrences by prefix '' does not return 7.", 
    //!       ix.getOccurrencesByPrefix("", DataTypes.TYPE_STRING).size() == 7);

    Assert.assertTrue("Occurrences by prefix 'a' does not return 6.", 
           ix.getOccurrencesByPrefix("a", DataTypes.TYPE_STRING).size() == 6);
    Assert.assertTrue("Occurrences by prefix 'a' does not return 6.", 
           ix.getOccurrencesByPrefix("a").size() == 6);

    Assert.assertTrue("Occurrences by prefix 'ab' does not return 3." + ix.getOccurrencesByPrefix("ab", DataTypes.TYPE_STRING), 
           ix.getOccurrencesByPrefix("ab", DataTypes.TYPE_STRING).size() == 3);
    Assert.assertTrue("Occurrences by prefix 'ab' does not return 3." + ix.getOccurrencesByPrefix("ab", DataTypes.TYPE_STRING), 
           ix.getOccurrencesByPrefix("ab").size() == 3);

    Assert.assertTrue("Occurrences by prefix 'abc' does not return 2.", 
           ix.getOccurrencesByPrefix("abc", DataTypes.TYPE_STRING).size() == 2);
    Assert.assertTrue("Occurrences by prefix 'abc' does not return 2.", 
           ix.getOccurrencesByPrefix("abc").size() == 2);

    Assert.assertTrue("Occurrences by prefix 'abcd' does not return 1.", 
           ix.getOccurrencesByPrefix("abcd", DataTypes.TYPE_STRING).size() == 1);
    Assert.assertTrue("Occurrences by prefix 'abcd' does not return 1.", 
           ix.getOccurrencesByPrefix("abcd").size() == 1);

    Assert.assertTrue("Occurrences by prefix 'abcde' does not return 1.", 
           ix.getOccurrencesByPrefix("abcde", DataTypes.TYPE_STRING).size() == 1);
    Assert.assertTrue("Occurrences by prefix 'abcde' does not return 1.", 
           ix.getOccurrencesByPrefix("abcde").size() == 1);

    Assert.assertTrue("Occurrences by prefix 'abcdef' does not return 0.", 
           ix.getOccurrencesByPrefix("abcdef", DataTypes.TYPE_STRING).size() == 0);
    Assert.assertTrue("Occurrences by prefix 'abcdef' does not return 0.", 
           ix.getOccurrencesByPrefix("abcdef").size() == 0);

    Assert.assertTrue("Occurrences by prefix 'ac' does not return 2.", 
           ix.getOccurrencesByPrefix("ac", DataTypes.TYPE_STRING).size() == 2);
    Assert.assertTrue("Occurrences by prefix 'ac' does not return 2.", 
           ix.getOccurrencesByPrefix("ac").size() == 2);

    Assert.assertTrue("Occurrences by prefix 'acd' does not return 1.", 
           ix.getOccurrencesByPrefix("acd", DataTypes.TYPE_STRING).size() == 1);
    Assert.assertTrue("Occurrences by prefix 'acd' does not return 1.", 
           ix.getOccurrencesByPrefix("acd").size() == 1);

    Assert.assertTrue("Occurrences by prefix 'acde' does not return 1.", 
           ix.getOccurrencesByPrefix("acde", DataTypes.TYPE_STRING).size() == 1);
    Assert.assertTrue("Occurrences by prefix 'acde' does not return 1.", 
           ix.getOccurrencesByPrefix("acde").size() == 1);

    Assert.assertTrue("Occurrences by prefix 'x' does not return 0.", 
           ix.getOccurrencesByPrefix("x", DataTypes.TYPE_STRING).size() == 0);
    Assert.assertTrue("Occurrences by prefix 'x' does not return 0.", 
           ix.getOccurrencesByPrefix("x").size() == 0);

    Assert.assertTrue("Occurrences by prefix 'y' does not return 1.", 
           ix.getOccurrencesByPrefix("y", DataTypes.TYPE_STRING).size() == 1);
    Assert.assertTrue("Occurrences by prefix 'y' does not return 1.", 
           ix.getOccurrencesByPrefix("y").size() == 1);

    // WARNING: "z" does not work correctly with postgresql 7.x as
    // next character is "}".

    // STATE 3: changing one of the occurrences
    oabc.setValue("bcd");

    //! Assert.assertTrue("Occurrences by prefix 'ab' does not return 2.", 
    //!        ix.getOccurrencesByPrefix("ab", DataTypes.TYPE_STRING).size() == 2);

    //! Assert.assertTrue("Occurrences by prefix '' does not return 7.", 
    //!        ix.getOccurrencesByPrefix("", DataTypes.TYPE_STRING).size() == 7);

    //! Assert.assertTrue("Occurrences by prefix '' does not contain 'a'.", 
    //!        ix.getOccurrencesByPrefix("", DataTypes.TYPE_STRING).contains(oa));

    //! Assert.assertTrue("Occurrences by prefix '' does not contain 'acde'.", 
    //!        ix.getOccurrencesByPrefix("", DataTypes.TYPE_STRING).contains(oacde));


  }

  @Test
  public void testOccurrenceIndexLocator() {
    // STATE 1: No Occurrence locators defined
    TopicIF topic = builder.makeTopic();
    TopicIF otype = builder.makeTopic();
    LocatorIF loc1 = URILocator.create("http://www.ontopia.net/test-data/occurrence-locator1.xml");
    LocatorIF loc2 = URILocator.create("http://www.ontopia.net/test-data/occurrence-locator2.xml");

    OccurrenceIF occ = builder.makeOccurrence(topic, otype, loc1);

    Assert.assertTrue("Index of occurrences by locator is not one.", 
							 ix.getOccurrences(loc1.getAddress(), DataTypes.TYPE_URI).size() == 1);
    Assert.assertTrue("Index of occurrences by locator is not empty.", 
							 ix.getOccurrences(loc2.getAddress(), DataTypes.TYPE_URI).isEmpty());

    // STATE 2: Occurrence locator added
    occ.setLocator(loc2);
    
    Assert.assertTrue("Index of occurrences by locator does not contain test value.",
							 ix.getOccurrences(loc2.getAddress(), DataTypes.TYPE_URI).contains(occ));
    Assert.assertTrue("Index of occurrences by locator is not empty.", 
							 ix.getOccurrences(loc1.getAddress(), DataTypes.TYPE_URI).isEmpty());

    // STATE 3: Duplicate occurrence locator added
    builder.makeOccurrence(topic, otype, loc2);

    Assert.assertTrue("second occurrence not found by locator",
							 ix.getOccurrences(loc2.getAddress(), DataTypes.TYPE_URI).size() == 2);

  }

  @Test
  public void testOccurrenceIndexByPrefixLocator() {
    // STATE 1: no occurrence values defined
    TopicIF topic = builder.makeTopic();
    TopicIF otype = builder.makeTopic();
    final String notation = "URI";

    Assert.assertTrue("Index of occurrences by value is not empty.", 
           ix.getOccurrencesByPrefix("a", DataTypes.TYPE_URI).isEmpty());

    // STATE 2: adding occurrences
    builder.makeOccurrence(topic, otype, new GenericLocator(notation, "a"));
    builder.makeOccurrence(topic, otype, new GenericLocator(notation, "ab"));
    OccurrenceIF oabc = builder.makeOccurrence(topic, otype, new GenericLocator(notation, "abc"));
    builder.makeOccurrence(topic, otype, new GenericLocator(notation, "abcde"));
    builder.makeOccurrence(topic, otype, new GenericLocator(notation, "ac"));
    builder.makeOccurrence(topic, otype, new GenericLocator(notation, "acde"));
    builder.makeOccurrence(topic, otype, new GenericLocator(notation, "y"));

    //! Assert.assertTrue("Occurrences by prefix '' does not return 7.", 
    //!        ix.getOccurrencesByPrefix("", DataTypes.TYPE_URI).size() == 7);

    Assert.assertTrue("Occurrences by prefix 'a' does not return 6.", 
           ix.getOccurrencesByPrefix("a", DataTypes.TYPE_URI).size() == 6);

    Assert.assertTrue("Occurrences by prefix 'ab' does not return 3.", 
           ix.getOccurrencesByPrefix("ab", DataTypes.TYPE_URI).size() == 3);

    Assert.assertTrue("Occurrences by prefix 'abc' does not return 2.", 
           ix.getOccurrencesByPrefix("abc", DataTypes.TYPE_URI).size() == 2);

    Assert.assertTrue("Occurrences by prefix 'abcd' does not return 1.", 
           ix.getOccurrencesByPrefix("abcd", DataTypes.TYPE_URI).size() == 1);

    Assert.assertTrue("Occurrences by prefix 'abcde' does not return 1.", 
           ix.getOccurrencesByPrefix("abcde", DataTypes.TYPE_URI).size() == 1);

    Assert.assertTrue("Occurrences by prefix 'abcdef' does not return 0.", 
           ix.getOccurrencesByPrefix("abcdef", DataTypes.TYPE_URI).size() == 0);

    Assert.assertTrue("Occurrences by prefix 'ac' does not return 2.", 
           ix.getOccurrencesByPrefix("ac", DataTypes.TYPE_URI).size() == 2);

    Assert.assertTrue("Occurrences by prefix 'acd' does not return 1.", 
           ix.getOccurrencesByPrefix("acd", DataTypes.TYPE_URI).size() == 1);

    Assert.assertTrue("Occurrences by prefix 'acde' does not return 1.", 
           ix.getOccurrencesByPrefix("acde", DataTypes.TYPE_URI).size() == 1);

    Assert.assertTrue("Occurrences by prefix 'x' does not return 0.", 
           ix.getOccurrencesByPrefix("x", DataTypes.TYPE_URI).size() == 0);

    Assert.assertTrue("Occurrences by prefix 'y' does not return 1.", 
           ix.getOccurrencesByPrefix("y", DataTypes.TYPE_URI).size() == 1);

    // STATE 3: changing one of the occurrences
    oabc.setLocator(new GenericLocator(notation, "bcd"));

    Assert.assertTrue("Occurrences by prefix 'ab' does not return 2.", 
           ix.getOccurrencesByPrefix("ab", DataTypes.TYPE_URI).size() == 2);

    //! Assert.assertTrue("Occurrences by prefix '' does not return 7.", 
    //!        ix.getOccurrencesByPrefix("", DataTypes.TYPE_URI).size() == 7);

    //! Assert.assertTrue("Occurrences by prefix '' does not contain 'a'.", 
    //!        ix.getOccurrencesByPrefix("", DataTypes.TYPE_URI).contains(oa));

    //! Assert.assertTrue("Occurrences by prefix '' does not contain 'acde'.", 
    //!        ix.getOccurrencesByPrefix("", DataTypes.TYPE_URI).contains(oacde));
  }

  @Test
  public void testNullParameters() {
    Collection<OccurrenceIF> occurrences = ix.getOccurrences(null);
    Assert.assertNotNull(occurrences);
    Assert.assertTrue(occurrences.isEmpty());
    
    occurrences = ix.getOccurrences(null, (LocatorIF) null);
    Assert.assertNotNull(occurrences);
    Assert.assertTrue(occurrences.isEmpty());

    occurrences = ix.getOccurrences(null, (TopicIF) null);
    Assert.assertNotNull(occurrences);
    Assert.assertTrue(occurrences.isEmpty());
    
    occurrences = ix.getOccurrences(null, null, null);
    Assert.assertNotNull(occurrences);
    Assert.assertTrue(occurrences.isEmpty());
    
    occurrences = ix.getOccurrencesByPrefix(null);
    Assert.assertNotNull(occurrences);
    Assert.assertTrue(occurrences.isEmpty());
    
    occurrences = ix.getOccurrencesByPrefix(null, null);
    Assert.assertNotNull(occurrences);
    Assert.assertTrue(occurrences.isEmpty());
    
    Iterator<String> strings = ix.getValuesGreaterThanOrEqual(null);
    Assert.assertNotNull(strings);
    Assert.assertFalse(strings.hasNext());
    
    strings = ix.getValuesSmallerThanOrEqual(null);
    Assert.assertNotNull(strings);
    Assert.assertFalse(strings.hasNext());
  }
  
  @Test
  public void testValuesGreaterThanOrEqual() {
    Assert.assertFalse(ix.getValuesGreaterThanOrEqual("").hasNext());
    
    builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "a");
    builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "b");
    builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "c");
    
    Iterator<String> it = ix.getValuesGreaterThanOrEqual("a");
    Assert.assertTrue(it.hasNext());
    Assert.assertEquals("a", it.next());
    Assert.assertEquals("b", it.next());
    Assert.assertEquals("c", it.next());
    Assert.assertFalse(it.hasNext());

    it = ix.getValuesGreaterThanOrEqual("c");
    Assert.assertTrue(it.hasNext());
    Assert.assertEquals("c", it.next());
    Assert.assertFalse(it.hasNext());
  }

  // not using sorting, until #537 is fixed
  @Test
  public void testValuesSmallerThanOrEqual() {
    Assert.assertFalse(ix.getValuesSmallerThanOrEqual("").hasNext());
    
    builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "a");
    builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "b");
    builder.makeOccurrence(builder.makeTopic(), builder.makeTopic(), "c");
    
    List<String> values = IteratorUtils.toList(ix.getValuesSmallerThanOrEqual("c"));
    
    Assert.assertEquals(3, values.size());
    Assert.assertTrue(values.contains("a"));
    Assert.assertTrue(values.contains("b"));
    Assert.assertTrue(values.contains("c"));

    values = IteratorUtils.toList(ix.getValuesSmallerThanOrEqual("a"));
    Assert.assertEquals(1, values.size());
    Assert.assertTrue(values.contains("a"));
  }

    @Test
    public void testOccurrences() {
        URILocator loc1 = URILocator.create("http://www.ontopia.net");
        URILocator loc2 = URILocator.create("ftp://sandbox.ontopia.net");

        // STATE 1: empty topic map
        // Assert.assertTrue("index finds spurious occurrence locators",
        //        ix.getOccurrenceLocators().size() == 0);

        Assert.assertTrue("index finds occurrences it shouldn't",
									 ix.getOccurrences(loc1.getAddress(), DataTypes.TYPE_URI).size() == 0);

        
        // STATE 2: topic map has some topics in it
        TopicIF t1 = builder.makeTopic();
        TopicIF otype = builder.makeTopic();
        OccurrenceIF o1 = builder.makeOccurrence(t1, otype, loc1);
        builder.makeOccurrence(t1, otype, "");
        
        // Assert.assertTrue("occurrence locator not found via locator",
        //        ix.getOccurrenceLocators().size() == 2);
        // Assert.assertTrue("occurrence locator identity lost",
        //        ix.getOccurrenceLocators().contains(loc1));
        // Assert.assertTrue("null locator not found",
        //        ix.getOccurrenceLocators().contains(null));
        Assert.assertTrue("occurrence not found via locator",
									 ix.getOccurrences(loc1.getAddress(), DataTypes.TYPE_URI).size() == 1);
        Assert.assertTrue("wrong occurrence found via locator",
									 ix.getOccurrences(loc1.getAddress(), DataTypes.TYPE_URI).iterator().next().equals(o1));

        Assert.assertTrue("spurious occurrence found via locator",
									 ix.getOccurrences(loc2.getAddress(), DataTypes.TYPE_URI).size() == 0);
        
        // STATE 3: topic map with duplicates
        builder.makeOccurrence(t1, otype, loc1);
        
        // Assert.assertTrue("duplicate occurrence locator not filtered out",
        //        ix.getOccurrenceLocators().size() == 2);
        Assert.assertTrue("second occurrence not found via locator",
									 ix.getOccurrences(loc1.getAddress(), DataTypes.TYPE_URI).size() == 2);
    }
  
}
