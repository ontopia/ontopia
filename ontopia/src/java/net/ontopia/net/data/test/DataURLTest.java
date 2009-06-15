
// $Id: DataURLTest.java,v 1.1 2005/03/20 12:59:15 larsga Exp $

package net.ontopia.net.data.test;

import java.io.IOException;
import net.ontopia.test.*;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.net.data.*;
import net.ontopia.utils.OntopiaRuntimeException;

public class DataURLTest extends AbstractOntopiaTestCase {
  
  public DataURLTest(String name) {
    super(name);
  }

  protected DataURL makeURI(String address) {
    try {
      return new DataURL(address);
    } catch (java.io.IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
    
  // --- tests

  public void testTrivial() {
    DataURL url = makeURI("data:,42");
    assertTrue("contents of data URL not decoded correctly",
               url.getContents().equals("42"));
  }

  // motivated by bug #1418
  public void testLatin1Chars() { 
    DataURL url = makeURI("data:,hei+p\u00E5+deg");
    String contents = url.getContents();
    assertTrue("latin1 chars in data URL not decoded correctly: '" + contents + "'",
               contents.equals("hei p\u00E5 deg"));
  }
}
