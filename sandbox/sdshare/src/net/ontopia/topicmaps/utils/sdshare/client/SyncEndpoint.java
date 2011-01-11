
package net.ontopia.topicmaps.utils.sdshare.client;

import java.util.ArrayList;
import java.util.Collection;

/**
 * PUBLIC: Represents an endpoint which sources are synchronized
 * into. What exactly the endpoint is depends on the backend used.  In
 * the Ontopia backend it is a topic map, while in the SPARQL backend
 * it's a triple store.
 */
public class SyncEndpoint {
  private String handle;
  private Collection<SyncSource> sources;

  public SyncEndpoint(String handle) {
    this.handle = handle;
    this.sources = new ArrayList<SyncSource>();
  }

  public void addSource(SyncSource source) {
    sources.add(source);
  }

  public String getHandle() {
    return handle;
  }

  public Collection<SyncSource> getSources() {
    return sources;
  }
}
