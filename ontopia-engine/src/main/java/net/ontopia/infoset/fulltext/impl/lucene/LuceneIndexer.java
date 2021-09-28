/*
 * #!
 * Ontopia Engine
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

package net.ontopia.infoset.fulltext.impl.lucene;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Iterator;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.fulltext.core.DocumentIF;
import net.ontopia.infoset.fulltext.core.FieldIF;
import net.ontopia.infoset.fulltext.core.IndexerIF;
import net.ontopia.infoset.fulltext.topicmaps.DefaultTopicMapIndexer;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapReader;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.utils.CmdlineOptions;
import net.ontopia.utils.CmdlineUtils;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.URIUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * INTERNAL: The Lucene indexer implementation. This indexer uses the
 * Lucene search engine to index documents.<p>
 */

public class LuceneIndexer implements IndexerIF {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(LuceneIndexer.class.getName());

  static {
    try {
      net.ontopia.net.data.Handler.install();
    } catch (SecurityException e) {
      // Fail silently if there are security issues.
    }      
  }
  
  protected String path;
  protected Directory dir;

  protected Analyzer analyzer;
  protected IndexWriter writer;
  protected IndexReader reader;
  
  /**
   * INTERNAL: Creates an indexer instance that will store its index in
   * the given file system directory. Tokenization will be done using
   * the StandardAnalyzer.<p>
   *
   * @param path The file system directory where the index is located.
   * @param create If true the indexer will create a new index (replacing
   *               the old one, if there is one). If false the indexed
   *               information will be added to the existing index.
   */  
  public LuceneIndexer(String path, boolean create) throws IOException {
    this(path, new StandardAnalyzer(Version.LUCENE_36), create);
  }
  
  /**
   * INTERNAL: Creates an indexer instance that will store its index in
   * the given file system directory and use the specified token
   * stream analyzer.<p>
   *
   * @param path The file system directory where the index is located.
   * @param analyzer The token stream analyzer that the searcer is to use.
   * @param create If true the indexer will create a new index (replacing
   *               the old one, if there is one). If false the indexed
   *               information will be added to the existing index.
   */
  public LuceneIndexer(String path, Analyzer analyzer, boolean create) throws IOException {
    this(FSDirectory.open(new File(path)), analyzer, create);
    this.path = path;
  }
  
  /**
   * INTERNAL: Creates an indexer instance that will store its index in
   * the given lucene directory and use the default token stream
   * analyzer.<p>
   *
   * @param dir The lucene directory where the index is located.
   * @param create If true the indexer will create a new index (replacing
   *               the old one, if there is one). If false the indexed
   *               information will be added to the existing index.
   * @since 3.0
   */
  public LuceneIndexer(Directory dir, boolean create) throws IOException {
    this(dir, new StandardAnalyzer(Version.LUCENE_36), create);
  }
  
  /**
   * INTERNAL: Creates an indexer instance that will store its index in
   * the given lucene directory and use the specified token stream
   * analyzer.<p>
   *
   * @param dir The lucene directory where the index is located.
   * @param analyzer The token stream analyzer that the searcer is to use.
   * @param create If true the indexer will create a new index (replacing
   *               the old one, if there is one). If false the indexed
   *               information will be added to the existing index.
   */
  public LuceneIndexer(Directory dir, Analyzer analyzer, boolean create) throws IOException {
    this.dir = dir;
    this.analyzer = analyzer;
    if (create)
      this.writer = new IndexWriter(dir, new IndexWriterConfig(Version.LUCENE_36, analyzer));
  }

  protected IndexWriter getWriter(){
    if (writer == null) {
      try {
        if (reader != null) {
          reader.close();
          reader = null;
        }
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        writer = new IndexWriter(dir, config);
      } catch (IOException e) {
        throw new OntopiaRuntimeException(e);
      }
    }
    return writer;
  }
  
  /**
   * INTERNAL: Returns the path where the index used is stored.<p>
   */    
  public String getPath() {
    return path;
  }

  /**
   * INTERNAL: Returns the number of documents stored in the index.
   */
  public synchronized int getDocs() throws IOException {
    return getWriter().numDocs();
  }
  
  public synchronized void index(DocumentIF document) throws IOException {
    getWriter().addDocument(getDocument(document));
  }
  
  public synchronized void delete(String field, String value) throws IOException {
    getWriter().deleteDocuments(new Term(field, value));
  }

  public synchronized void flush() throws IOException {
  }
  
  public synchronized void delete() throws IOException {
    // Close indexer
    close();

    // If this is a FSDirectory we can delete the directory
    // recursively.
    if (getPath() != null) FileUtils.delete(getPath(), true);
  }

  public synchronized void close() throws IOException {
    if (reader != null) {
      reader.close();
      reader = null;
    }
    if (writer != null)  {
      writer.close();
      writer = null;
    }
  }
  
  protected Document getDocument(DocumentIF document) throws IOException {
    Document lucene_document = new Document();
    Iterator<FieldIF> iter = document.getFields().iterator();
    while (iter.hasNext()) {      
      FieldIF field = iter.next();
      lucene_document.add(getField(field));
    }
    return lucene_document;
  }

  protected Field getField(FieldIF field) throws IOException {
    Field lucene_field;
    if (field.getReader() != null) {
      if (!field.isStored() && field.isIndexed() && field.isTokenized())
        lucene_field = new Field(field.getName(), field.getReader()); // Reader based field
      else {
        lucene_field = new Field(field.getName(), getStringValue(field.getReader()),
            getStoreSetting(field), getIndexSetting(field));
      }
    } else {
      lucene_field = new Field(field.getName(), field.getValue(), getStoreSetting(field), getIndexSetting(field));
    }
    return lucene_field;
  }
  
  protected Field.Store getStoreSetting(FieldIF field) {
    if (field.isStored()) {
      return Field.Store.YES;
    } else {
      return Field.Store.NO;
    }
  }
  
  protected Field.Index getIndexSetting(FieldIF field) {
    if (field.isIndexed()) {
      if (field.isTokenized()) {
        return Field.Index.ANALYZED;
      } else {
        return Field.Index.NOT_ANALYZED;
      }
    } else {
      return Field.Index.NO;
    }
  }

  protected String getStringValue(Reader reader) throws IOException {
    // Read the reader contents into a string
    StringWriter swriter = new StringWriter();
    int c;
    while ((c = reader.read()) != -1)
      swriter.write(c);
    return swriter.getBuffer().toString();
  }

  // -----------------------------------------------------------------------------
  // Command line
  // -----------------------------------------------------------------------------

  private static Analyzer createAnalyzer(String acname) {
    try {
      Class<?> aclass = Class.forName(acname);
      return (Analyzer)aclass.newInstance();
    } catch (Throwable e) {
      throw new OntopiaRuntimeException("Could not create analyzer: " + acname , e);
    }
  }

  /**
   * INTERNAL: Command line version of the indexer.
   */
  public static void main(String [] argv) {

    // Initialize logging
    CmdlineUtils.initializeLogging();
      
    // Initialize command line option parser and listeners
    CmdlineOptions options = new CmdlineOptions("LuceneIndexer", argv);
    OptionsListener ohandler = new OptionsListener();

    // Register local options
    options.addLong(ohandler, "props", 'p', true);
    options.addLong(ohandler, "syntax", 's', true);
    options.addLong(ohandler, "external", 'e');
    options.addLong(ohandler, "timeout", 't', true);
    options.addLong(ohandler, "maxthreads", 'x', true);
    options.addLong(ohandler, "preloaddir", 'd', true);
    options.addLong(ohandler, "analyzer", 'a', true);
      
    // Register logging options
    CmdlineUtils.registerLoggingOptions(options);
      
    // Parse command line options
    try {
      options.parse();
    } catch (CmdlineOptions.OptionsException e) {
      System.err.println("Error: " + e.getMessage());
      usage();
      System.exit(1);
    }

    // Get command line arguments
    String[] args = options.getArguments();    
    if (args.length != 2) {
      usage();
      System.exit(1);
    }

    try {
      // Create topic map reader
      String urlstring = args[1];
      LocatorIF url = URIUtils.getURI(urlstring);
      TopicMapReaderIF reader;

      if (ohandler.syntax == null) {
        if (ohandler.propfile == null) {
          reader = ImportExportUtils.getReader(url);
        } else {
          reader = ImportExportUtils.getReader(ohandler.propfile, urlstring);
        }
      }
      else if (ohandler.syntax.equals("ltm"))
        reader = new LTMTopicMapReader(url);
      else if (ohandler.syntax.equals("xtm")) {
        reader = new XTMTopicMapReader(url);
      } else
        throw new OntopiaRuntimeException("Unknown syntax: " + ohandler.syntax);
      
      // HACK: disable validation
      if (reader instanceof XTMTopicMapReader)
        ((XTMTopicMapReader)reader).setValidation(false);
      
      // Load the topic map
      TopicMapIF topicmap = reader.read();
      
      // Create a Lucene indexer
      IndexerIF lucene_indexer = null;
      if (ohandler.acname == null)
        lucene_indexer = new LuceneIndexer(args[0], true);
      else
        lucene_indexer = new LuceneIndexer(args[0], createAnalyzer(ohandler.acname), true);
      
      // Creates an instance of the default topic map indexer.
      DefaultTopicMapIndexer imanager = new DefaultTopicMapIndexer(lucene_indexer, ohandler.external, ohandler.preloaddir);
      
      // Indexes the topic map
      imanager.index(topicmap);
      
      // Close indexer and index manager
      imanager.close();
      lucene_indexer.close();
      
    }
    catch (Exception e) {
      e.printStackTrace();
      System.exit(3);
    }
  }
  
  protected static void usage() {
    System.out.println("java net.ontopia.infoset.fulltext.impl.lucene.LuceneIndexer [options] <index> <url> [<analyzer>]");
    System.out.println();
    System.out.println("  Indexes the specified topic map using the Lucene fulltext indexer.");
    System.out.println();
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("    --props=<propfile>: the topic map store properties file");
    System.out.println("    --syntax=[xtm|ltm]: the syntax of input document");
    System.out.println("    --external: download and index external resources (default: false)");
    System.out.println("    --timeout=<ms>: timeout in millisecs when downloading a resource (default: 60000)");
    System.out.println("    --maxthreads=<number>: maximum concurrent preloaders (default: 10)");
    System.out.println("    --preloaddir=<dir>: directory in which preloaded documents are stored");
    System.out.println("                  (default: $user.dir/preloader)");
    System.out.println("    --analyzer=<classname>: class name of the analyzer to use");
    System.out.println();
    System.out.println("  <index>: directory that is to contain the result index.");
    System.out.println("  <url>: source topic map");
  }

  private static class OptionsListener implements CmdlineOptions.ListenerIF {
    String propfile;
    String syntax;
    boolean external = false;
    int timeout = 60000;
    int max_threads = 10;
    String preloaddir = System.getProperty("user.dir") + File.separator + "preloader";
    String acname;
    public void processOption(char option, String value) throws CmdlineOptions.OptionsException {
      if (option == 'e')
        external = true;
      else if (option == 's')
        syntax = value;
      else if (option == 'p')
        propfile = value;
      else if (option == 't')
        timeout = Integer.parseInt(value);
      else if (option == 'd')
        preloaddir = value;
      else if (option == 'x')
        max_threads = Integer.parseInt(value);
      else if (option == 'a')
        acname = value;
    }
  }
    
}
