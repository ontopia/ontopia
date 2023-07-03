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

package net.ontopia.topicmaps.core;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.Collections;
import java.util.Objects;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.GenericLocator;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.utils.TestFileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public abstract class VariantNameTest extends AbstractScopedTest {
  protected VariantNameIF variant;
    
  @Override
  public void setUp() throws Exception {
    super.setUp();
    TopicIF topic = builder.makeTopic();
    TopicNameIF basename = builder.makeTopicName(topic, "");
    parent = basename;
    variant = builder.makeVariantName(basename, "", Collections.<TopicIF>emptySet());
    scoped = variant;
    object = variant;
  }

  // --- Test cases

	@Test
	public void testReification() {
		TopicIF reifier = builder.makeTopic();
		ReifiableIF reifiable = variant;

    Assert.assertTrue("Object reified by the reifying topic was found",
							 reifier.getReified() == null);
    Assert.assertTrue("Topic reifying the reifiable was found",
							 reifiable.getReifier() == null);

		reifiable.setReifier(reifier);
    Assert.assertTrue("No topic reifying the reifiable was found",
							 reifiable.getReifier() == reifier);
    Assert.assertTrue("No object reified by the reifying topic was found",
							 reifier.getReified() == reifiable);

		reifiable.setReifier(null);
    Assert.assertTrue("Object reified by the reifying topic was found",
							 reifier.getReified() == null);
    Assert.assertTrue("Topic reifying the first reifiable was found",
							 reifiable.getReifier() == null);
	}

  @Test
  public void testLocator() {
    Assert.assertTrue("initial locator not null", variant.getLocator() == null);

      URILocator loc = URILocator.create("http://www.ontopia.net");
      variant.setLocator(loc);
      Assert.assertTrue("locator identity not maintained after set",
             variant.getLocator().equals(loc));            
			Assert.assertTrue("data type is incorrect. should be xsd:anyURI", Objects.equals(variant.getDataType(), DataTypes.TYPE_URI));
            
			try {
				variant.setLocator(null);
				Assert.fail("value could be set to null");
			} catch (NullPointerException e) {
			}
  }

	@Test

	public void testNonURILocator() {
		try {
			variant.setLocator(new GenericLocator("URG", "l/e"));
			Assert.fail("non URI-locator could be set");
		} catch (ConstraintViolationException e) {
		}
		try {
			variant.setValue("foo", new GenericLocator("URG", "l/e"));
			Assert.fail("non URI datatype could be set");
		} catch (ConstraintViolationException e) {
		}
		try {
			variant.setReader(new StringReader("foo"), 3, new GenericLocator("URG", "l/e"));
			Assert.fail("non URI datatype could be set");
		} catch (ConstraintViolationException e) {
		}
	}

	@Test

	public void testGenericURILocator() {
		LocatorIF loc1 = new GenericLocator("URI", "foo:bar");
		variant.setLocator(loc1);
		LocatorIF loc2 = variant.getLocator();
		Assert.assertTrue("Locator notation is not URI", loc2.getNotation().equals("URI"));
		Assert.assertTrue("Locator value is not foo:bar", loc2.getAddress().equals("foo:bar"));
		Assert.assertTrue("Input locator is not equal output locator", loc2.equals(loc1));
		Assert.assertTrue("Output locator is not equal input locator", loc1.equals(loc2));
	}

  @Test
  public void testTopicName() {
    Assert.assertTrue("parent is not right object",
           variant.getTopicName().equals(parent));
  }

  @Test
  public void testTopic() {
    TopicIF topic = variant.getTopicName().getTopic();
    Assert.assertTrue("parent is not right object",
               variant.getTopic().equals(topic));
  }
    
  @Test
  public void testValue() {
    Assert.assertTrue("initial name value not null", "".equals(variant.getValue()));

    variant.setValue("testfaen");
    Assert.assertTrue("name not set correctly",
							 variant.getValue().equals("testfaen"));
		Assert.assertTrue("data type is incorrect. should be xsd:anyURI", Objects.equals(variant.getDataType(), DataTypes.TYPE_STRING));

		try {
			variant.setValue(null);
			Assert.fail("value could be set to null");
		} catch (NullPointerException e) {
		}
		Assert.assertTrue("data type is incorrect. should be xsd:anyURI", Objects.equals(variant.getDataType(), DataTypes.TYPE_STRING));
  }

  @Test
  public void testReader() throws Exception {
    // read file and store in object
    File filein = TestFileUtils.getTransferredTestInputFile("various", "clob.xml");
    File fileout = TestFileUtils.getTestOutputFile("various", "clob.xml.out");
    
    Reader ri = new FileReader(filein);
		long inlen = filein.length();
    variant.setReader(ri, inlen, DataTypes.TYPE_BINARY);

    Assert.assertTrue("Variant datatype is incorrect", Objects.equals(DataTypes.TYPE_BINARY, variant.getDataType()));
                 
    // read and decode content
    Reader ro = variant.getReader();
    try {
      Writer wo = new FileWriter(fileout);
      try {
        IOUtils.copy(ro, wo);
      } finally {
        wo.close();
      }
    } finally {
      ro.close();
    }
    Assert.assertTrue("Reader value is null", ro != null);
    try {
      ri = new FileReader(filein); 
      ro = new FileReader(fileout); 
			long outlen = variant.getLength();
      try {
        Assert.assertTrue("Variant value put in is not the same as the one we get out.", IOUtils.contentEquals(ro, ri));
        Assert.assertTrue("Variant value length is different", inlen == outlen);
      } finally {
        ri.close();
      }
    } finally {
      ro.close();
    }
  }

  @Test
  public void testTopicOfVariant() {
    Assert.assertTrue("parent and grandparent do not agree",
           variant.getTopicName().getTopic().equals(((TopicNameIF) parent).getTopic()));
  }

  public void _testInheritedScope() {
    TopicIF topic = builder.makeTopic();
    TopicIF theme1 = builder.makeTopic();
    TopicIF theme2 = builder.makeTopic();
    TopicIF theme3 = builder.makeTopic();

    TopicNameIF bn = builder.makeTopicName(topic, "");

    bn.addTheme(theme1);
    Assert.assertTrue("Base name does not have theme1", bn.getScope().size() == 1 && bn.getScope().contains(theme1));
    TopicNameIF vn = builder.makeTopicName(topic, "");
    Assert.assertTrue("Variant does not have theme1", vn.getScope().size() == 1 && vn.getScope().contains(theme1));

    bn.addTheme(theme2);
    Assert.assertTrue("Base name does not have theme1 and theme2",
               bn.getScope().size() == 2 && bn.getScope().contains(theme1) && bn.getScope().contains(theme2));
    Assert.assertTrue("Variant does not have theme1 and theme2",
               vn.getScope().size() == 2 && vn.getScope().contains(theme1) && vn.getScope().contains(theme2));

    try {
      vn.removeTheme(theme2);
      Assert.fail("Should not be able to remove theme2 from variant name.");
    } catch (ConstraintViolationException e) {
      // ok
    }

    vn.addTheme(theme3);
    Assert.assertTrue("Base name does not have theme1 and theme2",
               bn.getScope().size() == 2 && bn.getScope().contains(theme1) && bn.getScope().contains(theme2));
    Assert.assertTrue("Variant does not have theme1, theme2 and theme3",
               vn.getScope().size() == 3 &&
               vn.getScope().contains(theme1) &&
               vn.getScope().contains(theme2) && vn.getScope().contains(theme3));

    bn.removeTheme(theme3);
    Assert.assertTrue("Base name does not have theme1 and theme2",
               bn.getScope().size() == 2 && bn.getScope().contains(theme1) && bn.getScope().contains(theme2));
    Assert.assertTrue("Variant does not have theme1, theme2 and theme3",
               vn.getScope().size() == 3 &&
               vn.getScope().contains(theme1) &&
               vn.getScope().contains(theme2) && vn.getScope().contains(theme3));

    vn.removeTheme(theme3);
    Assert.assertTrue("Base name does not have theme1 and theme2",
               bn.getScope().size() == 2 && bn.getScope().contains(theme1) && bn.getScope().contains(theme2));
    Assert.assertTrue("Variant does not have theme1 and theme2",
               vn.getScope().size() == 2 && vn.getScope().contains(theme1) && vn.getScope().contains(theme2));

    bn.removeTheme(theme2);
    Assert.assertTrue("Base name does not have theme1",
               bn.getScope().size() == 1 && bn.getScope().contains(theme1));
    Assert.assertTrue("Variant does not have theme1",
               vn.getScope().size() == 1 && vn.getScope().contains(theme1));

    bn.removeTheme(theme1);
    Assert.assertTrue("Base name does have scope when it should not", bn.getScope().isEmpty());
    Assert.assertTrue("Variant does have scope when it should not", vn.getScope().isEmpty());
    
    builder.makeTopic();
  }
    
  // --- Internal methods

  @Override
  protected TMObjectIF makeObject() {
    TopicIF    topic = builder.makeTopic();
    TopicNameIF basename = builder.makeTopicName(topic, "");
    return builder.makeVariantName(basename, "", Collections.<TopicIF>emptySet());
  }

}
