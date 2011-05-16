
// $Id: StopList.java,v 1.8 2007/10/30 13:46:16 lars.garshol Exp $

package net.ontopia.topicmaps.classify;

import java.util.*;
import java.io.*;

import net.ontopia.utils.*;

/**
 * INTERNAL: A set of words considered "stop words" in a particular
 * language.
 */
public class StopList implements TermAnalyzerIF {

  protected Collection stopList;
  protected double stopFactor = 0.0001d;

  /**
   * INTERNAL: Loads the stop list as a resource. The format of the
   * stop list is a plain text file with one word per line.
   */
  public StopList(String filename) {
    ClassLoader cloader = StopList.class.getClassLoader();
    if (cloader == null)
      throw new OntopiaRuntimeException("Cannot find class loader.");
    InputStream istream = cloader.getResourceAsStream(filename);
    if (istream == null)
      throw new OntopiaRuntimeException("Cannot find resource: " + filename);
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
      try {
        this.stopList = load(reader);
      } finally {
        reader.close();
      }
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  /**
   * INTERNAL: Loads the stop list from a file. The format of the stop
   * list is a plain text file with one word per line.
   */
  public StopList(File file) {
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
      try {
        this.stopList = load(reader);
      } finally {
        reader.close();
      }
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  private Collection load(BufferedReader reader) throws IOException {
    Collection stopList = new HashSet();
    String line = null;
    while ((line = reader.readLine()) != null) {
      // downcase before adding to list
      stopList.add(line.trim().toLowerCase());
    }
    return stopList;
  }

  public void setStopFactor(double stopFactor) {
    this.stopFactor = stopFactor;
  }

  public boolean isStopWord(String word) {
    return stopList.contains(word);
  }
  
  public void analyzeTerm(Term term) {
    if (isStopWord(term.getStem()))
      term.multiplyScore(stopFactor, "stoplist adjustment");
  }
  
  public void startAnalysis(TermDatabase tdb) {
  }

  public void endAnalysis() {
  }
  
}
