
package net.ontopia.topicmaps.classify;

import java.util.*;
import java.lang.reflect.Method;

import net.ontopia.utils.*;
import net.sf.snowball.SnowballProgram;

/**
 * INTERNAL: 
 */
public class SnowballStemmer implements TermStemmerIF {

  private static final Object [] EMPTY_ARGS = new Object[0];
  private static Map languages;

  static {
    languages = new HashMap();
    languages.put("dk", "net.sf.snowball.ext.DanishStemmer");
    languages.put("nl", "net.sf.snowball.ext.DutchStemmer");
    languages.put("en", "net.sf.snowball.ext.EnglishStemmer");
    languages.put("fi", "net.sf.snowball.ext.FinnishStemmer");
    languages.put("fr", "net.sf.snowball.ext.FrenchStemmer");
    languages.put("de2", "net.sf.snowball.ext.German2Stemmer");
    languages.put("de", "net.sf.snowball.ext.GermanStemmer");
    languages.put("it", "net.sf.snowball.ext.ItalianStemmer");
    languages.put("kp", "net.sf.snowball.ext.KpStemmer");
    languages.put("lovins", "net.sf.snowball.ext.LovinsStemmer");
    languages.put("no", "net.sf.snowball.ext.NorwegianStemmer");
    languages.put("porter", "net.sf.snowball.ext.PorterStemmer");
    languages.put("pt", "net.sf.snowball.ext.PortugueseStemmer");
    languages.put("ru", "net.sf.snowball.ext.RussianStemmer");
    languages.put("es", "net.sf.snowball.ext.SpanishStemmer");
    languages.put("se", "net.sf.snowball.ext.SwedishStemmer");
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
