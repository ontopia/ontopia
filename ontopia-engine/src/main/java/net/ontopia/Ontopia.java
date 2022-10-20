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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import net.ontopia.utils.CmdlineUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StreamUtils;

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
  private static int major_version;
  private static int minor_version;
  private static int micro_version;
  private static int beta_version;
  private static Calendar build_date;
  private static String build_user;

  static {
	
    try {
      InputStream i = StreamUtils.getInputStream("classpath:net/ontopia/Ontopia.info");

      BufferedReader r = new BufferedReader(new InputStreamReader(i));

      name = r.readLine();
      String version = r.readLine();
      String datetime = r.readLine();
      build_user = r.readLine();

      r.close();

      String[] v = version.split("[\\.-]");
      major_version = Integer.parseInt(v[0]);
      minor_version = Integer.parseInt(v[1]);
      micro_version = Integer.parseInt(v[2]);

      // 3 = SNAPSHOT / ALPHA / BETA
      // 4 = ALPHA / BETA version
      if (v.length > 4) {
        beta_version = Integer.parseInt(v[4]);
      }

      String[] d = datetime.split("-");
      build_date = Calendar.getInstance();
      build_date.set(Integer.parseInt(d[0]), Integer.parseInt(d[1]) - 1, Integer.parseInt(d[2]), Integer.parseInt(d[3]), Integer.parseInt(d[4]));

    } catch (IOException ex) {
      
      System.err.println("Build Error: could not find version file");
      // fail safe

      name = "Ontopia Topic Maps Engine";
      major_version = 0;
      minor_version = 0;
      micro_version = 0;
      beta_version = 0;
      build_date = Calendar.getInstance();
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
   * PUBLIC: Returns the product version. E.g. "1.0, "1.4.2" or "1.4.0 b3".
   */
  public static String getVersion() {
    return getMajorVersion() + "." + getMinorVersion() + "." + getMicroVersion()
      + (getBetaVersion() <= 0 ? "" : "b" + getBetaVersion())
      ;
  }
  
  /**
   * PUBLIC: Returns the product major version number. E.g. 1 when the
   * version number is 1.4.2.
   */
  public static int getMajorVersion() {
    return major_version;
  }

  /**
   * PUBLIC: Returns the product minor version number. E.g. 4 when the
   * version number is 1.4.2.
   */
  public static int getMinorVersion() {
    return minor_version;
  }

  /**
   * PUBLIC: Returns the product micro version number. E.g. 2 when the
   * version number is 1.4.2. If this version number isn't applicable
   * -1 is returned.
   */
  public static int getMicroVersion() {
    return micro_version;
  }

  /**
   * PUBLIC: Returns the product beta version number. E.g. 3 when the
   * version number is 1.4.2 b3. If this version number isn't applicable
   * -1 is returned.
   */
  public static int getBetaVersion() {
    return beta_version;
  }

  /**
   * PUBLIC: Returns true if the product is a beta release.
   */
  public static boolean isBeta() {
    return (beta_version > 0);
  }

  /**
   * PUBLIC: Returns the time when the product was built.
   */
  public static Calendar getBuildDate() {
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
    int year = getBuildDate().get(Calendar.YEAR);
    int month = getBuildDate().get(Calendar.MONTH) + 1;
    int day = getBuildDate().get(Calendar.DATE);
    int hour = getBuildDate().get(Calendar.HOUR_OF_DAY);
    int minute = getBuildDate().get(Calendar.MINUTE);

    String user = getBuildUser();

    return year + "-"
      + (month < 10 ? "0" + month : String.valueOf(month)) + "-"
      + (day < 10 ? "0" + day : String.valueOf(day))
      + " " +  (hour < 10 ? "0" + hour : String.valueOf(hour)) + ":"
      + (minute < 10 ? "0" + minute : String.valueOf(minute))
      + (user == null ? "" : " by " + getBuildUser());
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





