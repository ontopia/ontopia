
package net.ontopia.topicmaps.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * PUBLIC: A topic map writer that can write topic maps out into the
 * interchange syntax defined by XTM 2.0.
 *
 * @since 4.0.0
 */
public final class XTM2TopicMapWriter extends AbstractXTM2TopicMapWriter {

  public XTM2TopicMapWriter(String filename) throws IOException {
    super(filename);
  }
  
  public XTM2TopicMapWriter(File file) throws IOException {
    super(file);
  }

  public XTM2TopicMapWriter(File file, String encoding) throws IOException {
    super(file, encoding);
  }

  public XTM2TopicMapWriter(OutputStream stream, String encoding)
    throws IOException, UnsupportedEncodingException {
    super(stream, encoding);
  }

  public XTM2TopicMapWriter(Writer writer, String encoding) throws IOException {
    super(writer, encoding);
  }

  protected final XTMVersion getVersion() {
    return XTMVersion.XTM_2_0;
  }

}
 