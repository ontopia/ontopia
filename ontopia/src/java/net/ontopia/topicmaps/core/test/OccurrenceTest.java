
// $Id: OccurrenceTest.java,v 1.19 2008/06/04 11:41:47 geir.gronmo Exp $

package net.ontopia.topicmaps.core.test;

import java.io.*;
import java.net.MalformedURLException;
import junit.framework.*;
import net.ontopia.net.Base64;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StreamUtils;
import net.ontopia.utils.ObjectUtils;
import net.ontopia.utils.ReaderInputStream;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.GenericLocator;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;

public class OccurrenceTest extends AbstractTypedScopedTest {
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
		assertTrue("data type is incorrect. should be xsd:string", ObjectUtils.equals(occurrence.getDataType(), DataTypes.TYPE_STRING));
		
		try {
			occurrence.setValue(null);
			fail("value could be set to null");
		} catch (NullPointerException e) {
		}
		assertTrue("value not maintained after set",
							 occurrence.getValue().equals(value));
		assertTrue("data type is incorrect. should be xsd:string", ObjectUtils.equals(occurrence.getDataType(), DataTypes.TYPE_STRING));
	}

  public void testLocator() {
    assertTrue("initial locator not null", occurrence.getLocator() == null);

    try {
      URILocator loc = new URILocator("http://www.ontopia.net");
      occurrence.setLocator(loc);
      assertTrue("locator identity not maintained after set",
             occurrence.getLocator().equals(loc));
			assertTrue("data type is incorrect. should be xsd:anyURI", ObjectUtils.equals(occurrence.getDataType(), DataTypes.TYPE_URI));
            
			try {
				occurrence.setLocator(null);
				fail("value could be set to null");
			} catch (NullPointerException e) {
			}
      assertTrue("locator identity not maintained after set",
             occurrence.getLocator().equals(loc));
			assertTrue("data type is incorrect. should be xsd:anyURI", ObjectUtils.equals(occurrence.getDataType(), DataTypes.TYPE_URI));
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
    File filein = new File(resolveFileName("various", "clob.xml"));
    File fileout = new File(resolveFileName("various", "clob.xml.out"));
    
		long inlen = filein.length();
    Reader ri = new FileReader(filein);
		try {
			occurrence.setReader(ri, inlen, DataTypes.TYPE_BINARY);
		} finally {
			try { ri.close(); } catch (Exception e) { e.printStackTrace(); };
		}
    assertTrue("Occurrence datatype is incorrect", ObjectUtils.equals(DataTypes.TYPE_BINARY, occurrence.getDataType()));
                 
    // read and decode content
    Reader ro = occurrence.getReader();
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
			long outlen = occurrence.getLength();
      try {
        assertTrue("Occurrence value put in is not the same as the one we get out.", StreamUtils.compare(ro, ri));
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
    File file = new File(resolveFileName("various", "blob.gif"));
    Reader ri = new InputStreamReader(new Base64.InputStream(new FileInputStream(file), Base64.ENCODE), "utf-8");
    occurrence.setReader(ri, file.length(), DataTypes.TYPE_BINARY);

    assertTrue("Occurrence datatype is incorrect", ObjectUtils.equals(DataTypes.TYPE_BINARY, occurrence.getDataType()));
                 
    // read and decode occurrence content
    Reader ro = occurrence.getReader();
    assertTrue("Reader value is null", ro != null);
    InputStream in = new Base64.InputStream(new ReaderInputStream(ro, "utf-8"), Base64.DECODE);
    try {
      OutputStream out = new FileOutputStream("/tmp/blob.gif");
      try {
        StreamUtils.transfer(in, out);
      } finally {
        out.close();
      }
    } finally {
      in.close();
    }
  }
  
  // --- Internal methods

  public void setUp() {
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
