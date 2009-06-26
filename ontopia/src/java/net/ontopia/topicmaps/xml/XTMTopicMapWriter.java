
// $Id: XTMTopicMapWriter.java,v 1.32 2008/11/03 12:26:35 lars.garshol Exp $

package net.ontopia.topicmaps.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
import net.ontopia.topicmaps.nav.utils.deciders.TMDecider;
import net.ontopia.topicmaps.nav.utils.deciders.TMExporterDecider;
import net.ontopia.utils.DeciderIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.xml.PrettyPrinter;

import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;

/**
 * PUBLIC: A topic map writer that can write topic maps out as either
 * XTM 1.0 or 2.0. The default is XTM 1.0.
 */
public class XTMTopicMapWriter implements TopicMapWriterIF {

  protected DocumentHandler out;
  
  // If writer is instantiated here we'll close it when we're done.
  protected Writer writer;
  
  protected DeciderIF filter;

  protected boolean export_srclocs = false;
  protected boolean add_ids = false;
  protected int xtm_version;

  private static int DEFAULT_XTM_VERSION = XTMSnifferContentHandler.VERSION_XTM10;
  
  /**
   * PUBLIC: Creates a topic map writer bound to the file given in the
   * arguments.  The topic map will be written out in the UTF-8
   * encoding.   
   * @param filename The name of the file to which the topic map is to
   * be written.
   */ 
  public XTMTopicMapWriter(String filename) throws IOException {
    this(new File(filename), "utf-8");
  }
  
  /**
   * PUBLIC: Creates a topic map writer bound to the file given in the
   * arguments.  The topic map will be written out in the UTF-8
   * encoding.   
   * @param file The file object to which the topic map is to be written.
   */
  public XTMTopicMapWriter(File file) throws IOException {
    this(file, "utf-8");
  }

  /**
   * PUBLIC: Creates a topic map writer bound to the file given in the
   * arguments.   
   * @param file The file object to which the topic map is to be written.
   * @param encoding The character encoding to write the topic map in.
   */
  public XTMTopicMapWriter(File file, String encoding) throws IOException {
    this.writer = new OutputStreamWriter(new FileOutputStream(file), encoding);
    this.out = new PrettyPrinter(writer, encoding);
    this.xtm_version = DEFAULT_XTM_VERSION;
  }

  /**
   * PUBLIC: Creates a topic map writer bound to the output stream
   * given in the arguments.   
   * @param stream The output stream to which the topic map is to be
   * written.
   * @param encoding The character encoding to write the topic map in.
   * @exception UnsupportedEncodingException Thrown when the character
   * encoding is not supported by the Java environment.
   */
  public XTMTopicMapWriter(OutputStream stream, String encoding)
    throws IOException, UnsupportedEncodingException {
    this(new OutputStreamWriter(stream, encoding), encoding);
  }

  /**
   * PUBLIC: Creates a topic map writer bound to the Writer given in
   * the arguments; we do <em>not</em> recommend the use of this
   * method.
   *
   * @param writer The Writer to which the topic map is to be
   * written.
   * @param encoding The character encoding the Writer writes in.
   * Note that this <em>must</em> be set correctly, or the XML
   * document will not parse correctly.
   * @since 1.1
   */
  public XTMTopicMapWriter(Writer writer, String encoding) throws IOException {
    this.out = new PrettyPrinter(writer, encoding);
    this.xtm_version = DEFAULT_XTM_VERSION;
  }

  /**
   * PUBLIC: Sets a filter used to filter the topic map before export.
   * Only topics, associations, and other characteristics accepted by
   * the filter are included in the export.
   *
   * @since 3.0
   */
  public void setFilter(DeciderIF filter) {
    this.filter = filter;
  }

  /**
   * PUBLIC: Returns true if configured to add IDs to all elements.
   * @since 2.0
   */
  public boolean getAddIds() {
    return add_ids;
  }

  /**
   * PUBLIC: Tells the exporter whether or not to add IDs to all
   * elements. (Default: false.)
   * @since 2.0
   */
  public void setAddIds(boolean add_ids) {
    this.add_ids = add_ids;
  }

  /**
   * PUBLIC: Set XTM version to use on export.
   * @since 4.0.0
   */
  public void setVersion(int version) {
    xtm_version = version;
  }

  public void write(TopicMapIF topicmap) throws IOException {
    try {
      if (xtm_version == XTMSnifferContentHandler.VERSION_XTM10) {
        XTMTopicMapExporter exporter = new XTMTopicMapExporter();
        if (filter != null)
          exporter.setFilter(filter);
        exporter.setExportSourceLocators(getExportSourceLocators());
        exporter.setAddIds(getAddIds());
        exporter.export(topicmap, out);
      } else if (xtm_version == XTMSnifferContentHandler.VERSION_XTM20) {
        XTM2TopicMapExporter exporter = new XTM2TopicMapExporter();
        if (filter != null)
          exporter.setFilter(filter);
        exporter.export(topicmap, out);
      } else
        throw new OntopiaRuntimeException("Unknown XTM version: " + xtm_version);
      
      if (writer != null) writer.close();
    }
    catch (SAXException e) {
      if (e.getException() instanceof IOException)
        throw (IOException) e.getException();
      throw new IOException("XML writing problem: " + e.toString());
    }
  }

  /**
   * INTERNAL: Returns true if source locators should be exported.
   */
  public boolean getExportSourceLocators() {
    return export_srclocs;
  }

  /**
   * INTERNAL: Set the flag that says whether source locators should
   * be exported or not.
   */
  public void setExportSourceLocators(boolean export_srclocs) {
    this.export_srclocs = export_srclocs;
  }
    
}
