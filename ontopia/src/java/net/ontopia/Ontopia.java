// $Id: AbstractProduct.java,v 1.28 2008/05/16 12:10:33 geir.gronmo Exp $

package net.ontopia;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import net.ontopia.utils.*;

/**
 * INTERNAL: Class for representing information about a the Ontopia
 * product and a method for testing if the product is correctly
 * installed.</p>
 *
 * Information about the product release can be retrieved through the
 * methods on this class. </p>
 */

public final class Ontopia {
  
  private static final String name;
  private static final int major_version;
  private static final int minor_version;
  private static final int micro_version;
  private static final int beta_version;
  private static final Calendar build_date;
  private static final int build_number;

  static {
    name = "Ontopia Topic Map Engine";

    major_version = 4;
    minor_version = 1;
    micro_version = 0;
    beta_version = 0;

    build_date = Calendar.getInstance();
    build_date.set(2009, 5, 16, 15, 51);
    build_number = 30;
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
    return (beta_version <= 0 ? false : true);
  }

  /**
   * PUBLIC: Returns the time when the product was built.
   */
  public static Calendar getBuildDate() {
    return build_date;
  }

  /**
   * PUBLIC: Returns the product build number.
   */
  public static int getBuildNumber() {
    return build_number;
  }

  /**
   * PUBLIC: Returns a formatted string with product the build date
   * and build number.
   */
  public static String getBuild() {
    int year = getBuildDate().get(Calendar.YEAR);
    int month = getBuildDate().get(Calendar.MONTH) + 1;
    int day = getBuildDate().get(Calendar.DATE);
    int hour = getBuildDate().get(Calendar.HOUR_OF_DAY);
    int minute = getBuildDate().get(Calendar.MINUTE);

    int bno = getBuildNumber();

    return year + "-"
      + (month < 10 ? "0" + month : String.valueOf(month)) + "-"
      + (day < 10 ? "0" + day : String.valueOf(day))
      + " " +  (hour < 10 ? "0" + hour : String.valueOf(hour)) + ":"
      + (minute < 10 ? "0" + minute : String.valueOf(minute))
      + " #" + (bno < 10 ? "0000" + bno : (bno < 100 ? "000" + bno : (bno < 1000 ? "00" + bno : (bno < 10000 ? "0" + bno : String.valueOf(bno)))));
  }

  private static void checkClass(String class_name, String jar_file) {
    try {
      Class.forName(class_name);
      return;
    } catch (NoClassDefFoundError e) {
    } catch (ClassNotFoundException e) {
    }
    String message = "Class '" + class_name + "' not found. Please add " + jar_file + " to your CLASSPATH.";
    // System.out.println(message);
    throw new OntopiaRuntimeException(message);
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
    checkClass("net.ontopia.topicmaps.core.TopicMapIF",        "ontopia-engine.jar");
    checkClass("javax.xml.parsers.SAXParserFactory",           "jaxp.jar");
    checkClass("org.xml.sax.ContentHandler",                   "crimson.jar or sax.jar");
    checkClass("org.apache.crimson.jaxp.SAXParserFactoryImpl", "crimson.jar");
    checkClass("org.apache.log4j.BasicConfigurator",           "log4j.jar");
    checkClass("org.apache.commons.collections.map.LRUMap",  "commons-collections.jar");
    checkClass("gnu.getopt.Getopt",                            "getopt.jar");
    checkClass("antlr.Parser",                                 "antlr.jar");
    checkClass("com.hp.hpl.jena.graph.Node",                   "jena.jar");
    checkClass("com.ibm.icu.util.Calendar",                    "icu4j.jar");
    checkClass("com.thaiopensource.relaxng.Schema",            "jing.jar");
    checkClass("org.apache.oro.text.regex.MalformedPatternException", "jakarta-oro.jar");
  }

  /**
   * INTERNAL: Check to see whether the JVM environment is correctly
   * set up for this product.
   */
  public static void check() {
    // Check to see if required classes get imported
    checkClasses();

    if (System.getProperty("java.version").compareTo("1.5.0") < 0)
      throw new OntopiaRuntimeException("Java 1.5 or newer needed; running " +
					System.getProperty("java.version"));
  }

  public static String getInfo() {  
    return getName() + " " + getVersion() + " (" + getBuild() + ")";
  }
  
  public static void main(String argv[]) {
    CmdlineUtils.initializeLogging();
    Ontopia.checkProduct();
  }
  
}





