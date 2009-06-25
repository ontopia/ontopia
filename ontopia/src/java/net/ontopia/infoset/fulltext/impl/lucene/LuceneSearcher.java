
// $Id: LuceneSearcher.java,v 1.24 2007/10/30 11:45:20 lars.garshol Exp $

package net.ontopia.infoset.fulltext.impl.lucene;

import java.io.IOException;

import net.ontopia.infoset.fulltext.core.DocumentIF;
import net.ontopia.infoset.fulltext.core.FieldIF;
import net.ontopia.infoset.fulltext.core.SearchResultIF;
import net.ontopia.infoset.fulltext.core.SearcherIF;
import net.ontopia.utils.CmdlineOptions;
import net.ontopia.utils.CmdlineUtils;
import net.ontopia.utils.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * PUBLIC: The Lucene search engine implementation. This searcher searches
 * documents using the Lucene search engine.
 * <p>
 */

public class LuceneSearcher implements SearcherIF {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(LuceneSearcher.class.getName());

  protected String path;
  protected Analyzer analyzer;
  protected Searcher searcher;
  protected String default_field = "content";

  /**
   * PUBLIC: Creates a searcher that will search a lucene index stored at the
   * given file system path location. Tokenization will be done using the
   * StandardAnalyzer.
   * <p>
   * 
   * @param path The file system directory in which the index is located.
   */
  public LuceneSearcher(String path) throws IOException {
    this(path, OmnigatorAnalyzer.INSTANCE);
  }

  /**
   * PUBLIC: Creates a searcher that will search a lucene index stored at the
   * given path location. Tokenization will be done using the specified
   * analyzer.
   * <p>
   * 
   * @param path The file system directory in which the index is located.
   * @param analyzer The token stream analyzer that the searcer is to use.
   */
  public LuceneSearcher(String path, Analyzer analyzer) throws IOException {
    this(FSDirectory.getDirectory(path, false), analyzer);
    this.path = path;
  }

  /**
   * PUBLIC: Creates a searcher that will search a lucene index stored in the
   * given lucene directory. Tokenization will be done using the default
   * analyzer.
   * <p>
   * 
   * @param dir The lucene directory where the index is located.
   * @param analyzer The token stream analyzer that the searcer is to use.
   * @since 3.0
   */
  public LuceneSearcher(Directory dir) throws IOException {
    this(dir, OmnigatorAnalyzer.INSTANCE);
  }

  /**
   * PUBLIC: Creates a searcher that will search a lucene index stored in the
   * given lucene directory. Tokenization will be done using the specified
   * analyzer.
   * <p>
   * 
   * @param dir The lucene directory where the index is located.
   * @param analyzer The token stream analyzer that the searcer is to use.
   */
  public LuceneSearcher(Directory dir, Analyzer analyzer) throws IOException {
    this.analyzer = analyzer;
    this.searcher = new IndexSearcher(dir);
  }

  /**
   * INTERNAL: Returns the file system path where the index used is stored. null
   * is returned if the index is not stored in a file system path.
   * <p>
   */
  public String getPath() {
    return path;
  }

  /**
   * PUBLIC: Returns the default field that lucene uses when searching.
   */
  public String getDefaultField() {
    return default_field;
  }

  /**
   * PUBLIC: Sets the default field that lucene is to use when searching.
   */
  public void setDefaultField(String default_field) {
    this.default_field = default_field;
  }

  public SearchResultIF search(String query) throws IOException {
    // ! Searcher searcher = new IndexSearcher(dir);
    try {
      log.debug("Searching for: '" + query + "'");
      Query _query = QueryParser.parse(query, default_field, analyzer);
      return new LuceneSearchResult(searcher.search(_query));
    } catch (org.apache.lucene.queryParser.ParseException e) {
      log.info("Error parsing query: '" + e.getMessage() + "'");
      throw new IOException(e.getMessage());
    }
    // ! NOTE: The Searcher must not be closed at this point since the
    // ! LuceneSearchResult instance has a pointer to the Hits instance
    // ! which is pointing to the Searcher.

    // ! finally {
    // ! // NOTE: This caused a Bad file descriptor exception to be thrown.
    // ! searcher.close();
    // ! }
  }

  public void close() throws IOException {
    searcher.close();
  }

  // -----------------------------------------------------------------------------
  // Command line
  // -----------------------------------------------------------------------------

  /**
   * INTERNAL: Command line version of the searcher.
   */
  public static void main(String[] argv) {

    // Initialize logging
    CmdlineUtils.initializeLogging();

    // Initialize command line option parser and listeners
    CmdlineOptions options = new CmdlineOptions("LuceneSearcher", argv);
    OptionsListener ohandler = new OptionsListener();

    // Register local options
    options.addLong(ohandler, "fields", 'f', true);

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
      String index = args[0];
      String query = args[1];

      // Create a searcher instance
      SearcherIF searcher = new LuceneSearcher(args[0]);
      System.out.println("Searching for '" + query + "' in index '" + index
          + "'");

      SearchResultIF result = searcher.search(query);

      String[] fields = StringUtils.split(ohandler.fields, ",");

      int hits = result.hits();
      for (int i = 0; i < hits; i++) {
        DocumentIF doc = result.getDocument(i);

        System.out.print("" + (int) (result.getScore(i) * 100) + "% ");

        for (int f = 0; f < fields.length; f++) {
          FieldIF field = doc.getField(fields[f]);
          // System.out.println("->" + fields[f] + "' " + field);
          if (field != null)
            System.out.print("\t" + field.getValue());
        }
        System.out.println();
      }
      System.out.println(hits + " hits.");

      // Close the searcher
      searcher.close();

    } catch (Exception e) {
      e.printStackTrace();
      System.exit(3);
    }
  }

  protected static void usage() {
    System.out
        .println("java net.ontopia.infoset.fulltext.impl.lucene.LuceneSearcher [options] <index> <query>");
    System.out.println();
    System.out.println("  Searches the specified Lucene index.");
    System.out.println();
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out
        .println("    --fields=<fields>: a comma separated list of field names (default: all fields are printed)");
    System.out.println();
    System.out.println("  <index>: directory that is to be searched.");
    System.out.println("  <query>: search query to execute");
  }

  private static class OptionsListener implements CmdlineOptions.ListenerIF {
    String fields = "object_id,class,content,notation,address";

    public void processOption(char option, String value)
        throws CmdlineOptions.OptionsException {
      if (option == 'f')
        fields = value;
    }
  }

}
