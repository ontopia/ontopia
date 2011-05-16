
// $Id: AbstractPredicateTest.java,v 1.5 2008/01/11 12:58:56 geir.gronmo Exp $

package net.ontopia.topicmaps.query.core;


public abstract class AbstractPredicateTest extends AbstractQueryTest {
  //! protected TopicMapBuilderIF builder;
  
  public AbstractPredicateTest(String name) {
    super(name);
  }

  //! /// setup
  //! 
  //! public void setUp() {
  //!   QueryMatches.initialSize = 1;
  //!   InMemoryTopicMapStore store = new InMemoryTopicMapStore();
  //!   topicmap = store.getTopicMap();
  //!   processor = new QueryProcessor(topicmap);
  //!   builder = topicmap.getBuilder();
  //! 
  //!   try {
  //!     base = new URILocator("http://www.example.com");
  //!   } catch (java.net.MalformedURLException e) {
  //!     throw new net.ontopia.utils.OntopiaRuntimeException(e);
  //!   }
  //! }
  
}
