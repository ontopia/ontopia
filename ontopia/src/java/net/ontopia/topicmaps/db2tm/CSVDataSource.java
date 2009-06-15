
// $Id: CSVDataSource.java,v 1.18 2006/12/01 14:06:09 grove Exp $

package net.ontopia.topicmaps.db2tm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.ontopia.utils.OntopiaRuntimeException;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;

/**
 * INTERNAL: Data source that reads CSV files from a directory.
 */
public class CSVDataSource implements DataSourceIF {

  // --- define a logging category.
  static Logger log = Logger.getLogger(CSVDataSource.class.getName());

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
    private TupleReader(File csvfile) {
      try {
        Reader r;
        if (encoding == null)
          r = new InputStreamReader(new FileInputStream(csvfile));
        else
          r = new InputStreamReader(new FileInputStream(csvfile), encoding);
        this.reader = new CSVReader(r, separator, quoteCharacter);
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
    }

  }

}
