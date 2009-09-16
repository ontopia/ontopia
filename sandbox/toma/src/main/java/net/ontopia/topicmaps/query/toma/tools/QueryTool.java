package net.ontopia.topicmaps.query.toma.tools;

import java.io.IOException;
import java.net.MalformedURLException;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.basic.TopicMap;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.ParsedQueryIF;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicQueryProcessor;
import net.ontopia.topicmaps.query.toma.impl.utils.Stringifier;
import net.ontopia.topicmaps.utils.DuplicateSuppressionUtils;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.utils.CmdlineOptions;
import net.ontopia.utils.CmdlineUtils;

public class QueryTool {

  public static void main(String [] argv) {

    // Initialize logging
    CmdlineUtils.initializeLogging();
      
    // Initialize command line option parser and listeners
    CmdlineOptions options = new CmdlineOptions("QueryTool", argv);
    OptionsListener ohandler = new OptionsListener();
      
    // Register local options
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
      System.exit(2);
    }
   }

   private static void usage() {
    System.out.println("QueryTool [options] <tm> <query>");
    System.out.println("");
    System.out.println("  Runs a toma query against a topic map.");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("    --debug: turn on debugging output");
    System.out.println("");
    System.out.println("    <tm>:  url or file name of topic map to be queried");
    System.out.println("    <query>: query string or file containing query");
    System.out.println("");
   }

   private static void runquery(String tmfile, String query,
                               OptionsListener ohandler)
    throws IOException, MalformedURLException, InvalidQueryException {

    TopicMapIF tm = ImportExportUtils.getReader(tmfile).read();
    // run duplicate suppression only on in-memory topic maps. too expensive on
    // rdbms topic maps.
    if (tm instanceof TopicMap)
      DuplicateSuppressionUtils.removeDuplicates(tm);

    if (ohandler.debug) {
      System.out.println("Query: " + query + "\n");
    }
    
    QueryProcessorIF processor = new BasicQueryProcessor(tm);
    ParsedQueryIF pquery = processor.parse(query);

    if (ohandler.debug) {
      System.out.println("Parsed query: \n" + pquery + "\n");
    }

    long time = System.currentTimeMillis();
    QueryResultIF result = pquery.execute();
    System.out.println("Query time: " + (System.currentTimeMillis() - time) + "ms\n");

    int rows = 0;

    for (int ix = 0; ix < result.getWidth(); ix++)
      System.out.print(String.format("%1$30s |", result.getColumnName(ix)));
    System.out.println();
    for (int ix = 0; ix < result.getWidth(); ix++) 
      System.out.print("-------------------------------|");
    System.out.println();
    
    while (result.next()) {
      for (int ix = 0; ix < result.getWidth(); ix++)
        System.out.print(String.format("%1$30s |", getString(Stringifier.toString(result.getValue(ix)), 30)));
      System.out.println("");
      rows++;
    }
    
    System.out.println("\nRows: " + rows);
   }

   private static String getString(String str, int maxLength) {
     if (str == null) return null;
     if (str.length() > maxLength) {
       return "..." + str.substring(str.length() - maxLength + 4, str.length());
     } else {
       return str;
     }
   }
   
   private static class OptionsListener implements CmdlineOptions.ListenerIF {
    boolean debug;
    
    public void processOption(char option, String value)
      throws CmdlineOptions.OptionsException {
      if (option == 'd') debug = true;
    }
   }
}
