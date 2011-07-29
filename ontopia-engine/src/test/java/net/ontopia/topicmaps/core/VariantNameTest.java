
package net.ontopia.topicmaps.core;

import java.io.*;
import java.net.MalformedURLException;
import net.ontopia.utils.StreamUtils;
import net.ontopia.utils.ObjectUtils;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.GenericLocator;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;

public abstract class VariantNameTest extends AbstractScopedTest {
  protected VariantNameIF variant;
    
  public VariantNameTest(String name) {
    super(name);
  }

  public void setUp() throws Exception {
    super.setUp();
    TopicIF topic = builder.makeTopic();
    TopicNameIF basename = builder.makeTopicName(topic, "");
    parent = basename;
    variant = builder.makeVariantName(basename, "");
    scoped = variant;
    object = variant;
  }

  // --- Test cases

	public void testReification() {
		TopicIF reifier = builder.makeTopic();
		ReifiableIF reifiable = variant;

    assertTrue("Object reified by the reifying topic was found",
							 reifier.getReified() == null);
    assertTrue("Topic reifying the reifiable was found",
							 reifiable.getReifier() == null);

		reifiable.setReifier(reifier);
    assertTrue("No topic reifying the reifiable was found",
							 reifiable.getReifier() == reifier);
    assertTrue("No object reified by the reifying topic was found",
							 reifier.getReified() == reifiable);

		reifiable.setReifier(null);
    assertTrue("Object reified by the reifying topic was found",
							 reifier.getReified() == null);
    assertTrue("Topic reifying the first reifiable was found",
							 reifiable.getReifier() == null);
	}

  public void testLocator() {
    assertTrue("initial locator not null", variant.getLocator() == null);

    try {
      URILocator loc = new URILocator("http://www.ontopia.net");
      variant.setLocator(loc);
      assertTrue("locator identity not maintained after set",
             variant.getLocator().equals(loc));            
			assertTrue("data type is incorrect. should be xsd:anyURI", ObjectUtils.equals(variant.getDataType(), DataTypes.TYPE_URI));
            
			try {
				variant.setLocator(null);
				fail("value could be set to null");
			} catch (NullPointerException e) {
			}
    }
    catch (MalformedURLException e) {
      fail("(INTERNAL) given URI was malformed");
    }
  }

	public void testNonURILocator() {
		try {
			variant.setLocator(new GenericLocator("URG", "l/e"));
			fail("non URI-locator could be set");
		} catch (ConstraintViolationException e) {
		}
		try {
			variant.setValue("foo", new GenericLocator("URG", "l/e"));
			fail("non URI datatype could be set");
		} catch (ConstraintViolationException e) {
		}
		try {
			variant.setReader(new StringReader("foo"), 3, new GenericLocator("URG", "l/e"));
			fail("non URI datatype could be set");
		} catch (ConstraintViolationException e) {
		}
	}

	public void testGenericURILocator() {
		LocatorIF loc1 = new GenericLocator("URI", "foo:bar");
		variant.setLocator(loc1);
		LocatorIF loc2 = variant.getLocator();
		assertTrue("Locator notation is not URI", loc2.getNotation().equals("URI"));
		assertTrue("Locator value is not foo:bar", loc2.getAddress().equals("foo:bar"));
		assertTrue("Input locator is not equal output locator", loc2.equals(loc1));
		assertTrue("Output locator is not equal input locator", loc1.equals(loc2));
	}

  public void testTopicName() {
    assertTrue("parent is not right object",
           variant.getTopicName().equals(parent));
  }

  public void testTopic() {
    TopicIF topic = variant.getTopicName().getTopic();
    assertTrue("parent is not right object",
               variant.getTopic().equals(topic));
  }
    
  public void testValue() {
    assertTrue("initial name value not null", "".equals(variant.getValue()));

    variant.setValue("testfaen");
    assertTrue("name not set correctly",
							 variant.getValue().equals("testfaen"));
		assertTrue("data type is incorrect. should be xsd:anyURI", ObjectUtils.equals(variant.getDataType(), DataTypes.TYPE_STRING));

		try {
			variant.setValue(null);
			fail("value could be set to null");
		} catch (NullPointerException e) {
		}
		assertTrue("data type is incorrect. should be xsd:anyURI", ObjectUtils.equals(variant.getDataType(), DataTypes.TYPE_STRING));
  }

  public void testReader() throws Exception {
    // read file and store in object
    File filein = TestFileUtils.getTransferredTestInputFile("various", "clob.xml");
    File fileout = TestFileUtils.getTestOutputFile("various", "clob.xml.out");
    
    Reader ri = new FileReader(filein);
		long inlen = filein.length();
    variant.setReader(ri, inlen, DataTypes.TYPE_BINARY);

    assertTrue("Variant datatype is incorrect", ObjectUtils.equals(DataTypes.TYPE_BINARY, variant.getDataType()));
                 
    // read and decode content
    Reader ro = variant.getReader();
    try {
      Writer wo = new FileWriter(fileout);
      try {
        StreamUtils.transfer(ro, wo);
      } finally {
        wo.close();
      }
    } finally {
      ro.close();
    }
    assertTrue("Reader value is null", ro != null);
    try {
      ri = new FileReader(filein); 
      ro = new FileReader(fileout); 
			long outlen = variant.getLength();
      try {
        assertTrue("Variant value put in is not the same as the one we get out.", StreamUtils.compare(ro, ri));
        assertTrue("Variant value length is different", inlen == outlen);
      } finally {
        ri.close();
      }
    } finally {
      ro.close();
    }
  }

  public void testTopicOfVariant() {
    assertTrue("parent and grandparent do not agree",
           variant.getTopicName().getTopic().equals(((TopicNameIF) parent).getTopic()));
  }

  public void _testInheritedScope() {
    TopicIF topic = builder.makeTopic();
    TopicIF theme1 = builder.makeTopic();
    TopicIF theme2 = builder.makeTopic();
    TopicIF theme3 = builder.makeTopic();

    TopicNameIF bn = builder.makeTopicName(topic, "");

    bn.addTheme(theme1);
    assertTrue("Base name does not have theme1", bn.getScope().size() == 1 && bn.getScope().contains(theme1));
    TopicNameIF vn = builder.makeTopicName(topic, "");
    assertTrue("Variant does not have theme1", vn.getScope().size() == 1 && vn.getScope().contains(theme1));

    bn.addTheme(theme2);
    assertTrue("Base name does not have theme1 and theme2",
               bn.getScope().size() == 2 && bn.getScope().contains(theme1) && bn.getScope().contains(theme2));
    assertTrue("Variant does not have theme1 and theme2",
               vn.getScope().size() == 2 && vn.getScope().contains(theme1) && vn.getScope().contains(theme2));

    try {
      vn.removeTheme(theme2);
      fail("Should not be able to remove theme2 from variant name.");
    } catch (ConstraintViolationException e) {
      // ok
    }

    vn.addTheme(theme3);
    assertTrue("Base name does not have theme1 and theme2",
               bn.getScope().size() == 2 && bn.getScope().contains(theme1) && bn.getScope().contains(theme2));
    assertTrue("Variant does not have theme1, theme2 and theme3",
               vn.getScope().size() == 3 &&
               vn.getScope().contains(theme1) &&
               vn.getScope().contains(theme2) && vn.getScope().contains(theme3));

    bn.removeTheme(theme3);
    assertTrue("Base name does not have theme1 and theme2",
               bn.getScope().size() == 2 && bn.getScope().contains(theme1) && bn.getScope().contains(theme2));
    assertTrue("Variant does not have theme1, theme2 and theme3",
               vn.getScope().size() == 3 &&
               vn.getScope().contains(theme1) &&
               vn.getScope().contains(theme2) && vn.getScope().contains(theme3));

    vn.removeTheme(theme3);
    assertTrue("Base name does not have theme1 and theme2",
               bn.getScope().size() == 2 && bn.getScope().contains(theme1) && bn.getScope().contains(theme2));
    assertTrue("Variant does not have theme1 and theme2",
               vn.getScope().size() == 2 && vn.getScope().contains(theme1) && vn.getScope().contains(theme2));

    bn.removeTheme(theme2);
    assertTrue("Base name does not have theme1",
               bn.getScope().size() == 1 && bn.getScope().contains(theme1));
    assertTrue("Variant does not have theme1",
               vn.getScope().size() == 1 && vn.getScope().contains(theme1));

    bn.removeTheme(theme1);
    assertTrue("Base name does have scope when it should not", bn.getScope().isEmpty());
    assertTrue("Variant does have scope when it should not", vn.getScope().isEmpty());
    
    TopicIF theme = builder.makeTopic();
  }
    
  // --- Internal methods

  protected TMObjectIF makeObject() {
    TopicIF    topic = builder.makeTopic();
    TopicNameIF basename = builder.makeTopicName(topic, "");
    return builder.makeVariantName(basename, "");
  }

}





