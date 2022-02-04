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

import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.utils.DeciderIF;

/**
 * INTERNAL: This decider accepts all objects in the unconstrained
 * scope, letting a sub-decider rule for object not in the unconstrained
 * scope. Useful for making other deciders accept objects in the
 * unconstrained scope.
 * @since 1.1
 */

@Deprecated
public class UnconstrainedScopeDecider implements DeciderIF<ScopedIF> {
  protected DeciderIF<? super ScopedIF> subdecider;

  public UnconstrainedScopeDecider(DeciderIF<? super ScopedIF> subdecider) {
    this.subdecider = subdecider;
  }
  
  @Override
  public boolean ok(ScopedIF scoped) {
    return scoped.getScope().isEmpty() ||
           subdecider.ok(scoped);
  }

}





