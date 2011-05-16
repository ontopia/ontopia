
// $Id: InMemoryContentStore.java,v 1.3 2004/01/27 22:11:54 larsga Exp $

package net.ontopia.infoset.content;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;
import net.ontopia.utils.StreamUtils;
import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * INTERNAL: Content store implementation that saves everything in memory.
 */
public class InMemoryContentStore implements ContentStoreIF {
  private static InMemoryContentStore store;

  private int nextKey;
  private Map<Integer, byte[]> content;

  // --- Static interface

  public static ContentStoreIF getInstance(TopicMapIF topicmap) {
    if (store == null)
      store = new InMemoryContentStore();
    return store;
  }

  // --- ContentStoreIF implementation

  public InMemoryContentStore() {
    content = new HashMap<Integer, byte[]>();
  }

  public boolean containsKey(int key) throws ContentStoreException {
    return content.containsKey(new Integer(key));
  }

  public ContentInputStream get(int key) throws ContentStoreException {
    byte[] data = content.get(new Integer(key));
    if (data == null)
      throw new ContentStoreException("No content for key " + key);
    return new ContentInputStream(new ByteArrayInputStream(data), data.length);
  }

  public int add(ContentInputStream data) throws ContentStoreException {
    return add(data, data.getLength());
  }

  public int add(InputStream data, int length) throws ContentStoreException {
    try {
      content.put(new Integer(nextKey), StreamUtils.read(data, length));
      return nextKey++;
    } catch (IOException e) {
      throw new ContentStoreException(e);
    }
  }

  public boolean remove(int key) throws ContentStoreException {
    Integer okey = new Integer(key);
    boolean result = content.containsKey(okey);
    content.remove(okey);
    return result;
  }

  public void close() throws ContentStoreException {
    content = null;
  }

}
