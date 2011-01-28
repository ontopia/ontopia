
package net.ontopia.topicmaps.utils.sdshare.client;

import java.util.List;

/**
 * PUBLIC: The interface to be implemented by SDshare client backends,
 * representing the operations necessary to actually receive SDshare
 * data.
 */
public interface ClientBackendIF {

  /**
   * PUBLIC: Loads the given snapshot into the given endpoint.
   */
  public void loadSnapshot(SyncEndpoint endpoint, Snapshot snapshot);

  /**
   * PUBLIC: Applies the given fragments to the given endpoint. As far
   * as possible this should be done atomically, as a single transaction.
   */
  public void applyFragments(SyncEndpoint endpoint, List<Fragment> fragments);
  
}