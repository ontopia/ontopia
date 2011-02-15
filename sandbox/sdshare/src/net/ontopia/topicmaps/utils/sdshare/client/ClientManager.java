
package net.ontopia.topicmaps.utils.sdshare.client;

import java.util.Collection;
import java.io.IOException;
import org.xml.sax.SAXException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the SyncThread. Has methods roughly corresponding to user
 * interface operations.
 */
public class ClientManager {
  private ClientConfig config;
  private SyncThread thread;
  static Logger log = LoggerFactory.getLogger(ClientManager.class.getName());

  public ClientManager(ClientConfig config) {
    this.config = config;
    this.thread = new SyncThread(config.getBackend(),
                                 config.getEndpoints());
  }

  public ClientConfig getConfig() {
    return config;
  }

  public String getStatus() {
    return thread.getStatus();
  }

  public boolean isRunning() {
    return thread.isRunning();
  }

  public boolean isStopped() { // FIXME: do we need this?
    return !thread.isRunning();
  }
  
  public void startThread() {
    thread.start();
  }

  public void stopThread() {
    thread.stopThread();
    thread = new SyncThread(config.getBackend(), config.getEndpoints());
  }

  public void loadSnapshots() throws IOException, SAXException {
    thread.loadSnapshots();
  }
  
  public void sync() throws IOException, SAXException {
    thread.sync(true);
  }

  public SyncSource getSource(String key) {
    return thread.getSource(key);
  }
}