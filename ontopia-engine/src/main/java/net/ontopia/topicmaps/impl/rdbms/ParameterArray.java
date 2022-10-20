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

package net.ontopia.topicmaps.impl.rdbms;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Object wrapper class for query parameter array. This
 * wrapper class is purely used to make it possible to use arrays as
 * map keys.<p>
 */
public class ParameterArray implements Externalizable {
  protected Object[] params;
  
  public ParameterArray() {
    // used with serialization only
  }

  public ParameterArray(Object[] params) {
    this.params = params;
  }

  public Object[] getArray() {
    return params;
  }

  @Override
  public String toString() {
    return "PA:" + java.util.Arrays.asList(params);
  }

  @Override
  public int hashCode() {
    int result = 1;
    for (int i=0; i < params.length; i++) {
      result = 31*result + (params[i] == null ? 0 : params[i].hashCode());
    }
    return result;
  }

  @Override
  public boolean equals(Object other) {
    ParameterArray o = (ParameterArray)other;
    for (int i=0; i < params.length; i++) {
      if (params[i] == null) {
        if (o.params[i] == null) {
          continue;
        } else {
          return false;
        }
      } else {
        if (o.params[i] == null) {
          return false;
        } else
          if (!params[i].equals(o.params[i])) {
            return false;
        }
      }
    }
    return true;
  }

  // ---------------------------------------------------------------------------
  // Externalization
  // ---------------------------------------------------------------------------
  
  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(params);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    params = (Object[])in.readObject();
  }

  @Override
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      // Ignore
      throw new OntopiaRuntimeException(e);
    }
  }
}
