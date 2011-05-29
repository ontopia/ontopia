
package net.ontopia.infoset.content;

import java.io.InputStream;

/**
 * INTERNAL: A simple content store interface that stores chunks of
 * data associated with an integer key. The underlying implementation
 * is responsible for creating the keys.
 */
public interface ContentStoreIF {

  /**
   * INTERNAL: Returns true if the content store contains an entry with
   * the specified key.
   */
  public boolean containsKey(int key) throws ContentStoreException;

  /**
   * INTERNAL: Gets the data value associated with the specified key.
   */
  public ContentInputStream get(int key) throws ContentStoreException;
  
  /**
   * INTERNAL: Creates an entry for the specified data value.
   */
  public int add(ContentInputStream data) throws ContentStoreException;
  
  /**
   * INTERNAL: Creates an entry for the specified data value.
   */
  public int add(InputStream data, int length) throws ContentStoreException;

  /**
   * INTERNAL: Removes the entry associated with the key. If the key
   * is not present the call has no effect.
   *
   * @return true if the key was present; false otherwise
   */
  public boolean remove(int key) throws ContentStoreException;

  /**
   * INTERNAL: Closes the content store. This allows all internal
   * resources to be released.
   */
  public void close() throws ContentStoreException;
  
}
