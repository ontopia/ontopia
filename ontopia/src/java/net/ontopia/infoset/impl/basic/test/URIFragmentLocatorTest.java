
// $Id: URIFragmentLocatorTest.java,v 1.1 2004/03/08 09:22:09 larsga Exp $

package net.ontopia.infoset.impl.basic.test;

import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.utils.OntopiaRuntimeException;

public class URIFragmentLocatorTest extends AbstractOntopiaTestCase {
  
  public URIFragmentLocatorTest(String name) {
    super(name);
  }

  // --- tests
  
  public void testGetExternalFormSimple() {
    testExternalForm("http://www.example.com", "fragment");
  }

  public void testGetExternalFormSimple2() {
    testExternalForm("http://www.example.com/index.jsp", "fragment");
  }

  public void testGetExternalFormSimple3() {
    testExternalForm("http://www.example.com/index.jsp?bongo", "fragment");
  }

  public void testGetExternalFormHostname() {
    testExternalForm("http://www.%F8l.no/", "fragment");
  }

  public void testGetExternalFormDirname() {
    testExternalForm("http://www.ontopia.no/%F8l.html", "fragment");
  }

  public void testGetExternalFormDirnameSpace() {
    testExternalForm("http://www.ontopia.no/space%20in%20url.html",
                     "fragment");
  }

  // --- helpers
  
  private void testExternalForm(String uri, String frag) {
    try {
      LocatorIF base = new URILocator(uri);
      LocatorIF locator = base.resolveAbsolute("#" + frag);

      String correct = base.getExternalForm() + "#" + frag;
      String external = locator.getExternalForm();
      
      assertTrue("incorrect external form for URI '" + locator.getAddress() +
                 "': '" + external + "', correct '" + correct + "'",
                 external.equals(correct));
    } catch (java.net.MalformedURLException e) {
      fail("INTERNAL ERROR: " + e);
    }
  }  
}
