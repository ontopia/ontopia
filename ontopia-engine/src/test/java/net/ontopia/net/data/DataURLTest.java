
package net.ontopia.net.data;

import junit.framework.TestCase;
import net.ontopia.utils.OntopiaRuntimeException;

public class DataURLTest extends TestCase {
  
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
    assertEquals("Contents of data URL not decoded correctly",
               "42", url.getContents());
  }

  // motivated by bug #1418
  public void testLatin1Chars() { 
    DataURL url = makeURI("data:,hei+p\u00E5+deg");
    String contents = url.getContents();
    assertEquals("Latin1 chars in data URL not decoded correctly: '" + contents + "'",
               "hei p\u00E5 deg", contents);
  }
}
