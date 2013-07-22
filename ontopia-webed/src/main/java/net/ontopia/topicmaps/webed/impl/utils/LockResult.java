/*
 * #!
 * Ontopia Webed
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
package net.ontopia.topicmaps.webed.impl.utils;

import java.util.Collection;
import java.util.ResourceBundle;

import net.ontopia.utils.OntopiaRuntimeException;

public class LockResult {
  private Collection unlockable;
  private NamedLock namedLock;
  private String name;
  private ResourceBundle bundle;
  
  public LockResult(Collection unlockable, NamedLock namedLock, String name,
                    ResourceBundle bundle) {
    this.unlockable = unlockable;
    this.namedLock = namedLock;
    this.name = name;
    this.bundle = bundle;
  }
  
  public Collection getUnlockable() {
    return unlockable;
  }
  
  public NamedLock getNamedLock() {
    return namedLock;
  }
  
  public String getLockUserIdLocalized() {    
    if (namedLock == null)
      // throw new OntopiaRuntimeException("Attempted to get name of null NamedLock.");
      return bundle.getString("unknown");
    if (namedLock.getUser() == null)
      throw new OntopiaRuntimeException("No user found on NamedLock");

    String lockName = namedLock.getUser().getId();
    if (lockName.equals("defaultUser"))
      lockName = bundle.getString("unknown");
    return lockName;
  }
  
  public String getName() {
    return name;
  }
}
