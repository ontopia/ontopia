package net.ontopia.tropics.resources;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class QueryParamTest {

  @Test
  public void testAliases() {
    QueryParam[] params = QueryParam.values();
    
    assertEquals(18, params.length);
    
    for (QueryParam param : params) {
      for (String alias : param.getAliases()) {
        assertEquals(param, QueryParam.getQueryParam(alias));
      }
    }
  }
}
