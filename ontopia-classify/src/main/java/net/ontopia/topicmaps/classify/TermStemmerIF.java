
package net.ontopia.topicmaps.classify;

import java.util.Collection;

import net.ontopia.utils.*;

/**
 * INTERNAL: A stemmer produces the stem of a word from a form of the
 * word. That is, "stemmer", "stemming", "stemmed", and "stem" should
 * all yield "stem".
 */
public interface TermStemmerIF {

  /**
   * INTERNAL: Return the stem of the term.
   */
  public String stem(String term);
  
}
