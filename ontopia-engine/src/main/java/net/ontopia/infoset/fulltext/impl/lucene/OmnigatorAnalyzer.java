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

package net.ontopia.infoset.fulltext.impl.lucene;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharTokenizer;
import org.apache.lucene.analysis.TokenStream;
  
/**
 * INTERNAL: A Lucene analyzer implementation used by the Omnigator
 * plugins. The analyzer breaks up tokens according to the
 * Character.isLetterOrDigit(char) method and also lower-cases
 * characters, so that search can be case-insensitive.<p>
 *
 * @since 2.1.1
 */

public class OmnigatorAnalyzer extends Analyzer {

  public static final OmnigatorAnalyzer INSTANCE = new OmnigatorAnalyzer();

  public TokenStream tokenStream(String fieldName, Reader reader) {
    return new OmnigatorTokenizer(reader);
  }
  
  private static class OmnigatorTokenizer extends CharTokenizer {
    
    OmnigatorTokenizer(Reader in) {
      super(in);
    }
    
    protected boolean isTokenChar(char c) {
      return Character.isLetterOrDigit(c);
    }
    
    protected char normalize(char c) {
      return Character.toLowerCase(c);
    }
    
  }

}
