
// $Id: XTM2TopicMapWriter.java,v 1.3 2008/07/04 10:20:49 lars.garshol Exp $

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
 * PUBLIC: A topic map writer that can write topic maps out into the
 * interchange syntax defined by XTM 2.0.
 *
 * @since 4.0
 */
public class XTM2TopicMapWriter extends XTMTopicMapWriter {
  
  /**
   * PUBLIC: Creates a topic map writer bound to the file given in the
   * arguments.  The topic map will be written out in the UTF-8
   * encoding.   
   * @param filename The name of the file to which the topic map is to
   * be written.
   */ 
  public XTM2TopicMapWriter(String filename) throws IOException {
    this(new File(filename), "utf-8");
  }
  
  /**
   * PUBLIC: Creates a topic map writer bound to the file given in the
   * arguments.  The topic map will be written out in the UTF-8
   * encoding.   
   * @param file The file object to which the topic map is to be written.
   */
  public XTM2TopicMapWriter(File file) throws IOException {
    this(file, "utf-8");
  }

  /**
   * PUBLIC: Creates a topic map writer bound to the file given in the
   * arguments.   
   * @param file The file object to which the topic map is to be written.
   * @param encoding The character encoding to write the topic map in.
   */
  public XTM2TopicMapWriter(File file, String encoding) throws IOException {
    super(file, encoding);
    this.xtm_version = XTMSnifferContentHandler.VERSION_XTM20;
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
  public XTM2TopicMapWriter(OutputStream stream, String encoding)
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
   */
  public XTM2TopicMapWriter(Writer writer, String encoding) throws IOException {
    super(writer, encoding);
    this.xtm_version = XTMSnifferContentHandler.VERSION_XTM20;
  }
}
