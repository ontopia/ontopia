
package net.ontopia.topicmaps.classify.test;

import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.topicmaps.classify.*;


public class FormatModuleTests extends AbstractOntopiaTestCase {
  
  public FormatModuleTests(String name) {
    super(name);
  }
  
  public void testFormats() {
    FormatModuleIF xml = new XMLFormatModule();
    matchIdentifierTrue(xml, "foo.xml");
    FormatModuleIF htm = new HTMLFormatModule();
    matchIdentifierTrue(htm, "foo.htm");
    matchIdentifierTrue(htm, "foo.html");
    matchIdentifierTrue(htm, "foo.shtml");
    matchIdentifierTrue(htm, "foo.xhtml");
    FormatModuleIF pdf = new PDFFormatModule();
    matchIdentifierTrue(pdf, "foo.pdf");
    FormatModuleIF txt = new PlainTextFormatModule();    
    matchIdentifierTrue(txt, "foo.txt");

    // NOTE: office formats do not really match on extensions alone,
    // so we cannot test them without having the office magic bytes
    
    //! FormatModuleIF doc = new WordFormatModule();
    //! matchIdentifierTrue(doc, "foo.doc");
    //! FormatModuleIF ppt = new PowerPointFormatModule();
    //! matchIdentifierTrue(ppt, "foo.ppt");
  }
  
  protected void matchIdentifierTrue(FormatModuleIF fm, String identifier) {
    ClassifiableContent cc = new ClassifiableContent();
    cc.setIdentifier(identifier);
    cc.setContent(new byte[] {});
    assertTrue("Format module " + fm + " did not match identifier " + cc.getIdentifier(), fm.matchesIdentifier(cc));
  }
  
}
