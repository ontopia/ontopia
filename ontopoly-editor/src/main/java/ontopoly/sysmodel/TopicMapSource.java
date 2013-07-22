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


import net.ontopia.topicmaps.entry.TopicMapSourceIF;
import ontopoly.OntopolyContext;

/**
 * INTERNAL: Represents a source in the Ontopoly topic map repository.
 * Only sources which support create are represented. This class only
 * exists so the repository can be notified when new topic maps are
 * created.
 */
public class TopicMapSource implements Serializable {

  private String sourceId;

  protected TopicMapSource() {
  }

  /**
   * INTERNAL: Creates a new source wrapper.
   */
  public TopicMapSource(String sourceId) {
    this.sourceId = sourceId;
  }

  /**
   * INTERNAL: Returns the ID of the source.
   */
  public String getId() {
    return getSource().getId();
  }

  /**
   * INTERNAL: Returns the display title of the source.
   */
  public String getTitle() {
    return getSource().getTitle();
  }

  protected TopicMapSourceIF getSource() {
    return OntopolyContext.getOntopolyRepository().getTopicMapRepository().getSourceById(sourceId);
  }

}
