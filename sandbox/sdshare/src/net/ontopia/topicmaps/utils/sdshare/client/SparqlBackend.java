
package net.ontopia.topicmaps.utils.sdshare.client;

import java.util.Map;
import java.util.HashMap;
import java.io.Writer;
import java.io.IOException;
import java.io.StringWriter;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.hp.hpl.jena.rdf.arp.AResource;
import com.hp.hpl.jena.rdf.arp.ALiteral;
import com.hp.hpl.jena.rdf.arp.StatementHandler;

import net.ontopia.utils.StreamUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.utils.rdf.RDFUtils;

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

    // WARN: this is the Virtuoso dialect of SPARQL Update, so these
    // queries will probably only work on Virtuoso. need to add a
    // property so that one can configure what SPARQL Update dialect
    // to use.
    
    // first, remove all statements about the current topic
    doUpdate(endpoint.getHandle(),
             "delete from <" + graph + "> " +
             "  { <" + subject + "> ?p ?v } " +
             "where " +
             "  { <" + subject + "> ?p ?v }");
    
    // second, load new fragment into graph
    // doUpdate(endpoint.getHandle(),
    //          "load " + uri + " into <" + graph + ">");
    StringWriter tmp = new StringWriter();
    try {
      RDFUtils.parseRDFXML(uri, new InsertWriter(tmp));
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
    doUpdate(endpoint.getHandle(),
             "insert data into <" + graph + "> { " +
             tmp.toString() + " }");
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

    // FIXME: in time we may have to use Keep-Alive so that we don't
    // need to open new TCP connections all the time.
    
    // WARN: it doesn't look like the spec actually describes the update
    // protocol, but we can probably guess what it looks like. so this is
    // based on a kind of reverse-engineering of the protocol by guesswork.

    // (1) putting together the request
    
    statement = statement.replace(' ', '+');
    byte rawdata[] = ("query=" + statement).getBytes("utf-8");
    
    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httppost = new HttpPost(endpoint);
    ByteArrayEntity reqbody = new ByteArrayEntity(rawdata);
    reqbody.setContentType("application/x-www-form-urlencoded");
    httppost.setEntity(reqbody);

    // (2) retrieving the response

    HttpResponse response = httpclient.execute(httppost);
    HttpEntity resEntity = response.getEntity();

    log.warn("Server response: " + response.getStatusLine());

    String msg = StreamUtils.read(new InputStreamReader(resEntity.getContent()));
    log.warn("Body: " + msg);

    if (response.getStatusLine().getStatusCode() != 200)
      throw new OntopiaRuntimeException("Error sending SPARQL query: " +
                                        response.getStatusLine());
  }

  // ===== Writing INSERT-format triples

  public static class InsertWriter implements StatementHandler {
    private Writer out;
    private Map<String, String> nodelabels;
    private int counter;

    public InsertWriter(Writer out) {
      this.out = out;
      this.nodelabels = new HashMap();
    }
    
    public void statement(AResource sub, AResource pred, ALiteral lit) {
      try {
        writeResource(sub);
        writeResource(pred);
        writeLiteral(lit);
        terminate();
      } catch (IOException e) {
        throw new OntopiaRuntimeException(e);
      }
    }

    public void statement(AResource sub, AResource pred, AResource obj) {
      try {
        writeResource(sub);
        writeResource(pred);
        writeResource(obj);
        terminate();
      } catch (IOException e) {
        throw new OntopiaRuntimeException(e);
      }
    }

    private void terminate() throws IOException {
      out.write(".");
    }

    private void writeLiteral(ALiteral lit) throws IOException {
      out.write("\"" + lit.toString().replace("\"", "\\\"") + "\" ");
    }

    private void writeResource(AResource res) throws IOException {
      if (res.isAnonymous()) {
        String id = res.getAnonymousID();
        String label = nodelabels.get(id);
        if (label == null) {
          label = "_:b" + counter++;
          nodelabels.put(id, label);
        }
        out.write(nodelabels.get(id) + " ");
      } else
        out.write("<" + res.getURI() + "> ");
    }
  }
}