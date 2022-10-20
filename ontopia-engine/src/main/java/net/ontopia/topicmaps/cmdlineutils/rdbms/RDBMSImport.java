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

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.util.Properties;
import org.xml.sax.InputSource;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore;
import net.ontopia.topicmaps.impl.rdbms.TopicMap;
import net.ontopia.topicmaps.utils.DuplicateSuppressionUtils;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.utils.CmdlineOptions;
import net.ontopia.utils.CmdlineUtils;
import net.ontopia.utils.PropertyUtils;
import net.ontopia.xml.AbstractXMLFormatReader;

/**
 * PUBLIC: Command line utility for importing topic map files into a
 * relational database system. Topic map files can either be imported
 * into an existing topic map or imported as separated topic maps in
 * the database.
 *
 * <p>Run the class with no arguments to see how to use it.
 */
public class RDBMSImport {
  
  public static void main(String[] argv) throws Exception {    

    // initialize logging
    CmdlineUtils.initializeLogging();
      
    // register logging options
    CmdlineOptions options = new CmdlineOptions("RDBMSImport", argv);
    OptionsListener ohandler = new OptionsListener();
    CmdlineUtils.registerLoggingOptions(options);
    options.addLong(ohandler, "tmid", 'i', true);
    options.addLong(ohandler, "title", 't', true);
    options.addLong(ohandler, "comments", 'c', true);
    options.addLong(ohandler, "validate", 'v', true);
    options.addLong(ohandler, "suppress", 's', true);
    options.addLong(ohandler, "loadExternal", 'e', true);
    options.addLong(ohandler, "validate", 'v', true);
    options.addLong(ohandler, "jdbcspy", 'j', true);
    options.addLong(ohandler, "progress", 'p', true);
      
    // parse command line options
    try {
      options.parse();
    } catch (CmdlineOptions.OptionsException e) {
      System.err.println("Error: " + e.getMessage());
      System.exit(1);      
    }

    // get command line arguments
    String[] args = options.getArguments();

    if (args.length < 2) {
      usage();
      System.exit(3);
    }
    
    // load properties file
    Properties props = PropertyUtils.loadProperties(new File(args[0]));

    // override shared cache property
    props.put("net.ontopia.topicmaps.impl.rdbms.Cache.shared", "false");
    
    RDBMSTopicMapStore store = new RDBMSTopicMapStore(PropertyUtils.toMap(props), ohandler.topicMapId);
    TopicMapIF tm = store.getTopicMap();

    // set topic map title
    if (ohandler.topicMapTitle != null) {
      ((TopicMap)tm).setTitle(ohandler.topicMapTitle);
    }

    // set topic map comments
    if (ohandler.topicMapComments != null) {
      ((TopicMap)tm).setComments(ohandler.topicMapComments);
    }
    
    for (int i=1; i < args.length; i++) {
      
      String filename = args[i];
      TopicMapReaderIF importer = ImportExportUtils.getReader(filename);
      
      // disable XTM validation
      if (importer instanceof XTMTopicMapReader && !ohandler.validate) {
        ((XTMTopicMapReader)importer).setValidation(false);
      }

      // disable following external topicRefs
      if (importer instanceof XTMTopicMapReader && !ohandler.loadExternal) {
        ((XTMTopicMapReader)importer).setFollowTopicRefs(false);
      }
      
      System.out.println("Importing " + filename + " into " + tm.getObjectId());
      long start = System.currentTimeMillis();
      
      if (ohandler.progress) {
        // user has asked for a progress report, so we do all this
        // complicated stuff to make it happen. the basic idea is that
        // we create the input stream ourselves, instead of letting
        // the parser do it, and use a subclass of FileIS which outputs
        // status reports as the parser reads from it.
        File file = new File(filename);
        if (importer instanceof AbstractXMLFormatReader &&
            file.exists()) {
          FileInputStream fis = new WrappedFileInputStream(file);
          InputSource src = ((AbstractXMLFormatReader) importer).getInputSource();
          src.setByteStream(fis);
        } else {
          System.out.println("Cannot produce progress report!");
        }
      }

      importer.importInto(tm);

      if (ohandler.suppress) {
        DuplicateSuppressionUtils.removeDuplicates(tm);
      }

      store.commit();
      
      long end = System.currentTimeMillis();      
      System.out.println("Done. " + (end - start) + " ms.");

    }

    if (ohandler.jdbcspyFile != null) {
      try {
        Class<?> spyDriverClass = Class.forName("net.ontopia.persistence.jdbcspy.SpyDriver");
        Method method = spyDriverClass.getMethod("writeReport", String.class);
        method.invoke(null, ohandler.jdbcspyFile);
      } catch (ClassNotFoundException cnfe) {
        System.out.println("JDBC-Spy driver was not found on the classpath, make sure you have the"
                + " ontopia-jdbcspy jar of the correct java version on your classpath");
      }
    }

    // close store (and database connection)    
    store.close();
  }

  private static void usage() {
    System.out.println("java net.ontopia.topicmaps.cmdlineutils.rdbms.RDBMSImport [options] <dbprops> <tmfile1> [<tmfile2>] ...");
    System.out.println("");
    System.out.println("  Imports topic map files into a topic map in a database.");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("    --tmid=<topic map id> : existing TM to import into (creates new TM by default)");
    System.out.println("    --title=<topic map title> : persistent name of topic map");
    System.out.println("    --comments=<topic map comments> : persistent comments about topic map");
    System.out.println("    --validate=true|false : if true topic map document will be validated (default: true)");
    System.out.println("    --suppress=true|false: suppress duplicate characteristics (default: false)");
    System.out.println("    --loadExternal=true|false : if true external topic references will be resolved (default: true)");
    System.out.println("    --jdbcspy=<filename> : write jdbcspy report to the given file");
    System.out.println("    --progress=true|false: write progress report while importing (default: false)");
    System.out.println("");
    System.out.println("  <dbprops>:   the database configuration file");
    System.out.println("  <tmfile#>:   the topic map files to import");
    System.out.println("");
  }

  private static class OptionsListener implements CmdlineOptions.ListenerIF {
    private long topicMapId = -1;
    private boolean validate = true;
    private boolean suppress = false;
    private boolean loadExternal = true;
    private String jdbcspyFile;
    private String topicMapTitle;
    private String topicMapComments;
    private boolean progress;
    
    @Override
    public void processOption(char option, String value) {
      if (option == 'i') {
        topicMapId = ImportExportUtils.getTopicMapId(value);
      }
      if (option == 'v') {
        validate = Boolean.valueOf(value).booleanValue();
      }
      if (option == 'e') {
        loadExternal = Boolean.valueOf(value).booleanValue();
      }
      if (option == 's') {
        suppress = Boolean.valueOf(value).booleanValue();
      }
      if (option == 'j') {
        jdbcspyFile = value;
      }
      if (option == 't') {
        topicMapTitle = value;
      }
      if (option == 'c') {
        topicMapComments = value;
      }
      if (option == 'p') {
        progress = Boolean.valueOf(value).booleanValue();
      }
    }
  }

  private static class WrappedFileInputStream extends FileInputStream {
    private long filesize;
    private int prevperc;
    private FileChannel fc;
    private boolean hasComplained;

    private WrappedFileInputStream(File file) throws FileNotFoundException {
      super(file);
      this.filesize = file.length();
      this.prevperc = -1;
      this.fc = getChannel();
    }
    
    @Override
    public int read() throws IOException {
      int res = super.read();
      status();
      return res;
    }

    @Override
    public int read(byte[] b) throws IOException {
      int res = super.read(b);
      status();
      return res;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
      int res = super.read(b, off, len);
      status();
      return res;
    }

    @Override
    public long skip(long n) throws IOException {
      long res = super.skip(n);
      status();
      return res;
    }

    private void status() {
      try {
        int perc = (int) (((float) fc.position() / (float) filesize) * 100.0);
        if (perc != prevperc) {
          System.out.println("" + perc + "%");
          prevperc = perc;
        }
      } catch (IOException e) {
        if (!hasComplained) {
          e.printStackTrace();
          hasComplained = true;
        }
      }
    }
  }
}
