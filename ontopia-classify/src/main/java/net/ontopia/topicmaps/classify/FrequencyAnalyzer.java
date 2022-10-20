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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import net.ontopia.utils.OntopiaRuntimeException;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import au.com.bytecode.opencsv.CSVReader;

/**
 * INTERNAL: A frequency table giving the frequency with which a
 * particular word is used in a particular language.
 */
public class FrequencyAnalyzer implements TermAnalyzerIF {
  protected TObjectDoubleHashMap<String> freqs;

  /**
   * INTERNAL: Loads a frequency table as a resource. The format is a
   * plain text file where each line is 'term;factor' where factor is
   * a real in the range 0-1. The score of the term after
   * classification is multiplied with the factor. Thus, a factor of
   * 0.5 will reduce the score of the term by half.
   */ 
  public FrequencyAnalyzer(String filename) {
    ClassLoader cloader = FrequencyAnalyzer.class.getClassLoader();
    if (cloader == null) {
      throw new OntopiaRuntimeException("Cannot find class loader.");
    }
    InputStream istream = cloader.getResourceAsStream(filename);
    if (istream == null) {
      throw new OntopiaRuntimeException("Cannot find resource: " + filename);
    }

    this.freqs = load(istream);
    // istream is closed inside load
  }
  
  /**
   * INTERNAL: Loads a frequency table from a file. The format is a
   * plain text file where each line is 'term;factor' where factor is
   * a real in the range 0-1. The score of the term after
   * classification is multiplied with the factor. Thus, a factor of
   * 0.5 will reduce the score of the term by half.
   */ 
  public FrequencyAnalyzer(File file) {
    FileInputStream istream = null;
    try {
      istream = new FileInputStream(file);
      this.freqs = load(istream);
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
    // istream is closed inside load
  }

  private TObjectDoubleHashMap<String> load(InputStream istream) {
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(istream, "utf-8"));
      TObjectDoubleHashMap<String> freqs = new TObjectDoubleHashMap<String>();
      char separator = ';'; 
      char quoteCharacter = '"';
      CSVReader csv = new CSVReader(reader, separator, quoteCharacter);
      try {
        String [] tuple = null;
        while ((tuple = csv.readNext()) != null) {
          String term = tuple[0].toLowerCase();
          double factor = Double.parseDouble(tuple[1]);
          freqs.put(term, factor);
        }
      } finally {
        csv.close();
      }
      return freqs;
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    } finally {
      try {
        istream.close();
      } catch (IOException e) {
        throw new OntopiaRuntimeException(e);
      }
    }
  }

  @Override
  public void analyzeTerm(Term term) {
    double total = 0;
    Variant[] variants = term.getVariants();
    for (int i=0; i < variants.length; i++) {
      Variant variant = variants[i];
      double freq = freqs.get(variant.getValue().toLowerCase());
      if (freq > 0d) {
        total += freq;
      } else {
        total += 1d;
      }
    }
    double average = (total / variants.length);
    if (average > 0d) {
      term.multiplyScore(average, "frequency adjustment");
    }
  }
  
  @Override
  public void startAnalysis(TermDatabase tdb) {
    // no-op
  }

  @Override
  public void endAnalysis() {
    // no-op
  }
  
}
