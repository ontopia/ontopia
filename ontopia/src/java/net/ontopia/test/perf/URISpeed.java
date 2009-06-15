// $Id: URISpeed.java,v 1.6 2009/02/27 11:57:52 lars.garshol Exp $

package net.ontopia.test.perf;

// Measures the time needed to create URILocator objects, and also to
// resolve relative URLs.

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import net.ontopia.infoset.impl.basic.URILocator;

public class URISpeed {

  protected static boolean debug = false;
  
  public static void main(String args[]) throws IOException {
    timeURICreation();
    timeRelativeURIResolution();
    timeURIHashcode();
    timeURIEquals();

    timeURICreation();
    timeRelativeURIResolution();
    timeURIHashcode();
    timeURIEquals();
  }    

  public static void timeURICreation() throws IOException {
    String[] uris = {
      "http://www.ontopia.net",
      "http://www.puccini.it/files/vocepucc.wav",
      "http://en.wikipedia.org/wiki/Voltaire",
      "http://www.topicmaps.org/xtm/1.0/core.xtm#display",
      "http://localhost:8080/operamap/occurs/theatre/teatro-della-pergola-640x480.jpg",
      "http://www.delteatro.it/hdoc/result_opera.asp?idopera=1353",
      "http://members.tripod.com/~Wolfgang5/index-2.html",
      "http://www.orc.soton.ac.uk/ngb/list2.html",
      "http://www.delteatro.it/hdoc/diz2home.asp",
      "file://Users/larsga/data/bilder/privat/ymse-2/p6071965.jpg",
      "file://Users/larsga/data/bilder/privat/2008/oda-daap/DSC_0070.jpg"
    };
    long start = System.currentTimeMillis();

    for (int ix = 0; ix < 1000000; ix++) {
      try {
        URILocator loc = new URILocator(uris[ix % uris.length]);
      } catch (MalformedURLException e) {
      }
    }
    long time = System.currentTimeMillis() - start;
    System.out.println("Average URI creation time in millisecs: " +
                       ((float) time / (float) 1000000));
  }

  public static void timeRelativeURIResolution() throws MalformedURLException {
    URILocator base = new URILocator("http://a/b/c/d;p?q");
    int times = 1000000;
    long start = System.currentTimeMillis();
    for (int ix = 0; ix < times; ix++) {
      base.resolveAbsolute("#foo");
      base.resolveAbsolute("xtm.xtm#foo");
      base.resolveAbsolute("../xtm.xtm#foo");
      base.resolveAbsolute("http://www.ontopia.net");
      if (debug && ix % 100000 == 0)
        System.out.println(ix);
    }
    
    long time = System.currentTimeMillis() - start;
    System.out.println("Average relative URI resolution time in millisecs: " +
                       ((float) time / (float) (times*4)));
  }

  public static void timeURIHashcode() throws MalformedURLException {
    URILocator uri1 = new URILocator("http://a/b/c/d;p?q");
    URILocator uri2 = new URILocator("mailto:larsga@ontopia.net");
    URILocator uri3 = new URILocator("http://a/b/c/d;p?q#fragment");
    URILocator uri4 = new URILocator("file:/home/larsga/cvs-co/topicmap.xtm#fragment");
    
    int times = 10000000;
    long start = System.currentTimeMillis();
    for (int ix = 0; ix < times; ix++) {
      uri1.hashCode();
      uri2.hashCode();
      uri3.hashCode();
      uri4.hashCode();
      if (debug && ix % 100000 == 0)
        System.out.println(ix);
    }
    
    long time = System.currentTimeMillis() - start;
    System.out.println("Average URI hashcode time in millisecs: " +
                       ((float) time / (float) (times*4)));
  }

  public static void timeURIEquals() throws MalformedURLException {
    
    URILocator base = new URILocator("http://www.ontopia.net/~grove");

    URILocator uri1 = new URILocator("ftp://ftp.ontopia.net/");
    URILocator uri2 = new URILocator("http://www.yahoo.com/");
    URILocator uri3 = new URILocator("http://www.ontopia.net/~grove");
    URILocator uri4 = new URILocator("http://www.ontopia.net/software/index.html");
    URILocator uri5 = new URILocator("http://www.ontopia.net/");
    
    int times = 10000000 * 5;
    long start = System.currentTimeMillis();
    for (int ix = 0; ix < times; ix++) {
      base.equals(uri1);
      base.equals(uri2);
      base.equals(uri3);
      base.equals(uri4);
      base.equals(uri5);
      if (debug && ix % 100000 == 0)
        System.out.println(ix);
    }
    
    long time = System.currentTimeMillis() - start;
    System.out.println("Average URI comparison (equals): " +
                       ((float) time / (float) (times*4)));
  }
  
}






