
package net.ontopia.topicmaps.utils.sdshare;

import java.io.IOException;
import java.io.OutputStream;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
import net.ontopia.topicmaps.core.TopicMapFragmentWriterIF;

/**
 * Represents a syntax into which a fragment or snapshot might be
 * serialized. 
 */
public interface SyntaxIF {

  // The actual implementations are in StartUpServlet.

  /**
   * Returns the ID as given in the property file.
   */
  public String getId();

  /**
   * Returns the MIME type used by SDshare.
   */
  public String getMIMEType();

  /**
   * Returns the topic map writer for this syntax.
   */
  public TopicMapWriterIF getWriter(OutputStream out, String encoding)
    throws IOException;
  
  /**
   * Returns the fragment writer for this syntax.
   */
  public TopicMapFragmentWriterIF getFragmentWriter(OutputStream out,
                                                    String encoding)
    throws IOException;
  
}