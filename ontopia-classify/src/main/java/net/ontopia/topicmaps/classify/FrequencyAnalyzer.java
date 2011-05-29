
package net.ontopia.topicmaps.classify;

import java.util.*;
import java.io.*;

import net.ontopia.utils.*;
import gnu.trove.TObjectDoubleHashMap;
import au.com.bytecode.opencsv.CSVReader;

/**
 * INTERNAL: A frequency table giving the frequency with which a
 * particular word is used in a particular language.
 */
public class FrequencyAnalyzer implements TermAnalyzerIF {
  protected TObjectDoubleHashMap freqs;

  /**
   * INTERNAL: Loads a frequency table as a resource. The format is a
   * plain text file where each line is 'term;factor' where factor is
   * a real in the range 0-1. The score of the term after
   * classification is multiplied with the factor. Thus, a factor of
   * 0.5 will reduce the score of the term by half.
   */ 
  public FrequencyAnalyzer(String filename) {
    ClassLoader cloader = FrequencyAnalyzer.class.getClassLoader();
    if (cloader == null)
      throw new OntopiaRuntimeException("Cannot find class loader.");
    InputStream istream = cloader.getResourceAsStream(filename);
    if (istream == null)
      throw new OntopiaRuntimeException("Cannot find resource: " + filename);

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

  private TObjectDoubleHashMap load(InputStream istream) {
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(istream, "utf-8"));
      TObjectDoubleHashMap freqs = new TObjectDoubleHashMap();
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

  public void analyzeTerm(Term term) {
    double total = 0;
    Variant[] variants = term.getVariants();
    for (int i=0; i < variants.length; i++) {
      Variant variant = variants[i];
      double freq = freqs.get(variant.getValue().toLowerCase());
      if (freq > 0d)
        total += freq;
      else
        total += 1d;
    }
    double average = (total / variants.length);
    if (average > 0d)
      term.multiplyScore(average, "frequency adjustment");
  }
  
  public void startAnalysis(TermDatabase tdb) {
  }

  public void endAnalysis() {
  }
  
}
