
// $Id: MD5.java,v 1.5 2004/08/20 10:24:45 trost Exp $

package net.ontopia.product;

import java.io.*;
import net.ontopia.net.MD5;

public class JarDigests {

  public static void main(String[] args) throws Exception {
    // usage: JarDigests /tmp/ .jar output.txt 
    String directory = args[0];
    String pattern = args[1];
    String outfile = args[2];

    MD5 md5 = new MD5();
    Writer writer = new FileWriter(outfile);
    try {
      File dir = new File(directory);
      String[] list = dir.list();
      for (int i=0; i < list.length; i++) {
        if (list[i].matches(pattern)) {
          String name = list[i];
          String filename = directory + name;
  
          InputStream istream = new FileInputStream(filename);
          try {
            writer.write(md5.getDigest(istream));
            writer.write("  ");
            writer.write(name);
            writer.write("\n");
          } finally {
            try { istream.close(); } catch (Exception e) {}
          }
        }
      }
    } finally {
      try { writer.close(); } catch (Exception e) {}
    }      
  }

}
