
package net.ontopia.topicmaps.utils.sdshare;

import java.net.URL;
import java.io.IOException;
import org.xml.sax.SAXException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;

/**
 * Manages a set of ConsumerClients based on a ClientConfig. Can keep
 * a set of topic maps in sync with their sources.
 */
public class ClientManager {
  private ClientConfig config;
  private TopicMapRepositoryIF repository;
  private SyncThread thread;
  static Logger log = LoggerFactory.getLogger(ClientManager.class.getName());

  public ClientManager(ClientConfig config, TopicMapRepositoryIF repository) {
    this.config = config;
    this.repository = repository;
  }

  public ClientConfig getConfig() {
    return config;
  }

  public String getStatus() {
    if (thread == null)
      return "Not running";
    else
      return thread.getStatus();
  }

  public boolean isRunning() {
    return thread != null && thread.isRunning();
  }

  public boolean isStopped() {
    return (thread == null) || (thread != null && !thread.isRunning());
  }
  
  public void startThread() {
    if (thread == null) {
      thread = new SyncThread();
      thread.start();
    }
  }

  public void stopThread() {
    if (thread != null)
      thread.stopThread();
  }

  public void loadSnapshots() throws IOException, SAXException {
    for (ClientConfig.TopicMap topicmap : config.getTopicMaps()) {
      log.info("Getting snapshots for " + topicmap.getId());
      for (ClientConfig.SyncSource source : topicmap.getSources()) {
        if (source.getClient() == null)
          continue; // skipping this since there's no client for it

        String feedurl = source.getSnapshotFeedURL();
        log.info("Loading from " + feedurl);
        ConsumerClient.SnapshotFeed feed = ConsumerClient.readSnapshotFeed(feedurl);
        ConsumerClient.Snapshot snapshot = feed.getSnapshots().get(0);
        String url = snapshot.getFeedURI().getAddress();
        LocatorIF base = feed.getPrefix();

        XTMTopicMapReader reader = new XTMTopicMapReader(new URL(url).openConnection().getInputStream(), base);
        TopicMapReferenceIF ref = source.getReference();
        TopicMapIF tm = ref.createStore(false).getTopicMap();
        reader.importInto(tm);
        // FIXME: commit!
      }
    }
  }
  
  public void sync() throws IOException, SAXException {
    for (ClientConfig.TopicMap topicmap : config.getTopicMaps()) {
      log.info("Checking " + topicmap.getId());
      for (ClientConfig.SyncSource source : topicmap.getSources()) {
        if (source.getClient() == null)
          continue; // skipping this since there's no client for it

        ConsumerClient client = source.getClient();
        if (!client.isTimeToCheck())
          continue;
        log.info("Syncing with " + client.getFeedURL());
        client.sync();
      }
    }
  }

  // --- Internal thread class

  class SyncThread extends Thread {
    private boolean stopped;
    private boolean running;

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
          log.error("Exception while syncing", e); // we carry on
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
  }
}