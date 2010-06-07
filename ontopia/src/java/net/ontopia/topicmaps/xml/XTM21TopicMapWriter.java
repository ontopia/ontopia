
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
 * interchange syntax defined by XTM 2.1.
 *
 * @since 5.1.0
 */
public final class XTM21TopicMapWriter extends AbstractXTM2TopicMapWriter {

  public XTM21TopicMapWriter(String filename) throws IOException {
    super(new File(filename), "utf-8");
  }
  
  public XTM21TopicMapWriter(File file) throws IOException {
    super(file, "utf-8");
  }

  public XTM21TopicMapWriter(File file, String encoding) throws IOException {
    super(file, encoding);
  }

  public XTM21TopicMapWriter(OutputStream stream, String encoding)
    throws IOException, UnsupportedEncodingException {
    super(new OutputStreamWriter(stream, encoding), encoding);
  }

  @Override
  protected final XTMVersion getVersion() {
    return XTMVersion.XTM_2_1;
  }

}
