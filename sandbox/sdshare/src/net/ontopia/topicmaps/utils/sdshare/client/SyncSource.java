
package net.ontopia.topicmaps.utils.sdshare.client;

import java.io.IOException;
import java.lang.reflect.Constructor;
import org.xml.sax.SAXException;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * PUBLIC: Represents information about an SDshare source to
 * synchronize from. Handles error blocking, time to next check, and
 * other housekeeping, and leaves the real work to the frontend.
 */
public class SyncSource {
  private ClientFrontendIF frontend;
  private String error; // error message; if null there is no error
  private int checkInterval; // in seconds!
  /**
   * The time (on this machine) of the last time we checked this source.
   * In milliseconds since epoch.
   */
  private long lastCheck;
  /**
   * The timestamp on the last change processed from this source, as given
   * by the server. In milliseconds since epoch.
   */
  private long lastChange;
  
  public SyncSource(String handle, int checkInterval, String frontend) {
    this.frontend = instantiate(handle, frontend);
    this.checkInterval = checkInterval;
  }
  
  public String getHandle() { // of source collection
    return frontend.getHandle();
  }

  public SnapshotFeed getSnapshotFeed() throws IOException, SAXException {
    return frontend.getSnapshotFeed();
  }

  public FragmentFeed getFragmentFeed() throws IOException, SAXException {
    return frontend.getFragmentFeed(lastChange);
  }

  /**
   * Returns the time we last checked for changes, as given by
   * System.currentTimeMillis.
   */
  public long getLastCheck() {
    return lastCheck;
  }
  
  /**
   * Returns the timestamp of the last change processed from this
   * source, as given in the Atom feed.
   */
  public long getLastChange() {
    return this.lastChange;
  }

  /**
   * Updates the timestamp of the last change *iff* this timestamp is
   * later than the latest one seen so far. The rationale is that we
   * are not absolutely sure what order fragments are returned in.
   */
  public void setLastChange(long lastChange) {
    if (lastChange > this.lastChange)
      this.lastChange = lastChange;
  }

  /**
   * Called to let the source know that it is now updated.
   */
  public void updated() {
    this.lastCheck = System.currentTimeMillis();
  }

  /**
   * Returns true iff the current time >= time of last check + check
   * interval.
   */
  public boolean isTimeToCheck() {
    return System.currentTimeMillis() >= lastCheck + (checkInterval * 1000);
  }

  public boolean isBlockedByError() {
    return error != null;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public void clearError() {
    this.error = null;
  }

  private ClientFrontendIF instantiate(String handle, String klass) {
    try {
      Class theklass = Class.forName(klass);
      Class[] paramdefs = new Class[] { java.lang.String.class };
      Constructor construct = theklass.getConstructor(paramdefs);
      Object[] params = new Object[] { handle };
      return (ClientFrontendIF) construct.newInstance(params);
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
}
