
// $Id: Generate.java,v 1.17 2008/06/11 16:56:01 geir.gronmo Exp $

package net.ontopia.topicmaps.schema.impl.osl.cmdline;

import java.io.*;
import java.util.*;

import org.xml.sax.Locator;

import net.ontopia.utils.CmdlineUtils;
import net.ontopia.utils.CmdlineOptions;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.utils.*;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.schema.core.*;
import net.ontopia.topicmaps.schema.impl.osl.*;

import org.apache.log4j.Logger;

public class Generate {

  /**
   * default constructor.
   */
  public Generate() {
    super();
  }

  /**
   * Creates a schema from the given topic map which is read in fist.
   *
   * @param input filename of the input topic map.
   */
  public OSLSchema createSchema(String input) throws IOException {
    TopicMapIF tm = ImportExportUtils.getReader(input).read();
    return createSchema(tm);
  }

  /**
   * Creates a schema from the given topic map. This method is invoked
   * by the schema export plug-in.
   * @param tm the topicmap to create a schema for.
   */
  public OSLSchema createSchema(TopicMapIF tm) {
    OSLSchema schema = new OSLSchema(null);
    
    ClassInstanceIndexIF index = (ClassInstanceIndexIF) tm
      .getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
    
    generateTopics(schema, index.getTopicTypes(), index);
    generateAssociations(schema, index.getAssociationTypes(), index);
    
    return schema;
  }


  /**
   * Generates the topic types in the schema
   */
  private void generateTopics(OSLSchema schema,
                              Collection ttypes,
                              ClassInstanceIndexIF index) {
    Iterator it = ttypes.iterator();
    while (it.hasNext()) {
      TopicIF ttype = (TopicIF)it.next();

      TopicClassAnalyzer analyzer = new TopicClassAnalyzer(schema, ttype, index.getTopics(ttype));
      analyzer.analyze();
    }
    
    // Find and register superclasses.
    it = ttypes.iterator();
    while (it.hasNext()) {
      TopicIF ttype = (TopicIF)it.next();          
      // Get the superclasses for this topic, 1 level up
      TypeHierarchyUtils hierUtils = new TypeHierarchyUtils();
      Collection superclasses = hierUtils.getSuperclasses(ttype, 1);
      TopicClass tclass = schema.getTopicClass("tc" + ttype.getObjectId());
      if (tclass != null) {
        Iterator it2 = superclasses.iterator();
        while (it2.hasNext()) {
          TopicIF stopic = (TopicIF) it2.next();
          TopicClass sclass = schema.getTopicClass("tc" + stopic.getObjectId());
          if (sclass != null) {
            tclass.setSuperclass(sclass);
          }
        }
      }
    }
  }

  /**
   * Generates the associations in the schema
   */
  private void generateAssociations(OSLSchema schema,
                                    Collection atypes,
                                    ClassInstanceIndexIF index) {
    Iterator it = atypes.iterator();
    while (it.hasNext()) {
      TopicIF atype = (TopicIF)it.next();

      AssociationClassAnalyzer analyzer = 
        new AssociationClassAnalyzer(schema, atype, index.getAssociations(atype));
      analyzer.analyze();
    }
  }



  //-----------------------------------------
  // The commandline version
  //-----------------------------------------

  public static void main(String[] argv) {
    // Initialize logging
    CmdlineUtils.initializeLogging();
      
    // Initialize command line option parser and listeners
    CmdlineOptions options = new CmdlineOptions("SchemaGenerator", argv);
      
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
      usage();
      System.exit(1);
    }

    try {
      Generate gen = new Generate();
      OSLSchema schema = gen.createSchema(args[0]);
      if (args[1] != null)
        new OSLSchemaWriter(new File(args[1]), "utf-8").write(schema);
      else
        System.err.println("Error: No schema filename given.");      
    } catch (IOException e) {
      System.out.print("Error creating the schema : " + e);
      System.exit(1);
    }

  }

  private static void usage() {
    System.out.println("Generate [options] <in> <out>");
    System.out.println("");
    System.out.println("  Reads a topic map in and writes it out a suitable schema.");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("");
    System.out.println("    <in>:  url or file name of topic map to be converted");
    System.out.println("    <out>: file to write the schema to");
    System.out.println("");
  }
}
