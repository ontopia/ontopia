
package net.ontopia.topicmaps.cmdlineutils;

import java.util.*;
import java.io.File;

import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.xml.XTMTopicMapWriter;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.topicmaps.utils.KeyGenerator;
import net.ontopia.topicmaps.impl.basic.index.TNCIndex;

/**
 * INTERNAL: Consistifies a topic map by merging topics based on the
 * TNC, removing duplicate associations, and so on.</p>
 */

public class Consistify {

  public static void main(String [] argv) {

    // Initialize logging
    CmdlineUtils.initializeLogging();
      
    // Initialize command line option parser and listeners
    CmdlineOptions options = new CmdlineOptions("Consistify", argv);
    OptionsListener ohandler = new OptionsListener();

    // Register local options
    options.addLong(ohandler, "normalize", 'n');
    options.addLong(ohandler, "encoding", 'e');
    options.addLong(ohandler, "xtm", 'x');
      
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
    if (args.length < 2) {
      System.err.println("Error: need at least two files as arguments.");
      usage();
      System.exit(1);
    }

    try {
      TopicMapIF loaded = load(args[0]);
      if (ohandler.normalize)
        normalizeTopicNames(loaded);
      doTNCMerge(loaded);
      removeDuplicates(loaded);

      char format = '?';
      if (ohandler.xtm)
        format = 'x';
      
      export(loaded, args[1], ohandler.encoding, format);
    }
    catch (java.io.IOException e) {
      System.err.println(e);
      System.exit(3);
    }
  }
  
  protected static void usage() {
    System.out.println("java Consistify [options] <input> <output>");
    System.out.println("");
    System.out.println("  Reads in a topic map, consistifies it, then writes it out again.");
    System.out.println("");
    System.out.println("  Options:");
    System.out.println("  -n: normalize whitespace in base names");
    System.out.println("  -e <encoding>: set output encoding");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("");
    System.out.println("    <input>: source topic map");
    System.out.println("    <output>: output topic map");
  }

  protected static TopicMapIF load(String stm) throws java.io.IOException {
    return ImportExportUtils.getReader(stm).read();
  }

  protected static void doTNCMerge(TopicMapIF tm) {
    TNCIndex index = new TNCIndex(tm);
    
    Iterator it = new ArrayList(tm.getTopics()).iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      if (topic.getTopicMap() == null)
        continue;

      Iterator it2 = new ArrayList(topic.getTopicNames()).iterator();
      while (it2.hasNext()) {
        TopicNameIF bn = (TopicNameIF) it2.next();

        Iterator it3 =index.getTopics(bn.getValue(), bn.getScope()).iterator();
        while (it3.hasNext()) {
          TopicIF source = (TopicIF) it3.next();
          if (source.equals(topic))
            continue;
          MergeUtils.mergeInto(topic, source);
        }
      }
    }
  }

  protected static void export(TopicMapIF tm, String outfile, String encoding,
                               char format)
    throws java.io.IOException {

    if (encoding == null)
      encoding = "utf-8";

    if (format == 'e')
      new XTMTopicMapWriter(new File(outfile), encoding).write(tm);
    else
      ImportExportUtils.getWriter(outfile, encoding).write(tm);
  }

  protected static void normalizeTopicNames(TopicMapIF tm) {
    Iterator it = tm.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();

      Iterator it2 = topic.getTopicNames().iterator();
      while (it2.hasNext()) {
        TopicNameIF bn = (TopicNameIF) it2.next();
        bn.setValue(StringUtils.normalizeWhitespace(bn.getValue()));
      }
    }
  }

  protected static void removeDuplicates(TopicMapIF tm) {
    Map keymap = new HashMap();
    AssociationIF[] assocs = new AssociationIF[tm.getAssociations().size()];
    tm.getAssociations().toArray(assocs);
    for (int i=0; i < assocs.length; i++) {
      AssociationIF assoc = assocs[i];
      String key = KeyGenerator.makeAssociationKey(assoc);
      if (keymap.containsKey(key)) {
        System.out.println("Removing: " + key);
        // If map contains key remove this association
        assoc.remove();
      } else {
        keymap.put(key, null);
      }
    }
  }
  
  // --- Listener class
  
  private static class OptionsListener implements CmdlineOptions.ListenerIF {
    boolean normalize = false;
    boolean xtm = false;
    String encoding = null;
    
    public void processOption(char option, String value)
      throws CmdlineOptions.OptionsException {
      if (option == 'n') normalize = true;
      if (option == 'x') xtm = true;
      if (option == 'e') encoding = value;
    }
  }
  
}
