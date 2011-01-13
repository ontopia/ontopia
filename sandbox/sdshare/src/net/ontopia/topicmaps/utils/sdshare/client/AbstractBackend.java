
package net.ontopia.topicmaps.utils.sdshare.client;

import java.util.Set;

/**
 * INTERNAL: Shared utility code between backend implementations.
 */
public abstract class AbstractBackend {

  /**
   * Picks the preferred link in a set of AtomLink objects. Uses
   * getLinkScore to do the selection. May return null, even if the
   * set is non-empty.
   */
  public AtomLink findPreferredLink(Set<AtomLink> links) {
    int highest = 0;
    AtomLink preferred = null;
    for (AtomLink link : links) {
      int score = getLinkScore(link);
      if (score > highest) {
        highest = score;
        preferred = link;
      }
    }
    return preferred;
  }

  /**
   * Gives a numeric preference score to the link, from 0 to 100. 0
   * means that the link is unacceptable, and so links with this score
   * will not be returned.
   */
  public abstract int getLinkScore(AtomLink link);
  
}