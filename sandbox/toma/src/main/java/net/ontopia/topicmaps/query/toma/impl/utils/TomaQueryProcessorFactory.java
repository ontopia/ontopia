package net.ontopia.topicmaps.query.toma.impl.utils;

import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.QueryProcessorFactoryIF;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicQueryProcessor;

/**
 * PUBLIC: QueryProcessorFactory implementation for the TOMA query language.
 */
public class TomaQueryProcessorFactory implements QueryProcessorFactoryIF {
  private static final String NAME = "TOMA";
  
  public String getQueryLanguage() {
    return NAME;
  }

  public QueryProcessorIF createQueryProcessor(TopicMapIF topicmap,
      LocatorIF base, Map<String, String> properties) {
    return new BasicQueryProcessor(topicmap);
  }
}
