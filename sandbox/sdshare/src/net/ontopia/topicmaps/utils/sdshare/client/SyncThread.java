
package net.ontopia.topicmaps.utils.sdshare.client;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import org.xml.sax.SAXException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ontopia.utils.StringUtils;
  
/**
 * INTERNAL: The thread that actually performs the sync operations.
 * Note that loadSnapshots() and sync() can be called both from run()
 * (that is, from within the thread), and from ClientManager in
 * response to UI events (in which case we're outside the thread).
 */
class SyncThread extends Thread {
  private boolean stopped;
  private boolean running;
  private boolean loaded;
  private ClientBackendIF backend;
  private Collection<SyncEndpoint> endpoints;
  private Map<String, SyncSource> map;
  static Logger log = LoggerFactory.getLogger(SyncThread.class.getName());

  public SyncThread(ClientBackendIF backend,
                    Collection<SyncEndpoint> endpoints) {
    this.backend = backend;
    this.endpoints = endpoints;
    this.loaded = false; // we load only when starting

    // build a map of the sources for lookup purposes
    this.map = new HashMap();
    for (SyncEndpoint endpoint : endpoints)
      for (SyncSource source : endpoint.getSources())
        map.put(endpoint.getHandle() + " " + source.getHandle(), source);
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

    if (!loaded)
      load();
      
    while (!stopped) {
      try {
        sync();
      } catch (IOException e) {
        // this should only be IOExceptions from saving state, and so logging
        // and carrying on should be fine
        log.warn("Exception while syncing", e);
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

  public SyncSource getSource(String key) {
    return map.get(key);
  }

  // --- THE ACTUAL OPERATIONS

  public void loadSnapshots() throws IOException, SAXException {
    for (SyncEndpoint endpoint : endpoints) {
      log.info("Getting snapshots for " + endpoint.getHandle());
      for (SyncSource source : endpoint.getSources()) {
        log.info("Loading from " + source.getHandle());        
        SnapshotFeed feed = source.getSnapshotFeed();
        Snapshot snapshot = feed.getSnapshots().get(0);
        backend.loadSnapshot(endpoint, snapshot);
      }
    }
  }

  public void sync() throws IOException {
    for (SyncEndpoint endpoint : endpoints) {
      log.debug("Checking " + endpoint.getHandle());
      for (SyncSource source : endpoint.getSources()) {
        // verify that it's time to check this source now, and that the source
        // hasn't failed.
        if (!source.isTimeToCheck() || source.isBlockedByError())
          return;

        // it was time, so we download the feed and go through the
        // actual fragments
        try {
          FragmentFeed feed = source.getFragmentFeed();
          log.info("FOUND " + feed.getFragments().size() + " fragments");
          if (feed.getFragments().isEmpty())
            continue; // nothing to do

          backend.applyFragments(endpoint, feed.getFragments());
          for (Fragment fragment : feed.getFragments())
            source.setLastChange(fragment.getUpdated());
        } catch (Throwable e) {
          // we log the error, and note it on the source. that stops further
          // updates from the source, until we are told that we can continue.
          log.warn("Source " + source.getHandle() + " failed", e);
          source.setError(e.getMessage());
        }

        // this notes the time of the last update time for this source,
        // even if it failed.
        source.updated();
      }
    }    
    save();
  }

  /**
   * Saves information about the current state of the client so that
   * we can carry on from where we stopped after the server is
   * restarted.
   */ 
  private void save() throws IOException {
    // line-based text format. each line is:
    //   endpoint-handle source-handle lastchange
    File f = new File(System.getProperty("java.io.tmpdir"),
                      "sdshare-client-state.txt");
    log.info("Saving state to " + f);
    FileWriter out = new FileWriter(f);

    for (SyncEndpoint endpoint : endpoints)
      for (SyncSource source : endpoint.getSources())
        out.write(endpoint.getHandle() + " " +
                  source.getHandle() + " " +
                  source.getLastChange() + "\n");
    
    out.close();
  }

  /**
   * Loads the state of the various sources from the save file.
   */
  private void load() {
    try {
      File f = new File(System.getProperty("java.io.tmpdir"),
                        "sdshare-client-state.txt");
      BufferedReader in = new BufferedReader(new FileReader(f));
      String line = in.readLine();
      while (line != null) {
        String[] row = StringUtils.split(line.trim());

        SyncSource source = getSource(row[0] + " " + row[1]);
        if (source != null) {
          long last = Long.parseLong(row[2]);
          source.setLastChange(last);
        }
        line = in.readLine();
      }
      in.close();
    } catch (IOException e) {
      log.warn("Couldn't load state of sources", e);
      // we carry on anyway, assuming that we don't need the state info.
      // the usual cause of this warning is that we don't have any state
      // yet.
    }
  }
}
