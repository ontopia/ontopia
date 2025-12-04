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

package net.ontopia.persistence.proxy.jgroups;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import net.ontopia.persistence.proxy.ClusterIF;
import net.ontopia.persistence.proxy.IdentityIF;

public class JGroupsEvent implements Externalizable {

  public int eventType;
  public Object value;
  public IdentityIF namespace;
  public int field;

  public JGroupsEvent() {

  }

  public JGroupsEvent(int eventType, Object value, IdentityIF namespace, int field) {
    this.eventType = eventType;
    this.value = value;
    this.namespace = namespace;
    this.field = field;
  }

  @Override
  public String toString() {
    return eventType + " " + namespace + " " + value + " " + field;
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeInt(eventType);
    out.writeObject(namespace);
    out.writeObject(value);
    out.writeInt(field);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    eventType = in.readInt();
    namespace = (IdentityIF)in.readObject();
    value = in.readObject();
    field = in.readInt();
  }

  public static JGroupsEvent evictIdentity(IdentityIF identity) {
    return new JGroupsEvent(ClusterIF.DATA_CACHE_IDENTITY_EVICT, identity, null, 0);
  }

  public static JGroupsEvent evictFields(IdentityIF identity) {
    return new JGroupsEvent(ClusterIF.DATA_CACHE_FIELDS_EVICT, identity, null, 0);
  }

  public static JGroupsEvent evictField(IdentityIF identity, int field) {
    return new JGroupsEvent(ClusterIF.DATA_CACHE_FIELD_EVICT, identity, null, field);
  }

  public static JGroupsEvent clear() {
    return new JGroupsEvent(ClusterIF.DATA_CACHE_CLEAR, null, null, 0);
  }

  // event type is same as cache type
  public static JGroupsEvent evictCache(IdentityIF namespace, int cacheType, Object key) {
    return new JGroupsEvent(cacheType, key, namespace, 0);
  }

  public static JGroupsEvent evictCache(IdentityIF namespace, int cacheType, Collection keys) {
    return new JGroupsEvent(cacheType, keys, namespace, 0);
  }

  // event type is same as cache type + 1
  public static JGroupsEvent clearCache(IdentityIF namespace, int cacheType) {
    return new JGroupsEvent(cacheType + 1, null, namespace, 0);
  }
}
