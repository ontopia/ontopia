
// $Id: TestMemoryUse.java,v 1.5 2004/11/28 13:45:21 larsga Exp $

package net.ontopia.test.perf;

import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.core.*;

public class TestMemoryUse {

  public static void main(String args[]) throws IOException {
    for (int ix = 0; ix < args.length; ix++)
      test(args[ix]);
  }

  private static void test(String file) throws IOException {
    TopicMapReaderIF reader = ImportExportUtils.getReader(file);
    TopicMapIF tm = reader.read();
    tm = null;
    reader = null;

    System.gc(); System.gc(); System.gc(); System.gc();
    System.gc(); System.gc(); System.gc(); System.gc();
    System.gc(); System.gc(); System.gc(); System.gc();
    System.gc(); System.gc(); System.gc(); System.gc();

    reader = ImportExportUtils.getReader(file);
    long before = Runtime.getRuntime().totalMemory() -
                  Runtime.getRuntime().freeMemory();

    tm = reader.read();

    System.gc(); System.gc(); System.gc(); System.gc();
    System.gc(); System.gc(); System.gc(); System.gc();
    System.gc(); System.gc(); System.gc(); System.gc();
    System.gc(); System.gc(); System.gc(); System.gc();

    long after = Runtime.getRuntime().totalMemory() -
                 Runtime.getRuntime().freeMemory();

    System.out.println(file + ": " + (after - before));

    reader = null;
    tm = null;

    System.gc(); System.gc(); System.gc(); System.gc();
    System.gc(); System.gc(); System.gc(); System.gc();
    System.gc(); System.gc(); System.gc(); System.gc();
    System.gc(); System.gc(); System.gc(); System.gc();

    long end = Runtime.getRuntime().totalMemory() -
               Runtime.getRuntime().freeMemory();
    System.out.println("finally: " + (end - before));
  }
}
