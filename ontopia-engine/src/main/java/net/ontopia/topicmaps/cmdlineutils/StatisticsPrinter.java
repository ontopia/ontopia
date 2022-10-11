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

package net.ontopia.topicmaps.cmdlineutils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.cmdlineutils.statistics.NoTypeCount;
import net.ontopia.topicmaps.cmdlineutils.statistics.TopicAssocDep;
import net.ontopia.topicmaps.cmdlineutils.statistics.TopicCounter;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.utils.CmdlineOptions;
import net.ontopia.utils.CmdlineUtils;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * PUBLIC: Prints various kinds of statistics for topic maps.</p>
 */


public class StatisticsPrinter {

  protected static BufferedReader stdInn = new BufferedReader(new InputStreamReader(System.in));
  protected TopicMapIF tm;

  /**
   * Constructor that accepts a topicmap as argument.
   */
  public StatisticsPrinter(TopicMapIF tm) {
    this.tm = tm;
  }

  /**
   * Used to request a filename when none is given.
   */
  protected static String requestFile() {
    String name = ""; 
    System.out.print("Please enter TopicMap file name: ");
    System.out.flush();
    try {
      name = stdInn.readLine().trim();
    } catch (IOException e) {
      System.out.println("Error : " + e);
    }
    return name;
  }

  /**
   * INTERNAL: Method that counts the number of TAO's, counts the
   * number of occurrences of each combination of association roles,
   * and counts the number topics, associations and occurrences 
   * that have no types.
   */
  private void topicStats() {
    //Count the number of topics, assocs, occrs, and print to screeen.
    countTopics();
          
    //Count all the assoc depedencies, and print to screen.
    countAssociationDep();
    
    //Get all the topics without type, and print them to screen.
    //getNoTypeTopics();
      
  }

  /**
   * Handles all the counting of different topics.
   */
  protected void countTopics() {

    TopicCounter topiccounter = new TopicCounter(tm);
    topiccounter.count();
    
    //Get the result from the topiccounter object.
    int numberOfTopics = topiccounter.getNumberOfTopics();
    int numberOfAssociations = topiccounter.getNumberOfAssociations();
    int numberOfOccurrences = topiccounter.getNumberOfOccurrences();
    HashMap topicTypes = topiccounter.getTopicTypes();
    HashMap assocTypes = topiccounter.getAssociationTypes();
    HashMap ocursTypes = topiccounter.getOccurrenceTypes();

    //Print out the result.
    //This section prints the number of different elements in a 
    //topic map.
    print("        Topic Map Count Result:          \n\n\n");
    print("Number of Topics:       " + numberOfTopics + "\n");
    print("Number of Associations: " + numberOfAssociations + "\n");
    print("Number of Occurrences:  " + numberOfOccurrences + "\n");
    print("---------------------------------------\n");
    print("Number of Taos:         " + (numberOfTopics+ numberOfAssociations
                                        + numberOfOccurrences) + "\n");

    print("=======================================\n\n\n");
    print("                 Types:           \n");



    //The topic types.
    //*******************************************************************    

    print("\n\n     Topics:\n\n");
    if (topicTypes.size() > 0) {
        print("\n" + format("Number of different topic types") + ": " + 
              topicTypes.keySet().size() + "\n\n");
        //Sort the out alphabetically
        String[] templist = sortAlpha(topicTypes.keySet());
        
        int i = 0;
        while (i < templist.length) {
          String t = templist[i];
          print(format(t) + ": " + ((Integer)topicTypes.get(t)).intValue() 
                + "\n");
          i++;
        }
    } else {
        print("There are no topics with type in this topic map.\n");
    }
    
    
    //The association types.
    //*******************************************************************    
    
    print("\n\n     Associations:     \n\n");
    if (assocTypes.size() > 0) {
      print("\n" + format("Number of different association types") + ": " + 
            assocTypes.keySet().size() + "\n\n");

      String[] templist = sortAlpha(assocTypes.keySet());
        
      int i = 0;
      while (i < templist.length) {
        String t = templist[i];
        print(format(t) + ": " + ((Integer)assocTypes.get(t)).intValue() 
              + "\n");
        i++;
      }
      
      //Iterator it = assocTypes.keySet().iterator();
      //while (it.hasNext()) {
      //  String t = (String)it.next();
      //  print(format(t) + ": " + ((Integer)assocTypes.get(t)).intValue() 
      //        + "\n");
      //}
    } else {
      print("There are no assocations with type in this topicmap.\n");
    }
    
    
    //The ocurrence types.
    //*******************************************************************    
    
    print("\n\n     Occurrences:\n\n");
    if (ocursTypes.size() > 0) {
      print("\n" + format("Number of different occurrence types") + ": " + 
            ocursTypes.keySet().size() + "\n\n");

      String[] templist = sortAlpha(ocursTypes.keySet());
        
      int i = 0;
      while (i < templist.length) {
        String t = templist[i];
        print(format(t) + ": " + ((Integer)ocursTypes.get(t)).intValue() 
              + "\n");
        i++;
      }

      //Iterator it = ocursTypes.keySet().iterator();      
      //while (it.hasNext()) {
      //  String t = (String)it.next();
      //  print(format(t) + ": " + ((Integer)ocursTypes.get(t)).intValue() 
      //        + "\n");
      //}
    } else {
      print("There are no occurrences with type in this topic map.\n");
    }

  }//end of countTopics.





  /**
   * Handles all the assciation dependecies.
   */
  protected void countAssociationDep() {
    
    TopicAssocDep topicassocdep = new TopicAssocDep(tm);
    //HashMap assocTypes;
    
    //Print out the result.
    print("\n\n\n         Association Dependencies:    \n\n\n");
    Iterator it = topicassocdep.getAssociations().iterator();
    while (it.hasNext()) {
      String a = (String)it.next();
      StringTokenizer st = new StringTokenizer(a, "$");
      String string = st.nextToken();
      print("\n\nThe association \"" + string + "\" has roles:\n");
      while (st.hasMoreTokens()) {
        string = st.nextToken();
        print("\"" + string+ "\"\n");
      }
      
      print("and occurs "  + topicassocdep.getNumberOfOccurrences(a) + " times\n");
    }

  }//end of countAssociationDep




  /**
   * Handles all the topics without type.
   */
  protected void getNoTypeTopics() {

    //Create a new NoType object.
    NoTypeCount notypes = new NoTypeCount(tm);
    notypes.traverse();
    
    //Get the result from teh nt object.
    Collection notypetopics = notypes.getNoTypeTopics();
    Collection notypeoccrs = notypes.getNoTypeOccurrences();
    Collection notypeassocs = notypes.getNoTypeAssociations();

    //Print out the result.
    //Prints out all the topics with no type.
    if (!notypetopics.isEmpty()) {
      Iterator it = notypetopics.iterator();
      print("\n\n\n       Topics without type:\n\n\n");
      while (it.hasNext()) {
        TopicIF t = (TopicIF)it.next();
        print("Topic : " + getTopicId(t) + "\n");
      }
    }

    //Prints out all the associations, and their roles, with no type.
    if (!notypeassocs.isEmpty()) {
      print("\n\n\n       Associations without type:\n\n\n");
      Iterator it = notypeassocs.iterator();
      while (it.hasNext()) {
        AssociationIF a = (AssociationIF)it.next();
        print("Association : " + a.getObjectId() + "\n");
        Collection roles = a.getRoles();
        Iterator itr = roles.iterator();
        while (itr.hasNext()) {
          AssociationRoleIF arif = (AssociationRoleIF)itr.next();
          print("Role : " + TopicStringifiers.toString(arif.getPlayer()) + "\n");
        }
      }
    }

    //Prints out all the occurrences with no type.
    if (!notypeoccrs.isEmpty()) {
      print("\n\n\n       Occurrences without type:\n\n\n");
      Iterator it = notypeoccrs.iterator();
      while (it.hasNext()) {
        OccurrenceIF o = (OccurrenceIF)it.next();
        LocatorIF l = o.getLocator();
        
        print("Occurrence : " + l.getAddress() + "\n");
      }
    }

  }//end of getNoTypeTopics



  /**
   * Method used for pretty print.
   */
  protected String format(String t) {
    int numberOfBlanks = 42 - t.length();
    for (int i = 0; i < numberOfBlanks; i++) {
      t = t + " ";
    }
    return t;
  }



  /**
   * Lazy print, used internaly.
   */
  protected static void print(String s) {
    System.out.print(s);
  }

  protected String getTopicId(TopicIF topic) {
    String id = null;
    if (topic.getTopicMap().getStore().getBaseAddress() != null) {
      String base = topic.getTopicMap().getStore().getBaseAddress().getAddress();
      Iterator it = topic.getItemIdentifiers().iterator();
      while (it.hasNext()) {
        LocatorIF sloc = (LocatorIF) it.next();
        if (sloc.getAddress().startsWith(base)) {
          String addr = sloc.getAddress();
          id = addr.substring(addr.indexOf('#') + 1);
          break;
        }
      }
    }
    if (id == null)
      id = "id" + topic.getObjectId();
    return id;
  }

  public static void main(String [] argv) {

    // Initialize logging
    CmdlineUtils.initializeLogging();
    
    // Initialize command line option parser and listeners
    CmdlineOptions options = new CmdlineOptions("StatisticsPrinter", argv);
      
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
    if (args.length < 1) {
      System.err.println("Error: Illegal number of arguments.");
      usage();
      System.exit(1);      
    }

    try {

      // Validate or transform <url> into a URL
      // String url = URIUtils.getURI(args[0]).toExternalForm();
      
      // If the user have specifed a parser to use, try to use it 

      TopicMapIF tm = ImportExportUtils.getReader(args[0]).read();
      if (tm == null) throw new OntopiaRuntimeException("No topic maps found.");      
      StatisticsPrinter statsprinter = new StatisticsPrinter(tm);
      statsprinter.topicStats();
    } catch (Exception e) {
      System.err.println("ERROR: " + e.getMessage());
      System.exit(1);
    }
  
  }


  private String[] sortAlpha(Collection collection) {
    String[] retur = new String[collection.size()];
    Iterator it = collection.iterator();
    int k = 0;
    while (it.hasNext()) {
      String temp = (String)it.next();
      retur[k] = temp;
      k++;
    }
    
    //Starting at the first index in the array.
    for (int i = 0; i+1 < retur.length; i++) {
      
      if (retur[i].compareTo(retur[i+1]) > 0){
        //Found one, shuffle it to the lowest index possible.
        String temp = retur[i];
        retur[i] = retur[i+1];
        retur[i+1] = temp;
        
        int j = i;
        boolean done = false;
        while (j != 0 && !done) {
          if (retur[j].compareTo(retur[j-1]) < 0) {
            temp = retur[j];
            retur[j] = retur[j-1];
            retur[j-1] = temp;
          } else done = true;
          j--;
        }//end of while.
      }//end of if
    }//end of for
    return retur;
  }

  protected static void usage() {
    System.out.println("java net.ontopia.topicmaps.cmdlineutils.StatisticsPrinter [options] <url> [parser]");
    System.out.println("");
    System.out.println("  Reads a topic map and outputs statistics about it.");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("");
    System.out.println("  <url>: url of topic map to output statistics for");
    System.out.println("  [parser]: (optional) lets you specify which xml parser to use.");
    
  }

}





