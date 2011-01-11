
package net.ontopia.topicmaps.utils.sdshare.client;

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
   * PUBLIC: Applies the given fragment to the given endpoint.
   */
  public void applyFragment(SyncEndpoint endpoint, Fragment fragment);
  
}