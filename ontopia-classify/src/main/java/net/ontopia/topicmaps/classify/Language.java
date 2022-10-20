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

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Object representing a particular language. The object is
 * really just a container for a stemmer, a stop list, and a frequency
 * analyzer.
 */
public class Language {

  // Define a logging category.
  private static Logger log = LoggerFactory.getLogger(Language.class.getName());

  // Initializer
  private static List<Language> languages;
  static {
    languages = new ArrayList<Language>();
    languages.add(Language.getLanguage("en"));
    languages.add(Language.getLanguage("no"));
  }

  protected String id;
  protected TermStemmerIF stemmer;
  protected StopList stoplist;
  protected FrequencyAnalyzer frequency;
  
  Language(String id) {
    this.id = id;
    this.stemmer = new SnowballStemmer(id);
    this.frequency = new FrequencyAnalyzer("net/ontopia/topicmaps/classify/lang/" + id + ".freq");
    this.stoplist = new StopList("net/ontopia/topicmaps/classify/lang/" + id + ".stop");
  }

  /**
   * INTERNAL: Used to add additional languages by passing in all
   * parameters explicitly.
   */
  public Language(String id, TermStemmerIF stemmer,
                  StopList stoplist, FrequencyAnalyzer frequency) {
    this.id = id;
    this.stemmer = stemmer;
    this.stoplist = stoplist;
    this.frequency = frequency;
  }

  public TermStemmerIF getStemmer() {
    return stemmer;
  }

  public TermAnalyzerIF getStopListAnalyzer() {
    return stoplist;
  }

  public TermAnalyzerIF getFrequencyAnalyzer() {
    return frequency;
  }    

  public int getScore(Document doc) {
    // score is the number of stop words found in the document
    StopWordCounter slc = new StopWordCounter();
    slc.stoplist = stoplist;
    doc.visitTokens(slc);
    return slc.count;
  }

  @Override
  public String toString() {
    return "Language[" + id + "]";
  }

  public static Language getLanguage(String lang) {
    return new Language(lang);
  }

  /**
   * INTERNAL: Registers a new language for use by detectLanguage.
   * <b>Warning:</b> this method is not idempotent.
   */
  public static void registerLanguage(Language lang) {
    languages.add(lang);
  }
  
  /**
   * INTERNAL: Detects the language of the document based on the
   * built-in languages and new languages registered.
   */
  public static Language detectLanguage(Document doc) {
    Language high = null;
    int highscore = -1;
    
    for (Language lang : languages) {
      int score = lang.getScore(doc);
      log.debug("Score '" + lang + "'=" + score);
      if (score >= highscore) {
        highscore = score;
        high = lang;
      }
    }
    log.debug("Detected language '" + high + "'");
    return high;
  }

  static class StopWordCounter extends TokenVisitor {
    private StopList stoplist;
    private int count;

    @Override
    public void visit(Token token) {
      if (token.getType() == Token.TYPE_VARIANT &&
          stoplist.isStopWord(token.getValue())) {
        count++;
      }
    }    
  }  
}
