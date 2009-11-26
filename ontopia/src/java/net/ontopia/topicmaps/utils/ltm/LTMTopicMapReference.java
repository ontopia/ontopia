
// $Id: LTMTopicMapReference.java,v 1.8 2005/07/13 09:00:31 grove Exp $

package net.ontopia.topicmaps.utils.ltm;

import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.core.TopicMapImporterIF;
import net.ontopia.topicmaps.entry.AbstractOntopolyURLReference;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.utils.DuplicateSuppressionUtils;

/**
 * INTERNAL: An LTM file topic map reference.
 */
public class LTMTopicMapReference extends AbstractOntopolyURLReference {
  
  public LTMTopicMapReference(URL url, String id, String title) {
    super(url, id, title, null);
  }
  
  public LTMTopicMapReference(URL url, String id, String title, LocatorIF base_address) {
    super(url, id, title, base_address);
  }

  // using loadTopicMap inherited from AbstractOntopolyURLReference

  public TopicMapImporterIF getImporter() {
    try {
      return makeReader();
    } catch (IOException e) {
      throw new OntopiaRuntimeException("Bad URL: " + url, e);
    }
  }

  private LTMTopicMapReader makeReader() throws IOException {
    if (base_address == null)
      return new LTMTopicMapReader(url.toString());
    else
      return new LTMTopicMapReader(new org.xml.sax.InputSource(url.toString()), base_address);      
  }
}
