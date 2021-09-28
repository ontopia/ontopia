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

package net.ontopia.topicmaps.utils.deciders;

import java.util.Collection;

import net.ontopia.utils.DeciderIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;

/**
 * INTERNAL: Decider that decides whether the scoped object is broader
 * than the context. The context must be identical or a subset of the
 * scopes. If there is no context, there can be no "ok".
 */
public class WithinScopeDecider implements DeciderIF<ScopedIF> {
  
  protected Collection<TopicIF> context;

  public WithinScopeDecider(Collection<TopicIF> context) {
    this.context = context;
  }

  public boolean ok(ScopedIF scoped) { 
    if (context == null || context.isEmpty())
      return false;
    Collection<TopicIF> objscope = scoped.getScope();
    if (objscope.containsAll(context))
      return true;
    return false;
  }
  
}





