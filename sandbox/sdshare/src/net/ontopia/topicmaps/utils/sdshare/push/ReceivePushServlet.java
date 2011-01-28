
package net.ontopia.topicmaps.utils.sdshare.push;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xml.sax.SAXException;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.utils.sdshare.client.Fragment;
import net.ontopia.topicmaps.utils.sdshare.client.FeedReaders;
import net.ontopia.topicmaps.utils.sdshare.client.FragmentFeed;
import net.ontopia.topicmaps.utils.sdshare.client.SyncEndpoint;
import net.ontopia.topicmaps.utils.sdshare.client.OntopiaBackend;
import net.ontopia.topicmaps.utils.sdshare.client.ClientBackendIF;

/**
 * INTERNAL: Servlet which receives an SDshare push POST request and
 * updates the topic map accordingly. The servlet is deployed using a
 * mapping in web.xml, and a URL pattern that goes: .../push/*.  The
 * part after the final slash in the request URI is interpreted as the
 * endpoint handle (topic map ID).
 */
public class ReceivePushServlet extends HttpServlet {
  private ClientBackendIF backend;  

  public ReceivePushServlet() {
    this.backend = new OntopiaBackend();
  }
  
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws IOException {
    // (1) parse incoming request
    String url = req.getRequestURI();
    if (req.getQueryString() != null)
      url += "?" + req.getQueryString(); // FIXME: is this wise?
    int slashpos = url.lastIndexOf('/');
    
    String handle = url.substring(slashpos + 1);
    FragmentFeed feed;
    try {
      feed = FeedReaders.readPostFeed(req.getReader());
    } catch (SAXException e) {
      throw new OntopiaRuntimeException(e);
    }
    SyncEndpoint endpoint = new SyncEndpoint(handle);
    
    // (2) run through the feed, and apply each fragment into the backend
    backend.applyFragments(endpoint, feed.getFragments());
  }
}