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
import java.util.Collection;
import java.util.HashSet;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: A set of words considered "stop words" in a particular
 * language.
 */
public class StopList implements TermAnalyzerIF {

  protected Collection<String> stopList;
  protected double stopFactor = 0.0001d;

  /**
   * INTERNAL: Loads the stop list as a resource. The format of the
   * stop list is a plain text file with one word per line.
   */
  public StopList(String filename) {
    ClassLoader cloader = StopList.class.getClassLoader();
    if (cloader == null) {
      throw new OntopiaRuntimeException("Cannot find class loader.");
    }
    InputStream istream = cloader.getResourceAsStream(filename);
    if (istream == null) {
      throw new OntopiaRuntimeException("Cannot find resource: " + filename);
    }
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

  private Collection<String> load(BufferedReader reader) throws IOException {
    Collection<String> stopList = new HashSet<String>();
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
  
  @Override
  public void analyzeTerm(Term term) {
    if (isStopWord(term.getStem())) {
      term.multiplyScore(stopFactor, "stoplist adjustment");
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
