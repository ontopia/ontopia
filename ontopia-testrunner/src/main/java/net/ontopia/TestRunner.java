package net.ontopia;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import net.ontopia.utils.ResourcesDirectoryReader;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runners.model.InitializationError;

/**
 * The class used to run tests from the command-line.
 */
public class TestRunner {

  protected HashSet<Class> testClasses;

  public static void main(String[] argv) throws IllegalAccessException, InstantiationException, SecurityException, InitializationError {
    TestRunner self = new TestRunner();
    self.run();
  }

  public TestRunner() {
  }

  public void run() throws InstantiationException, IllegalAccessException, SecurityException, InitializationError {

    testClasses = new HashSet<Class>();

    ResourcesDirectoryReader reader = new ResourcesDirectoryReader("net/ontopia/", true, ".class");
    for (String resource : reader.getResources()) {
      if ((match("*Test.class", resource)
              || match("*Tests.class", resource)
              || match("*Test*.class", resource)
              || match("*TestCase.class", resource)) && (!resource.contains("$"))) {
        // test case candidate 

        // file to classname
        String classname = resource.replaceAll("/", ".");

        // remove extension
        classname = classname.substring(0, classname.length() - 6);

        try {
          Class<?> testClass = Class.forName(classname);
          if (!testClass.equals(this.getClass())) {

            //skip abstrac classes
            if (!Modifier.isAbstract(testClass.getModifiers())) {


              // skip RDBMS for now
              if (!classname.contains(".rdbms.")) {
                testClasses.add(testClass);
              } else {
                System.out.println("Skipping rdbms test class [" + classname + "]");
              }
            }
          }
        } catch (ClassNotFoundException cnfe) {
          System.out.println("Class not found: " + classname);
        }
      }
    }

    JUnitCore core = new JUnitCore();
    CountingRunListener globalcounter = new CountingRunListener(true);
    core.addListener(globalcounter);
    for (Class test : testClasses) {

      CountingRunListener classcounter = new CountingRunListener(false);
      core.addListener(classcounter);
      core.run(test);

      System.out.println("Running: " + test.getName() + (classcounter.fails + classcounter.errs > 0 ? " << FAILURES" : ""));
      System.out.println("Tests run: " + classcounter.count + ", Failures: " + classcounter.fails + ", Errors: " + classcounter.errs + ", Skipped: " + classcounter.skip + ", Time elapsed: " + classcounter.time);

      // remove listener for this class
      core.removeListener(classcounter);
    }

    System.out.println();
    System.out.println("---------------------------------------------------------------------");
    System.out.println();
    System.out.println("Tests run: " + globalcounter.count + ", Failures: " + globalcounter.fails + ", Errors: " + globalcounter.errs + ", Skipped: " + globalcounter.skip);
    System.out.println();
    System.out.println("---------------------------------------------------------------------");
    System.out.println();
    System.out.println("Test failures: ");
    for (Failure f : globalcounter.failures) {
      System.out.println("   " + f.getTestHeader());
    }
    System.out.println();
    System.out.println("Test errors: ");
    for (Failure f : globalcounter.errors) {
      System.out.println("   " + f.getTestHeader() + ": " + f.getException().getMessage());
    }
    System.out.println();
    System.out.println("---------------------------------------------------------------------");

  }

  class CountingRunListener extends RunListener {

    public int count = 0;
    public int fails = 0;
    public int errs = 0;
    public int skip = 0;
    public long time = 0;
    private boolean saveFailures;
    public List<Failure> failures = null;
    public List<Failure> errors = null;

    public CountingRunListener(boolean saveFailures) {
      this.saveFailures = saveFailures;

      if (saveFailures) {
        failures = new ArrayList<Failure>();
        errors = new ArrayList<Failure>();
      }
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
      super.testRunStarted(description);
      time = System.currentTimeMillis();
    }

    @Override
    public void testFinished(Description description) throws Exception {
      super.testFinished(description);
      count++;
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
      super.testRunFinished(result);
      time = System.currentTimeMillis() - time;
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
      super.testFailure(failure);

      if (failure.getException() != null) {
        errs++;
        if (saveFailures) {
          errors.add(failure);
        }
      } else {
        fails++;
        if (saveFailures) {
          failures.add(failure);
        }
      }
    }

    @Override
    public void testIgnored(Description description) throws Exception {
      super.testIgnored(description);
      skip++;
    }
  }

  // copied from plexus utils
  public static boolean match(String pattern, String str) {
    return match(pattern, str, true);
  }

  public static boolean match(String pattern, String str,
          boolean isCaseSensitive) {
    char[] patArr = pattern.toCharArray();
    char[] strArr = str.toCharArray();
    int patIdxStart = 0;
    int patIdxEnd = patArr.length - 1;
    int strIdxStart = 0;
    int strIdxEnd = strArr.length - 1;
    char ch;

    boolean containsStar = false;
    for (int i = 0; i < patArr.length; i++) {
      if (patArr[i] == '*') {
        containsStar = true;
        break;
      }
    }

    if (!containsStar) {
      // No '*'s, so we make a shortcut
      if (patIdxEnd != strIdxEnd) {
        return false; // Pattern and string do not have the same size
      }
      for (int i = 0; i <= patIdxEnd; i++) {
        ch = patArr[i];
        if (ch != '?' && !equals(ch, strArr[i], isCaseSensitive)) {
          return false; // Character mismatch
        }
      }
      return true; // String matches against pattern
    }

    if (patIdxEnd == 0) {
      return true; // Pattern contains only '*', which matches anything
    }

    // Process characters before first star
    while ((ch = patArr[patIdxStart]) != '*' && strIdxStart <= strIdxEnd) {
      if (ch != '?' && !equals(ch, strArr[strIdxStart], isCaseSensitive)) {
        return false; // Character mismatch
      }
      patIdxStart++;
      strIdxStart++;
    }
    if (strIdxStart > strIdxEnd) {
      // All characters in the string are used. Check if only '*'s are
      // left in the pattern. If so, we succeeded. Otherwise failure.
      for (int i = patIdxStart; i <= patIdxEnd; i++) {
        if (patArr[i] != '*') {
          return false;
        }
      }
      return true;
    }

    // Process characters after last star
    while ((ch = patArr[patIdxEnd]) != '*' && strIdxStart <= strIdxEnd) {
      if (ch != '?' && !equals(ch, strArr[strIdxEnd], isCaseSensitive)) {
        return false; // Character mismatch
      }
      patIdxEnd--;
      strIdxEnd--;
    }
    if (strIdxStart > strIdxEnd) {
      // All characters in the string are used. Check if only '*'s are
      // left in the pattern. If so, we succeeded. Otherwise failure.
      for (int i = patIdxStart; i <= patIdxEnd; i++) {
        if (patArr[i] != '*') {
          return false;
        }
      }
      return true;
    }

    // process pattern between stars. padIdxStart and patIdxEnd point
    // always to a '*'.
    while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
      int patIdxTmp = -1;
      for (int i = patIdxStart + 1; i <= patIdxEnd; i++) {
        if (patArr[i] == '*') {
          patIdxTmp = i;
          break;
        }
      }
      if (patIdxTmp == patIdxStart + 1) {
        // Two stars next to each other, skip the first one.
        patIdxStart++;
        continue;
      }
      // Find the pattern between padIdxStart & padIdxTmp in str between
      // strIdxStart & strIdxEnd
      int patLength = (patIdxTmp - patIdxStart - 1);
      int strLength = (strIdxEnd - strIdxStart + 1);
      int foundIdx = -1;
      strLoop:
      for (int i = 0; i <= strLength - patLength; i++) {
        for (int j = 0; j < patLength; j++) {
          ch = patArr[patIdxStart + j + 1];
          if (ch != '?' && !equals(ch, strArr[strIdxStart + i + j], isCaseSensitive)) {
            continue strLoop;
          }
        }

        foundIdx = strIdxStart + i;
        break;
      }

      if (foundIdx == -1) {
        return false;
      }

      patIdxStart = patIdxTmp;
      strIdxStart = foundIdx + patLength;
    }

    // All characters in the string are used. Check if only '*'s are left
    // in the pattern. If so, we succeeded. Otherwise failure.
    for (int i = patIdxStart; i <= patIdxEnd; i++) {
      if (patArr[i] != '*') {
        return false;
      }
    }
    return true;
  }

  private static boolean equals(char c1, char c2, boolean isCaseSensitive) {
    if (c1 == c2) {
      return true;
    }
    if (!isCaseSensitive) {
      // NOTE: Try both upper case and lower case as done by String.equalsIgnoreCase()
      if (Character.toUpperCase(c1) == Character.toUpperCase(c2)
              || Character.toLowerCase(c1) == Character.toLowerCase(c2)) {
        return true;
      }
    }
    return false;
  }
}
