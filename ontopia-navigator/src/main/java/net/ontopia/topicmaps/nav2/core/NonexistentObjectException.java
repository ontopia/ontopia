
package net.ontopia.topicmaps.nav2.core;

/**
 * INTERNAL: This exception is thrown when the navigator framework is
 * passed a topic map object ID that does not belong to any actual
 * topic map object. It is thrown by &lt;logic:context> and
 * &lt;tm:lookup parameter="..."> when they are passed object IDs that
 * are invalid.
 *
 * @since 2.0
 */
public class NonexistentObjectException extends RuntimeException {
  private String objectid;
  private String topicmapid;
  
  /**
   * INTERNAL: Constructor with empty error message.
   * @param objectid The object ID that did not resolve to an object.
   * @param topicmapid The ID of the topic map this was attempted on.
   */
  public NonexistentObjectException(String objectid, String topicmapid) {
    super("Topic object with ID '" + objectid + "' not found in topicmap '" +
          topicmapid + "', maybe wrong object ID.");
    this.objectid = objectid;
    this.topicmapid = topicmapid;
  }

  /**
   * INTERNAL: Returns the object id that caused the exception.
   */
  public String getObjectId() {
    return objectid;
  }

  /**
   * INTERNAL: Returns the ID of the topic map where the object ID did
   * not resolve.
   */
  public String getTopicMapId() {
    return topicmapid;
  }
  
}
