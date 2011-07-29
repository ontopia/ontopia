
package net.ontopia.topicmaps.query.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.net.MalformedURLException;
import junit.framework.TestCase;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.URIUtils;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapImporterIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.query.impl.basic.QueryProcessor;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapReader;
import net.ontopia.topicmaps.xml.TMXMLReader;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.URIUtils;
import org.xml.sax.InputSource;

public abstract class AbstractQueryTest extends TestCase {

  private final static String testdataDirectory = "query";

  protected static final String OPT_TYPECHECK_OFF =
    "/* #OPTION: compiler.typecheck = false */ ";
  
  public LocatorIF        base;
  public TopicMapIF       topicmap;
  public TopicMapBuilderIF builder;
  public QueryProcessorIF processor;
  
  public AbstractQueryTest(String name) {
    super(name);
  }
  
  // ===== Helper methods (topic maps)

  protected TopicIF getTopicBySI(String uri) throws MalformedURLException {
    return topicmap.getTopicBySubjectIdentifier(new URILocator(uri));
  }
  
  protected TopicIF getTopicById(String id) {
    return (TopicIF) topicmap.getObjectByItemIdentifier(base.resolveAbsolute("#"+id));
  }

  protected TMObjectIF getObjectById(String id) {
    return topicmap.getObjectByItemIdentifier(base.resolveAbsolute("#"+id));
  }

  protected void closeStore() {
    if (topicmap != null)
      topicmap.getStore().close();
    base = null;
    topicmap = null;
    builder = null;
    processor = null;
  }
  
  protected void load(String filename) throws IOException {
    // IMPORTANT: This method is being overloaded by the RDBMS
    // implementation to provide the right object implementations.
    filename = TestFileUtils.getTestInputFile(testdataDirectory, filename);

    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    topicmap = store.getTopicMap();
    builder = store.getTopicMap().getBuilder();
    base = URIUtils.getURI(filename);

    TopicMapImporterIF importer = ImportExportUtils.getImporter(filename);
    if (importer instanceof XTMTopicMapReader)
      ((XTMTopicMapReader) importer).setValidation(false);
    importer.importInto(topicmap);

    processor = new QueryProcessor(topicmap, base);
  }
  
  protected void makeEmpty() {
    makeEmpty(true);
  }

  protected void makeEmpty(boolean setbase) {
    // IMPORTANT: This method is being overloaded by the RDBMS
    // implementation to provide the right object implementations.
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();

    if (setbase) {
      try {
        base = new URILocator("http://example.com");
        store.setBaseAddress(base);
      } catch (MalformedURLException e) {
        throw new OntopiaRuntimeException(e);
      }
    }
    
    topicmap = store.getTopicMap();    
      
    builder = store.getTopicMap().getBuilder();
    processor = new QueryProcessor(topicmap);
  }

  // ===== Helper methods (query)

  public void addMatch(List matches) {
    Map match = new HashMap();
    matches.add(match);
  }
  
  public void addMatch(List matches, String var1, Object obj1) {
    Map match = new HashMap();
    match.put(var1, obj1);
    matches.add(match);
  }
  
  public void addMatch(List matches, String var1, Object obj1,
                       String var2, Object obj2) {
    Map match = new HashMap();
    match.put(var1, obj1);
    match.put(var2, obj2);
    matches.add(match);
  }

  public void addMatch(List matches, String var1, Object obj1,
                       String var2, Object obj2,
                       String var3, Object obj3) {
    Map match = new HashMap();
    match.put(var1, obj1);
    match.put(var2, obj2);
    match.put(var3, obj3);
    matches.add(match);
  }

  public void addMatch(List matches, String var1, Object obj1,
                       String var2, Object obj2,
                       String var3, Object obj3,
                       String var4, Object obj4) {
    Map match = new HashMap();
    match.put(var1, obj1);
    match.put(var2, obj2);
    match.put(var3, obj3);
    match.put(var4, obj4);
    matches.add(match);
  }

  /**
   * Tests whether the given query returns a single row with no columns,
   * i.e. a query match with no unbound variables.
   * @param query The query to test.
   */
  protected void verifyQuery(String query) throws InvalidQueryException {
    // verify that we do not get any parse or query errors
    QueryResultIF result = processor.execute(query);
    try {
      assertTrue(result.next());
      assertEquals(0, result.getWidth());
      assertFalse(result.next());
    } finally {
      result.close();
    }
  }
  
  protected void verifyQuery(List matches, String query)
    throws InvalidQueryException {
    verifyQuery(matches, query, null, null);
  }
  
  protected void verifyQuery(List matches, String query, Map args)
    throws InvalidQueryException {
    verifyQuery(matches, query, null, args);
  }
  
  protected void verifyQuery(List matches, String query, String ruleset)
    throws InvalidQueryException {
    verifyQuery(matches, query, ruleset, null);
  }
  
  protected void verifyQuery(List matches, String query, String ruleset, Map args)
    throws InvalidQueryException {

    matches = new ArrayList(matches); // avoid modifying caller's list
    
    if (ruleset != null)
      processor.load(ruleset);

    QueryResultIF result = null;
    if (args != null) 
      result = processor.parse(query).execute(args);
    else
      result = processor.execute(query);

    //! System.out.println("____QUERY: " + query);
    //! System.out.println("    MATCHES: " + matches);
    //! int i=0;
    try {
      while (result.next()) {
        //! i++;
        //! System.out.println("    ROW " + i + ": " + Arrays.asList(result.getValues()));
        Map match = getMatch(result);
        assertTrue("match not found in expected results: " + match + " => " + matches,
            matches.contains(match));
        matches.remove(match);
        //! System.out.println("____removing: " + match);
      }
    } finally {
      result.close();
    }
    assertTrue("expected matches not found: " + matches,
               matches.isEmpty());
  }
  
  protected void verifyQuerySubset(List matches, String query)
    throws InvalidQueryException {
    verifyQuerySubset(matches, query, null, null);
  }
  
  protected void verifyQuerySubset(List matches, String query, Map args)
    throws InvalidQueryException {
    verifyQuerySubset(matches, query, null, args);
  }
  
  protected void verifyQuerySubset(List matches, String query, String ruleset)
    throws InvalidQueryException {
    verifyQuerySubset(matches, query, ruleset, null);
  }
  
  protected void verifyQuerySubset(List matches, String query, String ruleset, Map args)
    throws InvalidQueryException {

    matches = new ArrayList(matches); // avoid modifying caller's list
    
    if (ruleset != null)
      processor.load(ruleset);

    QueryResultIF result = null;
    if (args != null) 
      result = processor.parse(query).execute(args);
    else
      result = processor.execute(query);

    //! System.out.println("____QUERY: " + query);
    //! System.out.println("    MATCHES: " + matches);
    //! int i=0;
    try {
      while (result.next()) {
        //! i++;
        //! System.out.println("    ROW " + i + ": " + Arrays.asList(result.getValues()));
        Map match = getMatch(result);
        matches.remove(match);
        //! System.out.println("____removing: " + match);
      }
    } finally {
      result.close();
    }
    assertTrue("expected matches not found: " + matches,
               matches.isEmpty());
  }

  protected void verifyQueryPre(List matches, String decls, String query)
    throws InvalidQueryException {

    // parse the declarations
    DeclarationContextIF context = QueryUtils.parseDeclarations(topicmap, decls);

    // run the query
    QueryResultIF result = processor.execute(query, context);

    //! System.out.println("____QUERY: " + query);
    //! System.out.println("    MATCHES: " + matches);
    //! int i=0;
    try {
      while (result.next()) {
        //! i++;
        //! System.out.println("    ROW " + i + ": " + Arrays.asList(result.getValues()));
        Map match = getMatch(result);
        assertTrue("match not found in expected results: " + match + " => " + matches,
            matches.contains(match));
        matches.remove(match);
        //! System.out.println("____removing: " + match);
      }
    } finally {
      result.close();
    }

    assertTrue("expected matches not found: " + matches,
               matches.isEmpty());
  }
  
  protected void verifyQueryOrder(List matches, String query)
    throws InvalidQueryException {

    int pos = 0;
    QueryResultIF result = processor.execute(query);
    try {
      while (result.next()) {
        if (matches.size() <= pos)
          fail("too many rows in query result");
        
        Map match = getMatch(result);
        assertTrue("match not found in position " +  pos + ": " + match + " => " + matches.get(pos),
            matches.get(pos).equals(match));
        pos++;
      }
    } finally {
      result.close();
    }

    assertTrue("bad number of matches returned: " + pos,
           matches.size() == pos);
  }

  protected void findAny(String query) throws InvalidQueryException {
    // verify that we do not get any parse or query errors
    QueryResultIF result = processor.execute(query);
    try {
      while (result.next()) {
        // just loop over rows for the sake of it
      }
    } finally {
      result.close();
    }
  }

  protected void findNothing(String query) throws InvalidQueryException {
    QueryResultIF result = processor.execute(query);
    try {
      assertTrue("found values, but shouldn't have",
                 !result.next());
    } finally {
      result.close();
    }
  }

  protected void findNothing(String query, Map args) throws InvalidQueryException {
    QueryResultIF result = processor.execute(query, args);
    try {
      assertTrue("found values, but shouldn't have",
                 !result.next());
    } finally {
      result.close();
    }
  }

  public DeclarationContextIF parseContext(String decls)
    throws InvalidQueryException {
    return QueryUtils.parseDeclarations(topicmap, decls);
  }

  public int update(String query) throws InvalidQueryException {
    return processor.update(query);
  }

  public int update(String query, DeclarationContextIF context)
    throws InvalidQueryException {
    return processor.update(query, null, context);
  }
  
  public int update(String query, Map params) throws InvalidQueryException {
    return processor.update(query, params);
  }

  public int update(String query, Map params, DeclarationContextIF context)
    throws InvalidQueryException {
    return processor.update(query, params, context);
  }
  
  public void updateError(String query) throws InvalidQueryException {
    try {
      update(query);
      fail("No error from query");
    } catch (InvalidQueryException e) {
      // as expected
    }
  }
  
  protected void getParseError(String query) {
    getParseError(query, Collections.EMPTY_MAP);
  }

  protected void getParseError(String query, Map parameters) {
    QueryResultIF result = null;
    try {
      result = processor.execute(query, parameters);
      fail("query '" + query + "' parsed OK, but shouldn't have");
    } catch (InvalidQueryException e) {
    } finally {
      if (result != null) result.close();
    }
  }
  
  public ParsedQueryIF parse(String query) throws InvalidQueryException {
    return processor.parse(query);
  }

  public Map getMatch(QueryResultIF result) {
    Map match = new HashMap();
    for (int ix = 0; ix < result.getWidth(); ix++) {
      String vname = result.getColumnName(ix);
      match.put(vname, result.getValue(ix));
    }
    return match;
  }

  public Set getMatchSet(QueryResultIF result) {
    Set match = new HashSet();
    for (int ix = 0; ix < result.getWidth(); ix++) 
      match.add(result.getValue(ix));
    return match;
  }

  public Map makeArguments(String argument, String value) {
    Map args = new HashMap();
    args.put(argument, getTopicById(value));
    return args;
  }
  
  public Map makeArguments(String argument, TMObjectIF value) {
    Map args = new HashMap();
    args.put(argument, value);
    return args;
  }

  // --- test to check if query processor implementation is rdbms

  public boolean isRDBMSTolog() {
    return processor.getClass().getName().equals("net.ontopia.topicmaps.query.impl.rdbms.QueryProcessor");
  }

  public boolean isInMemoryTolog() {
    return processor.getClass().getName().equals("net.ontopia.topicmaps.query.impl.basic.QueryProcessor");
  }

}
