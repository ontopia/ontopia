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

package net.ontopia.topicmaps.cmdlineutils.rdbms;

import java.util.Iterator;
import java.io.File;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.UniquenessViolationException;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.utils.CmdlineOptions;
import net.ontopia.utils.CmdlineUtils;
import net.ontopia.utils.URIUtils;

/**
 * INTERNAL: Command line utility for translating the source locators
 * in an imported topic map from their original base to the new base.
 *
 * @since 2.0
 */
public class TranslateSourceLocators {

  public static void main(String[] argv) throws Exception {    

    // Initialize logging
    CmdlineUtils.initializeLogging();
      
    // Register logging options
    CmdlineOptions options = new CmdlineOptions("TranslateSourceLocators", argv);
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

    if (args.length < 3 || args.length > 3) {
      usage();
      System.exit(3);
    }

    translate(args[0], args[1], args[2]);
  }

  private static void translate(String propfile, String tmid, String tmfile)
    throws Exception {
    RDBMSTopicMapStore store = new RDBMSTopicMapStore(propfile, Integer.parseInt(tmid));      
    TopicMapIF tm = store.getTopicMap();

    LocatorIF newbase = store.getBaseAddress();
    String oldbase = newbase.resolveAbsolute(URIUtils.toURL(new File(tmfile)).toString()).getAddress();
    int oldlen = oldbase.length();
    
    Iterator it = tm.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();

      Object[] srclocs = topic.getItemIdentifiers().toArray();
      for (int ix = 0; ix < srclocs.length; ix++) {
        LocatorIF srcloc = (LocatorIF) srclocs[ix];
        String url = srcloc.getAddress();

        if (url.startsWith(oldbase)) {
          LocatorIF newloc = newbase.resolveAbsolute(url.substring(oldlen));
          topic.removeItemIdentifier(srcloc);

          try {
            topic.addItemIdentifier(newloc);
          } catch (UniquenessViolationException e) {
            // ooops! this topic already exists, so we must merge
            TopicIF other = (TopicIF) tm.getObjectByItemIdentifier(newloc);
            if (other == null) {
              other = tm.getTopicBySubjectIdentifier(newloc);
            }
            MergeUtils.mergeInto(other, topic); // this kills our topic, not the other
          }
        }
      }
    }

    store.commit();
    store.close();
  }

  private static void usage() {
    System.out.println("java net.ontopia.topicmaps.cmdlineutils.rdbms.TranslateSourceLocators [options] <dbprops> <tmid> <tmfile>");
    System.out.println("");
    System.out.println("  Translates the base addresses of source locators to that of the topic map.");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("");
    System.out.println("  <dbprops>:   the database configuration file");
    System.out.println("  <tmid>   :   the id of the topic map to modify");
    System.out.println("  <tmfile> :   the original file name");
    System.out.println("");
  }
  
}
