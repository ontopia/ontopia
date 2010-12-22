
package net.ontopia.topicmaps.xml;

import java.io.Writer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import org.xml.sax.SAXException;
import org.xml.sax.DocumentHandler;

import net.ontopia.xml.PrettyPrinter;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapFragmentWriterIF;

/**
 * PUBLIC: A fragment writer for XTM. // FIXME: what versions?
 */
public class XTMTopicMapFragmentWriter implements TopicMapFragmentWriterIF {
  private Writer out;
  private DocumentHandler dh;
  private XTMFragmentExporter exporter;
  
  public XTMTopicMapFragmentWriter(OutputStream out, String encoding)
    throws IOException {
    this.out = new OutputStreamWriter(out, encoding);
    this.dh = new PrettyPrinter(this.out, encoding);
    this.exporter = new XTMFragmentExporter();
  }

  /**
   * PUBLIC: Starts the fragment.
   */
  public void startTopicMap() throws IOException {
    try {
      exporter.startTopicMap(dh);
    } catch (SAXException e) {
      // FIXME: all options appear to suck here.
      throw new IOException(e.getMessage()); 
    }
  }

  /**
   * PUBLIC: Exports all the topics returned by the iterator, and
   * wraps them with startTopicMap() and endTopicMap() calls.
   */
  public void exportAll(Iterator<TopicIF> it) throws IOException {
    try {
      exporter.exportAll(it, dh);
    } catch (SAXException e) {
      // FIXME: all options appear to suck here.
      throw new IOException(e.getMessage()); 
    }
  }

  /**
   * PUBLIC: Exports all the topics returned by the iterator.
   */
  public void exportTopics(Iterator<TopicIF> it) throws IOException {
    try {
      exporter.exportTopics(it, dh);
    } catch (SAXException e) {
      // FIXME: all options appear to suck here.
      throw new IOException(e.getMessage()); 
    }
  }

  /**
   * PUBLIC: Ends the fragment.
   */
  public void endTopicMap() throws IOException {
    try {
      exporter.endTopicMap(dh);
    } catch (SAXException e) {
      // FIXME: all options appear to suck here.
      throw new IOException(e.getMessage()); 
    }
  }
  
}