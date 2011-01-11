
package net.ontopia.topicmaps.utils.sdshare.client;

import java.util.Set;

/**
 * PUBLIC: Represents an individual fragment in a fragment feed.
 */
public class Fragment {
  private Set<String> topicSIs;
  private String fragmenturi;
  private String mimetype;
  private long updated;
  private FragmentFeed parent;

  public Fragment(String fragmenturi, String mimetype,
                  Set<String> topicSIs, long updated) {
    this.fragmenturi = fragmenturi;
    this.mimetype = mimetype;
    this.topicSIs = topicSIs;
    this.updated = updated;
  }

  public String getFragmentURI() {
    return fragmenturi;
  }

  public String getMIMEType() {
    return mimetype;
  }

  public Set<String> getTopicSIs() {
    return topicSIs;
  }

  public long getUpdated() {
    return updated;
  }

  public void setParent(FragmentFeed parent) {
    this.parent = parent;
  }
  
  public FragmentFeed getParent() {
    return parent;
  }
}
