
package net.ontopia.topicmaps.query.core;

import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * PUBLIC: Interface for query language implementations. An instance of a
 * {@link QueryProcessorFactoryIF} create an appropriate
 * {@link QueryProcessorIF} for the provided {@link TopicMapIF}.
 * 
 * @since 5.1
 */
public interface QueryProcessorFactoryIF {
  /**
   * PUBLIC: Returns the query language that is used by this
   * {@link QueryProcessorFactoryIF} implementation.
   * 
   * @return the name of this {@link QueryProcessorFactoryIF} implementation.
   */
  public String getQueryLanguage();

  /**
   * PUBLIC: Creates a new {@link QueryProcessorIF} instance to query a given
   * topic map.
   * 
   * @param topicmap the topic map to be used by the query processor.
   * @param base base address of the topic map if known.
   * @param properties additional properties used to configure the query
   *          processor.
   * @return a {@link QueryProcessorIF} instance that can be used to query the
   *         topic map.
   */
  public QueryProcessorIF createQueryProcessor(TopicMapIF topicmap,
      LocatorIF base, Map<String, String> properties);
}
