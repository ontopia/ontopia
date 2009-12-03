package net.ontopia.sandbox.solrutils;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.entry.TopicMaps;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.utils.URIUtils;

import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

/**
 * Command line tool for indexing topic maps to a Solr index.
 * 
 * Usage: time java -Xmx256M net.ontopia.sandbox.solrutils.SolrIndexer postgresql-930001 solr-indexer:core0 http://localhost:8080/solr/core0 500
 *
 */
public class SolrIndexer {
  
  private TopicMapIF tm;
  private QueryProcessorIF qp;
  private String indexerId;
  private String solrUrl;
  private int batchSize = 1000;
  
  public SolrIndexer(String topicmapId, String indexerName, String solrUrl) {
    this.tm = TopicMaps.createStore(topicmapId, true).getTopicMap();
    this.qp = QueryUtils.getQueryProcessor(tm);
    this.indexerId = indexerName;
    this.solrUrl = solrUrl;
  }
  
  public static void main(String[] args){
    String topicMapId = args[0];
    String indexerName = args[1];
    String solrUrl = args[2];
    SolrIndexer app = new SolrIndexer(topicMapId, indexerName, solrUrl);
    if (args.length >= 4)
      app.batchSize = Integer.parseInt(args[3]);
    app.index();
  }

  public void index() {
    System.out.println("Using Solr index: " + solrUrl);
    
    clearIndex();
    
    for (IndexerRule ir : getIndexerRules(indexerId)) {

      Map<String, SolrInputDocument> documents = new HashMap<String, SolrInputDocument>(batchSize);

      QueryResultIF qr;
      try {
        int doccount = 0;
        
        System.out.println("Executing indexer rule query with parameters: this:" + ir.indexerRule + ",\n" + ir.indexerQuery);
        qr = qp.execute(ir.indexerQuery, Collections.singletonMap("this", ir.indexerRule));
        while (qr.next()) {
          String id = (String)qr.getValue(0);
          String field = (String)qr.getValue(1);
          String value = (String)qr.getValue(2);
          
          if (!documents.containsKey(id)) {
            if (documents.size() > 0 && (documents.size() % batchSize == 0)) {              
              // perform indexing
              System.out.println("Indexing " + documents.size() + " documents (count=" + doccount + ")...");
              indexDocuments(documents.values());
              // reset documents map
              documents = new HashMap<String, SolrInputDocument>(batchSize);
            }
            doccount++;
            SolrInputDocument newdoc = new SolrInputDocument();
            newdoc.addField("id", id);
            documents.put(id, newdoc);
          }
          SolrInputDocument doc = documents.get(id);
          doc.addField(field, value);            
        }
        if  (!documents.isEmpty()) {
          // index remaining documents
            System.out.println("Indexing " + documents.size() + " documents...");
          indexDocuments(documents.values());       
        }
        System.out.println(TopicStringifiers.toString(ir.indexerRule) + " indexed " + doccount +  " documents.");
      } catch (InvalidQueryException e) {
        throw new RuntimeException(e);
      }
    }
  }
  
  public Collection<IndexerRule> getIndexerRules(String indexerId) {
    // get indexer rules
    TopicIF indexer = tm.getTopicBySubjectIdentifier(URIUtils.getURI(indexerId));
    
    Set<IndexerRule> result = new HashSet<IndexerRule>();
    QueryResultIF qr;
    try {
      String query = "using on for i\"http://psi.ontopia.net/ontology/\" " +
        "select $IR, $IRQ from " +  
        "on:indexer-rules(%indexer% : on:indexer, $IR : on:indexer-rule), " +
        "on:indexer-query($IR, $IRQ)?";
      System.out.println("Retrieving indexer rules for indexer " + indexerId + ": \n" + query);
      qr = qp.execute(query, Collections.singletonMap("indexer", indexer));
      while (qr.next()) {
        result.add(new IndexerRule((TopicIF)qr.getValue(0), (String)qr.getValue(1)));        
      }
      return result;    
    } catch (InvalidQueryException e) {
      throw new RuntimeException(e);
    }   
  }

  private CommonsHttpSolrServer getSolrServer() throws MalformedURLException  {
    CommonsHttpSolrServer server = new CommonsHttpSolrServer(solrUrl);
    server.setSoTimeout(10000);  // socket read timeout
    server.setConnectionTimeout(100);
    server.setDefaultMaxConnectionsPerHost(100);
    server.setMaxTotalConnections(100);
    server.setFollowRedirects(false);  // defaults to false
    server.setMaxRetries(1); // defaults to 0.  > 1 not recommended.
    return server;
  }
  
  private void clearIndex() {
    System.out.println("Clearing index...");
    try {
      CommonsHttpSolrServer server = getSolrServer();
      server.deleteByQuery("*:*");
      server.commit();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }        
  }
  
  private void indexDocuments(Collection<SolrInputDocument> documents) {
    try {
      CommonsHttpSolrServer server = getSolrServer();
      server.add(documents);
      server.commit();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }    
  }
 
  private static class IndexerRule {
    TopicIF indexerRule;
    String indexerQuery;
    IndexerRule(TopicIF indexerRule, String indexerQuery) {
      this.indexerRule = indexerRule;
      this.indexerQuery = indexerQuery;
    }
  }
  
}
