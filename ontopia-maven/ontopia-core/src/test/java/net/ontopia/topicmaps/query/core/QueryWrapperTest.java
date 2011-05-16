
// $Id: QueryWrapperTest.java,v 1.1 2007/11/20 09:07:41 lars.garshol Exp $

package net.ontopia.topicmaps.query.core;

import java.util.Map;
import java.io.IOException;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.query.utils.QueryWrapper;

public class QueryWrapperTest extends AbstractQueryTest {
  private QueryWrapper wrapper;
  
  public QueryWrapperTest(String name) {
    super(name);
  }

  /// context management

  public void setUp() {
  }

  public void tearDown() {
    closeStore();
  }

  public void load(String tmid) throws IOException {
    super.load(tmid);
    wrapper = new QueryWrapper(topicmap);
  }

  /// query for map tests

  public void testQueryForMapNothing() throws IOException {
    load("instance-of.ltm");
    Map map = wrapper.queryForMap("instance-of($T, topic1)?");
    assertNull("Returned map for query with no rows", map);
  }  

  public void testQueryForMapNormal() throws IOException {
    load("instance-of.ltm");
    Map map = wrapper.queryForMap(
      "instance-of(topic1, $TYPE), " +
      "subject-identifier($TYPE, $PSI)?");

    assertTrue("map has wrong size", map.size() == 2);
    assertTrue("'TYPE' has wrong value",
               map.get("TYPE").equals(getTopicById("type1")));
    assertTrue("'PSI' has wrong value",
               map.get("PSI").equals("http://psi.ontopia.net/test/#1"));
  }  

  public void testQueryForMapTooMany() throws IOException {
    load("instance-of.ltm");
    try {
      wrapper.queryForMap("instance-of($T, type1)?");
      fail("No error despite too many rows");
    } catch (OntopiaRuntimeException e) {
      // the expected error
    }
  }  
}
