/*
 * #!
 * Ontopia Realm
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

package net.ontopia.topicmaps.nav2.realm;

import java.io.Serializable;
import java.security.Principal;
import java.util.Objects;

public class RolePrincipal implements Principal, Serializable {

  public static final long serialVersionUID = 502L;
  
  private String name;

  public RolePrincipal(String name) {
    Objects.requireNonNull(name, "illegal null input");
    this.name = name;
  }
  
  @Override
  public String getName() {
    return name;
  }
  
  @Override
  public String toString() {
    return("RolePrincipal:  " + name);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null)
      return false;
    
    if (this == o)
      return true;
    
    if (!(o instanceof RolePrincipal))
      return false;
    
    RolePrincipal that = (RolePrincipal) o;
    return getName().equals(that.getName());
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
