
package net.ontopia.topicmaps.utils.sdshare.client;

import java.util.Collection;
import java.net.URL;
import java.io.IOException;
import org.xml.sax.SAXException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: The thread that actually performs the sync operations.
 * Note that loadSnapshots() and sync() can be called both from run()
 * (that is, from within the thread), and from ClientManager in
 * response to UI events (in which case we're outside the thread).
 */
class SyncThread extends Thread {
  private boolean stopped;
  private boolean running;
  private ClientBackendIF backend;
  private Collection<SyncEndpoint> endpoints;
  static Logger log = LoggerFactory.getLogger(SyncThread.class.getName());

  public SyncThread(ClientBackendIF backend,
                    Collection<SyncEndpoint> endpoints) {
    this.backend = backend;
    this.endpoints = endpoints;
  }
  
  public String getStatus() {
    if (running) {
      if (stopped)
        return "Waiting for thread to stop";
      else
        return "Running";
    } else {
      return "Not running .";
    }
  }
  
  public boolean isStopped() {
    return stopped;
  }

  public boolean isRunning() {
    return running;
  }

  public void stopThread() {
    stopped = true;
  }
    
  public void run() {
    stopped = false;
    running = true;
    
    while (!stopped) {
      try {
        sync();
      } catch (Exception e) {
        // FIXME: we log the error and carry on, but it's not clear that
        // this really is a good idea. we should have a better handling of
        // this. for example, we might want to stop sources which have
        // errors and carry on with everything else.
        log.error("Exception while syncing", e); 
      }
      
      try {
        Thread.sleep(1000); // wait a second, then check again
      } catch (InterruptedException e) {
        // this exception is really annoying
      }
    }
    
    running = false;
    stopped = false;
  }

  // --- THE ACTUAL OPERATIONS

  public void loadSnapshots() throws IOException, SAXException {
    for (SyncEndpoint endpoint : endpoints) {
      log.info("Getting snapshots for " + endpoint.getHandle());
      for (SyncSource source : endpoint.getSources()) {
        String feedurl = source.getSnapshotFeedURL();
        log.info("Loading from " + feedurl);
        SnapshotFeed feed = FeedReaders.readSnapshotFeed(feedurl);
        Snapshot snapshot = feed.getSnapshots().get(0);
        backend.loadSnapshot(endpoint, snapshot);
      }
    }
  }

  public void sync() throws IOException, SAXException {
    for (SyncEndpoint endpoint : endpoints) {
      log.info("Checking " + endpoint.getHandle());
      for (SyncSource source : endpoint.getSources()) {
        // verify that it's time to check this source now
        if (!source.isTimeToCheck())
          return;

        // it was time, so we download the feed and go through the
        // actual fragments
        FragmentFeed feed = FeedReaders.readFragmentFeed(source.getFragmentFeedURL(), source.getLastChange());
        log.info("FOUND " + feed.getFragments().size() + " fragments");
        if (feed.getFragments().isEmpty())
          return; // nothing to do

        for (Fragment fragment : feed.getFragments()) {
          backend.applyFragment(endpoint, fragment);
          // we assume that unless we see an exception, the fragment is
          // successfully applied. conversely, if we get here we assume
          // the fragment succeeded.
          source.setLastChange(fragment.getUpdated());
          
          // FIXME: not catching exceptions yet, because we don't know
          // what to do with them. just as well to simply break off and
          // stop, probably, but not sure how to do that yet. for now
          // we just leave this dangling.
        }
        
        source.updated();
      }
    }
  }
}
