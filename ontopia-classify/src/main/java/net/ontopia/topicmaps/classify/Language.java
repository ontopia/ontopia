
// $Id: Language.java,v 1.9 2007/10/30 13:46:16 lars.garshol Exp $

package net.ontopia.topicmaps.classify;

import java.util.*;
import java.io.*;

import net.ontopia.utils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Object representing a particular language. The object is
 * really just a container for a stemmer, a stop list, and a frequency
 * analyzer.
 */
public class Language {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(Language.class.getName());

  // Initializer
  private static List languages;
  static {
    languages = new ArrayList();
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
    
    Iterator it = languages.iterator();
    while (it.hasNext()) {
      Language lang = (Language) it.next();
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
    StopList stoplist;
    int count;

    public void visit(Token token) {
      if (token.getType() == Token.TYPE_VARIANT &&
          stoplist.isStopWord(token.getValue()))
        count++;
    }    
  }  
}
