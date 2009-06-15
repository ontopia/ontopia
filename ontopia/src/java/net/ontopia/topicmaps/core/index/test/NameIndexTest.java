// $Id: NameIndexTest.java,v 1.20 2008/06/24 12:43:39 geir.gronmo Exp $

package net.ontopia.topicmaps.core.index.test;

import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.index.NameIndexIF;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;

public class NameIndexTest extends AbstractIndexTest {
  
  protected NameIndexIF ix;

  public NameIndexTest(String name) {
    super(name);
  }

  protected void setUp() {
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

  public void testVariantIndexInternal()  {
    
    // STATE 1: No variants
    TopicIF t1 = builder.makeTopic();
    TopicNameIF bn1 = builder.makeTopicName(t1, "");
    TopicIF t2 = builder.makeTopic();
    TopicNameIF bn2 = builder.makeTopicName(t1, "");

    assertTrue("TopicName index is not empty", ix.getTopicNames("TopicName").isEmpty());

    // STATE 2: Variants added
    VariantNameIF vn1 = builder.makeVariantName(bn1, "VariantName");
    VariantNameIF vn2 = builder.makeVariantName(bn2, "VariantName2");
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
    VariantNameIF v4 = builder.makeVariantName(bn1, "VariantName");
        
    assertTrue("duplicate variant name string not found via string",
           ix.getVariants("VariantName", DataTypes.TYPE_STRING).size() == 2);
 
    // STATE 4: variant names with difficult characters
    VariantNameIF v5 = builder.makeVariantName(bn1, "Erlend \u00d8verby");
    VariantNameIF v6 = builder.makeVariantName(bn1, "Kana: \uFF76\uFF85"); // half-width katakana
    
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
    VariantNameIF vn = builder.makeVariantName(bn, "");
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
    VariantNameIF vn2 = builder.makeVariantName(bn, loc);
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
        VariantNameIF v1 = builder.makeVariantName(bn1, loc1);
        VariantNameIF v2 = builder.makeVariantName(bn1, "");
        
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
        VariantNameIF v3 = builder.makeVariantName(bn1, loc1);
        
        // assertTrue("duplicate variant locator not filtered out",
        //        ix.getVariantLocators().size() == 2);
        assertTrue("second variant not found via locator",
               ix.getVariants(loc1.getAddress(), DataTypes.TYPE_URI).size() == 2);
    }
  
}





