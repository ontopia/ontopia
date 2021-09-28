/*
 * #!
 * Ontopia DB2TM
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

package net.ontopia.topicmaps.db2tm;

import au.com.bytecode.opencsv.CSVReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import net.ontopia.utils.OntopiaRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Data source that reads CSV files from a directory.
 */
public class CSVDataSource implements DataSourceIF {

  // --- define a logging category.
  static Logger log = LoggerFactory.getLogger(CSVDataSource.class.getName());

  protected RelationMapping mapping;

  protected File path;

  protected String encoding = "iso-8859-1";
  protected char separator = ';'; 
  protected char quoteCharacter = '"'; 
  //! protected char escaping = '\\';
  protected int ignoreFirstLines = 0;
  
  CSVDataSource(RelationMapping mapping) {
    this.mapping = mapping;
  }

  void setPath(String _path) {
    File baseDirectory = mapping.getBaseDirectory();
    File path = new File(_path);
    if (baseDirectory != null && !path.isAbsolute())
      this.path = new File(baseDirectory, _path);
    else
      this.path = path;
    if (!this.path.exists())
      throw new DB2TMException("CSV data source path " + this.path + " does not exist.");
  }
  
  void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  void setSeparator(char separator) {
    this.separator = separator;
  }

  void setQuoteCharacter(char quoteCharacter) {
    this.quoteCharacter = quoteCharacter;
  }

  //! void setEscaping(char escaping) {
  //!   this.escaping = escaping;
  //! }

  void setIgnoreFirstLines(int ignoreFirstLines) {
    this.ignoreFirstLines = ignoreFirstLines;
  }

  public Collection getRelations() {
    Collection relations = new ArrayList();
    // scan directory to find csv files    
    String[] files = path.list();
    for (int i=0;  i< files.length; i++) {
      String filename = files[i];
      Relation relation = mapping.getRelation(filename);
      //! if (relation == null && filename.endsWith(".csv"))
      //!   relation = mapping.getRelation(filename.substring(0, filename.length() - 4));
      if (relation != null)
        relations.add(relation);
      else
        log.debug("No mapping found for file '" + filename + "'.");
    }
    return relations;
  }

  public TupleReaderIF getReader(String relation) {
    File file = new File(path, relation);
    if (!file.exists()) throw new DB2TMException("Unknown relation: " + relation);
    return new TupleReader(file);
  }

  public ChangelogReaderIF getChangelogReader(Changelog changelog, String startOrder) {
    throw new UnsupportedOperationException();
  }

  public String getMaxOrderValue(Changelog changelog) {
    throw new UnsupportedOperationException();
  }

  public void close() {
    // no-op
  }
  
  public String toString() {
    return "CSVDataSource[path=" + path + "]";
  }

  private class TupleReader implements TupleReaderIF {
    private CSVReader reader;
    private Reader in;
    
    private TupleReader(File csvfile) {
      try {
        if (encoding == null)
          in = new InputStreamReader(new FileInputStream(csvfile));
        else
          in = new InputStreamReader(new FileInputStream(csvfile), encoding);
        this.reader = new CSVReader(in, separator, quoteCharacter);
        // ignore first N lines
        for (int i=0; i < ignoreFirstLines; i++) {
          java.util.Arrays.asList(readNext());
        }
      } catch (Throwable e) {
        throw new OntopiaRuntimeException(e);
      }
    }

    public String[] readNext() {
      try {
        return reader.readNext();
      } catch (java.io.IOException e) {
        throw new OntopiaRuntimeException(e);
      }
    }

    public void close() {
      try {
        if (in != null)
          in.close();
      } catch (IOException e) {
        throw new OntopiaRuntimeException(e);
      }
    }
  }
}
