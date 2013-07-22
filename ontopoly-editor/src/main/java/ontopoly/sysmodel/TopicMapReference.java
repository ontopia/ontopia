/*
 * #!
 * Ontopoly Editor
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

package ontopoly.sysmodel;

import java.io.Serializable;

import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import ontopoly.OntopolyContext;

/**
 * INTERNAL: Represents a topic map in the Ontopoly topic map
 * repository.
 */
public class TopicMapReference implements Serializable {

  protected String referenceId;

  protected TopicMapReference() {
  }

  /**
   * INTERNAL: Creates a reference to a non-Ontopoly topic map.
   */
  TopicMapReference(String referenceId) {
    this.referenceId = referenceId;
  }

  /**
   * INTERNAL: Creates a reference to an Ontopoly topic map. The
   * reference parameter will be null if the topic map does not
   * actually exist.
   */
  TopicMapReference(TopicMapReferenceIF reference) {
    this.referenceId = reference.getId();
  }

  protected TopicMapReferenceIF getReference() {
    return OntopolyContext.getOntopolyRepository().getTopicMapRepository().getReferenceByKey(referenceId);
  }

  /**
   * INTERNAL: Returns the ID of the reference (like 'foo.xtm').
   */
  public String getId() {
    return referenceId;
  }

  /**
   * INTERNAL: Returns the name of the topic map. For non-Ontopoly
   * topic maps this will be the same as the ID.
   */
  public String getName() {
    TopicMapReferenceIF reference = getReference();
    return reference == null ? referenceId : reference.getTitle();
  }

  /**
   * INTERNAL: Tests if the topic map is actually in the repository.
   */
  public boolean isPresent() {
    return getReference() != null;
  }

  @Override
  public boolean equals(Object o) {
      if (o instanceof TopicMapReference) {
        return ((TopicMapReference)o).referenceId.equals(referenceId);
      }
      return false;
  }

  @Override
  public int hashCode() {
      return referenceId.hashCode();
  }

  @Override
  public String toString() {
    return "<TopicMapReference " + getId() + ">";
  }

}
