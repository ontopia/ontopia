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

import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.infoset.impl.basic.GenericLocator;

public abstract class OccurrenceIndexTest extends AbstractIndexTest {
  protected OccurrenceIndexIF ix;
  
  public OccurrenceIndexTest(String name) {
    super(name);
  }

  @Override
  protected void setUp() throws Exception {
    ix = (OccurrenceIndexIF)super.setUp("OccurrenceIndexIF");
  }

  public void testOccurrenceIndex() {
    // STATE 1: No Occurrence locators defined
    TopicIF topic = builder.makeTopic();
    TopicIF otype = builder.makeTopic();
		String value0 = "dummy0";
		String value1 = "dummy";
    OccurrenceIF occ = builder.makeOccurrence(topic, otype, value0);

    assertTrue("Index of occurrences by value is not empty.", 
							 ix.getOccurrences(value1, DataTypes.TYPE_STRING).isEmpty());

    // STATE 2: Occurrence value added
    occ.setValue(value1);
    
    assertTrue("Index of occurrences by value does not contain test value.",
           ix.getOccurrences(value1, DataTypes.TYPE_STRING).contains(occ));

    // STATE 3: Duplicate occurrence value added
    OccurrenceIF occ2 = builder.makeOccurrence(topic, otype, value1);

    assertTrue("second occurrence not found by value",
           ix.getOccurrences(value1, DataTypes.TYPE_STRING).size() == 2);

    // STATE 4: Change first occurrence value
    String value2 = "dummy2";
    occ.setValue(value2);

    assertTrue("list of occurrences not updated",
           ix.getOccurrences(value1, DataTypes.TYPE_STRING).size() == 1);

    assertTrue("first occurrence not found by new value",
           ix.getOccurrences(value2, DataTypes.TYPE_STRING).size() == 1);

  }

  public void testOccurrenceIndexByType() {
    // STATE 1: no occurrence values defined
    TopicIF topic = builder.makeTopic();
    TopicIF otype1 = builder.makeTopic();
    TopicIF otype2 = builder.makeTopic();
    String value0 = "dummy0";
    String value1 = "dummy";
    OccurrenceIF occ = builder.makeOccurrence(topic, otype1, value0);
    assertTrue("Index of occurrences by value and type is not empty.",
      ix.getOccurrences(value1, otype1).isEmpty());

    // STATE 2: Occurrence value added
    occ.setValue(value1);
    assertTrue("Index of occurrences by value and type does not contain test value.",
      ix.getOccurrences(value1, otype1).contains(occ));

    // STATE 3: Duplicate occurrence value added
    OccurrenceIF occ2 = builder.makeOccurrence(topic, otype1, value1);
    assertTrue("second occurrence not found by value",
      ix.getOccurrences(value1, otype1).size() == 2);

    // STATE 4: Change first occurrence value
    String value2 = "dummy2";
    occ.setValue(value2);
    assertTrue("list of occurrences not updated",
      ix.getOccurrences(value1, otype1).size() == 1);
    assertTrue("first occurrence not found by new value",
      ix.getOccurrences(value2, otype1).size() == 1);

    // STATE 5: Change occurrence types
    assertTrue("Index of occurrences by value and type is not empty for original type",
      ix.getOccurrences(value2, otype2).isEmpty());
    occ.setType(otype2);
    assertFalse("Index of occurrences by value and type does not detect changed type",
      ix.getOccurrences(value2, otype2).isEmpty());
    assertTrue("Index of occurrences by value and type does not detect aborted type",
      ix.getOccurrences(value2, otype1).isEmpty());

    // STATE 6: Change second occurrence type
    occ2.setType(otype2);
    assertFalse("Index of occurrences by value and type contains occurrence with wrong value",
      ix.getOccurrences(value2, otype2).contains(occ2));
  }

  public void testOccurrenceIndexByPrefix() {
    // STATE 1: no occurrence values defined
    TopicIF topic = builder.makeTopic();
    TopicIF otype = builder.makeTopic();

    assertTrue("Index of occurrences by value is not empty.", 
							 ix.getOccurrencesByPrefix("a", DataTypes.TYPE_STRING).isEmpty());

    // STATE 2: adding occurrences
    OccurrenceIF oa = builder.makeOccurrence(topic, otype, "a");
    OccurrenceIF oab = builder.makeOccurrence(topic, otype, "ab");
    OccurrenceIF oabc = builder.makeOccurrence(topic, otype, "abc");
    OccurrenceIF oabcde = builder.makeOccurrence(topic, otype, "abcde");
    OccurrenceIF oac = builder.makeOccurrence(topic, otype, "ac");
    OccurrenceIF oacde = builder.makeOccurrence(topic, otype, "acde");
    OccurrenceIF oz = builder.makeOccurrence(topic, otype, "y");

    //! assertTrue("Occurrences by prefix '' does not return 7.", 
    //!       ix.getOccurrencesByPrefix("", DataTypes.TYPE_STRING).size() == 7);

    assertTrue("Occurrences by prefix 'a' does not return 6.", 
           ix.getOccurrencesByPrefix("a", DataTypes.TYPE_STRING).size() == 6);

    assertTrue("Occurrences by prefix 'ab' does not return 3." + ix.getOccurrencesByPrefix("ab", DataTypes.TYPE_STRING), 
           ix.getOccurrencesByPrefix("ab", DataTypes.TYPE_STRING).size() == 3);

    assertTrue("Occurrences by prefix 'abc' does not return 2.", 
           ix.getOccurrencesByPrefix("abc", DataTypes.TYPE_STRING).size() == 2);

    assertTrue("Occurrences by prefix 'abcd' does not return 1.", 
           ix.getOccurrencesByPrefix("abcd", DataTypes.TYPE_STRING).size() == 1);

    assertTrue("Occurrences by prefix 'abcde' does not return 1.", 
           ix.getOccurrencesByPrefix("abcde", DataTypes.TYPE_STRING).size() == 1);

    assertTrue("Occurrences by prefix 'abcdef' does not return 0.", 
           ix.getOccurrencesByPrefix("abcdef", DataTypes.TYPE_STRING).size() == 0);

    assertTrue("Occurrences by prefix 'ac' does not return 2.", 
           ix.getOccurrencesByPrefix("ac", DataTypes.TYPE_STRING).size() == 2);

    assertTrue("Occurrences by prefix 'acd' does not return 1.", 
           ix.getOccurrencesByPrefix("acd", DataTypes.TYPE_STRING).size() == 1);

    assertTrue("Occurrences by prefix 'acde' does not return 1.", 
           ix.getOccurrencesByPrefix("acde", DataTypes.TYPE_STRING).size() == 1);

    assertTrue("Occurrences by prefix 'x' does not return 0.", 
           ix.getOccurrencesByPrefix("x", DataTypes.TYPE_STRING).size() == 0);

    assertTrue("Occurrences by prefix 'y' does not return 1.", 
           ix.getOccurrencesByPrefix("y", DataTypes.TYPE_STRING).size() == 1);

    // WARNING: "z" does not work correctly with postgresql 7.x as
    // next character is "}".

    // STATE 3: changing one of the occurrences
    oabc.setValue("bcd");

    //! assertTrue("Occurrences by prefix 'ab' does not return 2.", 
    //!        ix.getOccurrencesByPrefix("ab", DataTypes.TYPE_STRING).size() == 2);

    //! assertTrue("Occurrences by prefix '' does not return 7.", 
    //!        ix.getOccurrencesByPrefix("", DataTypes.TYPE_STRING).size() == 7);

    //! assertTrue("Occurrences by prefix '' does not contain 'a'.", 
    //!        ix.getOccurrencesByPrefix("", DataTypes.TYPE_STRING).contains(oa));

    //! assertTrue("Occurrences by prefix '' does not contain 'acde'.", 
    //!        ix.getOccurrencesByPrefix("", DataTypes.TYPE_STRING).contains(oacde));


  }

  public void testOccurrenceIndexLocator() {
    // STATE 1: No Occurrence locators defined
    TopicIF topic = builder.makeTopic();
    TopicIF otype = builder.makeTopic();
    LocatorIF loc1 = URILocator.create("http://www.ontopia.net/test-data/occurrence-locator1.xml");
    LocatorIF loc2 = URILocator.create("http://www.ontopia.net/test-data/occurrence-locator2.xml");

    OccurrenceIF occ = builder.makeOccurrence(topic, otype, loc1);

    assertTrue("Index of occurrences by locator is not one.", 
							 ix.getOccurrences(loc1.getAddress(), DataTypes.TYPE_URI).size() == 1);
    assertTrue("Index of occurrences by locator is not empty.", 
							 ix.getOccurrences(loc2.getAddress(), DataTypes.TYPE_URI).isEmpty());

    // STATE 2: Occurrence locator added
    occ.setLocator(loc2);
    
    assertTrue("Index of occurrences by locator does not contain test value.",
							 ix.getOccurrences(loc2.getAddress(), DataTypes.TYPE_URI).contains(occ));
    assertTrue("Index of occurrences by locator is not empty.", 
							 ix.getOccurrences(loc1.getAddress(), DataTypes.TYPE_URI).isEmpty());

    // STATE 3: Duplicate occurrence locator added
    OccurrenceIF occ2 = builder.makeOccurrence(topic, otype, loc2);

    assertTrue("second occurrence not found by locator",
							 ix.getOccurrences(loc2.getAddress(), DataTypes.TYPE_URI).size() == 2);

  }

  public void testOccurrenceIndexByPrefixLocator() {
    // STATE 1: no occurrence values defined
    TopicIF topic = builder.makeTopic();
    TopicIF otype = builder.makeTopic();
    final String notation = "URI";

    assertTrue("Index of occurrences by value is not empty.", 
           ix.getOccurrencesByPrefix("a", DataTypes.TYPE_URI).isEmpty());

    // STATE 2: adding occurrences
    OccurrenceIF oa = builder.makeOccurrence(topic, otype, new GenericLocator(notation, "a"));
    OccurrenceIF oab = builder.makeOccurrence(topic, otype, new GenericLocator(notation, "ab"));
    OccurrenceIF oabc = builder.makeOccurrence(topic, otype, new GenericLocator(notation, "abc"));
    OccurrenceIF oabcde = builder.makeOccurrence(topic, otype, new GenericLocator(notation, "abcde"));
    OccurrenceIF oac = builder.makeOccurrence(topic, otype, new GenericLocator(notation, "ac"));
    OccurrenceIF oacde = builder.makeOccurrence(topic, otype, new GenericLocator(notation, "acde"));
    OccurrenceIF oz = builder.makeOccurrence(topic, otype, new GenericLocator(notation, "y"));

    //! assertTrue("Occurrences by prefix '' does not return 7.", 
    //!        ix.getOccurrencesByPrefix("", DataTypes.TYPE_URI).size() == 7);

    assertTrue("Occurrences by prefix 'a' does not return 6.", 
           ix.getOccurrencesByPrefix("a", DataTypes.TYPE_URI).size() == 6);

    assertTrue("Occurrences by prefix 'ab' does not return 3.", 
           ix.getOccurrencesByPrefix("ab", DataTypes.TYPE_URI).size() == 3);

    assertTrue("Occurrences by prefix 'abc' does not return 2.", 
           ix.getOccurrencesByPrefix("abc", DataTypes.TYPE_URI).size() == 2);

    assertTrue("Occurrences by prefix 'abcd' does not return 1.", 
           ix.getOccurrencesByPrefix("abcd", DataTypes.TYPE_URI).size() == 1);

    assertTrue("Occurrences by prefix 'abcde' does not return 1.", 
           ix.getOccurrencesByPrefix("abcde", DataTypes.TYPE_URI).size() == 1);

    assertTrue("Occurrences by prefix 'abcdef' does not return 0.", 
           ix.getOccurrencesByPrefix("abcdef", DataTypes.TYPE_URI).size() == 0);

    assertTrue("Occurrences by prefix 'ac' does not return 2.", 
           ix.getOccurrencesByPrefix("ac", DataTypes.TYPE_URI).size() == 2);

    assertTrue("Occurrences by prefix 'acd' does not return 1.", 
           ix.getOccurrencesByPrefix("acd", DataTypes.TYPE_URI).size() == 1);

    assertTrue("Occurrences by prefix 'acde' does not return 1.", 
           ix.getOccurrencesByPrefix("acde", DataTypes.TYPE_URI).size() == 1);

    assertTrue("Occurrences by prefix 'x' does not return 0.", 
           ix.getOccurrencesByPrefix("x", DataTypes.TYPE_URI).size() == 0);

    assertTrue("Occurrences by prefix 'y' does not return 1.", 
           ix.getOccurrencesByPrefix("y", DataTypes.TYPE_URI).size() == 1);

    // STATE 3: changing one of the occurrences
    oabc.setLocator(new GenericLocator(notation, "bcd"));

    assertTrue("Occurrences by prefix 'ab' does not return 2.", 
           ix.getOccurrencesByPrefix("ab", DataTypes.TYPE_URI).size() == 2);

    //! assertTrue("Occurrences by prefix '' does not return 7.", 
    //!        ix.getOccurrencesByPrefix("", DataTypes.TYPE_URI).size() == 7);

    //! assertTrue("Occurrences by prefix '' does not contain 'a'.", 
    //!        ix.getOccurrencesByPrefix("", DataTypes.TYPE_URI).contains(oa));

    //! assertTrue("Occurrences by prefix '' does not contain 'acde'.", 
    //!        ix.getOccurrencesByPrefix("", DataTypes.TYPE_URI).contains(oacde));
  }

  public void _testNullParameters() {
    testNull("getOccurrences", "net.ontopia.infoset.core.LocatorIF");
  }

    public void testOccurrences() {
        URILocator loc1 = null;
        URILocator loc2 = null;

        try {
            loc1 = new URILocator("http://www.ontopia.net");
            loc2 = new URILocator("ftp://sandbox.ontopia.net");
        }
        catch (java.net.MalformedURLException e) {
            fail("(INTERNAL) bad URLs given");
        }
        
        // STATE 1: empty topic map
        // assertTrue("index finds spurious occurrence locators",
        //        ix.getOccurrenceLocators().size() == 0);

        assertTrue("index finds occurrences it shouldn't",
									 ix.getOccurrences(loc1.getAddress(), DataTypes.TYPE_URI).size() == 0);

        
        // STATE 2: topic map has some topics in it
        TopicIF t1 = builder.makeTopic();
        TopicIF otype = builder.makeTopic();
        OccurrenceIF o1 = builder.makeOccurrence(t1, otype, loc1);
        OccurrenceIF o2 = builder.makeOccurrence(t1, otype, "");
        
        // assertTrue("occurrence locator not found via locator",
        //        ix.getOccurrenceLocators().size() == 2);
        // assertTrue("occurrence locator identity lost",
        //        ix.getOccurrenceLocators().contains(loc1));
        // assertTrue("null locator not found",
        //        ix.getOccurrenceLocators().contains(null));
        assertTrue("occurrence not found via locator",
									 ix.getOccurrences(loc1.getAddress(), DataTypes.TYPE_URI).size() == 1);
        assertTrue("wrong occurrence found via locator",
									 ix.getOccurrences(loc1.getAddress(), DataTypes.TYPE_URI).iterator().next().equals(o1));

        assertTrue("spurious occurrence found via locator",
									 ix.getOccurrences(loc2.getAddress(), DataTypes.TYPE_URI).size() == 0);
        
        // STATE 3: topic map with duplicates
        OccurrenceIF o3 = builder.makeOccurrence(t1, otype, loc1);
        
        // assertTrue("duplicate occurrence locator not filtered out",
        //        ix.getOccurrenceLocators().size() == 2);
        assertTrue("second occurrence not found via locator",
									 ix.getOccurrences(loc1.getAddress(), DataTypes.TYPE_URI).size() == 2);
    }
  
}
