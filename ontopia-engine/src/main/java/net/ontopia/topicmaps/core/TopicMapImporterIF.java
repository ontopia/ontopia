
package net.ontopia.topicmaps.core;

import java.io.IOException;

/**
 * PUBLIC: A topic map importer interface which enables the
 * destination of the importation to be given, but allows the source
 * from which the importer reads its input topic map to be implicit
 * and implementation-dependent.</p>
 *
 * @see <code> net.ontopia.core.topicmaps.TopicMapReaderIF </code>
 * @see <code> net.ontopia.core.topicmaps.TopicMapWriterIF </code>
 */

public interface TopicMapImporterIF {

  /**
   * PUBLIC: Imports an implicitly designated topic map into the given topic map.
   *
   * @param topicmap The topic map into which the import will be done;
   *                 an object implementing TopicMapIF.
   */
  public void importInto(TopicMapIF topicmap) throws IOException;
  
}





