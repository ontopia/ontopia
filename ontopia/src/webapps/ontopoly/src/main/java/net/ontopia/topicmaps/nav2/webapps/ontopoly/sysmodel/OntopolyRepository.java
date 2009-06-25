
// $Id: OntopolyRepository.java,v 1.5 2009/05/25 05:48:06 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.webapps.ontopoly.sysmodel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;
import net.ontopia.topicmaps.entry.TopicMapSourceIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryWrapper;
import net.ontopia.topicmaps.query.utils.RowMapperIF;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapWriter;
import net.ontopia.utils.ObjectUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.URIUtils;
  
/**
 * INTERNAL: Represents the system topic map describing all the topic
 * maps in the Ontopoly topic maps repository.
 */
public class OntopolyRepository {

  public static final float CURRENT_VERSION_NUMBER = 1.9f;

  private TopicMapIF systemtm;
  private TopicMapRepositoryIF repository;
  private List tms;
  private List sources;

  public OntopolyRepository(TopicMapRepositoryIF repository) {
    this.repository = repository;
    // open system topic map
    String referenceId = getRepositoryTopicMapId();
    TopicMapReferenceIF topicMapReferenceIF = repository.getReferenceByKey(referenceId);
    if (topicMapReferenceIF == null)
      throw new OntopiaRuntimeException("Cannot find topic map with id '" + referenceId);
      
    try {
      this.systemtm = topicMapReferenceIF.createStore(false).getTopicMap();
    } catch(IOException e) {
      throw new OntopiaRuntimeException(e);
    }
    // build internal state
    build();
  }
  
  protected String getRepositoryTopicMapId() {
    return "ontopoly-system.ltm";
  }
  
  private void build() {
    // get all TMs in repository first
    final Map byid = new HashMap(); // so we can reference from rowmapper
    Iterator it = repository.getReferences().iterator();
    while (it.hasNext()) {
      TopicMapReferenceIF ref = (TopicMapReferenceIF) it.next();

      // <UGLY>
      if (ObjectUtils.isTrue(ObjectUtils.getProperty(ref.getSource(), "hidden", null)))
        continue;
      // </UGLY>
      
      byid.put(ref.getId(), ref);
    }

    // now query to find Ontopoly TMs
    QueryWrapper qw = new QueryWrapper(systemtm);
    tms = qw.queryForList(
      "using ont for i\"http://psi.ontopia.net/ontology/\" " +
      "instance-of($T, ont:ted-topic-map), " +
      "ont:topic-map-id($T, $ID)?",
      new RowMapperIF() {
        // Java anonymous classes stink
          
        public Object mapRow(QueryResultIF result, int ix) {
          TopicIF topic = (TopicIF) result.getValue("T");
          String id = (String) result.getValue("ID");
          TopicMapReferenceIF ref = repository.getReferenceByKey(id);
          byid.remove(id); // so we know what we've seen already
          return new TopicMapReference(topic, id, ref, OntopolyRepository.this);
        }
      });

    // now go through remaining non-ontopoly TMs
    it = byid.values().iterator();
    while (it.hasNext()) {
      TopicMapReferenceIF ref = (TopicMapReferenceIF) it.next();
      tms.add(new TopicMapReference(ref, this));
    }

    // now sort them
    sortTopicMaps();

    // get the sources as well
    sources = new ArrayList();
    it = repository.getSources().iterator();
    while (it.hasNext()) {
      TopicMapSourceIF src = (TopicMapSourceIF) it.next();
      if (src.supportsCreate())
        sources.add(new TopicMapSource(src, this));
    }
  }

  public TopicMapRepositoryIF getTopicMapRepository() {
    return repository;
  }
  
  /**
   * INTERNAL: Returns an alphabetically sorted list of all the topic
   * maps in the repository, whether they are Ontopoly topic maps or
   * not.
   * @return a List of TopicMapReference objects
   */
  public List getTopicMaps() {
    return tms;
  }
  
  /**
   * INTERNAL: Returns an alphabetically sorted list of all the
   * Ontopoly topic maps.
   * @return a List of TopicMapReference objects
   */
  public List getOntopolyTopicMaps() {
    List tmp = new ArrayList(tms.size());
    for (int ix = 0; ix < tms.size(); ix++) {
      TopicMapReference ref = (TopicMapReference) tms.get(ix);
      if (ref.isOntopolyTopicMap())
        tmp.add(ref);
    }
    return tmp;
  }

  /**
   * INTERNAL: Returns an alphabetically sorted list of all the
   * non-Ontopoly topic maps.
   * @return a List of TopicMapReference objects
   */
  public List getNonOntopolyTopicMaps() {
    List tmp = new ArrayList(tms.size());
    for (int ix = 0; ix < tms.size(); ix++) {
      TopicMapReference ref = (TopicMapReference) tms.get(ix);
      if (!ref.isOntopolyTopicMap())
        tmp.add(ref);
    }
    return tmp;
  }

  /**
   * INTERNAL: Returns the reference with the given ID, if it exists,
   * or null if it does not.
   */
  public TopicMapReference getReference(String id) {
    for (int ix = 0; ix < tms.size(); ix++) {
      TopicMapReference ref = (TopicMapReference) tms.get(ix);
      if (ref.getId().equals(id))
        return ref;
    }
    return null;
  }
  
  /**
   * INTERNAL: Returns a list of the active sources in the repository
   * which support creating new topic maps. Sources which do not
   * support creation are not included.   
   * @return a List of TopicMapSource objects
   */
  public List getSources() {
    return sources;
  }
  
  public TopicMapSource getSource(String topicMapSourceId) {
    TopicMapSource source = null;
    
    Iterator it = getSources().iterator();
    while(it.hasNext()) {
      TopicMapSource s = (TopicMapSource) it.next();
      if(s.getId().equals(topicMapSourceId)) {
        source = s;
        break;
      }
    }
    return source;
  }

  // --- PRIVATE
  
  /**
   * INTERNAL: References should always be created via sources, and
   * the source wrapper calls this method to let the repository know.
   */
  void addNewReference(TopicMapReference ref) {  
    tms.add(ref);
    sortTopicMaps();
    repository.refresh();
    commit(); 
  }

  /**
   * INTERNAL: References should always be deleted via the delete
   * method on the reference itself. The reference calls this method
   * to let the repository know.
   */
  void removeReference(TopicMapReference ref) {   
    tms.remove(ref);
    repository.refresh();
    commit();
  }
  
  /**
   * INTERNAL: Sort the list of topic maps in place.
   */
  private void sortTopicMaps() {
    Collections.sort(tms, new Comparator() {
        public int compare(Object o1, Object o2) {
          TopicMapReference ref1 = (TopicMapReference) o1;
          TopicMapReference ref2 = (TopicMapReference) o2;
          return ref1.getName().compareTo(ref2.getName());
        }
      });
  }

  /**
   * INTERNAL: Creation of new topics is initiated by sources and
   * references, but takes place here in order to avoid repetition.
   */
  TopicIF makeTopicFor(String name, String id) {
    TopicIF tmtype = systemtm.getTopicBySubjectIdentifier(psitm);
    TopicIF idtype = systemtm.getTopicBySubjectIdentifier(psiid);
    TopicMapBuilderIF builder = systemtm.getBuilder();
    TopicIF tmtopic = builder.makeTopic(tmtype);
    builder.makeOccurrence(tmtopic, idtype, id);
    builder.makeTopicName(tmtopic, name);
    return tmtopic;
  }
  
  /**
   * INTERNAL: Saves the system topic map to disk.
   */
  void commit() {
    try {
      LocatorIF base = systemtm.getStore().getBaseAddress();
      File file = URIUtils.getURIFile(base);
      FileOutputStream stream = new FileOutputStream(file);
      new LTMTopicMapWriter(stream).write(systemtm);
      stream.close();
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  // ----- CONSTANTS

  private static LocatorIF psibase;
  private static LocatorIF psitm;
  private static LocatorIF psiid;
  
  static {
    try {
      psibase = new URILocator("http://psi.ontopia.net/ontology/");
      psitm = psibase.resolveAbsolute("ted-topic-map");
      psiid = psibase.resolveAbsolute("topic-map-id");
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
}
