
// $Id: MD5.java,v 1.5 2004/08/20 10:24:45 trost Exp $

package net.ontopia.product;

import java.io.*;
import java.util.*;
import net.ontopia.utils.StreamUtils;

public class CreateProductClass {

  public static void main(String[] args) throws Exception {
    // usage: CreateProductClass directory product MAJOR_VERSION MINOR_VERSION MICRO_VERSION BETA_VERSION BUILD_USER
    String directory = args[0];
    String product = args[1];
    String major_version = args[2];
    String minor_version = args[3];
    String micro_version = args[4];
    String beta_version = args[5];
    String build_user = args[6];

    Calendar cal = Calendar.getInstance();
    String build_date = cal.get(Calendar.YEAR) + ", " + cal.get(Calendar.MONTH) + 
      ", " + cal.get(Calendar.DAY_OF_MONTH) + ", " + cal.get(Calendar.HOUR_OF_DAY) + 
      ", " + cal.get(Calendar.MINUTE);
    
    FileReader reader = new FileReader(new File(directory, product + ".java.in"));
    String content = StreamUtils.read(reader);
    reader.close();

    content = content.replace("%(MAJOR_VERSION)s", major_version);
    content = content.replace("%(MINOR_VERSION)s", minor_version);
    content = content.replace("%(MICRO_VERSION)s", micro_version);
    content = content.replace("%(BETA_VERSION)s", beta_version);
    content = content.replace("%(BUILD_DATE)s", build_date);
    content = content.replace("%(BUILD_USER)s", build_user);

    File outfile = new File(directory, product + ".java");
    System.out.println("Generating product class: " + outfile);

    FileWriter writer = new FileWriter(outfile);
    writer.write(content);
    writer.close();

  }

}
