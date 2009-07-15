
// $Id: ImportExportUtils.java,v 1.41 2009/02/12 11:51:39 lars.garshol Exp $

package net.ontopia.topicmaps.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import net.ontopia.utils.URIUtils;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.core.TopicMapImporterIF;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.topicmaps.xml.XTMTopicMapWriter;
import net.ontopia.topicmaps.xml.XTM2TopicMapWriter;
import net.ontopia.topicmaps.xml.TMXMLReader;
import net.ontopia.topicmaps.xml.TMXMLWriter;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapReader;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapWriter;
import net.ontopia.topicmaps.utils.ctm.CTMTopicMapReader;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapReader;
import net.ontopia.topicmaps.utils.rdf.RDFTopicMapReader;
import net.ontopia.topicmaps.utils.rdf.RDFTopicMapWriter;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * PUBLIC: Utilities for importing and exporting topic maps.
 * 
 * @since 1.2
 */
public class ImportExportUtils {

  /**
   * PUBLIC: Given the topic map store properties file and file name
   * or URL of a topic map, returns a topic map reader of the right
   * class. Uses the file extension to determine what reader to
   * create. Supports the suffixes '.xtm' and '.ltm' and URI schemes
   * 'x-ontopia:tm-rdbms:'.
   * 
   * @since 1.2.4
   */
  public static TopicMapReaderIF getReader (String propfile,
      String filename_or_url) {

    if (filename_or_url.startsWith ("x-ontopia:tm-rdbms:"))
      return new RDBMSTopicMapReader (propfile, getTopicMapId (filename_or_url));
    // Otherwise fall back to the property-less getReader method
    return getReader (filename_or_url);
  }

  /**
   * PUBLIC: Given the topic map store properties and file name or URL
   * of a topic map, returns a topic map reader of the right
   * class. Uses the file extension to determine what reader to
   * create. Supports the suffixes '.xtm' and '.ltm' and URI schemes
   * 'x-ontopia:tm-rdbms:'.
   * 
   * @since 1.2.4
   */
  public static TopicMapReaderIF getReader (Map properties,
      String filename_or_url) {

    if (filename_or_url.startsWith ("x-ontopia:tm-rdbms:"))
      return new RDBMSTopicMapReader (properties,
          getTopicMapId (filename_or_url));
    // Otherwise fall back to the property-less getReader method
    return getReader (filename_or_url);
  }

  /**
   * PUBLIC: Given a file reference to a topic map, returns a topic
   * map reader of the right class. Uses the file extension to
   * determine what reader to create. Supports '.xtm', and '.ltm'.
   * 
   * @since 2.0
   */
  public static TopicMapReaderIF getReader (java.io.File file)
      throws java.io.IOException {
    return getReader (file.toURL ().toExternalForm ());
  }

  /**
   * PUBLIC: Given the file name or URL of a topic map, returns a
   * topic map reader of the right class. Uses the file extension to
   * determine what reader to create. Supports '.xtm', and '.ltm'.
   */
  public static TopicMapReaderIF getReader (String filename_or_url) {
    return getReader (URIUtils.getURI (filename_or_url));
  }

  /**
   * PUBLIC: Given a locator referring to a topic map, returns a topic
   * map reader of the right class. Uses the file extension to
   * determine what reader to create. Supports '.xtm', '.tmx', and
   * '.ltm'.
   * 
   * @since 2.0
   */
  public static TopicMapReaderIF getReader (LocatorIF url) {

    String address = url.getAddress ();

    if (address.startsWith ("x-ontopia:tm-rdbms:"))
      return new RDBMSTopicMapReader (getTopicMapId (address));
    else if (address.endsWith (".xtm"))
      return new XTMTopicMapReader (url);
    else if (address.endsWith (".ltm"))
      return new LTMTopicMapReader (url);
    else if (address.endsWith (".tmx"))
      return new TMXMLReader (url);
    else if (address.endsWith (".rdf"))
      return makeRDFReader (url, "RDF/XML");
    else if (address.endsWith (".n3"))
      return makeRDFReader (url, "N3");
    else if (address.endsWith (".nt"))
      return makeRDFReader (url, "N-TRIPLE");
    else if (address.endsWith (".xml"))
      return new TMXMLReader(url); 
    else if (address.endsWith (".ctm"))
      return new CTMTopicMapReader(url);
   else
      return new XTMTopicMapReader (url);
  }

  private static TopicMapReaderIF makeRDFReader (LocatorIF url, String syntax) {
    // the purpose of this method is to reduce bytecode reliance on Jena
    return new RDFTopicMapReader (url, syntax);
  }

  /**
   * PUBLIC: Given the file name or URL of a topic map, returns a
   * topic map importer of the right class. Uses the file extension to
   * determine what importer to create. Supports '.xtm', and '.ltm'.
   */
  public static TopicMapImporterIF getImporter (String filename_or_url) {
    return getImporter (URIUtils.getURI (filename_or_url));
  }

  /**
   * PUBLIC: Given a locator referring to a topic map, returns a topic
   * map reader of the right class. Uses the file extension to
   * determine what reader to create. Supports '.xtm', '.tmx', and
   * '.ltm'.
   * 
   * @since 2.0
   */
  public static TopicMapImporterIF getImporter (LocatorIF url) {

    String address = url.getAddress ();

    if (address.endsWith (".xtm"))
      return new XTMTopicMapReader (url);
    else if (address.endsWith (".ltm"))
      return new LTMTopicMapReader (url);
    else if (address.endsWith (".tmx"))
      return new TMXMLReader (url);
    else if (address.endsWith (".rdf"))
      return (TopicMapImporterIF)makeRDFReader (url, "RDF/XML");
      else
      return new XTMTopicMapReader (url);
  }

  /**
   * PUBLIC: Given the file name of a topicmap, returns a topicmap
   * writer of the right class. Uses the file extension to determine
   * what writer to create.  Supports '.xtm' and '.tmx'. If the suffix
   * is unknown, the default writer is a XTM writer.
   */
  public static TopicMapWriterIF getWriter (String tmfile)
      throws java.io.IOException {
    if (tmfile.endsWith (".rdf"))
      return new RDFTopicMapWriter (new FileOutputStream (tmfile));
    else if (tmfile.endsWith (".ltm"))
      return new LTMTopicMapWriter (new FileOutputStream (tmfile));
    else if (tmfile.endsWith (".tmx"))
      return new TMXMLWriter (tmfile);
    else if (tmfile.endsWith (".xtm1"))
      return new XTMTopicMapWriter (new File (tmfile));
    else
      return new XTM2TopicMapWriter (new File (tmfile));
  }

  /**
   * PUBLIC: Given the file name of a topicmap, returns a topicmap writer of the
   * right class. Uses the file extension to determine what writer to create.
   * Supports '.xtm' and '.tmx'. If the suffix is unknown, the default
   * writer is a XTM writer.
   */
  public static TopicMapWriterIF getWriter (String tmfile, String encoding)
      throws java.io.IOException {
    if (encoding == null)
      return getWriter(tmfile);

    if (tmfile.endsWith(".tmx"))
      return new TMXMLWriter (tmfile, encoding);
    else if (tmfile.endsWith(".xtm1"))
      return new XTMTopicMapWriter(new File(tmfile), encoding);
    else
      return new XTM2TopicMapWriter(new File(tmfile), encoding);
  }

  /**
   * INTERNAL: Gets the numeric topic map id from an RDBMS URI or a
   * simple topic map id reference. Examples: x-ontopia:tm-rdbms:123,
   * x-ontopia:tm-rdbms:M123, 123 and M123.
   */
  public static long getTopicMapId (String address) {
    int offset = 0;
    if (address.startsWith("M"))
      offset = 1;
    else if (address.startsWith("x-ontopia:tm-rdbms:")) {
      // Syntax: x-ontopia:tm-rdbms:12345
      offset = "x-ontopia:tm-rdbms:".length ();
      
      // Ignore M suffix on topic map id
      if (address.charAt (offset) == 'M')
        offset = offset + 1;
    }
    
    try {
      return Long.parseLong (address.substring (offset));
    } catch (NumberFormatException e) {
      throw new OntopiaRuntimeException ("'" + address
          + " is not a valid rdbms topic map URI.");
    }
  }
}
