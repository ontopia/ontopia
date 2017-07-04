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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.util.Objects;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.GenericLocator;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.utils.ReaderInputStream;
import net.ontopia.utils.StreamUtils;
import net.ontopia.utils.TestFileUtils;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.io.IOUtils;

public abstract class OccurrenceTest extends AbstractTypedScopedTest {
  protected OccurrenceIF occurrence;
  
  public OccurrenceTest(String name) {
    super(name);
  }
    
  // --- Test cases

	public void testReification() {
		TopicIF reifier = builder.makeTopic();
		ReifiableIF reifiable = occurrence;

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

  public void testValue() {
    assertTrue("initial locator not null", "".equals(occurrence.getValue()));

		String value = "foo";
		occurrence.setValue(value);
		assertTrue("value not maintained after set",
							 occurrence.getValue().equals(value));
		assertTrue("data type is incorrect. should be xsd:string", Objects.equals(occurrence.getDataType(), DataTypes.TYPE_STRING));
		
		try {
			occurrence.setValue(null);
			fail("value could be set to null");
		} catch (NullPointerException e) {
		}
		assertTrue("value not maintained after set",
							 occurrence.getValue().equals(value));
		assertTrue("data type is incorrect. should be xsd:string", Objects.equals(occurrence.getDataType(), DataTypes.TYPE_STRING));
	}

  public void testLocator() {
    assertTrue("initial locator not null", occurrence.getLocator() == null);

    try {
      URILocator loc = new URILocator("http://www.ontopia.net");
      occurrence.setLocator(loc);
      assertTrue("locator identity not maintained after set",
             occurrence.getLocator().equals(loc));
			assertTrue("data type is incorrect. should be xsd:anyURI", Objects.equals(occurrence.getDataType(), DataTypes.TYPE_URI));
            
			try {
				occurrence.setLocator(null);
				fail("value could be set to null");
			} catch (NullPointerException e) {
			}
      assertTrue("locator identity not maintained after set",
             occurrence.getLocator().equals(loc));
			assertTrue("data type is incorrect. should be xsd:anyURI", Objects.equals(occurrence.getDataType(), DataTypes.TYPE_URI));
    }
    catch (MalformedURLException e) {
      fail("(INTERNAL) given URI was malformed");
    }
  }

	public void testNonURILocator() {
		try {
			occurrence.setLocator(new GenericLocator("URG", "l/e"));
			fail("non URI-locator could be set");
		} catch (ConstraintViolationException e) {
		}
		try {
			occurrence.setValue("foo", new GenericLocator("URG", "l/e"));
			fail("non URI datatype could be set");
		} catch (ConstraintViolationException e) {
		}
		try {
			occurrence.setReader(new StringReader("foo"), 3, new GenericLocator("URG", "l/e"));
			fail("non URI datatype could be set");
		} catch (ConstraintViolationException e) {
		}
	}

	public void testGenericURILocator() {
		LocatorIF loc1 = new GenericLocator("URI", "foo:bar");
		occurrence.setLocator(loc1);
		LocatorIF loc2 = occurrence.getLocator();
		assertTrue("Locator notation is not URI", loc2.getNotation().equals("URI"));
		assertTrue("Locator value is not foo:bar", loc2.getAddress().equals("foo:bar"));
		assertTrue("Input locator is not equal output locator", loc2.equals(loc1));
		assertTrue("Output locator is not equal input locator", loc1.equals(loc2));
	}

  public void testParentTopic() {
    assertTrue("parent not set to right object",
           occurrence.getTopic().equals(parent));
  }

  public void testReader() throws Exception {
    // read file and store in object
    File filein = TestFileUtils.getTransferredTestInputFile("various", "clob.xml");
    File fileout = TestFileUtils.getTestOutputFile("various", "clob.xml.out");

		long inlen = filein.length();
    Reader ri = new FileReader(filein);
		try {
			occurrence.setReader(ri, inlen, DataTypes.TYPE_BINARY);
		} finally {
			try { ri.close(); } catch (Exception e) { e.printStackTrace(); };
		}
    assertTrue("Occurrence datatype is incorrect", Objects.equals(DataTypes.TYPE_BINARY, occurrence.getDataType()));
                 
    // read and decode content
    Reader ro = occurrence.getReader();
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
    assertTrue("Reader value is null", ro != null);
    try {
      ri = new FileReader(filein); 
      ro = new FileReader(fileout); 
			long outlen = occurrence.getLength();
      try {
        assertTrue("Occurrence value put in is not the same as the one we get out.", IOUtils.contentEquals(ro, ri));
        assertTrue("Occurrence value length is different", inlen == outlen);
      } finally {
        ri.close();
      }
    } finally {
      ro.close();
    }
  }

  public void _testBinaryReader() throws Exception {
    // read file and store in occurrence
    String file = TestFileUtils.getTestInputFile("various", "blob.gif");
    Reader ri = new InputStreamReader(new Base64InputStream(new FileInputStream(file), true), "utf-8");
    occurrence.setReader(ri, file.length(), DataTypes.TYPE_BINARY);

    assertTrue("Occurrence datatype is incorrect", Objects.equals(DataTypes.TYPE_BINARY, occurrence.getDataType()));
                 
    // read and decode occurrence content
    Reader ro = occurrence.getReader();
    assertTrue("Reader value is null", ro != null);
    InputStream in = new Base64InputStream(new ReaderInputStream(ro, "utf-8"), false);
    try {
      OutputStream out = new FileOutputStream("/tmp/blob.gif");
      try {
        IOUtils.copy(in, out);
      } finally {
        out.close();
      }
    } finally {
      in.close();
    }
  }

  public void testHugeReifiedOccurrenceMerge() throws Exception {
    TopicMapIF source = ImportExportUtils.getReader(TestFileUtils.getTestInputFile("various", "huge-occurrence.ltm")).read();
    MergeUtils.mergeInto(topicmap, source);
    topicmap.getStore().commit();
  }
  
  // --- Internal methods

  public void setUp() throws Exception {
    super.setUp();
    TopicIF topic = builder.makeTopic();
    parent = topic;
		TopicIF otype = builder.makeTopic();
    occurrence = builder.makeOccurrence(topic, otype, "");
    object = occurrence;
    scoped = occurrence;
    typed = occurrence;
  }

  protected TMObjectIF makeObject() {
    TopicIF topic = builder.makeTopic();
		TopicIF otype = builder.makeTopic();
    return builder.makeOccurrence(topic, otype, "");
  }
    
}
