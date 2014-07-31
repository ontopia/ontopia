/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.query.utils;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorFactoryIF;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.impl.basic.PredicateFactory;
import net.ontopia.topicmaps.query.impl.utils.TologQueryProcessorFactory;
import net.ontopia.topicmaps.query.parser.GlobalParseContext;
import net.ontopia.topicmaps.query.parser.LocalParseContext;
import net.ontopia.topicmaps.query.parser.ParseContextIF;
import net.ontopia.topicmaps.query.parser.TologOptions;
import net.ontopia.topicmaps.query.parser.TologParser;
import net.ontopia.utils.ServiceUtils;
import org.apache.commons.collections4.map.AbstractReferenceMap;
import org.apache.commons.collections4.map.ReferenceMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PUBLIC: Utility methods for getting QueryProcessorIFs for a topic
 * map.
 *
 * @since 1.4
 */
@SuppressWarnings("unchecked")
public class QueryUtils {
  static Logger log = LoggerFactory.getLogger(QueryUtils.class.getName());

  // QueryProcessorIF cache structure {TopicMapIF : {LocatorIF : {String : SoftReference(QueryProcessorIF)}}}
  private static Map<TopicMapIF, Map<LocatorIF, Map<String, Reference<QueryProcessorIF>>>> qpcache = 
    new ReferenceMap<TopicMapIF, Map<LocatorIF, Map<String, Reference<QueryProcessorIF>>>>
        (AbstractReferenceMap.ReferenceStrength.SOFT, AbstractReferenceMap.ReferenceStrength.HARD);

  private static final String DEFAULT_LANGUAGE = TologQueryProcessorFactory.NAME;
  
  private static Map<String, QueryProcessorFactoryIF> qpFactoryMap;
  
  static {
    loadQueryProcessorFactories();
  }

  /**
   * INTERNAL: Reads all the service descriptors and stores the available
   * QueryProcessorFactoryIF implementations into a map for quick access.
   */
  private static void loadQueryProcessorFactories() {
    qpFactoryMap = new HashMap<String, QueryProcessorFactoryIF>();

    try {
      for (QueryProcessorFactoryIF  factory : ServiceUtils.loadServices(QueryProcessorFactoryIF.class)) {
        qpFactoryMap.put(factory.getQueryLanguage().toUpperCase(), factory);
      }
      
    } catch (IOException e) {
      log.error("Could not read from QueryProcessorFactoryIF service descriptor.", e);
    }

    // if TOLOG has not been found so far, include it now
    if (!qpFactoryMap.containsKey(DEFAULT_LANGUAGE)) {
      qpFactoryMap.put(DEFAULT_LANGUAGE, new TologQueryProcessorFactory());
    }
  }
  
  /**
   * PUBLIC: Returns all available query language implementations.
   * 
   * @return a {@link Collection} of all available query languages.
   * @since 5.1
   */
  public static Collection<String> getAvailableQueryLanguages() {
    return qpFactoryMap.keySet();
  }

  /**
   * PUBLIC: Returns the {@link QueryProcessorFactoryIF} instance associated
   * with a specific query language. If the language is not available, null will
   * be returned. 
   * 
   * @param language the query language to be used (case insensitive).
   * @return the {@link QueryProcessorFactoryIF} instance for this language, or
   *         null, if not available.
   * @since 5.1
   */
  public static QueryProcessorFactoryIF getQueryProcessorFactory(String language) {
    return qpFactoryMap.get(language.toUpperCase());
  }
  
  /**
   * PUBLIC: Returns a query processor for the given topic map; will
   * always return the same processor with the default query language 
   * for the same topic map. The base address of the topic map store 
   * will be the base address of the query processor.
   */
  public static QueryProcessorIF getQueryProcessor(TopicMapIF topicmap) {
    return getQueryProcessor(topicmap, (LocatorIF) null);
  }
  
  public static QueryProcessorIF getQueryProcessor(String queryLanguage, TopicMapIF topicmap) {
    return getQueryProcessor(queryLanguage, topicmap, null);
  }
  
  public static QueryProcessorIF getQueryProcessor(TopicMapIF topicmap, LocatorIF base) {
    return getQueryProcessor(DEFAULT_LANGUAGE, topicmap, base);
  }

  /**
   * PUBLIC: Returns the default query processor for the given topic
   * map and base address. Will always return the same processor for
   * the same (query language, topic map, base address) combination.
   *
   * @since 2.0
   */
  public static QueryProcessorIF getQueryProcessor(String queryLanguage, TopicMapIF topicmap,
      LocatorIF base) {
    // Get {LocatorIF : QueryProcessorIF} entry
    Map<LocatorIF, Map<String, Reference<QueryProcessorIF>>> qps = qpcache.get(topicmap);
    if (qps == null) {
      qps = new HashMap<LocatorIF, Map<String, Reference<QueryProcessorIF>>>();
      qpcache.put(topicmap, qps);
    }

    Map<String, Reference<QueryProcessorIF>> refmap = qps.get(base);
    if (refmap == null) {
      refmap = new HashMap<String, Reference<QueryProcessorIF>>();
      qps.put(base, refmap);
    }

    // Get QueryProcessorIF
    Reference<QueryProcessorIF> ref = refmap.get(queryLanguage);
    if (ref != null) {
      if (ref.get() != null) {
        return ref.get();
      }
    }
    
    QueryProcessorIF qp = createQueryProcessor(queryLanguage, topicmap, base);
    refmap.put(queryLanguage, new SoftReference<QueryProcessorIF>(qp));
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
  public static QueryProcessorIF createQueryProcessor(TopicMapIF topicmap,
      LocatorIF base) {
    return createQueryProcessor(DEFAULT_LANGUAGE, topicmap, base, null);
  }

  public static QueryProcessorIF createQueryProcessor(String queryLanguage, TopicMapIF topicmap,
      LocatorIF base) {
    return createQueryProcessor(queryLanguage, topicmap, base, null);
  }

  /**
   * EXPERIMENTAL: ...
   */
  public static QueryProcessorIF createQueryProcessor(TopicMapIF topicmap,
      Map properties) {
    return createQueryProcessor(DEFAULT_LANGUAGE, topicmap, (LocatorIF) null, properties);
  }
  
  public static QueryProcessorIF createQueryProcessor(TopicMapIF topicmap,
      LocatorIF base, Map properties) {
    return createQueryProcessor(DEFAULT_LANGUAGE, topicmap, base, properties);
  }
  
  public static QueryProcessorIF createQueryProcessor(String queryLanguage, TopicMapIF topicmap,
      Map properties) {
    return createQueryProcessor(queryLanguage, topicmap, (LocatorIF) null, properties);
  }

  /**
   * EXPERIMENTAL: ...
   */
  public static QueryProcessorIF createQueryProcessor(String queryLanguage, TopicMapIF topicmap,
      LocatorIF base, Map properties) {
    QueryProcessorFactoryIF factory = getQueryProcessorFactory(queryLanguage);
    return (factory == null) ? null
      : factory.createQueryProcessor(topicmap, base, properties);
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
      String declarations) throws InvalidQueryException {
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
      String declarations, DeclarationContextIF context)
    throws InvalidQueryException {
    // find the base
    LocatorIF base = topicmap.getStore().getBaseAddress();

    ParseContextIF pctxt = (ParseContextIF) context;
    if (pctxt == null) {
      // create the innermost context
      pctxt = new GlobalParseContext(new PredicateFactory(topicmap, base),
          topicmap, base);
    }

    // create a nested context
    pctxt = new LocalParseContext(pctxt);

    // parse the declarations into this
    TologParser parser = new TologParser(pctxt, TologOptions.defaults);
    return (DeclarationContextIF) parser.parseDeclarations(declarations);
  }
}
