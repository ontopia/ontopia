
// $Id: TestRunner.java,v 1.26 2005/07/01 07:23:00 grove Exp $

package net.ontopia.test;

import java.util.Enumeration;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.ontopia.utils.CmdlineOptions;
import net.ontopia.utils.CmdlineUtils;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * The class used to run tests from the command-line.
 */
public class TestRunner extends junit.textui.TestRunner {

  protected TestSuite tests;
  protected Class generatorIF;

  // Define a logging category.
  static Logger log = Logger.getLogger(TestRunner.class.getName());

  public static void main(String[] argv) {
    // Set up test runner (includes logging initialization)
    TestRunner self = new TestRunner();
      
    // Initialize command line option parser and listeners
    CmdlineOptions options = new CmdlineOptions("TestRunner", argv);
      
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
    
    try {
      if (args.length < 1) {
        usage();
        System.exit(1);
      } else if (args.length == 1) 
        self.run(args[0], "default");
      else {
        String[] groups = new String[args.length - 1];
        // run for all specified test groups 
        for (int i=1; i < args.length; i++)
          groups[i-1] = args[i];
        self.run(args[0], groups);
      }
    }
    catch (java.io.IOException e) {
      System.err.println("Couldn't read file: " + e.getMessage());
    }
    catch (org.xml.sax.SAXException e) {
      System.err.println("XML parse error: " + e);
    }
  }

  public TestRunner() {

    // Initialize logging
    CmdlineUtils.initializeLogging();
                               
    if (System.getProperty("log4j.configuration") == null) 
      LogManager.getLoggerRepository().setThreshold((Level) Level.WARN);

    try {
      generatorIF = Class.forName("net.ontopia.test.TestCaseGeneratorIF");
    }
    catch (ClassNotFoundException e) {
      // can't continue if not found
      throw new RuntimeException(e.toString()); 
    }       
  }
 
  public void run(String collectionFile, String testGroup) 
    throws java.io.IOException, org.xml.sax.SAXException {
    String[] testGroups = new String[1];
    testGroups[0] = testGroup;
    run(collectionFile, testGroups);
  }

  public void run(String collectionFile, String testGroups[]) 
    throws java.io.IOException, org.xml.sax.SAXException {

    // 1: read in test case definition file
    tests = new TestSuite();
    XMLCollectionReader reader = new XMLCollectionReader(collectionFile);

    // 2: figure out what the user wants to run, build test case    
    for (int i=0; i < testGroups.length; i++) {
      Iterator iterator = reader.getTests(testGroups[i]);
      if (iterator == null) {
        System.err.println("ERROR: group " + testGroups[i] + " unknown!");
        return;
      }
      while (iterator.hasNext())
        addClass((String) iterator.next());

      // List tests [debug info]
      if (log.isDebugEnabled()) {
        Enumeration enumeration = tests.tests();
        while (enumeration.hasMoreElements()) {
          log.debug("Test: " + enumeration.nextElement());
        }
      }
    } // for
    
    // Output header
    System.out.println("Running tests");
        
    // 3: Run the test case using junit.textui.TestRunner
    doRun(tests, false);

    // Output assert count
    System.out.println("Asserts: " + AbstractOntopiaTestCase.getAssertCount());
  }

  public void addClass(String name) {
    try {
      Class test = Class.forName(name);

      if (!isTestCaseGenerator(test)) {
        // it's an ordinary class implementing TestCase
        tests.addTestSuite(test);
        return;
      }

      // it's a class implementing TestCaseGeneratorIF
      TestCaseGeneratorIF generator = 
        (TestCaseGeneratorIF) test.newInstance();
      Iterator it = generator.generateTests();
      while (it.hasNext())
        tests.addTest((Test) it.next());
    }
    catch (ClassNotFoundException e) {
      System.err.println("ERROR: Couldn't find class '" + name + "'");
    }
    catch (InstantiationException e) {
      System.err.println("ERROR: Couldn't instantiate class " + name);
    }
    catch (IllegalAccessException e) {
      System.err.println("ERROR: Couldn't instantiate class " + name);
    }
  }

  public boolean isTestCaseGenerator(Class test) {
    Class[] interfaces = test.getInterfaces();
    for (int ix = 0; ix < interfaces.length; ix++)
      if (generatorIF.equals(interfaces[ix]))
        return true;
        
    if (test.getSuperclass() != null)
      return isTestCaseGenerator(test.getSuperclass());
    else
      return false;
  }

  private static void usage() {
    System.out.println("java net.ontopia.test.TestRunner [options] <xmlfile> [<testgroup1>] [<testgroup2>] ...");
    System.out.println("");
    System.out.println("  Runs the tests specified for all the given test groups.");
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("");
    System.out.println("  <xmlfile>:    the test configuration file");
    System.out.println("  <testgroupN>: the test group to run");
    System.out.println("");
  }

}
