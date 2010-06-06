
// $Id$

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
 * @since 5.1.0
 */
public final class XTM20TopicMapWriter extends AbstractXTM2TopicMapWriter {

  public XTM20TopicMapWriter(String filename) throws IOException {
    super(new File(filename), "utf-8");
  }
  
  public XTM20TopicMapWriter(File file) throws IOException {
    super(file, "utf-8");
  }

  public XTM20TopicMapWriter(File file, String encoding) throws IOException {
    super(file, encoding);
  }

  public XTM20TopicMapWriter(OutputStream stream, String encoding)
    throws IOException, UnsupportedEncodingException {
    super(new OutputStreamWriter(stream, encoding), encoding);
  }

  protected final XTMTopicMapWriter.Version getVersion() {
    return XTMTopicMapWriter.Version.XTM_2_0;
  }

}
 