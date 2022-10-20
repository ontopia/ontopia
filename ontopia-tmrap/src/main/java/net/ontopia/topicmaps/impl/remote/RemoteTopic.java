/*
 * #!
 * Ontopia TMRAP
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

package net.ontopia.topicmaps.impl.remote;

import java.util.Collection;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.impl.basic.Topic;
import net.ontopia.topicmaps.impl.basic.TopicMap;
import net.ontopia.topicmaps.impl.utils.ObjectStrings;
import net.ontopia.topicmaps.utils.tmrap.RemoteTopicIndex;
import net.ontopia.utils.OntopiaRuntimeException;
  
/**
 * INTERNAL: The remote topic implementation. Checks that it is loaded 
 * and then delegates calls to the super class.
 */
public class RemoteTopic extends Topic {

  /** Indicates if this topic has been fully loaded from 
      the remote topic map to which it belongs. */
  private boolean isLoaded;
  
  /** References a topic when this topic is removed through merging.*/
  private RemoteTopic realTopic;
  
  RemoteTopic(TopicMap tm) {
    super(tm);    
  }
  
  // Special to remote topic -----------------------------------------------------
  
  public boolean isLoaded() {
    return isLoaded;
  }

  public void setLoaded(boolean isLoaded) {
    this.isLoaded = isLoaded;
  }
  
  protected void setRealTopic(RemoteTopic topic) {
    realTopic = topic;  
  }

  public TopicIF getRealTopic() {
    return realTopic;
  }
  
  public synchronized void checkLoad() {
    if (!isLoaded() && isConnected()) {
      load();
    }
  }
  
  /** Attempts to load topic from remote topic map.*/
  private synchronized void load() {
    RemoteTopicMapStore store = (RemoteTopicMapStore) getTopicMap().getStore();
    RemoteTopicIndex tindex = store.getTopicIndex();

    if (getSubjectIdentifiers().isEmpty() &&
        getItemIdentifiers().isEmpty() &&
        getSubjectLocators().isEmpty()) {
      throw new OntopiaRuntimeException("Can't load topic without identity");
    }
    
    // isLoaded gets set from elsewhere, strangely
    tindex.getTopics(getSubjectIdentifiers(), getItemIdentifiers(),
                     getSubjectLocators());
  }

  // ----------------------------------------------------------------------------
  // TopicIF implementation
  // ----------------------------------------------------------------------------
  
  @Override
  public Collection<LocatorIF> getSubjectLocators() {
    if (realTopic!=null) {
      return realTopic.getSubjectLocators();
    } else {
      return super.getSubjectLocators();
    }
  }

  @Override
  public void addSubjectLocator(LocatorIF subject_locator) throws ConstraintViolationException {
    if (realTopic != null) {
      realTopic.addSubjectLocator(subject_locator);
    } else {
      super.addSubjectLocator(subject_locator);
    }
  }

  @Override
  public void removeSubjectLocator(LocatorIF subject_locator) {
    checkLoad();
    if (realTopic != null) {
      realTopic.removeSubjectLocator(subject_locator);
    } else {
      super.removeSubjectLocator(subject_locator);
    }      
  }
  
  @Override
  public Collection<LocatorIF> getSubjectIdentifiers() {
    if (realTopic!=null) {
      return realTopic.getSubjectIdentifiers();
    } else {
      return super.getSubjectIdentifiers();
    }
  }

  @Override
  public void addSubjectIdentifier(LocatorIF subject_indicator) throws ConstraintViolationException {
    if (realTopic != null) {
      realTopic.addSubjectIdentifier(subject_indicator);
    } else {
      super.addSubjectIdentifier(subject_indicator);
    }
  }

  @Override
  public void removeSubjectIdentifier(LocatorIF subject_indicator) {
    checkLoad();
    if (realTopic != null) {
      realTopic.removeSubjectIdentifier(subject_indicator);
    } else {
      super.removeSubjectIdentifier(subject_indicator);
    }      
  }
  
  @Override
  public Collection<TopicNameIF> getTopicNames() {
    checkLoad();
    if (realTopic != null) {
      return realTopic.getTopicNames();
    } else {
      return super.getTopicNames();
    }
  }

  @Override
  protected void addTopicName(TopicNameIF _basename) {
    checkLoad();
    if (realTopic!=null) {
      realTopic.addTopicName(_basename);
    } else {
      super.addTopicName(_basename);
    }
  }

  @Override
  protected void removeTopicName(TopicNameIF _basename) {
    checkLoad();
    if (realTopic!=null) {
      realTopic.removeTopicName(_basename);
    } else {
      super.removeTopicName(_basename);
    }
  }
  
  @Override
  public Collection<OccurrenceIF> getOccurrences() {
    checkLoad();
    if (realTopic!=null) {
      return realTopic.getOccurrences();
    } else {
      return super.getOccurrences();
    }
  }

  @Override
  protected void addOccurrence(OccurrenceIF _occurrence) {
    checkLoad();
    if (realTopic!=null) {
      realTopic.addOccurrence(_occurrence);
    } else {
      super.addOccurrence(_occurrence);
    }
  }

  @Override
  protected void removeOccurrence(OccurrenceIF _occurrence) {
    checkLoad();
    if (realTopic!=null) {
      realTopic.removeOccurrence(_occurrence);
    } else {
      super.removeOccurrence(_occurrence);
    }
  }

  @Override
  public Collection<AssociationRoleIF> getRoles() {
    checkLoad();
    if (realTopic!=null) {
      return realTopic.getRoles();
    } else {
      return super.getRoles();
    }
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public Collection<TopicIF> getTypes() {
    checkLoad();
    if (realTopic != null) {
      return realTopic.getTypes();
    } else {
      return super.getTypes();
    }
  }

  @Override
  public void addType(TopicIF type) {
    // this gets called before we know the identity of the topic
    if (realTopic != null) {
      realTopic.addType(type);
    } else {
      super.addType(type);
    }
  }

  @Override
  public void removeType(TopicIF type) {
    checkLoad();
    if (realTopic!=null) {
      realTopic.removeType(type);
    } else {
      super.removeType(type);
    }
  }
  
  // -----------------------------------------------------------------------------
  // Misc. methods
  // -----------------------------------------------------------------------------

  @Override
  public String toString() {
    if (realTopic!=null) {
      return "{" + realTopic.toString() + "}";
    } else {    
      return ObjectStrings.toString("remote.Topic", (TopicIF)this) + ":" + isLoaded();
    }    
  }

  // tm object implementation
  
  @Override
  public String getObjectId() {
    if (realTopic != null) {
      return realTopic.getObjectId();
    } else {
      return super.getObjectId();
    }
  }

  @Override
  public boolean isReadOnly() {
    if (realTopic!=null) {
      return realTopic.isReadOnly();
    } else {    
      return super.isReadOnly();
    }
  }

  @Override
  public TopicMapIF getTopicMap() {
    if (realTopic!=null) {
      return realTopic.getTopicMap();
    } else {    
      return super.getTopicMap();
    }  
  }

  @Override
  public Collection<LocatorIF> getItemIdentifiers() {
    if (realTopic!=null) {
      return realTopic.getItemIdentifiers();
    } else {    
      return super.getItemIdentifiers();
    }     
  }

  @Override
  public void addItemIdentifier(LocatorIF source_locator) throws ConstraintViolationException {
    if (realTopic != null) {
      realTopic.addItemIdentifier(source_locator);
    } else {
      super.addItemIdentifier(source_locator);
    }
  }
  
  @Override
  public void removeItemIdentifier(LocatorIF source_locator) {
    if (realTopic != null) {
      realTopic.removeItemIdentifier(source_locator);
    } else {
      super.removeItemIdentifier(source_locator);
    }
  }
  
  @Override
  public synchronized void merge(TopicIF topic) {
    if (realTopic != null) {
      throw new OntopiaRuntimeException("THIS SHOULD NEVER HAPPEN.");
    }

    RemoteTopic rtopic = (RemoteTopic) topic;
            
    if (rtopic.isLoaded || this.isLoaded) {
      rtopic.setLoaded(true);
      setLoaded(true);
    }
                         
    super.merge(topic);                     
    rtopic.setRealTopic(this);
  } 

  public void debug() {
    System.out.println("-----RemoteTopic " + getObjectId() + " " + System.identityHashCode(this));
    if (realTopic != null) {
      System.out.println("Deferring to real topic");
      realTopic.debug();
    }
    System.out.println("isLoaded: " + isLoaded() + ", isConnected: " + isConnected());
    System.out.println("base names: " + super.getTopicNames().size());
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof RemoteTopic) {
      if( realTopic == null ) {
        return ((RemoteTopic) obj).equals(this);
      }
      return obj.equals(realTopic);
    }
        
    if (realTopic == null) {
      return super.equals(obj);
    }
    return realTopic.equals(obj);
  }

  public boolean equals(RemoteTopic obj) {
    if (realTopic == null) {
      return super.equals(obj);
    }
    return realTopic.equals(obj);
  }

  @Override
  public int hashCode() {
    if (realTopic == null) {
      return super.hashCode();
    }
    return realTopic.hashCode();
  }
}
