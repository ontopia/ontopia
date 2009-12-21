
// $Id: TopicMapSource.java,v 1.4 2009/04/20 06:55:46 geir.gronmo Exp $

package ontopoly.sysmodel;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import ontopoly.utils.OntopolyModelUtils;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.TopicMapSourceIF;
import net.ontopia.topicmaps.utils.IdentityUtils;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.topicmaps.xml.XTMTopicMapReference;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Represents a source in the Ontopoly topic map repository.
 * Only sources which support create are represented. This class only
 * exists so the repository can be notified when new topic maps are
 * created.
 */
public class TopicMapSource {

  public static final String ONTOLOGY_TOPIC_MAP_ID = "ontopoly-ontology.xtm";

  private TopicMapSourceIF source;
  private OntopolyRepository repository;

  /**
   * INTERNAL: Creates a new source wrapper.
   */
  TopicMapSource(TopicMapSourceIF source, OntopolyRepository repository) {
    this.source = source;
    this.repository = repository;
  }

  /**
   * INTERNAL: Returns the ID of the source.
   */
  public String getId() {
    return source.getId();
  }

  /**
   * INTERNAL: Returns the display title of the source.
   */
  public String getTitle() {
    return source.getTitle();
  }

  /**
   * INTERNAL: Creates a new Ontopoly topic map, and updates the
   * system topic map accordingly.
   */
  public TopicMapReference createTopicMap(String name) {
    // FIXME: should we move name -> id mapping here, rather than
    // having it in the source?

    // make reference
    TopicMapReferenceIF ref = source.createTopicMap(name, null);

    TopicMapStoreIF store = null;
    try {
      store = ref.createStore(false);
      TopicMapIF tm = store.getTopicMap();
     
      // import TED ontology
      TopicMapReferenceIF ontologyTopicMapReference = repository.getTopicMapRepository().getReferenceByKey(ONTOLOGY_TOPIC_MAP_ID);
      if (ontologyTopicMapReference == null)
        throw new OntopiaRuntimeException("Could not find ontology topic map '" + ONTOLOGY_TOPIC_MAP_ID + "'");
      TopicMapStoreIF ontologyTopicMapStore = ontologyTopicMapReference.createStore(true);
      try {
        TopicMapIF ontologyTopicMap = ontologyTopicMapStore.getTopicMap();
        MergeUtils.mergeInto(tm, ontologyTopicMap);

        // do some magic to port old reifier to new topic map
        TopicIF oldReifier = ontologyTopicMap.getReifier();
        TopicIF newReifier = null;
        if (oldReifier != null) {
          Collection sameTopic = IdentityUtils.findSameTopic(tm, oldReifier);
          if (!sameTopic.isEmpty())
            newReifier = (TopicIF)sameTopic.iterator().next();
        }
        TopicIF reifier = tm.getReifier();
        if (reifier != null && newReifier != null && !reifier.equals(newReifier)) {
          reifier.merge(newReifier);
        } else if (reifier == null && newReifier != null) {
          tm.setReifier(newReifier);
          reifier = tm.getReifier();
        }
                    
        OntopolyModelUtils.setName(null, reifier, name, Collections.EMPTY_SET);
      } finally {
        ontologyTopicMapStore.close();
      }
      // save topic map with TED ontology
      if (ref instanceof XTMTopicMapReference)
        ((XTMTopicMapReference) ref).save();

      store.commit();

    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    } finally {
      if (store != null)
        store.close();
    }    
    
    // make topic
    TopicIF tmtopic = repository.makeTopicFor(name, ref.getId());

    // notify repository and wrap up
    TopicMapReference newref = new TopicMapReference(tmtopic, ref.getId(), ref,
                                                     repository);
    repository.addNewReference(newref);
    return newref;
  }

}
