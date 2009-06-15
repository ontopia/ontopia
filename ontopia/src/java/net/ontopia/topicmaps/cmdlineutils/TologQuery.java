
// $Id: TologQuery.java,v 1.10 2008/06/25 11:17:59 lars.garshol Exp $

package net.ontopia.topicmaps.cmdlineutils;

import java.io.IOException;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.query.core.*;
import net.ontopia.topicmaps.query.utils.*;
import net.ontopia.topicmaps.query.impl.basic.QueryTracer;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.utils.DuplicateSuppressionUtils;

/**
 * PUBLIC: Runs tolog queries against a given topic map.
 */
public class TologQuery {

  public static void main(String [] argv) {

    // Initialize logging
    CmdlineUtils.initializeLogging();
      
    // Initialize command line option parser and listeners
    CmdlineOptions options = new CmdlineOptions("TologQuery", argv);
    OptionsListener ohandler = new OptionsListener();
      
    // Register local options
    options.addLong(ohandler, "trace", 't');
    options.addLong(ohandler, "debug", 'd');
    
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
      System.err.println("Error: Must have exactly two arguments!");
      usage();
      System.exit(1);
    }

    // Set up tracing
    if (ohandler.trace)
      QueryTracer.addListener(new OutQueryTracer());
    
    try {
      // Run the query
      runquery(args[0], args[1], ohandler);
    }
    catch (java.net.MalformedURLException e) {
      System.err.println(e);
      System.exit(2);
    }
    catch (java.io.IOException e) {
      System.err.println(e);
      System.exit(2);
    }
    catch (InvalidQueryException e) {
      System.err.println(e);
      //throw new net.ontopia.utils.OntopiaRuntimeException(e);
      System.exit(2);
    }
  }

  private static void usage() {
    System.out.println("TologQuery [options] <tm> <query>");
    System.out.println("");
    System.out.println("  Runs a tolog query against a topic map.");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("    --trace: turn on query tracing");
    System.out.println("");
    System.out.println("    <tm>:  url or file name of topic map to be queried");
    System.out.println("    <query>: query string or file containing query");
    System.out.println("");
  }

  private static void runquery(String tmfile, String qryfile,
                               OptionsListener ohandler)
    throws IOException, java.net.MalformedURLException, InvalidQueryException {

    TopicMapIF tm = ImportExportUtils.getReader(tmfile).read();
    // run duplicate suppression only on in-memory topic maps. too expensive on
    // rdbms topic maps.
    if (tm instanceof net.ontopia.topicmaps.impl.basic.TopicMap)
      DuplicateSuppressionUtils.removeDuplicates(tm);

    String query;
    if (qryfile.trim().endsWith("?"))
      query = qryfile;
    else
      query = StreamUtils.read(new java.io.FileReader(qryfile));

    QueryProcessorIF processor = QueryUtils.getQueryProcessor(tm);
    ParsedQueryIF pquery = processor.parse(query);

    if (ohandler.debug) {
      System.out.println("Parsed query: " + pquery + "\n\n");

      net.ontopia.topicmaps.query.impl.basic.ParsedQuery pq =
        (net.ontopia.topicmaps.query.impl.basic.ParsedQuery) pquery;
      java.util.Iterator it = pq.getAllVariables().iterator();
      while (it.hasNext()) {
        String var = (String) it.next();
        System.out.println(var + ": " +
                           net.ontopia.utils.DebugUtils.toString(pq.getVariableTypes(var)));
      }
    }

    long time = System.currentTimeMillis();
    QueryResultIF result = pquery.execute();
    System.out.println("Query time: " + (System.currentTimeMillis() - time));

    int rows = 0;
                                          
    while (result.next()) {
      for (int ix = 0; ix < result.getWidth(); ix++)
        System.out.print(result.getValue(ix) + "\t");
      System.out.println("");
      rows++;
    }

    System.out.println("Rows: " + rows);
  }

  private static class OptionsListener implements CmdlineOptions.ListenerIF {
    boolean trace;
    boolean debug;
    
    public void processOption(char option, String value)
      throws CmdlineOptions.OptionsException {
      if (option == 't') trace = true;
      if (option == 'd') debug = true;
    }
  }

  private static class OutQueryTracer extends QueryTracer.TracePrinter {
    public boolean isEnabled() {
      return true;
    }

    public void output(String message) {
      System.out.println(message);
      System.out.flush();
    }  
  }
}
