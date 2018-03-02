/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2017 The Ontopia Project
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

package net.ontopia.topicmaps.impl.basic.index;

import java.util.Collection;
import java.util.Collections;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.index.IdentifierIndexIF;
import net.ontopia.topicmaps.impl.basic.SubjectIdentityCache;
import net.ontopia.topicmaps.impl.utils.BasicIndex;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

public class IdentifierIndex extends BasicIndex implements IdentifierIndexIF {

  private final SubjectIdentityCache sicache;

  public IdentifierIndex(SubjectIdentityCache sicache) {
    this.sicache = sicache;
  }

  @Override
  public Collection<LocatorIF> getItemIdentifiers() {
    return Collections.unmodifiableCollection(sicache.getItemIdentifiers());
  }

  @Override
  public Collection<LocatorIF> getItemIdentifiersByPrefix(String prefix) {
    return Collections.unmodifiableCollection(
            CollectionUtils.select(getItemIdentifiers(), new LocatorPrefixedPredicate(prefix))
    );
  }

  @Override
  public Collection<LocatorIF> getSubjectIdentifiers() {
    return Collections.unmodifiableCollection(sicache.getSubjectIdentifiers());
  }

  @Override
  public Collection<LocatorIF> getSubjectIdentifiersByPrefix(String prefix) {
    return Collections.unmodifiableCollection(
            CollectionUtils.select(getSubjectIdentifiers(), new LocatorPrefixedPredicate(prefix))
    );
  }

  private class LocatorPrefixedPredicate implements Predicate<LocatorIF> {

    private final String prefix;

    public LocatorPrefixedPredicate(String prefix) {
      this.prefix = prefix;
    }

    @Override
    public boolean evaluate(LocatorIF locator) {
      return locator.getAddress().startsWith(prefix);
    }
  }
}
