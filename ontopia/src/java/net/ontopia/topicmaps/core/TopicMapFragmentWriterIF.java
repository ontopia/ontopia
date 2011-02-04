
package net.ontopia.topicmaps.core;

import java.util.Iterator;
import java.io.IOException;

/**
 * PUBLIC: Implementations of this interface can export fragments of
 * topic maps to some Topic Maps syntax given a collection of topics
 * to include. Generally, the fragments will include all identifiers,
 * types, names, occurrences, and associations of the topics.
 *
 * @since %NEXT%
 */
public interface TopicMapFragmentWriterIF {

  /**
   * PUBLIC: Starts the fragment.
   */
  public void startTopicMap() throws IOException;

  /**
   * PUBLIC: Exports all the topics returned by the iterator, and
   * wraps them with startTopicMap() and endTopicMap() calls.
   */
  public void exportAll(Iterator<TopicIF> it) throws IOException;

  /**
   * PUBLIC: Exports all the topics returned by the iterator.
   */
  public void exportTopics(Iterator<TopicIF> it) throws IOException;

  /**
   * PUBLIC: Exports the given topic.
   */
  public void exportTopic(TopicIF topic) throws IOException;
  
  /**
   * PUBLIC: Ends the fragment.
   */
  public void endTopicMap() throws IOException;
  
}