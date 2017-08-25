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

import java.util.Collections;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;

public abstract class NameIndexTest extends AbstractIndexTest {
  
  protected NameIndexIF ix;

  public NameIndexTest(String name) {
    super(name);
  }

  @Override
  protected void setUp() throws Exception {
    ix = (NameIndexIF)super.setUp("NameIndexIF");
  }
  
  public void testTopicNameIndex() {
    TopicIF t1 = builder.makeTopic();
    TopicNameIF bn1 = builder.makeTopicName(t1, "");
    TopicNameIF bn2 = builder.makeTopicName(t1, "");
    TopicNameIF bn3 = builder.makeTopicName(t1, "-"); // FIXME: using hyphen for now as oracle treats empty string as null.

    assertTrue("TopicName index is not empty", ix.getTopicNames("TopicName").isEmpty());

    bn1.setValue("TopicName");
    bn2.setValue("TopicName2");

    assertTrue("TopicName not indexed", 
           ix.getTopicNames("TopicName").contains(bn1));
    assertTrue("TopicName2 incorrectly indexed.",
           !ix.getTopicNames("TopicName").contains(bn2));
    assertTrue("TopicName2 not indexed",
           ix.getTopicNames("TopicName2").contains(bn2));
    assertTrue("TopicName incorrectly indexed",
           !ix.getTopicNames("TopicName2").contains(bn1));
    assertTrue("Could not find empty base name via \"\"",
           ix.getTopicNames("-").size() == 1);
    assertTrue("Wrong base name found via \"\"",
           ix.getTopicNames("-").contains(bn3));

    // Duplicate base names:
    bn3 = builder.makeTopicName(t1, "TopicName");
    assertTrue("second base name not found via string",
            ix.getTopicNames("TopicName").size() == 2);

    // STATE 4: base names with difficult characters
    TopicNameIF bn4 = builder.makeTopicName(t1, "Erlend \u00d8verby");
    TopicNameIF bn5 = builder.makeTopicName(t1, "Kana: \uFF76\uFF85"); // half-width katakana
        
    assertTrue("couldn't find base name via latin1 string",
           ix.getTopicNames("Erlend \u00d8verby").size() == 1);
    assertTrue("wrong base name found via latin1 string",
           ix.getTopicNames("Erlend \u00d8verby").iterator().next().equals(bn4));
    
    assertTrue("couldn't find base name via hw-kana string",
           ix.getTopicNames("Kana: \uFF76\uFF85").size() == 1);
    assertTrue("wrong base name found via hw-kana string",
           ix.getTopicNames("Kana: \uFF76\uFF85").iterator().next().equals(bn5));
        
  }

  public void testTopicNameIndexByType() {
    // STATE 1: no topic name values defined
    TopicIF topic = builder.makeTopic();
    TopicIF nameType1 = builder.makeTopic();
    TopicIF nameType2 = builder.makeTopic();
    String value0 = "dummy0";
    String value1 = "dummy";
    TopicNameIF topicName = builder.makeTopicName(topic, nameType1, value0);
    assertTrue("Index of topic names by value and type is not empty.",
      ix.getTopicNames(value1, nameType1).isEmpty());

    // STATE 2: Topic name value added
    topicName.setValue(value1);
    assertTrue("Index of topic names by value and type does not contain test value.",
      ix.getTopicNames(value1, nameType1).contains(topicName));

    // STATE 3: Duplicate topic name value added
    TopicNameIF topicName2 = builder.makeTopicName(topic, nameType1, value1);
    assertTrue("second topic name not found by value",
      ix.getTopicNames(value1, nameType1).size() == 2);

    // STATE 4: Change first topic name value
    String value2 = "dummy2";
    topicName.setValue(value2);
    assertTrue("list of topic names not updated",
      ix.getTopicNames(value1, nameType1).size() == 1);
    assertTrue("first topic name not found by new value",
      ix.getTopicNames(value2, nameType1).size() == 1);

    // STATE 5: Change topic name types
    assertTrue("Index of topic names by value and type is not empty for original type",
      ix.getTopicNames(value2, nameType2).isEmpty());
    topicName.setType(nameType2);
    assertFalse("Index of topic names by value and type does not detect changed type",
      ix.getTopicNames(value2, nameType2).isEmpty());
    assertTrue("Index of topic names by value and type does not detect aborted type",
      ix.getTopicNames(value2, nameType1).isEmpty());

    // STATE 6: Change second topic name type
    topicName2.setType(nameType2);
    assertFalse("Index of topic names by value and type contains topic name with wrong value",
      ix.getTopicNames(value2, nameType2).contains(topicName2));
  }

  public void testVariantIndexInternal()  {
    
    // STATE 1: No variants
    TopicIF t1 = builder.makeTopic();
    TopicNameIF bn1 = builder.makeTopicName(t1, "");
    TopicIF t2 = builder.makeTopic();
    TopicNameIF bn2 = builder.makeTopicName(t1, "");

    assertTrue("TopicName index is not empty", ix.getTopicNames("TopicName").isEmpty());

    // STATE 2: Variants added
    VariantNameIF vn1 = builder.makeVariantName(bn1, "VariantName", Collections.<TopicIF>emptySet());
    VariantNameIF vn2 = builder.makeVariantName(bn2, "VariantName2", Collections.<TopicIF>emptySet());
    bn1.setValue("TopicName");
    bn2.setValue("TopicName2");
    
    assertTrue("VariantName not indexed", 
							 ix.getVariants("VariantName", DataTypes.TYPE_STRING).contains(vn1));
    assertTrue("VariantName2 incorrectly indexed.",
							 !ix.getVariants("VariantName", DataTypes.TYPE_STRING).contains(vn2));
    assertTrue("VariantName2 not indexed",
							 ix.getVariants("VariantName2", DataTypes.TYPE_STRING).contains(vn2));
    assertTrue("VariantName incorrectly indexed",
							 !ix.getVariants("VariantName2", DataTypes.TYPE_STRING).contains(vn1));
    //! assertTrue("couldn't find variant name via null",
    //!            ix.getVariants((String)null, DataTypes.TYPE_STRING).size() == 1);
    //! assertTrue("wrong base name found via null",
    //!            ix.getVariants((String)null, DataTypes.TYPE_STRING).contains(vn3));

    // STATE 3: Duplicate added
    VariantNameIF v4 = builder.makeVariantName(bn1, "VariantName", Collections.<TopicIF>emptySet());
        
    assertTrue("duplicate variant name string not found via string",
           ix.getVariants("VariantName", DataTypes.TYPE_STRING).size() == 2);
 
    // STATE 4: variant names with difficult characters
    VariantNameIF v5 = builder.makeVariantName(bn1, "Erlend \u00d8verby", Collections.<TopicIF>emptySet());
    VariantNameIF v6 = builder.makeVariantName(bn1, "Kana: \uFF76\uFF85", Collections.<TopicIF>emptySet()); // half-width katakana
    
    assertTrue("couldn't find variant name via latin1 string",
           ix.getVariants("Erlend \u00d8verby", DataTypes.TYPE_STRING).size() == 1);
    assertTrue("wrong variant name found via latin1 string",
           ix.getVariants("Erlend \u00d8verby", DataTypes.TYPE_STRING).iterator().next().equals(v5));
    
    assertTrue("couldn't find variant name via hw-kana string",
           ix.getVariants("Kana: \uFF76\uFF85", DataTypes.TYPE_STRING).size() == 1);
    assertTrue("wrong variant name found via hw-kana string",
           ix.getVariants("Kana: \uFF76\uFF85", DataTypes.TYPE_STRING).iterator().next().equals(v6));
        
  }

  public void testVariantIndexExternal() {
    // STATE 1: No variant locators defined
    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "");
    VariantNameIF vn = builder.makeVariantName(bn, "", Collections.<TopicIF>emptySet());
    LocatorIF loc = null;
    try {
      loc = new URILocator("http://www.ontopia.net/test-data/variant-locator.xml");
    } catch (java.net.MalformedURLException ex) {
      fail("Test Setup: Malformed URL while creating locator for variant name test.");
    }
    assertTrue("Index of variant names by locator is not empty.", 
							 ix.getVariants(loc.getAddress(), DataTypes.TYPE_URI).isEmpty());

    // STATE 2: Variant locator defined
    vn.setLocator(loc);
    
    assertTrue("Index of variant names by locator does not contain test value.",
           ix.getVariants(loc.getAddress(), DataTypes.TYPE_URI).contains(vn));

    // STATE 3: Duplicate variant locator defined
    VariantNameIF vn2 = builder.makeVariantName(bn, loc, Collections.<TopicIF>emptySet());
    assertTrue("second variant not found by locator",
           ix.getVariants(loc.getAddress(), DataTypes.TYPE_URI).size() == 2);
  }

  public void _testNullParameters() {
    // FIXME: tests for the wrong thing!
    testNull("getTopicNames", "java.lang.String");
    testNull("getVariants", "java.lang.String");
  }

  public void testTopicRemoval() {
    TopicIF t1 = builder.makeTopic();
    TopicNameIF bn1 = builder.makeTopicName(t1, "bn1");

    assertTrue("couldn't find base name via name",
           ix.getTopicNames("bn1").size() == 1);
    assertTrue("wrong base name found via latin1 string",
           ix.getTopicNames("bn1").iterator().next().equals(bn1));

    t1.remove();

    assertTrue("found base name after topic had been removed",
           ix.getTopicNames("bn1").size() == 0);
  }

    public void testVariants() {
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
        // assertTrue("index finds spurious variant locators",
        //        ix.getVariantLocators().size() == 0);

        assertTrue("index finds variants it shouldn't",
               ix.getVariants(loc1.getAddress(), DataTypes.TYPE_URI).size() == 0);

        
        // STATE 2: topic map has some topics in it
        TopicIF t1 = builder.makeTopic();
        TopicNameIF bn1 = builder.makeTopicName(t1, "");
        VariantNameIF v1 = builder.makeVariantName(bn1, loc1, Collections.<TopicIF>emptySet());
        VariantNameIF v2 = builder.makeVariantName(bn1, "", Collections.<TopicIF>emptySet());
        
        // assertTrue("variant locator not found",
        //        ix.getVariantLocators().size() == 2);
        // assertTrue("variant locator identity lost",
        //        ix.getVariantLocators().contains(loc1));
        // assertTrue("null locator not found",
        //        ix.getVariantLocators().contains(null));
        assertTrue("variant not found via locator",
               ix.getVariants(loc1.getAddress(), DataTypes.TYPE_URI).size() == 1);
        assertTrue("wrong variant found via locator",
               ix.getVariants(loc1.getAddress(), DataTypes.TYPE_URI).iterator().next().equals(v1));

        assertTrue("spurious variant found via locator",
               ix.getVariants(loc2.getAddress(), DataTypes.TYPE_URI).size() == 0);
        
        // STATE 3: topic map with duplicates
        VariantNameIF v3 = builder.makeVariantName(bn1, loc1, Collections.<TopicIF>emptySet());
        
        // assertTrue("duplicate variant locator not filtered out",
        //        ix.getVariantLocators().size() == 2);
        assertTrue("second variant not found via locator",
               ix.getVariants(loc1.getAddress(), DataTypes.TYPE_URI).size() == 2);
    }
  
}





