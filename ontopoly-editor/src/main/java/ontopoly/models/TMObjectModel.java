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
package ontopoly.models;


import java.util.Objects;
import net.ontopia.topicmaps.core.TMObjectIF;
import ontopoly.OntopolyContext;
import ontopoly.model.TopicMap;
import org.apache.wicket.model.LoadableDetachableModel;

public class TMObjectModel extends LoadableDetachableModel<TMObjectIF> {

  private static final long serialVersionUID = -8374148020034895666L;

  private String topicMapId;

  private String objectId;

  public TMObjectModel(String topicMapId, TMObjectIF object) {
    super(object);
    if (object != null) {
      this.topicMapId = topicMapId;
      this.objectId = object.getObjectId();
    }
  }

  public TMObjectModel(String topicMapId, String objectId) {
    Objects.requireNonNull(topicMapId, "topicMapId parameter cannot be null.");
    Objects.requireNonNull(objectId, "objectId parameter cannot be null.");
    this.topicMapId = topicMapId;
    this.objectId = objectId;
  }
  
  public TMObjectIF getTMObject() {
    return (TMObjectIF)getObject();
  }
  
  @Override
  protected TMObjectIF load() {
    if (topicMapId == null) return null;
    TopicMap tm = OntopolyContext.getTopicMap(topicMapId);
    // FIXME: should probably complain if object not found
    return (TMObjectIF) tm.getTopicMapIF().getObjectById(objectId);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof TMObjectModel)
      return Objects.equals(topicMapId, ((TMObjectModel)obj).topicMapId) &&
        Objects.equals(objectId, ((TMObjectModel)obj).objectId);
    else
      return false;
  }

  @Override
  public int hashCode() {
    return topicMapId.hashCode() + objectId.hashCode();
  }
  
}
