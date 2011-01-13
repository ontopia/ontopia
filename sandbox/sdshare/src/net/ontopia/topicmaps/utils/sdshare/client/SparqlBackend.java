
package net.ontopia.topicmaps.utils.sdshare.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Backend which uses SPARQL to update an RDF triple store.
 */
public class SparqlBackend extends AbstractBackend implements ClientBackendIF {
  static Logger log = LoggerFactory.getLogger(SparqlBackend.class.getName());

  // FIXME: need to be able to pick preferred syntax somehow.
  // alternatives are either an isMIMETypeOK method or a rankMIMEType
  // method. the more work we can move out of the backend, the better.

  // FIXME: also, how to represent syntaxes? MIME type strings have
  // a syntax, so perhaps they should be parsed by the client?
  
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
    doUpdate(endpoint.getHandle(),
             "delete from <" + graph + "> " +
             "  { <" + subject + "> ?p ?v } " +
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

  private void doUpdate(String endpoint, String statement) {
    // FIXME: need to actually connect to the SPARQL endpoint and send
    // statements there
    log.warn("doUpdate: " + statement);
  }
  
}