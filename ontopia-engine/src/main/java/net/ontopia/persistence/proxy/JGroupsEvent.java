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

/**
 * INTERNAL: 
 */

public class JGroupsEvent implements Externalizable {

  public int eventType;
  public Object value;
  public IdentityIF namespace;
  public int field;
  
  public String toString() {
    return eventType + " " + namespace + " " + value + " " + field;
  }

  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeInt(eventType);
    out.writeObject(namespace);
    out.writeObject(value);
    out.writeInt(field);
  }

  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    eventType = in.readInt();
    namespace = (IdentityIF)in.readObject();
    value = in.readObject();
    field = in.readInt();
  }
  
}
