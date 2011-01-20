
package net.ontopia.topicmaps.utils.sdshare.client;

import java.net.URL;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ontopia.utils.StreamUtils;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Backend which uses SPARQL to update an RDF triple store.
 */
public class SparqlBackend extends AbstractBackend implements ClientBackendIF {
  static Logger log = LoggerFactory.getLogger(SparqlBackend.class.getName());
  
  public void loadSnapshot(SyncEndpoint endpoint, Snapshot snapshot) {
    String graph = snapshot.getFeed().getPrefix();
    String uri = snapshot.getSnapshotURI();

    // first, clear the graph
    doUpdate(endpoint.getHandle(),
             "clear graph <" + graph + ">");

    // second, import the snapshot
    doUpdate(endpoint.getHandle(),
             "load <" + uri + "> into <" + graph + ">");
  }

  public void applyFragment(SyncEndpoint endpoint, Fragment fragment) {
    String graph = fragment.getFeed().getPrefix();
    String uri = findPreferredLink(fragment.getLinks()).getUri();

    // FIXME: we don't support more than one SI at a time yet.
    assert fragment.getTopicSIs().size() == 1;
    String subject = fragment.getTopicSIs().iterator().next();
    
    // first, remove all statements about the current topic
    // doUpdate(endpoint.getHandle(),
    //          "delete from <" + graph + "> " +
    //          "  { <" + subject + "> ?p ?v } " +
    //          "where " +
    //          "  { <" + subject + "> ?p ?v }");
    doUpdate(endpoint.getHandle(),
             "with <" + graph + "> " +
             "delete { <" + subject + "> ?p ?v } " +
             "where " +
             "  { <" + subject + "> ?p ?v }");
    
    // second, load new fragment into graph
    doUpdate(endpoint.getHandle(),
             "load <" + uri + "> into <" + graph + ">");
  }

  // ===== Implementation code

  public int getLinkScore(AtomLink link) {
    MIMEType mimetype = link.getMIMEType();
    // FIXME: this is too simplistic. we could probably support more
    // syntaxes than just this one, but for now this will have to do.
    if (mimetype.getType().equals("application/rdf+xml"))
      return 100;
    return 0;
  }

  public static void doUpdate(String endpoint, String statement) {    
    try {
      doUpdate_(endpoint, statement);
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  public static void doUpdate_(String endpoint, String statement) throws IOException {
    log.warn("doUpdate: " + statement);

    // WARN: it doesn't look like the spec actually describes the update
    // protocol, but we can probably guess what it looks like. so this is
    // based on a kind of reverse-engineering of the protocol by guesswork.

    URL url = new URL(endpoint);
    statement = statement.replace(' ', '+');
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestProperty("content-type",
                            "application/x-www-form-urlencoded");

    // this part turns the request into a POST request (argh)
    conn.setDoOutput(true); // means we intend to push data into connection
    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
    wr.write("update=" + statement);
    wr.close();

    log.warn("doUpdate response code: " + conn.getResponseCode());

    String msg = StreamUtils.read(new InputStreamReader(conn.getInputStream()));
    log.warn("doUpdate response: " + msg);

    // well, that's it. so long as the response code is 200 everything is
    // hunky dory, and we carry on. not sure what is returned if something
    // goes wrong. we'll get back to that later.
  }
  
}