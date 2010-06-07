
// $Id: XTM20TopicMapWriter.java 1108 2010-06-06 21:08:51Z lars.heuer $

package net.ontopia.topicmaps.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * PUBLIC: A topic map writer that can write topic maps out into the
 * interchange syntax defined by XTM 2.0.
 *
 * @since 4.0.0
 */
public final class XTM2TopicMapWriter extends AbstractXTM2TopicMapWriter {

  public XTM2TopicMapWriter(String filename) throws IOException {
    super(new File(filename), "utf-8");
  }
  
  public XTM2TopicMapWriter(File file) throws IOException {
    super(file, "utf-8");
  }

  public XTM2TopicMapWriter(File file, String encoding) throws IOException {
    super(file, encoding);
  }

  public XTM2TopicMapWriter(OutputStream stream, String encoding)
    throws IOException, UnsupportedEncodingException {
    super(new OutputStreamWriter(stream, encoding), encoding);
  }

  protected final XTMVersion getVersion() {
    return XTMVersion.XTM_2_0;
  }

}
 