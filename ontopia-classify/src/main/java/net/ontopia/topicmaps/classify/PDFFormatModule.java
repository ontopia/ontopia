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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * INTERNAL: 
 */
public class PDFFormatModule implements FormatModuleIF {
  protected String[] extensions = new String[] {".pdf"};
  protected byte[] magicBytes = FormatModule.getBytes("%PDF-");
  
  @Override
  public boolean matchesContent(ClassifiableContentIF cc) {
    return FormatModule.startsWith(cc.getContent(), magicBytes);
  }

  @Override
  public boolean matchesIdentifier(ClassifiableContentIF cc) {
    return FormatModule.matchesExtension(cc.getIdentifier(), extensions);
  }

  @Override
  public void readContent(ClassifiableContentIF cc, TextHandlerIF handler) {
    try {
      PDDocument pdoc = PDDocument.load(new BufferedInputStream(new ByteArrayInputStream(cc.getContent())));
      PDFTextStripper stripper = new PDFTextStripper();
      String s = stripper.getText(pdoc);
      pdoc.close();
      char[] c = s.toCharArray();
      handler.startRegion("document");
      handler.text(c, 0, c.length);
      handler.endRegion();
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }    
  }
  
}
