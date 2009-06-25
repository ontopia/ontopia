
// $Id: QueryUtils.java,v 1.32 2006/04/07 07:02:05 grove Exp $

package net.ontopia.topicmaps.query.utils;

import java.util.HashMap;
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.impl.basic.PredicateFactory;
import net.ontopia.topicmaps.query.parser.GlobalParseContext;
import net.ontopia.topicmaps.query.parser.LocalParseContext;
import net.ontopia.topicmaps.query.parser.ParseContextIF;
import net.ontopia.topicmaps.query.parser.TologParser;
import net.ontopia.utils.OntopiaRuntimeException;

import org.apache.commons.collections.ReferenceMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PUBLIC: Utility methods for getting QueryProcessorIFs for a topic
 * map.
 *
 * @since 1.4
 */
public class QueryUtils {
  static Logger log = LoggerFactory.getLogger(QueryUtils.class.getName());

  // QueryProcessorIF cache structure {TopicMapIF : {LocatorIF : SoftReference(QueryProcessorIF)}}
  private static Map qpcache = new ReferenceMap(ReferenceMap.SOFT, ReferenceMap.HARD);

  private static String PROP_IMPLEMENTATION =
    "net.ontopia.topicmaps.query.core.QueryProcessorIF";
  
  /**
   * PUBLIC: Returns a query processor for the given topic map; will
   * always return the same processor for the same topic map. The base
   * address of the topic map store will be the base address of the
   * query processor.
   */
  public static QueryProcessorIF getQueryProcessor(TopicMapIF topicmap) {
    return getQueryProcessor(topicmap, (LocatorIF)null);
  }

  /**
   * PUBLIC: Returns the default query processor for the given topic
   * map and base address. Will always return the same processor for
   * the same (topic map, base address) combination.
   *
   * @since 2.0
   */
  public static QueryProcessorIF getQueryProcessor(TopicMapIF topicmap, LocatorIF base) {
    // Get {LocatorIF : QueryProcessorIF} entry
    Map qps = (Map)qpcache.get(topicmap);
    if (qps == null) {
      qps = new HashMap();
      qpcache.put(topicmap, qps);
    }
    // Get QueryProcessorIF
    java.lang.ref.Reference ref = (java.lang.ref.Reference)qps.get(base);
    if (ref != null) {
      Object qp = ref.get();
      if (qp != null)
        return (QueryProcessorIF)qp;
    }
    
    QueryProcessorIF qp = createQueryProcessor(topicmap, base);
    qps.put(base, new java.lang.ref.SoftReference(qp));
    return qp;
  }

  /**
   * PUBLIC: Factory method for creating a query processor for a given
   * topic map; always returns a new processor. The base address of
   * the topic map store will be the base address of the query
   * processor.
   *
   * @since 2.0
   */
  public static QueryProcessorIF createQueryProcessor(TopicMapIF topicmap) {
    return createQueryProcessor(topicmap, (LocatorIF) null);
  }

  /**
   * PUBLIC: Factory method for creating a new query processor for a
   * given topic map and base address. Always returns a new processor.
   *
   * @since 2.0
   */
  public static QueryProcessorIF createQueryProcessor(TopicMapIF topicmap, LocatorIF base) {
    String propval = null;
    int implementation = topicmap.getStore().getImplementation();
    if (implementation == TopicMapStoreIF.RDBMS_IMPLEMENTATION) {
      propval = ((net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore) topicmap.getStore())
        .getProperty(PROP_IMPLEMENTATION);
      log.debug("Query processor setting: '" + propval + "'");
      if (propval != null && propval.equals("rdbms")) {
         log.debug("Creating RDBMS query processor for: " + topicmap);
        if (base == null)
          return new net.ontopia.topicmaps.query.impl.rdbms.QueryProcessor(topicmap);
        else
          return new net.ontopia.topicmaps.query.impl.rdbms.QueryProcessor(topicmap, base);
      }
    }
    // Otherwise use basic query processor
    if (propval == null || propval.equals("in-memory")) {
      log.debug("Creating basic query processor for: " + topicmap);
      if (base == null)
        return new net.ontopia.topicmaps.query.impl.basic.QueryProcessor(topicmap);
      else
        return new net.ontopia.topicmaps.query.impl.basic.QueryProcessor(topicmap, base);
    } else {
      throw new OntopiaRuntimeException("Property '" + PROP_IMPLEMENTATION + "' contains invalid value: '" + propval + "'");
    }
  }

  /**
   * EXPERIMENTAL: ...
   */
  public static QueryProcessorIF createQueryProcessor(TopicMapIF topicmap, Map properties) {
    return createQueryProcessor(topicmap, (LocatorIF) null, properties);
  }

  /**
   * EXPERIMENTAL: ...
   */
  public static QueryProcessorIF createQueryProcessor(TopicMapIF topicmap, LocatorIF base, Map properties) {
    String propval = null;
    int implementation = topicmap.getStore().getImplementation();
    if (implementation == TopicMapStoreIF.RDBMS_IMPLEMENTATION) {
      propval = (String)properties.get(PROP_IMPLEMENTATION);
      if (propval == null)
        propval = ((net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore)topicmap.getStore())
          .getProperty(PROP_IMPLEMENTATION);
      log.debug("Query processor setting: '" + propval + "'");
      if (propval != null && propval.equals("rdbms")) {
        if (base == null)
          return new net.ontopia.topicmaps.query.impl.rdbms.QueryProcessor(topicmap);
        else
          return new net.ontopia.topicmaps.query.impl.rdbms.QueryProcessor(topicmap, base);
      }
    }
    // Otherwise use basic query processor
    if (propval == null || propval.equals("in-memory")) {
      if (base == null)
        return new net.ontopia.topicmaps.query.impl.basic.QueryProcessor(topicmap);
      else
        return new net.ontopia.topicmaps.query.impl.basic.QueryProcessor(topicmap, base);
    } else {
      throw new OntopiaRuntimeException("Property '" + PROP_IMPLEMENTATION + "' contains invalid value: '" + propval + "'");
    }
  }

  /**
   * PUBLIC: Parses a set of tolog declarations and returns an
   * object representing the resulting declaration context. The
   * context cannot be introspected, but it can be given to a query
   * processor to execute queries in that context.
   *
   * @since 2.1
   */
  public static DeclarationContextIF parseDeclarations(TopicMapIF topicmap,
                                                       String declarations)
    throws InvalidQueryException {

    return parseDeclarations(topicmap, declarations, null);
  }

  /**
   * PUBLIC: Parses a set of tolog declarations in an existing
   * context, and returns an object representing the resulting nested
   * declaration context. The context cannot be introspected, but it
   * can be given to a query processor to execute queries in that
   * context.
   *
   * @since 2.1
   */
  public static DeclarationContextIF parseDeclarations(TopicMapIF topicmap,
                                                       String declarations,
                                                       DeclarationContextIF context)
    throws InvalidQueryException {

    // find the base
    LocatorIF base = topicmap.getStore().getBaseAddress();

    ParseContextIF pctxt = (ParseContextIF) context;
    if (pctxt == null)
      // create the innermost context
      pctxt = new GlobalParseContext(new PredicateFactory(topicmap, base), topicmap, base);

    // create a nested context
    pctxt = new LocalParseContext(pctxt);

    // parse the declarations into this
    TologParser parser = new TologParser(pctxt);
    return (DeclarationContextIF) parser.parseDeclarations(declarations);
  }
}
