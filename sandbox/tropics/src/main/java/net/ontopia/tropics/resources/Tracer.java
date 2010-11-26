package net.ontopia.tropics.resources;

import org.restlet.Request;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class Tracer extends ServerResource {
  
  @Get("txt")
  public String trace() {
    Request request = getRequest();
    StringBuilder sb = new StringBuilder();
    
    sb.append("Method       : ").append(request.getMethod()).append('\n');
    sb.append("Resource URI : ").append(request.getResourceRef()).append('\n');
    sb.append("Host Ref     : ").append(request.getHostRef()).append('\n');
    sb.append("IP address   : ").append(request.getClientInfo().getAddress()).append('\n');
    sb.append("Agent name   : ").append(request.getClientInfo().getAgentName()).append('\n');
    sb.append("Agent version: ").append(request.getClientInfo().getAgentVersion()).append('\n');
    
    return sb.toString();
  }  

}
