
// $Id: TopicMapReference.java,v 1.1 2008/10/23 05:18:38 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.webapps.ontopoly.sysmodel;

import java.io.Serializable;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.utils.TopicStringifiers;

/**
 * INTERNAL: Represents a topic map in the Ontopoly topic map
 * repository.
 */
public class TopicMapReference implements Serializable {
  private TopicMapReferenceIF reference;
  private TopicIF topic; // in system topic map
  private String id;
  private OntopolyRepository repository;

  /**
   * INTERNAL: Creates a reference to a non-Ontopoly topic map.
   */
  TopicMapReference(TopicMapReferenceIF reference,
                    OntopolyRepository repository) {
    this.reference = reference;
    this.repository = repository;
  }

  /**
   * INTERNAL: Creates a reference to an Ontopoly topic map. The
   * reference parameter will be null if the topic map does not
   * actually exist.
   */
  TopicMapReference(TopicIF topic, String id,
                    TopicMapReferenceIF reference,
                    OntopolyRepository repository) {
    this.topic = topic;
    this.id = id;
    this.reference = reference;
    this.repository = repository;
  }
  /**
   * INTERNAL: Returns the ID of the reference (like 'foo.xtm').
   */
  public OntopolyRepository getRepository() {
    return repository;
  }

  /**
   * INTERNAL: Returns the ID of the reference (like 'foo.xtm').
   */
  public String getId() {
    if (id != null)
      return id;
    else
      return reference.getId();
  }

  /**
   * INTERNAL: Returns the name of the topic map. For non-Ontopoly
   * topic maps this will be the same as the ID.
   */
  public String getName() {
    if (topic != null) {
      String name = TopicStringifiers.toString(topic);
      if(!name.equals("")) {
        return name;
      }
    }
    if (reference == null)
      return id;
    else 
      return reference.getId();
  }

  /**
   * INTERNAL: Sets the name of the topic map, and saves the system
   * topic map. If the topic map is not an Ontopoly topic map an
   * exception will be thrown.
   */
  public void setName(String name) {
    if (!isOntopolyTopicMap())
      throw new OntopiaRuntimeException("Only Ontopoly topic maps have names");
    
    TopicNameIF bn = (TopicNameIF) topic.getTopicNames().iterator().next();
    bn.setValue(name);

    repository.commit();
  }

  /**
   * INTERNAL: Tests if the topic map is an Ontopoly topic map.
   */
  public boolean isOntopolyTopicMap() {
    return topic != null;
  }

  /**
   * INTERNAL: Tests if the topic map is actually in the repository.
   */
  public boolean isPresent() {
    return reference != null;
  }

  /**
   * INTERNAL: Deletes the topic map reference from the topic map repository, the system topic
   * map, and saves the system topic map. After the call the reference
   * becomes useless.
   */
  public void delete() {
		// remove from topic map repository
	  if (reference != null && !reference.isDeleted()) reference.delete();
		
		// update and save system TM
		topic.remove();    
		repository.removeReference(this);
		
		// self-destruct
		topic = null;
		id = null;
		repository = null;
  }

  /**
   * INTERNAL: Turns the topic map into an Ontopoly topic map in the
   * repository, but does not actually change the topic map itself.
   */
  public void makeOntopolyTopicMap() {
    if (!isPresent())
      throw new OntopiaRuntimeException("Can't upgrade non-existent topic maps");
    if (isOntopolyTopicMap())
      throw new OntopiaRuntimeException("Is already an Ontopoly topic map");
    
    topic = repository.makeTopicFor(getId(), getId());
    repository.commit();
  }

  public String toString() {
    return "<TopicMapReference " + getId() + ", " + topic + ">";
  }
}
