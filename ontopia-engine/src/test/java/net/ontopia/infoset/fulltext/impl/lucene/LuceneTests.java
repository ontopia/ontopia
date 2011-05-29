
package net.ontopia.infoset.fulltext.impl.lucene;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import junit.framework.TestCase;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.fulltext.core.DocumentIF;
import net.ontopia.infoset.fulltext.core.IndexerIF;
import net.ontopia.infoset.fulltext.core.SearchResultIF;
import net.ontopia.infoset.fulltext.core.SearcherIF;
import net.ontopia.infoset.fulltext.topicmaps.DefaultTopicMapIndexer;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.utils.FileUtils;

import org.apache.lucene.analysis.StopAnalyzer;

public class LuceneTests extends TestCase {
  
  protected TopicMapIF topicmap;       // topic map of object being tested
  protected TopicMapBuilderIF builder; // builder used for creating new objects
  
  protected String indexDir;
  protected SearcherIF searcher;
  
  public LuceneTests(String name) {
    super(name);
  }
  
  public void setUp() throws IOException {
    TopicMapStoreIF store = new InMemoryTopicMapStore();
    topicmap = store.getTopicMap();
    builder = topicmap.getBuilder();

    String root = FileUtils.getTestdataOutputDirectory();
    indexDir = root + File.separator + "indexes" + File.separator;
    FileUtils.verifyDirectory(root, "indexes");
  }

  protected void tearDown() throws IOException {
    // Close searcher
    if (searcher != null) {
      searcher.close();
      searcher = null;
    }
  }
  
  // ---- test cases

  public void testEmpty() throws IOException {
    index(topicmap);
    assertTrue("hits found in empty topic map",
	   findNothing(topicmap, "stuff"));
  }

  public void testTopicName() throws IOException {
    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "bar");
    index(topicmap);

    assertTrue("found non-existent base names in topic map",
	   findNothing(topicmap, "foo"));

    SearchResultIF result = searcher.search("bar");
    findSingle(result, bn, " bar");
    
    result = searcher.search("bar AND class:B");
    findSingle(result, bn, " in class:B");
    
    assertTrue("found base names when searching for variant name",
	   findNothing(topicmap, "bar AND class:V"));

    remove(bn);
    
    assertTrue("found removed base name",
	   findNothing(topicmap, "bar"));
  }

   public void testTopicNameUnicode() throws IOException {
    TopicIF topic = builder.makeTopic();
    
    String katakana = "\u30AB\u30BF\u30AB\u30CA";
    TopicNameIF bn1 = builder.makeTopicName(topic, katakana);

    String norsk = "\u00E6\u00F8\u00E5\u00C6\u00D8\u00C5";
    TopicNameIF bn2 = builder.makeTopicName(topic, norsk);
    
    index(topicmap);

    assertTrue("found non-existent base names in topic map",
	   findNothing(topicmap, "foo"));

    SearchResultIF result = searcher.search(katakana);
    findSingle(result, bn1, " using Unicode characters");
    
    result = searcher.search(katakana + " AND class:B");
    findSingle(result, bn1, " in class:B using Unicode characters (katakana)");

    result = searcher.search(norsk + " AND class:B");
    findSingle(result, bn2, " in class:B using Unicode characters (norsk)");
    
    assertTrue("found base names when searching for variant name",
	   findNothing(topicmap, "bar AND class:V"));

    remove(bn1);
    remove(bn2);
    
    assertTrue("found removed base name",
	   findNothing(topicmap, "bar"));
  }

  public void testVariantName() throws IOException {
    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "bar");
    VariantNameIF vn = builder.makeVariantName(bn, "baz");
    index(topicmap);

    assertTrue("found non-existent objects in topic map",
	   findNothing(topicmap, "foo"));

    SearchResultIF result = searcher.search("baz");
    findSingle(result, vn, "");
    
    result = searcher.search("baz AND class:N");
    findSingle(result, vn, " in class:N");
    
    assertTrue("found variant names when searching for base name",
	   findNothing(topicmap, "baz AND class:B"));

    remove(vn);
    
    assertTrue("found removed variant name",
	   findNothing(topicmap, "baz"));
  }

  public void testVariantLocator() throws IOException {
    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "bar");
    VariantNameIF vn = builder.makeVariantName(bn, makeLocator("http://www.ontopia.net"));
    index(topicmap);

    assertTrue("found non-existent objects in topic map",
	   findNothing(topicmap, "foo"));

    SearchResultIF result = searcher.search("address:ontopia");
    //SearchResultIF result = searcher.search("address:http://www.ontopia.net");
    findSingle(result, vn, " with locator");
    
    result = searcher.search("address:ontopia AND class:N");
    findSingle(result, vn, " in class:N");
    
    assertTrue("found variant names when searching for base name",
	   findNothing(topicmap, "address:ontopia AND class:B"));

    remove(vn);
    
    assertTrue("found removed variant name",
	   findNothing(topicmap, "address:ontopia"));
  }

  public void testOccurrence() throws IOException {
    TopicIF topic = builder.makeTopic();
    TopicIF otype = builder.makeTopic();
    OccurrenceIF occ = builder.makeOccurrence(topic, otype, "value");
    index(topicmap);

    assertTrue("found non-existent objects in topic map",
	   findNothing(topicmap, "foo"));

    SearchResultIF result = searcher.search("value");
    findSingle(result, occ, " with inline value");
    
    result = searcher.search("value AND class:O");
    findSingle(result, occ, " in class:O");
    
    assertTrue("found occurrences when searching for variant names",
	   findNothing(topicmap, "value AND class:V"));

    remove(occ);
    
    assertTrue("found removed occurrence",
	   findNothing(topicmap, "value"));
  }

  public void testOccurrenceLocator() throws IOException {
    TopicIF topic = builder.makeTopic();
    TopicIF otype = builder.makeTopic();
    OccurrenceIF occ = builder.makeOccurrence(topic, otype, makeLocator("http://www.ontopia.net"));
    index(topicmap);

    assertTrue("found non-existent objects in topic map",
	   findNothing(topicmap, "foo"));

    SearchResultIF result = searcher.search("address:ontopia");
    findSingle(result, occ, " with locator");
    
    result = searcher.search("address:ontopia AND class:O");
    findSingle(result, occ, " in class:O");
    
    assertTrue("found occurrences when searching for variant names",
	   findNothing(topicmap, "address:ontopia AND class:V"));

    remove(occ);
    
    assertTrue("found removed occurrence",
	   findNothing(topicmap, "address:ontopia"));
  }
  
  public void testDeleteIndex() throws IOException {
    // Create a topic and an occurrence
    TopicIF topic = builder.makeTopic();
    TopicIF otype = builder.makeTopic();
    OccurrenceIF occ = builder.makeOccurrence(topic, otype, makeLocator("http://www.ontopia.net"));
    
    // Index topic map
    index(topicmap);

    // Close searcher
    if (searcher != null) {
      searcher.close();
      searcher = null;
    }
    
    // Open index
    IndexerIF indexer = new LuceneIndexer(indexDir, false);
    
    // Delete index
    indexer.delete();
    
    // Verify that index was deleted
    File idir = new File(indexDir);
    assertTrue("Index directory exists after LuceneIndexer.delete() was called.", !idir.exists());
  }
  
  // ---- utilities

  protected void index(TopicMapIF topicmap) throws IOException {
    // Close searcher
    if (searcher != null) {
      searcher.close();
      searcher = null;
    }
    
    //!IndexerIF indexer = new LuceneIndexer(indexDir, true);
    IndexerIF indexer = new LuceneIndexer(indexDir, new StopAnalyzer(), true);
    DefaultTopicMapIndexer ix = new DefaultTopicMapIndexer(indexer, false, "preloader");    
    ix.index(topicmap);
    ix.close();
    indexer.close();

    // Assign new searcher instance
    //!searcher = new LuceneSearcher(indexDir);
    searcher = new LuceneSearcher(indexDir, new StopAnalyzer());    
  }

  protected void remove(TMObjectIF object) throws IOException {
    // Close searcher
    if (searcher != null) {
      searcher.close();
      searcher = null;
    }

    //!IndexerIF indexer = new LuceneIndexer(indexDir, false);
    IndexerIF indexer = new LuceneIndexer(indexDir, new StopAnalyzer(), false);
    DefaultTopicMapIndexer ix = new DefaultTopicMapIndexer(indexer, false, "preloader");    
    ix.delete(object);
    ix.close();
    indexer.close();
    
    // Assign new searcher instance
    //!searcher = new LuceneSearcher(indexDir);    
    searcher = new LuceneSearcher(indexDir, new StopAnalyzer());    
  }

  protected void findSingle(SearchResultIF result, TMObjectIF object, String m)
    throws IOException {
    assertTrue("wrong number of hits: " + result.hits() + m, result.hits() == 1);

    DocumentIF doc = result.getDocument(0);
    assertTrue("didn't find object" + m,
	   doc.getField("object_id").getValue().equals(object.getObjectId()));
  }
  
  protected boolean findNothing(TopicMapIF topicmap, String query) throws IOException {
    SearchResultIF result = searcher.search(query);
    return result.hits() == 0;
  }

  protected LocatorIF makeLocator(String uri) {
    try {
      return new URILocator(uri);
    }
    catch (MalformedURLException e) {
      fail(e.toString());
      return null;
    }
  }
}
