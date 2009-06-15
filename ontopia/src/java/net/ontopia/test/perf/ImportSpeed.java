
// $Id: ImportSpeed.java,v 1.11 2008/06/13 08:17:50 geir.gronmo Exp $

package net.ontopia.test.perf;

import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Arrays;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.ImportExportUtils;

public class ImportSpeed {
  private final static int times = 100;
  
  public static void main(String args[]) throws IOException {
    for (int ix = 0; ix < args.length; ix++) {
      System.out.println("===== " + args[ix]);
      test(args[ix]);
    }
  }

  public static void test(String file) throws IOException {
    TopicMapReaderIF reader;

    long timings[] = new long[times];
    int objects = 0;

    for (int ix = 0; ix < times; ix++) {
      reader = ImportExportUtils.getReader(file);
      
      long start = System.currentTimeMillis();
      TopicMapIF tm = reader.read();
      long time = System.currentTimeMillis();
      timings[ix] += time - start;

      if (objects == 0) {
        objects = countObjects(tm);
        System.out.println("Object count: " + objects);
      }
      
      tm = null;
      System.gc();
      System.out.println("Iteration: " + (ix + 1) + ": " + (time - start));
    }

    // reduce(operator.add, timings.sort()[5 : -5])
    Arrays.sort(timings);
    long total = 0;
    for (int ix = 5; ix < times - 5; ix++)
      total += timings[ix];
    
    float totalSecs = (float) total / 1000;
    System.out.println("Average import time in seconds: " +
                       ((totalSecs / (times-10))));
    System.out.println("Object count: " + objects);
    System.out.println("Obj/sec: " + (objects / (totalSecs / (times-10))));
  }

  public static int countObjects(TopicMapIF tm) {
    int count = 1; // tm itself
    count += tm.getItemIdentifiers().size();

    Iterator it = tm.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      
      count++; // topic
      count += topic.getItemIdentifiers().size();
      count += topic.getSubjectIdentifiers().size();
      count += topic.getSubjectLocators().size();

      Iterator it2 = topic.getTopicNames().iterator();
      while (it2.hasNext()) {
        TopicNameIF bn = (TopicNameIF) it2.next();
        
        count++; // basename
        count += bn.getItemIdentifiers().size();

        Iterator it3 = bn.getVariants().iterator();
        while (it3.hasNext()) {
          VariantNameIF vn = (VariantNameIF) it3.next();
        
          count++; // variant
          count += vn.getItemIdentifiers().size();
          if (vn.getLocator() != null) count++;
        }       
      }

      it2 = topic.getOccurrences().iterator();
      while (it2.hasNext()) {
        OccurrenceIF occ = (OccurrenceIF) it2.next();
        count++; // occurrence
        count += occ.getItemIdentifiers().size();
        if (occ.getLocator() != null) count++;
      }
    }


    it = tm.getAssociations().iterator();
    while (it.hasNext()) {
      AssociationIF assoc = (AssociationIF) it.next();
      
      count++; // assoc
      count += assoc.getItemIdentifiers().size();
      
      Iterator it2 = assoc.getRoles().iterator();
      while (it2.hasNext()) {
        AssociationRoleIF role = (AssociationRoleIF) it2.next();
        
        count++; // role
        count += role.getItemIdentifiers().size();
      }
    }

    return count;
  }
}
