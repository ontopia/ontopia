/*
 * #!
 * Ontopia OSL Schema
 * #-
 * Copyright (C) 2001 - 2014 The Ontopia Project
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

package net.ontopia.topicmaps.schema.impl.osl.cmdline;

import java.io.File;
import org.xml.sax.Locator;
import net.ontopia.utils.CmdlineUtils;
import net.ontopia.utils.CmdlineOptions;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.schema.core.SchemaIF;
import net.ontopia.topicmaps.schema.core.SchemaValidatorIF;
import net.ontopia.topicmaps.schema.core.SchemaViolationException;
import net.ontopia.topicmaps.schema.core.SchemaSyntaxException;
import net.ontopia.topicmaps.schema.impl.osl.OSLSchemaReader;
import net.ontopia.topicmaps.schema.utils.TextValidationHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Validate {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(Validate.class.getName());

  public static void main(String[] args) {
    // Initialize logging
    CmdlineUtils.initializeLogging();
      
    // Initialize command line option parser and listeners
    CmdlineOptions options = new CmdlineOptions("Validate", args);
      
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
    args = options.getArguments();    
    if (args.length < 2) {
      System.err.println("Error: need at least two files as arguments.");
      usage();
      System.exit(1);
    }

    try {
      validate(args[0], args[1]);
    } catch (java.io.IOException e) {
      System.err.println("ERROR: " + e.getMessage());
    } catch (SchemaViolationException e) {
      System.err.println("ERROR: " + e.getMessage()); // shouldn't happen
    } catch (SchemaSyntaxException e) {
      System.err.println("SYNTAX ERROR: " + e.getMessage());
      Locator loc = e.getErrorLocation();
      System.err.println("  at " + loc.getSystemId() + ":" +
                         loc.getLineNumber() + ":" + loc.getColumnNumber());
    }
  }

  // --- Internal methods

  private static void validate(String tmfile, String schemafile)
    throws java.io.IOException, SchemaViolationException,
    SchemaSyntaxException {

    log.info("Reading schema");
    OSLSchemaReader sreader = new OSLSchemaReader(new File(schemafile));
    SchemaIF schema = sreader.read();
    SchemaValidatorIF validator = schema.getValidator();
    validator.setValidationHandler(new TextValidationHandler());

    log.info("Reading topic map");
    TopicMapReaderIF tmreader = ImportExportUtils.getReader(tmfile);
    TopicMapIF tm = tmreader.read();

    log.info("Validating topic map");
    validator.validate(tm);

    log.info("Done validating");
  }

  private static void usage() {
    System.out.println("");
    System.out.println("Usage:");
    System.out.println("  java net.ontopia...Validate <topicmap> <schema>");
    System.out.println("");
    System.out.println("  <topicmap>: file name of a topic map");
    System.out.println("  <schema>:   file name of a schema document");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
  }
}
