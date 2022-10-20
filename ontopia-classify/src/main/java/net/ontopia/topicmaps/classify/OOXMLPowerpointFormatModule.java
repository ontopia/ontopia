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

import java.io.ByteArrayInputStream;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.poi.sl.extractor.SlideShowExtractor;
import org.apache.poi.sl.usermodel.SlideShowFactory;

/**
 * INTERNAL: A format module for the OOXML PresentationML format.
 */
public class OOXMLPowerpointFormatModule implements FormatModuleIF {
  protected String[] extensions = new String[] {".pptx"};
  // these are really magic bytes for all zip files...
  protected byte[] magicBytes = new byte[] {
    (byte) 0x50, (byte) 0x4B, (byte) 0x03, (byte) 0x04 };
  
  @Override
  public boolean matchesContent(ClassifiableContentIF cc) {
    return false;
  }

  @Override
  public boolean matchesIdentifier(ClassifiableContentIF cc) {
    boolean matches = FormatModule.matchesExtension(cc.getIdentifier(), extensions);
    if (!matches) {
      return false;
    }
    // name matches, then check office magic bytes
    return FormatModule.startsWith(cc.getContent(), magicBytes);
  }

  @Override
  public void readContent(ClassifiableContentIF cc, TextHandlerIF handler) {
    try {
      SlideShowExtractor<?, ?> extractor = new SlideShowExtractor<>(SlideShowFactory.create(new ByteArrayInputStream(cc.getContent())));
      String s = extractor.getText();
      char[] c = s.toCharArray();
      handler.startRegion("document");
      handler.text(c, 0, c.length);
      handler.endRegion();
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }    
  }
}
