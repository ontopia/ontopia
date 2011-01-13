
package net.ontopia.topicmaps.utils.sdshare.client;

/**
 * Represents a <link> element in an Atom feed.
 */
public class AtomLink {
  private MIMEType mimetype; // can be null
  private String uri;

  public AtomLink(MIMEType mimetype, String uri) {
    this.mimetype = mimetype;
    this.uri = uri;
  }

  public MIMEType getMIMEType() {
    return mimetype;
  }

  public String getUri() {
    return uri;
  }
}