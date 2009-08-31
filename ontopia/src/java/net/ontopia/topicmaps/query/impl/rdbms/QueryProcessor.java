
// $Id: QueryProcessor.java,v 1.42 2008/07/22 06:50:22 geir.gronmo Exp $

package net.ontopia.topicmaps.query.impl.rdbms;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.persistence.proxy.ObjectRelationalMappingIF;
import net.ontopia.persistence.proxy.RDBMSAccess;
import net.ontopia.persistence.proxy.RDBMSMapping;
import net.ontopia.persistence.proxy.TransactionIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.ParsedQueryIF;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.impl.utils.QueryOptimizer;
import net.ontopia.topicmaps.query.parser.GlobalParseContext;
import net.ontopia.topicmaps.query.parser.LocalParseContext;
import net.ontopia.topicmaps.query.parser.ParseContextIF;
import net.ontopia.topicmaps.query.parser.PredicateFactoryIF;
import net.ontopia.topicmaps.query.parser.TologParser;
import net.ontopia.topicmaps.query.parser.TologQuery;

/**
 * INTERNAL: This is the front-end to the RDBMS-specific query
 * processor.
 */
public class QueryProcessor implements QueryProcessorIF {

  protected PredicateFactoryIF predicateFactory;
  protected TopicMapIF topicmap; // the topic map to query
  protected LocatorIF base; // the base address to solve relative references against
  protected TologParser parser;
  
  protected TransactionIF txn;
  protected RDBMSAccess access;
  protected RDBMSMapping mapping;
  
  protected net.ontopia.topicmaps.query.impl.basic.QueryProcessor bprocessor;
  
  public QueryProcessor(TopicMapIF topicmap) {
    this(topicmap, topicmap.getStore().getBaseAddress());
  }
  
  public QueryProcessor(TopicMapIF topicmap, LocatorIF base) {
    this.topicmap = topicmap;
    this.base = base;

    this.bprocessor = new net.ontopia.topicmaps.query.impl.basic.QueryProcessor(topicmap);
    
    ParseContextIF context = new GlobalParseContext(
      new PredicateFactory(topicmap, base),
      topicmap, base);
    context = new LocalParseContext(context);
    parser = new TologParser(context, bprocessor.getOptions());

    RDBMSTopicMapStore store = (RDBMSTopicMapStore)topicmap.getStore();
    this.txn = store.getTransactionIF();
    this.access = (RDBMSAccess) txn.getStorageAccess();
    this.mapping = access.getStorage().getMapping();
  }

  public String getProperty(String name) {
    return access.getStorage().getProperty(name);
  }

  public QueryResultIF execute(String query) throws InvalidQueryException {
    return parseQuery(query, null).execute();
  }

  public QueryResultIF execute(String query, DeclarationContextIF context)
    throws InvalidQueryException {
    return parseQuery(query, context).execute();
  }  

  public QueryResultIF execute(String query, Map arguments)
    throws InvalidQueryException {
    return parseQuery(query, null).execute(arguments);
  }

  public QueryResultIF execute(String query, Map arguments,
                               DeclarationContextIF context)
    throws InvalidQueryException {
    return parseQuery(query, context).execute(arguments);
  }  
  
  public ParsedQueryIF parse(String query) throws InvalidQueryException {
    return parseQuery(query, null);
  }

  public ParsedQueryIF parse(String query, DeclarationContextIF context)
    throws InvalidQueryException {
    return parseQuery(query, context);
  }

  // internal method used to parse a query
  protected ParsedQuery parseQuery(String query, DeclarationContextIF context)
    throws InvalidQueryException {

    TologQuery tquery;
    if (context == null)
      // there is no context, so we just use the default parser
      tquery = parser.parse(query);
    else {
      // there is a context, so we have to use a new parser for this
      TologParser localparser = new TologParser((ParseContextIF) context,
                                                bprocessor.getOptions());
      tquery = localparser.parse(query);
    }

    return new ParsedQuery(this, bprocessor,
                           QueryOptimizer.getOptimizer(tquery).optimize(tquery));
  }
  
  public void load(String ruleset) throws InvalidQueryException {
    parser.load(ruleset);
  }

  public void load(Reader ruleset) throws InvalidQueryException, IOException {
    parser.load(ruleset);
  }

  public void setContext(DeclarationContextIF context) {
    parser = new TologParser((LocalParseContext) context,
                             bprocessor.getOptions());
  }

  /// query builder code

  public TopicMapIF getTopicMap() {
    return topicmap;
  }

  public TransactionIF getTransaction() {
    return txn;
  }

  public ObjectRelationalMappingIF getMapping() {
    return mapping;
  }
  
  public net.ontopia.topicmaps.query.impl.basic.QueryProcessor getBasicQueryProcessor() {
    return bprocessor;
  }
}
