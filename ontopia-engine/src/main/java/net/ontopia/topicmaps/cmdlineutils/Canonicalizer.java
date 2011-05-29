
package net.ontopia.topicmaps.cmdlineutils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapImporterIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.utils.DuplicateSuppressionUtils;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;
import net.ontopia.topicmaps.xml.InvalidTopicMapException;
import net.ontopia.utils.CmdlineOptions;
import net.ontopia.utils.CmdlineUtils;
import net.ontopia.utils.URIUtils;

/**
 * PUBLIC: Reads a topic map and writes it out in ISO CXTM.
 */
public class Canonicalizer {
  
  public static void main(String [] argv) {
    // Initialize logging
    CmdlineUtils.initializeLogging();
      
    // Initialize command line option parser and listeners
    CmdlineOptions options = new CmdlineOptions("Canonicalizer", argv);
    OptionsListener ohandler = new OptionsListener();

    // Register local options
    options.addLong(ohandler, "readall", 'a', false);
      
    // Register logging options
    CmdlineUtils.registerLoggingOptions(options);
      
    // Parse command line options
    try {
      options.parse();
    } catch (CmdlineOptions.OptionsException e) {
      System.err.println("Error: " + e.getMessage());
      System.exit(1);      
    }

    // Get command line arguments
    String[] args = options.getArguments();
    
    if (args.length != 2) {
      usage();
      System.exit(1);
    }

    try {

      // Validate or transform <url> into a URL
      LocatorIF url = URIUtils.getURI(args[0]);
      
      // Canonicalize document
      canonicalize(url, args[1], ohandler.readall);
    }
    catch (MalformedURLException e) {
      System.err.println(e);
      System.exit(2);
    }
    catch (IOException e) {
      System.err.println(e);
      System.exit(2);
    }
  }

  protected static void usage() {
    System.out.println("");
    System.out.println("java net.ontopia.topicmaps.cmdlineutils.Canonicalizer [options] <in> <out>");
    System.out.println("");
    System.out.println("  Reads a topic map and writes it out in canonical form");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("    --readall: reads all topic maps  in the document");
    System.out.println("");
    System.out.println("  <in>:  file name or url of source topic map");
    System.out.println("  <out>: file to write canonical version to");
  }

  protected static void canonicalize(LocatorIF stm, String ctm, boolean readall) 
    throws IOException, MalformedURLException {
    TopicMapIF source;
    
    try {
      if (readall) {
        TopicMapImporterIF importer = ImportExportUtils.getImporter(stm);          
        source = new InMemoryTopicMapStore().getTopicMap();
        importer.importInto(source);
      } else {
        TopicMapReaderIF reader = ImportExportUtils.getReader(stm);          
        source = reader.read();
      }

      DuplicateSuppressionUtils.removeDuplicates(source);      
    }
    catch (InvalidTopicMapException e) {
      System.err.println("ERROR reading file: " + e.getMessage());
      return;
    }

    FileOutputStream out = new FileOutputStream(ctm);
    CanonicalXTMWriter cwriter = new CanonicalXTMWriter(out);
    cwriter.write(source);
    out.close();
  }

  private static class OptionsListener implements CmdlineOptions.ListenerIF {
    boolean readall = false;
    public void processOption(char option, String value) throws CmdlineOptions.OptionsException {
      if (option == 'a')
        readall = true;
    }
  }
  
}






