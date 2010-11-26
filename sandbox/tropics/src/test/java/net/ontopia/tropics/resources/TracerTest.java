package net.ontopia.tropics.resources;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.restlet.resource.ClientResource;

public class TracerTest extends BasicTropicsTest {
  private static final String TRACE_URI = "http://localhost:8182/api/v1/trace";
  
  @Test
  public void testTrace() {
    ClientResource client = new ClientResource(TRACE_URI);        
    String response = get(client);
    
    String expected = "Method       : GET\n" +
                      "Resource URI : http://localhost:8182/api/v1/trace\n" +
                      "Host Ref     : http://localhost:8182\n" +
                      "IP address   : 127.0.0.1\n" +
                      "Agent name   : Restlet-Framework\n" +
                      "Agent version: 2.0rc3\n";
    
    assertEquals(expected, response);
  }

}
