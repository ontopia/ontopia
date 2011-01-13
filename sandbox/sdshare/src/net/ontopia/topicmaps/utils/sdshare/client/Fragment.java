
package net.ontopia.topicmaps.utils.sdshare.client;

import java.util.Set;

/**
 * PUBLIC: Represents an individual fragment in a fragment feed.
 */
public class Fragment {
  private Set<String> topicSIs;
  private Set<AtomLink> links;
  private long updated;
  private FragmentFeed parent;

  public Fragment(Set<AtomLink> links, Set<String> topicSIs, long updated) {
    this.links = links;
    this.topicSIs = topicSIs;
    this.updated = updated;
  }

  public Set<AtomLink> getLinks() {
    return links;
  }

  public Set<String> getTopicSIs() {
    return topicSIs;
  }

  public long getUpdated() {
    return updated;
  }

  public void setFeed(FragmentFeed parent) {
    this.parent = parent;
  }
  
  public FragmentFeed getFeed() {
    return parent;
  }
}
