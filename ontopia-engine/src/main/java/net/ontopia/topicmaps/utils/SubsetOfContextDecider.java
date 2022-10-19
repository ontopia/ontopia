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

package net.ontopia.topicmaps.utils;

import java.util.Collection;
import java.util.function.Predicate;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;

/**
 * INTERNAL: Decider that decides whether the ScopedIF's scope is a
 * subset of the user context or not.</p>
 *
 * See {@link net.ontopia.topicmaps.utils.ScopeUtils} for
 * more information.
 */

public class SubsetOfContextDecider implements Predicate<ScopedIF> {
  
  protected Collection<TopicIF> context;
  
  public SubsetOfContextDecider(Collection<TopicIF> context) {
    this.context = context;
  }
  
  @Override
  public boolean test(ScopedIF scoped) {
    return ScopeUtils.isSubsetOfContext(scoped, context);
  }

}





