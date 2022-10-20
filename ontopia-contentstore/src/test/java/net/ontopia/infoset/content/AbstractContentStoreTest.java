/*
 * #!
 * Ontopia Content Store
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.infoset.content;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.ontopia.utils.StringUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractContentStoreTest {
  protected ContentStoreIF store;
  
  // protocol expected from subclasses:
  //  - setUp must populate the store attribute with an empty store
  //  - tearDown must close it cleanly

  // --- Test methods

  @Test
  public void testEmptyStore() throws ContentStoreException {
    Assert.assertTrue("Empty store contains key 0", !store.containsKey(0));
    Assert.assertTrue("Empty store contains key 1", !store.containsKey(1));

    try {
      store.get(0);
      Assert.fail("Empty store allowed get of key 0");
    } catch (ContentStoreException e) {}

    try {
      store.get(1);
      Assert.fail("Empty store allowed get of key 1");
    } catch (ContentStoreException e) {}

    Assert.assertTrue("Empty store returned true on removal of key 0",
               !store.remove(0));
    Assert.assertTrue("Empty store returned true on removal of key 1",
               !store.remove(1));
  }

  @Test
  public void testAddOneEntry() throws ContentStoreException, IOException {
    String CONTENT = "content of first entry";
    int key = store.add(getStream(CONTENT.getBytes()));

    Assert.assertTrue("Entry just added not in store (" + key + ")",
               store.containsKey(key));

    compare(key, CONTENT.getBytes());

    Assert.assertTrue("Entry just added could not be removed (" + key + ")",
               store.remove(key));

    Assert.assertTrue("Entry just removed still in store (" + key + ")",
               !store.containsKey(key));

    Assert.assertTrue("Entry could be removed twice (" + key + ")",
               !store.remove(key));    
  }

  @Test
  public void testUnusualBytes() throws ContentStoreException, IOException {
    byte[] CONTENT = new byte[256];
    for (int ix = 0; ix < CONTENT.length; ix++) {
      CONTENT[ix] = (byte) ix;
    }
    
    int key = store.add(getStream(CONTENT));
    compare(key, CONTENT);
  }
  
  @Test
  public void testProbabilistic() throws ContentStoreException, IOException {
    Map entries = new HashMap();

    final int ADD_NEW = 0;
    final int CHECK_CONTENT = 1;
    final int DELETE = 2;
    final int CHECK_PRESENCE = 3;

    Random random = new Random();
    
    for (int ix = 0; ix < 1000; ix++) {
      int operation = random.nextInt(4);
      if (entries.isEmpty()) {
        operation = ADD_NEW;
      }

      switch (operation) {
      case ADD_NEW:
        byte[] content = StringUtils.makeRandomId(50).getBytes();
        int key = store.add(new ByteArrayInputStream(content), 50);
        entries.put(key, content);
        break;
      case CHECK_CONTENT:
        key = chooseRandomKey(entries);
        compare(key, (byte[]) entries.get(key));
        break;
      case DELETE:
        key = chooseRandomKey(entries);
        Assert.assertTrue("Existing entry could not be deleted " + key,
                   store.remove(key));
        entries.remove(key);
        break;
      case CHECK_PRESENCE:
        key = chooseRandomKey(entries) + 1;
        Assert.assertTrue("Key presence does not match double-checking",
                   entries.containsKey(key) == store.containsKey(key));
      }
    }
  }

  // --- Helpers

  private int chooseRandomKey(Map entries) {
    List keys = new ArrayList(entries.keySet());
    return ((Integer) keys.get((int) (Math.random() * keys.size()))).intValue();
  }
  
  protected ContentInputStream getStream(byte[] content) throws IOException {
    return new ContentInputStream(new ByteArrayInputStream(content), content.length);
  }

  protected void compare(int key, byte[] CONTENT)
    throws ContentStoreException, IOException {
    ContentInputStream cis = store.get(key);
    byte[] content = IOUtils.toByteArray(cis);
    cis.close();

    Assert.assertTrue("Returned content of wrong length",
               content.length == CONTENT.length);
    
    for (int ix = 0; ix < CONTENT.length; ix++) {
      Assert.assertTrue("Returned content differs from original in byte " + ix,
                 CONTENT[ix] == content[ix]);
    }
  }
}
