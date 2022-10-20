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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import net.ontopia.topicmaps.core.TopicMapIF;
import org.apache.commons.io.IOUtils;

/**
 * INTERNAL: Content store implementation that saves everything in memory.
 */
public class InMemoryContentStore implements ContentStoreIF {
  private static InMemoryContentStore store;

  private int nextKey;
  private Map<Integer, byte[]> content;

  // --- Static interface

  public static ContentStoreIF getInstance(TopicMapIF topicmap) {
    if (store == null) {
      store = new InMemoryContentStore();
    }
    return store;
  }

  // --- ContentStoreIF implementation

  public InMemoryContentStore() {
    content = new HashMap<Integer, byte[]>();
  }

  @Override
  public boolean containsKey(int key) throws ContentStoreException {
    return content.containsKey(key);
  }

  @Override
  public ContentInputStream get(int key) throws ContentStoreException {
    byte[] data = content.get(key);
    if (data == null) {
      throw new ContentStoreException("No content for key " + key);
    }
    return new ContentInputStream(new ByteArrayInputStream(data), data.length);
  }

  @Override
  public int add(ContentInputStream data) throws ContentStoreException {
    return add(data, data.getLength());
  }

  @Override
  public int add(InputStream data, int length) throws ContentStoreException {
    try {
      content.put(nextKey, IOUtils.toByteArray(data));
      return nextKey++;
    } catch (IOException e) {
      throw new ContentStoreException(e);
    }
  }

  @Override
  public boolean remove(int key) throws ContentStoreException {
    Integer okey = Integer.valueOf(key);
    boolean result = content.containsKey(okey);
    content.remove(okey);
    return result;
  }

  @Override
  public void close() throws ContentStoreException {
    content = null;
  }

}
