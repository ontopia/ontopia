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

/**
 * INTERNAL: Helper class for storing a collection of TopicIF objects
 * for scope specification as well as a flag which indicates that the
 * unconstrained scope is allowed.
 */
public class ScopeStorage {

  protected Collection scope;
  protected boolean unconstrainedIncluded;
  
  public ScopeStorage(Collection scope, boolean unconstrainedIncluded) {
    this.scope = scope;
    this.unconstrainedIncluded = unconstrainedIncluded;
  }

  public Collection getScope() {
    return scope;
  }

  public void setScope(Collection scope) {
    this.scope = scope;
  }

  public boolean isUnconstrainedIncluded() {
    return unconstrainedIncluded;
  }

  public void setUnconstrainedIncluded(boolean unconstrainedIncluded) {
    this.unconstrainedIncluded = unconstrainedIncluded;
  }
  
}
