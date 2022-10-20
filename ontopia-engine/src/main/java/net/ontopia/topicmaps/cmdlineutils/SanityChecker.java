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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.cmdlineutils.sanity.AssociationSanity;
import net.ontopia.topicmaps.cmdlineutils.sanity.DuplicateNames;
import net.ontopia.topicmaps.cmdlineutils.sanity.DuplicateOccurrences;
import net.ontopia.topicmaps.cmdlineutils.sanity.NoNames;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.utils.CmdlineOptions;
import net.ontopia.utils.CmdlineUtils;

/**
 * PUBLIC: Checks a topic map for dubious constructs.</p>
 */
public class SanityChecker {

  protected TopicMapIF tm;

  public static void main(String [] argv) throws Exception {

    // Initialize logging
    CmdlineUtils.initializeLogging();
    
    // Initialize command line option parser and listeners
    CmdlineOptions options = new CmdlineOptions("SanityChecker", argv);
      
    // Register logging options
    CmdlineUtils.registerLoggingOptions(options);
      
    // Parse command line options
    try {
      options.parse();
      
      // Get command line arguments
      String[] args = options.getArguments();    
      
      if (args.length == 1) {
        new SanityChecker(args[0]);
      } else {
        System.err.println("Error: Illegal number of arguments.");
        usage();
        System.exit(1);      
      }
      
    } catch (CmdlineOptions.OptionsException e) {
      System.err.println("Error: " + e.getMessage());
      System.exit(1);      
    }
    
  }

  /**
   * Constructor that accepts a topicmap as argument.
   */
  public SanityChecker(TopicMapIF tm) {
    this.tm = tm;
    topicSanity();
  }

  /**
   * Constructor that accepts a File object as argument (XTM file).
   */
  public SanityChecker(File file) throws MalformedURLException, IOException {
    this(ImportExportUtils.getReader(file).read());
  }
  
  /**
   * Constructor that accepts a url as argument (XTM file).
   */
  public SanityChecker(String url) throws MalformedURLException, IOException {
    this(ImportExportUtils.getReader(url).read());
  }

  private void topicSanity() {

    //Get all the duplicate assocs, and print them to screen.
    findDuplicateAssociations();
      
    //Get all the topics without name, and print them to screen.
    getNoNameTopics();
      
    //Get all the topics with duplicated occurrences, and print them to screen.
    getDuplicateOccurrences();
    
    //Get all the topics with duplicated names, and print them to screen.
    getDuplicatedNames();

  }

  /**
   * Handles all the duplicate assocaitions.
   */
  private void findDuplicateAssociations() {

    //Creates a new AssociationSanoty object.
    AssociationSanity ts = new AssociationSanity(tm);
    ts.traverse();

    //Get the result from the ts object.
    HashMap duplicateAssocs = ts.getDuplicateAssociations();
    HashMap numberOfDuplicates = ts.getNumberOfDuplicates();
    
    //Prints out all the duplicate assocations.
    if (duplicateAssocs.size() > 0) {
      print("\nThis Topic Map contains " + duplicateAssocs.size() + 
            " duplicate Associations\n");
      Iterator it = duplicateAssocs.keySet().iterator();
      while(it.hasNext()) {

        String s = (String)it.next();

        StringTokenizer st = new StringTokenizer(s, "$");
        String association = st.nextToken();
        print("\n\nAssociation : \"" + association + "\" with:\n");
        while (st.hasMoreTokens()) {
          String value = st.nextToken();
          String attribute = "<no attribute>";
          if (st.hasMoreTokens()) {
            attribute = st.nextToken();
          } 
          print("attribute : \"" + attribute + "\", and value : \"" + value + "\"\n"); 
        }

        //AssociationIF a = (AssociationIF)duplicateAssocs.get(s);
        Integer i = (Integer)numberOfDuplicates.get(s);
        print("Appears " + i.intValue() + " times.\n");
      }
    } else {
      print("This Topic Map contains no duplicate Associations.\n");
    }
  }

 /**
   * Handles all the topics without name.
   */
  private void getNoNameTopics() {
    //Create a new NoNames object
    NoNames nn = new NoNames(tm);

    nn.findNoNameTopics();

    //Get the result from the nn object, and just prints out
    //the number of different topicmap elements without name.
    Collection nonametopics = nn.getNoNameTopics();
    print("\nNumber of Topics with no name: " + nonametopics.size() + "\n");

    Iterator it = nonametopics.iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF)it.next();
      print(getTopicId(topic) + "\n");
    }

    Collection noChar = nn.getNoCharacteristics();
    print("\nTopics with no characteristics: " + noChar.size() +"\n");

    it = noChar.iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF)it.next();
      print(getTopicId(topic) + "\n");
    }

    Collection noscopes = nn.getNoNameUnconstrained();
    print("\nNumber of topics with no name in the unconstrained scope: " +
          noscopes.size() + "\n");
    list(noscopes);
    
  }

  /**
   * Handles all the topics with duplicate occurrences.
   */
  private void getDuplicateOccurrences() {
    //Create a new DuplicateOccurrences object.
    DuplicateOccurrences dupocc = new DuplicateOccurrences(tm);

    //Get the result from the dupocc object, and prints the result.
    Collection duplicateoccurrences = dupocc.getDuplicateOccurrences();
    if (duplicateoccurrences.size() > 0) {
      print("\nNumber of duplicate occurrences : " + 
            duplicateoccurrences.size() + "\n");
      print("Topics containing duplicate occurrences :\n");
      Iterator it = duplicateoccurrences.iterator();
      while (it.hasNext()) {
        print(getTopicId((TopicIF)it.next()) + "\n");
      }
    } else {
      print("\nThis TopicMap contains no duplicate occurrences.\n");
    }
  }

  /**
   * Handles all the topics with duplicate names, which means same basename
   * and scope.
   */
  private void getDuplicatedNames() {
    //Create a new DuplicateNames object.
    DuplicateNames dn = new DuplicateNames(tm);

    //Get the result from the dn object.
    Collection topics = dn.getDuplicatedNames();

    if (topics.size() > 0) {
      //Print out the result.
      print("\nNumber of topics with same basename and scope : " + 
            topics.size() + "\n");
      Iterator it = topics.iterator();
      while (it.hasNext()) {
        TopicIF t = (TopicIF)it.next();
        print(t.getObjectId() + "\n");
      }
    } else {
      print("\nThis TopicMap contains no topics with same basename and scope.\n");
    }
  }

  /**
   * Lazy print, used internally.
   */
  private void print(String s) {
    System.out.print(s);
  }

  private static void usage() {
    System.out.println("java net.ontopia.topicmaps.cmdlineutils.SanityChecker [options] <url>");
    System.out.println("");
    System.out.println("  Checks a topic map for dubious constructs.");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("");
    System.out.println("  <url>:  url or file name of source topic map");
  }

  private String getTopicId(TopicIF topic) {
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
    if (id == null) {
      id = "id" + topic.getObjectId();
    }
    return id;
  }

  private void list(Collection tmobjects) {
    Iterator it = tmobjects.iterator();
    while (it.hasNext()) {
      print("  " + it.next() + "\n");
    }
  }
  
}
