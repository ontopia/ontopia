
// $Id: OntopolyRepository.java,v 1.5 2009/05/25 05:48:06 geir.gronmo Exp $

package ontopoly.sysmodel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;
import net.ontopia.topicmaps.entry.TopicMapSourceIF;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.utils.IdentityUtils;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapWriter;
import net.ontopia.topicmaps.xml.XTMTopicMapReference;
import net.ontopia.utils.DeciderIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.URIUtils;
import ontopoly.model.PSI;
import ontopoly.model.QueryMapper;
import ontopoly.utils.OntopolyModelUtils;
  
/**
 * INTERNAL: Represents the system topic map describing all the topic
 * maps in the Ontopoly topic maps repository.
 */
public class OntopolyRepository {

  public static final String ONTOLOGY_TOPIC_MAP_ID = "ontopoly-ontology.xtm";

  private static final Comparator<TopicMapReference> REFERENCE_COMPARATOR = new Comparator<TopicMapReference>() {
        public int compare(TopicMapReference r1, TopicMapReference r2) {
          return r1.getName().compareTo(r2.getName());
        }
      };

  private TopicMapRepositoryIF repository;
  private String systemTopicMapId;
  private transient TopicMapIF _systemtm;

  public OntopolyRepository(TopicMapRepositoryIF repository, String systemTopicMapId) {
    this.repository = repository;
    this.systemTopicMapId = systemTopicMapId;
  }

  private TopicMapIF getSystemTopicMap() {
    // open system topic map
    TopicMapReferenceIF topicMapReferenceIF = getTopicMapRepository().getReferenceByKey(systemTopicMapId);
    if (topicMapReferenceIF == null)
      throw new OntopiaRuntimeException("Cannot find topic map with id '" + systemTopicMapId);

    try {
      this._systemtm = topicMapReferenceIF.createStore(false).getTopicMap();
    } catch(IOException e) {
      throw new OntopiaRuntimeException(e);
    }
    return _systemtm;
  }

  private List<String> getRegisteredTopicMaps() {
    // now query to find Ontopoly topic maps
    QueryProcessorIF processor = QueryUtils.getQueryProcessor(getSystemTopicMap());
    QueryMapper<String> qm = new QueryMapper<String>(processor);
    return qm.queryForList(
      "using ont for i\"http://psi.ontopia.net/ontology/\" " +
      "select $ID from instance-of($T, ont:ted-topic-map), " +
      "ont:topic-map-id($T, $ID)?");
  }

  public TopicMapRepositoryIF getTopicMapRepository() {
    return repository;
  }
  
  /**
   * INTERNAL: Returns an alphabetically sorted list of all the
   * Ontopoly topic maps.
   * @return a List of TopicMapReference objects
   */
  public List<TopicMapReference> getOntopolyTopicMaps() {
    List<String> registeredTopicMaps = getRegisteredTopicMaps();
    List<TopicMapReference> result = new ArrayList<TopicMapReference>(registeredTopicMaps.size());
    for (String referenceId : registeredTopicMaps) {
        result.add(new TopicMapReference(referenceId));
    }
    Collections.sort(result, REFERENCE_COMPARATOR);
    return result;
  }

  /**
   * INTERNAL: Returns an alphabetically sorted list of all the
   * non-Ontopoly topic maps.
   * @return a List of TopicMapReference objects
   */
  public List<TopicMapReference> getNonOntopolyTopicMaps() {
    Set<String> registeredTopicMaps = new HashSet<String>(getRegisteredTopicMaps());
    List<TopicMapReference> result = new ArrayList<TopicMapReference>();
    for (TopicMapReferenceIF ref : getTopicMapRepository().getReferences()) {
        if (!registeredTopicMaps.contains(ref.getId())) {
            result.add(new TopicMapReference(ref));
        }
    }
    return result;
  }

  public List<TopicMapSource> getEditableSources() {
    return getSources(new DeciderIF() {
      public boolean ok(Object o) {
        TopicMapSourceIF source = (TopicMapSourceIF)o;
        return source.supportsCreate() && source.getId() != null;
      }
    });
  }

  private List<TopicMapSource> getSources(DeciderIF decider) {
    Collection<TopicMapSourceIF> sources = getTopicMapRepository().getSources();
    List<TopicMapSource> result = new ArrayList<TopicMapSource>(sources.size());
    for (TopicMapSourceIF source : sources) {
      if (decider.ok(source))
        result.add(new TopicMapSource(source.getId()));
    }
    return result;
  }

  /**
   * INTERNAL: Creates a new Ontopoly topic map, and updates the
   * system topic map accordingly.
   * @return The reference id of the new topic map
   */
  public String createOntopolyTopicMap(String sourceId, String name) {
    TopicMapSourceIF source = getTopicMapRepository().getSourceById(sourceId);
    TopicMapReferenceIF ref = source.createTopicMap(name, null);

    TopicMapStoreIF store = null;
    try {
      store = ref.createStore(false);
      TopicMapIF tm = store.getTopicMap();

      // import ontopoly ontology
      TopicMapReferenceIF ontologyTopicMapReference = getTopicMapRepository().getReferenceByKey(ONTOLOGY_TOPIC_MAP_ID);
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
          Collection<TopicIF> sameTopic = IdentityUtils.findSameTopic(tm, oldReifier);
          if (!sameTopic.isEmpty())
            newReifier = sameTopic.iterator().next();
        }
        TopicIF reifier = tm.getReifier();
        if (reifier != null && newReifier != null && !reifier.equals(newReifier)) {
          reifier.merge(newReifier);
        } else if (reifier == null && newReifier != null) {
          tm.setReifier(newReifier);
          reifier = tm.getReifier();
        }
        Collection<TopicIF> scope = Collections.emptySet();
        OntopolyModelUtils.setName(null, reifier, name, scope);
      } finally {
        ontologyTopicMapStore.close();
      }
      // save topic map with ontology
      if (ref instanceof XTMTopicMapReference)
        ((XTMTopicMapReference) ref).save();

      store.commit();

    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    } finally {
      if (store != null)
        store.close();
    }

    // make ontopoly topic map
    registerOntopolyTopicMap(ref.getId(), name);

    // notify repository and wrap up
    getTopicMapRepository().refresh();

    return ref.getId();
  }

   public void deleteTopicMap(String referenceId) {
    TopicMapRepositoryIF repository = getTopicMapRepository();
    TopicMapReferenceIF reference = repository.getReferenceByKey(referenceId);

    // remove from topic map repository
    if (reference != null && !reference.isDeleted()) reference.delete();

    // make ontopoly topic map
    unregisterOntopolyTopicMap(reference.getId());

    repository.refresh();
  }

  /**
   * INTERNAL: Turns the topic map into an Ontopoly topic map in the
   * repository, but does not actually change the topic map itself.
   */
  public void registerOntopolyTopicMap(String referenceId, String name) {
    if (getTopicMapRepository().getReferenceByKey(referenceId) == null)
      throw new OntopiaRuntimeException("Can't upgrade non-existent topic map: '" + referenceId + "'");

    // create topic for topic map
    TopicMapIF systemtm = getSystemTopicMap();
    TopicIF tmtype = systemtm.getTopicBySubjectIdentifier(PSI.ON_TED_TOPIC_MAP);
    TopicIF idtype = systemtm.getTopicBySubjectIdentifier(PSI.ON_TOPIC_MAP_ID);
    TopicMapBuilderIF builder = systemtm.getBuilder();
    TopicIF tmtopic = builder.makeTopic(tmtype);
    builder.makeOccurrence(tmtopic, idtype, referenceId);
    builder.makeTopicName(tmtopic, name);

    saveSystemTopicMap();
  }

  public void unregisterOntopolyTopicMap(String referenceId) {

    QueryProcessorIF processor = QueryUtils.getQueryProcessor(getSystemTopicMap());
    QueryMapper<TopicIF> qm = new QueryMapper<TopicIF>(processor);
    List<TopicIF> topics = qm.queryForList(
      "using ont for i\"http://psi.ontopia.net/ontology/\" " +
      "select $T from instance-of($T, ont:ted-topic-map), " +
      "ont:topic-map-id($T, %ID%)?", Collections.singletonMap("ID", referenceId));
    for (TopicIF topic : topics) {
      topic.remove();
    }

    saveSystemTopicMap();
  }

  /**
   * INTERNAL: Saves the system topic map to disk.
   */
  private void saveSystemTopicMap() {
    if (_systemtm != null) {
      try {
        LocatorIF base = _systemtm.getStore().getBaseAddress();
        File file = URIUtils.getURIFile(base);
        FileOutputStream stream = new FileOutputStream(file);
        new LTMTopicMapWriter(stream).write(_systemtm);
        stream.close();
      } catch (IOException e) {
        throw new OntopiaRuntimeException(e);
      }
    }
  }
    
}
