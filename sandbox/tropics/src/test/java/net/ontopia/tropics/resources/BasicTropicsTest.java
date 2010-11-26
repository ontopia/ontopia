package net.ontopia.tropics.resources;

import static org.junit.Assert.fail;

import java.io.IOException;

import net.ontopia.tropics.TropicsServer;

import org.junit.After;
import org.junit.Before;
import org.restlet.Client;
import org.restlet.data.Protocol;
import org.restlet.resource.ClientResource;

public class BasicTropicsTest {
  private TropicsServer tropics = TropicsServer.create("tropics.props");
  
  protected Client client = new Client(Protocol.HTTP);
  
  public BasicTropicsTest() {
    client.setConnectTimeout(5000);
  }
  
  @Before
  public void setUp() throws Exception {
    tropics.start();
  }

  @After
  public void tearDown() throws Exception {
    tropics.stop();
  }
  
  protected String get(ClientResource clientResource) {
    clientResource.setNext(client);
    
    clientResource.get();
    if (!clientResource.getStatus().isSuccess()) {
      fail("GET for <" + clientResource.getReference() + "> was unsuccessful. (" + clientResource.getStatus().getCode() + ", " + clientResource.getStatus().getDescription() + ")");
    }
     
    if (!clientResource.getResponseEntity().isAvailable()) {
      fail("Resource for <" + clientResource.getReference() + "> was unavailable.");
    }
    
    String response = null;
    try {
      response = clientResource.getResponseEntity().getText();
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }

    return response;
  }  
  
}
