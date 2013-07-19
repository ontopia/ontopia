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

import java.util.*;
import java.lang.reflect.Method;

import net.ontopia.utils.*;
import org.tartarus.snowball.SnowballProgram;

/**
 * INTERNAL: 
 */
public class SnowballStemmer implements TermStemmerIF {

  private static final Object [] EMPTY_ARGS = new Object[0];
  private static Map languages;

  static {
    languages = new HashMap();
    languages.put("dk", "org.tartarus.snowball.ext.DanishStemmer");
    languages.put("nl", "org.tartarus.snowball.ext.DutchStemmer");
    languages.put("en", "org.tartarus.snowball.ext.EnglishStemmer");
    languages.put("fi", "org.tartarus.snowball.ext.FinnishStemmer");
    languages.put("fr", "org.tartarus.snowball.ext.FrenchStemmer");
    languages.put("de2", "org.tartarus.snowball.ext.German2Stemmer");
    languages.put("de", "org.tartarus.snowball.ext.GermanStemmer");
    languages.put("it", "org.tartarus.snowball.ext.ItalianStemmer");
    languages.put("kp", "org.tartarus.snowball.ext.KpStemmer");
    languages.put("lovins", "org.tartarus.snowball.ext.LovinsStemmer");
    languages.put("no", "org.tartarus.snowball.ext.NorwegianStemmer");
    languages.put("porter", "org.tartarus.snowball.ext.PorterStemmer");
    languages.put("pt", "org.tartarus.snowball.ext.PortugueseStemmer");
    languages.put("ru", "org.tartarus.snowball.ext.RussianStemmer");
    languages.put("es", "org.tartarus.snowball.ext.SpanishStemmer");
    languages.put("se", "org.tartarus.snowball.ext.SwedishStemmer");
  }

  protected SnowballProgram stemmer;
  protected Method stemMethod;
  
  public SnowballStemmer(String lang) {
    String stemClassName = (String)languages.get(lang);
    if (stemClassName == null)
      throw new OntopiaRuntimeException("Unknown language: '" + lang + "'");
    try {
      Class stemClass = Class.forName(stemClassName);
      this.stemmer = (SnowballProgram) stemClass.newInstance();
      // why doesn't the SnowballProgram class have an abstract stem method?
      stemMethod = stemClass.getMethod("stem", new Class[0]);
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  public String stem(String term) {
    stemmer.setCurrent(term);
    try {
      stemMethod.invoke(stemmer, EMPTY_ARGS);
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
    // lower-case stem
    String stem = stemmer.getCurrent();
    return (stem == null ? null : stem.toLowerCase());
  }
  
}
