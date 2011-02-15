
package net.ontopia.topicmaps.utils.sdshare.test;

import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.topicmaps.utils.sdshare.client.MIMEType;

public class MIMETypeTest extends AbstractOntopiaTestCase {
  
  public MIMETypeTest(String name) {
    super(name);
  }

  public void testBasic() {
    MIMEType mime = new MIMEType("application", "x-tm+xml", "1.0");
    assertEquals("wrong main type", mime.getMainType(), "application");
    assertEquals("wrong subtype", mime.getSubType(), "x-tm+xml");
    assertEquals("wrong version", mime.getVersion(), "1.0");

    mime = new MIMEType("application", "x-tm+xml", null);
    assertEquals("wrong main type", mime.getMainType(), "application");
    assertEquals("wrong subtype", mime.getSubType(), "x-tm+xml");
    assertEquals("wrong version", mime.getVersion(), null);
  }

  public void testParseSimple() {
    MIMEType mime = new MIMEType("application/x-tm+xml");
    assertEquals("wrong main type", mime.getMainType(), "application");
    assertEquals("wrong subtype", mime.getSubType(), "x-tm+xml");
    assertEquals("wrong version", mime.getVersion(), null);
  }

  public void testParseVersion() {
    MIMEType mime = new MIMEType("application/x-tm+xml; version=1.0");
    assertEquals("wrong main type", mime.getMainType(), "application");
    assertEquals("wrong subtype", mime.getSubType(), "x-tm+xml");
    assertEquals("wrong version", "1.0", mime.getVersion());
  }

  public void testParseVersionNoSpace() {
    MIMEType mime = new MIMEType("application/x-tm+xml;version=1.0");
    assertEquals("wrong main type", mime.getMainType(), "application");
    assertEquals("wrong subtype", mime.getSubType(), "x-tm+xml");
    assertEquals("wrong version", "1.0", mime.getVersion());
  }

  public void testParseVersionLotsOfSpaces() {
    MIMEType mime = new MIMEType("application/x-tm+xml; version = 1.0  ");
    assertEquals("wrong main type", mime.getMainType(), "application");
    assertEquals("wrong subtype", mime.getSubType(), "x-tm+xml");
    assertEquals("wrong version", "1.0", mime.getVersion());
  }

  public void testParseVersionMalformed() {
    MIMEType mime = new MIMEType("application/x-tm+xml;");
    assertEquals("wrong main type", mime.getMainType(), "application");
    assertEquals("wrong subtype", mime.getSubType(), "x-tm+xml");
    assertEquals("wrong version", null, mime.getVersion());
  }

  public void testParseVersionMalformed2() {
    MIMEType mime = new MIMEType("application/x-tm+xml; ");
    assertEquals("wrong main type", mime.getMainType(), "application");
    assertEquals("wrong subtype", mime.getSubType(), "x-tm+xml");
    assertEquals("wrong version", null, mime.getVersion());
  }

  public void testParseVersionMalformed3() {
    MIMEType mime = new MIMEType("application/x-tm+xml; version");
    assertEquals("wrong main type", mime.getMainType(), "application");
    assertEquals("wrong subtype", mime.getSubType(), "x-tm+xml");
    assertEquals("wrong version", null, mime.getVersion());
  }

  public void testParseVersionMalformed4() {
    MIMEType mime = new MIMEType("application/x-tm+xml;version ");
    assertEquals("wrong main type", mime.getMainType(), "application");
    assertEquals("wrong subtype", mime.getSubType(), "x-tm+xml");
    assertEquals("wrong version", null, mime.getVersion());
  }

  public void testParseVersionMalformed5() {
    MIMEType mime = new MIMEType("application/x-tm+xml;version !");
    assertEquals("wrong main type", mime.getMainType(), "application");
    assertEquals("wrong subtype", mime.getSubType(), "x-tm+xml");
    assertEquals("wrong version", null, mime.getVersion());
  }

  public void testParseCharset() {
    MIMEType mime = new MIMEType("application/x-tm+xml; charset=utf-8");
    assertEquals("wrong main type", mime.getMainType(), "application");
    assertEquals("wrong subtype", mime.getSubType(), "x-tm+xml");
    assertEquals("wrong version", null, mime.getVersion());
  }
}
