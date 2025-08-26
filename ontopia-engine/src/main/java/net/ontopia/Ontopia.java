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

package net.ontopia;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import net.ontopia.utils.CmdlineUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StreamUtils;
import org.apache.commons.io.IOUtils;

/**
 * INTERNAL: Class for representing information about a the Ontopia
 * product and a method for testing if the product is correctly
 * installed.</p>
 *
 * Information about the product release can be retrieved through the
 * methods on this class. </p>
 */

public final class Ontopia {

  private static String name;
  private static String version;
  private static String build_date;
  private static String build_user;

  static {

    try (InputStream i = StreamUtils.getInputStream("classpath:net/ontopia/Ontopia.info")) {
      List<String> lines = IOUtils.readLines(i, StandardCharsets.UTF_8);

      name = lines.get(0);
      version = lines.get(1);
      build_date = lines.get(2);
      build_user = lines.get(3);

    } catch (Throwable ex) {

      System.err.println("Build Error: could not find version file");
      // fail safe

      name = "Ontopia Topic Maps Engine";
      version = "0";
      build_date = "unknown";
      build_user = "unknown";
    }
  }

  private Ontopia() {
  }

  /**
   * PUBLIC: Returns the product name.
   */
  public static String getName() {
    return name;
  }

  /**
   * PUBLIC: Returns the product version. E.g. "1.0, "1.4.2".
   */
  public static String getVersion() {
    return version;
  }

  /**
   * PUBLIC: Returns the time when the product was built.
   */
  public static String getBuildDate() {
    return build_date;
  }

  /**
   * PUBLIC: Returns the product build user.
   */
  public static String getBuildUser() {
    return build_user;
  }

  /**
   * PUBLIC: Returns a formatted string with product the build date
   * and build user.
   */
  public static String getBuild() {
    return getBuildDate() + (getBuildUser() == null ? "" : " by " + getBuildUser());
  }

  private static void checkClass(String class_name, String jar_file) {
    try {
      Class.forName(class_name);
    } catch (NoClassDefFoundError | ClassNotFoundException e) {
      String message = "Class '" + class_name + "' not found. Please add " + jar_file + " to your CLASSPATH.";
      // System.out.println(message);
      throw new OntopiaRuntimeException(message);
    }
  }

  private static void checkProduct() {
    System.out.println(getInfo());
    try {
      check();
      System.out.println("Success: All required classes found.");
    } catch (OntopiaRuntimeException e) {
      System.err.println(e.getMessage());
    }
  }

  /**
   * INTERNAL: Check to see whether the CLASSPATH has been set up
   * correctly for this product.
   */
  public static void checkClasses() {
    checkClass("net.ontopia.topicmaps.core.TopicMapIF",        "ontopia.jar");
    checkClass("org.slf4j.Logger",                             "slf4j-api.jar");
    checkClass("org.apache.commons.collections4.map.LRUMap",   "commons-collections4.jar");
    checkClass("gnu.getopt.Getopt",                            "getopt.jar");
    checkClass("antlr.Parser",                                 "antlr.jar");

    // moved to RDF module
    // checkClass("com.hp.hpl.jena.graph.Node",                   "jena.jar");
    // checkClass("com.ibm.icu.util.Calendar",                    "icu4j.jar");
    // checkClass("com.hp.hpl.jena.iri.IRIFactory",               "iri.jar");
  }

  /**
   * INTERNAL: Check to see whether the JVM environment is correctly
   * set up for this product.
   */
  public static void check() {
    // Check to see if required classes get imported
    checkClasses();

    if (System.getProperty("java.version").compareTo("1.7.0") < 0) {
      throw new OntopiaRuntimeException("Java 1.7 or newer needed; running " +
					System.getProperty("java.version"));
    }
  }

  public static String getInfo() {
    return getName() + " " + getVersion() + " (" + getBuild() + ")";
  }

  public static void main(String argv[]) {
    CmdlineUtils.initializeLogging();
    Ontopia.checkProduct();
  }

}
