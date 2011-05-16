package net.ontopia.topicmaps.query.impl.rdbms;

import java.io.IOException;

public class QueryParserTest extends net.ontopia.topicmaps.query.core.QueryParserTest {

  public QueryParserTest(String name) {
    super(name);
  }

  @Override
  protected void load(String filename) throws IOException {
    RDBMSTestUtils.load(this, filename);
  }

  @Override
  protected void makeEmpty() {
    RDBMSTestUtils.makeEmpty(this);
  }
}
