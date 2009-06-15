
// $Id: AbstractOntopiaTestCase.java,v 1.26 2008/06/02 10:50:12 geir.gronmo Exp $

package net.ontopia.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.TestCase;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.OntopiaRuntimeException;

public abstract class AbstractOntopiaTestCase extends TestCase {

  protected static int asserts = 0;

  public AbstractOntopiaTestCase(String name) {
    super(name);
  }

  public static int getAssertCount() {
    return asserts;
  }

  static public void assertTrue(String message, boolean condition) {
    TestCase.assertTrue(message, condition);
    asserts++;
  }
  static public void assertTrue(boolean condition) {
    TestCase.assertTrue(condition);
    asserts++;
  }
  static public void assertEquals(String message, Object expected, Object actual) {
    TestCase.assertEquals(message, expected, actual);
    asserts++;
  }
  static public void assertEquals(Object expected, Object actual) {
    TestCase.assertEquals(expected, actual);
    asserts++;
  }
  static public void assertEquals(String message, double expected, double actual, double delta) {
    TestCase.assertEquals(message, expected, actual, delta);
    asserts++;
  }
  static public void assertEquals(double expected, double actual, double delta) {
    TestCase.assertEquals(expected, actual, delta);
    asserts++;
  }
  static public void assertEquals(String message, float expected, float actual, float delta) {
    TestCase.assertEquals(message, expected, actual, delta);
    asserts++;
  }
  static public void assertEquals(float expected, float actual, float delta) {
    TestCase.assertEquals(expected, actual, delta);
    asserts++;
  }
  static public void assertEquals(String message, long expected, long actual) {
    TestCase.assertEquals(message, expected, actual);
    asserts++;
  }
  static public void assertEquals(long expected, long actual) {
    TestCase.assertEquals(expected, actual);
    asserts++;
  }
  static public void assertEquals(String message, boolean expected, boolean actual) {
    TestCase.assertEquals(message, expected, actual);
    asserts++;
  }
  static public void assertEquals(boolean expected, boolean actual) {
    TestCase.assertEquals(expected, actual);
    asserts++;
  }
  static public void assertEquals(String message, byte expected, byte actual) {
    TestCase.assertEquals(message, expected, actual);
    asserts++;
  }
  static public void assertEquals(byte expected, byte actual) {
    TestCase.assertEquals(expected, actual);
    asserts++;
  }
  static public void assertEquals(String message, char expected, char actual) {
    TestCase.assertEquals(message, expected, actual);
    asserts++;
  }
  static public void assertEquals(char expected, char actual) {
    TestCase.assertEquals(expected, actual);
    asserts++;
  }
  static public void assertEquals(String message, short expected, short actual) {
    TestCase.assertEquals(message, expected, actual);
    asserts++;
  }
  static public void assertEquals(short expected, short actual) {
    TestCase.assertEquals(expected, actual);
    asserts++;
  }
  static public void assertEquals(String message, int expected, int actual) {
    TestCase.assertEquals(message, expected, actual);
    asserts++;
  }
  static public void assertEquals(int expected, int actual) {
    TestCase.assertEquals(expected, actual);
    asserts++;
  }
  static public void assertNotNull(Object object) {
    TestCase.assertNotNull(object);
    asserts++;
  }
  static public void assertNotNull(String message, Object object) {
    TestCase.assertNotNull(message, object);
    asserts++;
  }
  static public void assertNull(Object object) {
    TestCase.assertNull(object);
    asserts++;
  }
  static public void assertNull(String message, Object object) {
    TestCase.assertNull(message, object);
    asserts++;
  }
  static public void assertSame(String message, Object expected, Object actual) {
    TestCase.assertSame(message, expected, actual);
    asserts++;
  }
  static public void assertSame(Object expected, Object actual) {
    TestCase.assertSame(expected, actual);
    asserts++;
  }

  static public void fail(String message, Throwable e) {
    e.printStackTrace();
		TestCase.fail(message);
  }
  
 
  // Test directory

  public static String getTestDirectory() {
    String testroot = System.getProperty("net.ontopia.test.root");
    // Fall back to the user home directory
    if (testroot == null)
      testroot = System.getProperty("user.dir") + File.separator + "test-data";
    // Complain if the directory couldn't be found.
    if (testroot == null)
      throw new OntopiaRuntimeException("Could not find test root directory." +
                                        " Please set the 'net.ontopia.test.root'" +
                                        " system property.");
    return testroot;
  }

  // File convenience methods

  protected void verifyDirectory(String dir) {
    File thedir = new File(dir);
    if (!thedir.exists())
      thedir.mkdir();
  }

  protected void verifyDirectory(String base, String dir) {
    File thedir = new File(base + File.separator + dir);
    if (!thedir.exists())
      thedir.mkdir();
  }

  protected void verifyDirectory(String base, String sub1, String sub2) {
    File thedir = new File(base + File.separator + sub1 + File.separator +
                           sub2);
    if (!thedir.exists())
      thedir.mkdirs();
  }
  
  /**
   * Read in content from file. If the file doesn't exist or has no content
   * a valid empty String will be returned.
   */
  protected String getFileContent(String file) {
    StringBuffer content = new StringBuffer("");
    try {
      BufferedReader read = new BufferedReader(new FileReader(file));
      String line = read.readLine();
      if (line != null)
        content.append( line );
      while (line != null) {
        line = read.readLine();
        if (line != null)
          content.append( line );
      }
      read.close();
    } catch (IOException e) {
      // we accept that
    }

    return content.toString();
  }

  public String resolveFileName(String filename) {
    String root = getTestDirectory();
    return root + File.separator + filename;
  }
  
  public String resolveFileName(String dir, String filename) {
    String root = getTestDirectory();
    return root + File.separator + dir + File.separator + filename;
  }

  public String resolveFileName(String dir, String subdir, String filename) {
    String root = getTestDirectory();
    return root + File.separator + dir + File.separator + subdir + File.separator +
           filename;
  }

}
