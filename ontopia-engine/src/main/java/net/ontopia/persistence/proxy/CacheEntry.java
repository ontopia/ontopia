/*
 * #!
 * Ontopia Engine
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

package net.ontopia.persistence.proxy;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;

/**
 * INTERNAL: Class used by storage caches to hold field values for a
 * single object.
 */

public class CacheEntry implements Externalizable {

  private static final long serialVersionUID = 9010124046386798540L;

  protected static final int[] MASKS;

  static {
    int[] masks = new int[32];
    for (int i=0; i < 32; i++) {
      masks[i] = (int)Math.pow(2, i);
    }
    MASKS = masks;
  }

  private IdentityIF identity; // identity used to key the cache entry
  private int lflags; // is field specified
  private Object[] values; // field values
  
  public CacheEntry() {
    // no-args used for deserialization.
  }
  
  public CacheEntry(IdentityIF identity, int fields) {
    this.identity = identity;
    this.values = new Object[fields];
  }

  public IdentityIF getIdentity() {
    return identity;
  }
  
  public synchronized boolean contains(int field) {
    return ((lflags & MASKS[field]) == MASKS[field]);
  }
  
  public synchronized Object getValue(int field) {
    return values[field];
  }
  
  public synchronized void setValue(int field, Object value) {
    lflags |= MASKS[field]; // set flag    
    values[field] = value;
  }

  public synchronized void unsetValue(int field, Object value) {
    lflags &= ~(MASKS[field]); // unset flag
    values[field] = null;
  }

  public synchronized void clear() {
    // NOTE: clear fields, but keep identity
    // reset lflags
    lflags = 0;
    for (int i=0; i < values.length; i++) {
      // clear values
      values[i] = null;
    }
  }
  
  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(identity);
    out.writeInt(lflags);
    out.writeObject(values);
  }
  
  @Override
  public synchronized void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    identity = (IdentityIF)in.readObject();
    lflags = in.readInt();
    values = (Object[])in.readObject();
  }

  @Override
  public synchronized String toString() {
    return "<CacheEntry " + identity + " | " + lflags + "|" + (values == null ? null : Arrays.asList(values).toString() + ">");
  }
  
}
