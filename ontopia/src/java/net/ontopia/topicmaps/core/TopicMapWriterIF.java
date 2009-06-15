
// $Id: TopicMapWriterIF.java,v 1.9 2007/09/06 12:42:39 geir.gronmo Exp $

package net.ontopia.topicmaps.core;

import java.io.IOException;

/**
 * PUBLIC: A topic map writer is used to write/export topic maps in an
 * implementation dependent way to an implicit destination.</p>
 *
 * @see <code>net.ontopia.core.topicmaps.TopicMapReaderIF</code>
 * @see <code>net.ontopia.core.topicmaps.TopicMapImporterIF</code>
 */

public interface TopicMapWriterIF {

  /**
   * PUBLIC: Writes the given topic map to an implicit implementation
   * dependent destination. The write method will close any resources
   * opened internally. This means that the write method can
   * only be called once if the stream/writer was opened internally.
   *
   * @exception IOException Thrown if writing the topic map fails.
   *
   * @param source_topicmap The topic map to be exported/written;
   *                         an object implementing TopicMapIF
   */
  public void write(TopicMapIF source_topicmap) throws IOException;
  
}
