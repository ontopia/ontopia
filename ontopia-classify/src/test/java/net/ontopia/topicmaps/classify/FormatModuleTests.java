/*
 * #!
 * Ontopia Classify
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

package net.ontopia.topicmaps.classify;

import org.junit.Assert;
import org.junit.Test;

public class FormatModuleTests {

  @Test
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
    Assert.assertTrue("Format module " + fm + " did not match identifier " + cc.getIdentifier(), fm.matchesIdentifier(cc));
  }
  
}
