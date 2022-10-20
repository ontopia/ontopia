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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: 
 */
public class CharacterAnalyzer implements TermAnalyzerIF {

  // Define a logging category.
  private static Logger log = LoggerFactory.getLogger(CharacterAnalyzer.class.getName());

  private static final CharacterAnalyzer INSTANCE = new CharacterAnalyzer();
  private static double FACTOR_NO_LETTERS = 0.05d;
  private static double FACTOR_DIGITS_AND_OTHER = 0.1d;
  private static double FACTOR_DIGITS = 0.3d;
  private static double FACTOR_OTHER = 0.8d;

  public static CharacterAnalyzer getInstance() {
    return INSTANCE;
  }
  
  @Override
  public void startAnalysis(TermDatabase tdb) {
    // no-op
  }
  
  @Override
  public void analyzeTerm(Term term) {

    // score down if term contains non-letter characters
    String stem = term.getStem();
    int length = stem.length();
    int cnt_letters = 0;
    int cnt_digits = 0;
    int cnt_other = 0;

    // count character types
    for (int i=0; i < length; i++) {
      char c = stem.charAt(i);
      if (Character.isLetter(c) || Character.isWhitespace(c)) {
        cnt_letters++;
      } else if (Character.isDigit(c)) {
        cnt_digits++;
      } else {
        cnt_other++;
      }                                    
    }

    if (log.isDebugEnabled()) {
      log.debug("t: " + term + " l: " + cnt_letters + " d: " + cnt_digits + " o: " + cnt_other);
    }

    // if term contains non-letter characters then score down
    double score = term.getScore();
    if (score > 0d) {
      if (cnt_letters == 0) {
        term.multiplyScore(FACTOR_NO_LETTERS, "no letters");
      } else if (cnt_digits > 0 && cnt_other > 0) {
        term.multiplyScore(FACTOR_DIGITS_AND_OTHER, "digits and other chars");
      } else if (cnt_digits > 0) {
        term.multiplyScore(FACTOR_DIGITS, "contains digits");
      } else if (cnt_other > 0) {
        term.multiplyScore(FACTOR_OTHER, "contains other chars");
      }
    }    
  }

  @Override
  public void endAnalysis() {
    // no-op
  }
  
}
