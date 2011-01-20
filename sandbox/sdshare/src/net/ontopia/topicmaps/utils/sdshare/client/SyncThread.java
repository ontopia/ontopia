
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
  static Logger log = LoggerFactory.getLogger(SyncThread.class.getName());

  public SyncThread(ClientBackendIF backend,
                    Collection<SyncEndpoint> endpoints) {
    this.backend = backend;
    this.endpoints = endpoints;
    this.loaded = false; // we load only when starting
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
      } catch (Exception e) {
        // FIXME: we log the error and carry on, but it's not clear that
        // this really is a good idea. we should have a better handling of
        // this. for example, we might want to stop sources which have
        // errors and carry on with everything else. there might even
        // be some operation in the UI for clearing sources which are
        // blocked on errors. the UI should also display the error so
        // that we can carry on.
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
          continue; // nothing to do

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
    save();
  }

  /**
   * Saves information about the current state of the client so that
   * we can carry on from where we stopped after the server is
   * restarted.
   */ 
  private void save() throws IOException {
    // line-based text format. each line is:
    //   endpoint-handle source-url lastchange
    File f = new File(System.getProperty("java.io.tmpdir"),
                      "sdshare-client-state.txt");
    log.info("Saving state to " + f);
    FileWriter out = new FileWriter(f);

    for (SyncEndpoint endpoint : endpoints)
      for (SyncSource source : endpoint.getSources())
        out.write(endpoint.getHandle() + " " +
                  source.getURL() + " " +
                  source.getLastChange() + "\n");
    
    out.close();
  }

  /**
   * Loads the state of the various sources from the save file.
   */
  private void load() {
    // to make it easier to match state lines to actual sources we
    // build a map first
    Map<String, SyncSource> map = new HashMap();
    for (SyncEndpoint endpoint : endpoints)
      for (SyncSource source : endpoint.getSources())
        map.put(endpoint.getHandle() + " " + source.getURL(), source);

    // ok, now we can load the file
    try {
      File f = new File(System.getProperty("java.io.tmpdir"),
                        "sdshare-client-state.txt");
      BufferedReader in = new BufferedReader(new FileReader(f));
      String line = in.readLine();
      while (line != null) {
        String[] row = StringUtils.split(line.trim());

        SyncSource source = map.get(row[0] + " " + row[1]);
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
