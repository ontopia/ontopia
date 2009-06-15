
// $Id: ExportSpeed.java,v 1.2 2003/02/26 20:47:20 larsga Exp $

package net.ontopia.test.perf;

import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.ImportExportUtils;

public class ExportSpeed {

  public static void main(String args[]) throws IOException {
    TopicMapReaderIF reader;

    for (int arg = 0; arg < args.length; arg++) {
      System.out.println("===== " + args[arg]);
      reader = ImportExportUtils.getReader(args[arg]);
      TopicMapIF tm = reader.read();
      dotiming(tm,
               "out" + args[arg],
               ImportSpeed.countObjects(tm));
    }
  }

  public static void dotiming(TopicMapIF tm, String file,
                              int objects) throws IOException {
    long times = 100;
    long total = 0;
    
    for (int ix = 0; ix < times; ix++) {
      TopicMapWriterIF writer = ImportExportUtils.getWriter(file);
      
      long start = System.currentTimeMillis();
      writer.write(tm);
      long time = System.currentTimeMillis();
      total += time - start;
      
      System.gc();
      System.out.println("Iteration: " + (ix + 1) + ": " + (time - start));
    }

    float totalSecs = (float) total / 1000;
    System.out.println("Average export time in seconds: " +
                       ((totalSecs / times)));
    System.out.println("Object count: " + objects);
    System.out.println("Obj/sec: " + (objects / (totalSecs / times)));
  }

}
