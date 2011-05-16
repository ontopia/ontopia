
// $Id: AbstractContentStoreTest.java,v 1.2 2004/01/28 13:32:17 larsga Exp $

package net.ontopia.infoset.content;

import java.io.*;
import java.util.*;
import net.ontopia.utils.StreamUtils;
import net.ontopia.utils.StringUtils;
import junit.framework.TestCase;

public abstract class AbstractContentStoreTest extends TestCase {
  protected ContentStoreIF store;
  
  public AbstractContentStoreTest(String name) {
    super(name);
  }

  // protocol expected from subclasses:
  //  - setUp must populate the store attribute with an empty store
  //  - tearDown must close it cleanly

  // --- Test methods

  public void testEmptyStore() throws ContentStoreException {
    assertTrue("Empty store contains key 0", !store.containsKey(0));
    assertTrue("Empty store contains key 1", !store.containsKey(1));

    try {
      store.get(0);
      fail("Empty store allowed get of key 0");
    } catch (ContentStoreException e) {}

    try {
      store.get(1);
      fail("Empty store allowed get of key 1");
    } catch (ContentStoreException e) {}

    assertTrue("Empty store returned true on removal of key 0",
               !store.remove(0));
    assertTrue("Empty store returned true on removal of key 1",
               !store.remove(1));
  }

  public void testAddOneEntry() throws ContentStoreException, IOException {
    String CONTENT = "content of first entry";
    int key = store.add(getStream(CONTENT.getBytes()));

    assertTrue("Entry just added not in store (" + key + ")",
               store.containsKey(key));

    compare(key, CONTENT.getBytes());

    assertTrue("Entry just added could not be removed (" + key + ")",
               store.remove(key));

    assertTrue("Entry just removed still in store (" + key + ")",
               !store.containsKey(key));

    assertTrue("Entry could be removed twice (" + key + ")",
               !store.remove(key));    
  }

  public void testUnusualBytes() throws ContentStoreException, IOException {
    byte[] CONTENT = new byte[256];
    for (int ix = 0; ix < CONTENT.length; ix++)
      CONTENT[ix] = (byte) ix;
    
    int key = store.add(getStream(CONTENT));
    compare(key, CONTENT);
  }
  
  public void testProbabilistic() throws ContentStoreException, IOException {
    Map entries = new HashMap();

    final int ADD_NEW = 0;
    final int CHECK_CONTENT = 1;
    final int DELETE = 2;
    final int CHECK_PRESENCE = 3;

    Random random = new Random();
    
    for (int ix = 0; ix < 1000; ix++) {
      int operation = random.nextInt(4);
      if (entries.isEmpty())
        operation = ADD_NEW;

      switch (operation) {
      case ADD_NEW:
        byte[] content = StringUtils.makeRandomId(50).getBytes();
        int key = store.add(new ByteArrayInputStream(content), 50);
        entries.put(new Integer(key), content);
        break;
      case CHECK_CONTENT:
        key = chooseRandomKey(entries);
        compare(key, (byte[]) entries.get(new Integer(key)));
        break;
      case DELETE:
        key = chooseRandomKey(entries);
        assertTrue("Existing entry could not be deleted " + key,
                   store.remove(key));
        entries.remove(new Integer(key));
        break;
      case CHECK_PRESENCE:
        key = chooseRandomKey(entries) + 1;
        assertTrue("Key presence does not match double-checking",
                   entries.containsKey(new Integer(key)) == store.containsKey(key));
      }
    }
  }

  // --- Helpers

  private int chooseRandomKey(Map entries) {
    List keys = new ArrayList(entries.keySet());
    return ((Integer) keys.get((int) (Math.random() * keys.size()))).intValue();
  }
  
  protected ContentInputStream getStream(byte[] content) throws IOException {
    return new ContentInputStream(new ByteArrayInputStream(content), (int) content.length);
  }

  protected void compare(int key, byte[] CONTENT)
    throws ContentStoreException, IOException {
    ContentInputStream cis = store.get(key);
    byte[] content = StreamUtils.read(cis, cis.getLength());

    assertTrue("Returned content of wrong length",
               content.length == CONTENT.length);
    
    for (int ix = 0; ix < CONTENT.length; ix++)
      assertTrue("Returned content differs from original in byte " + ix,
                 CONTENT[ix] == content[ix]);
  }
}
