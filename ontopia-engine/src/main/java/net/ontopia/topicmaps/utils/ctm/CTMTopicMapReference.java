
package net.ontopia.topicmaps.utils.ctm;

import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

import org.xml.sax.InputSource;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.core.TopicMapImporterIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.entry.AbstractOntopolyURLReference;
import net.ontopia.topicmaps.utils.DuplicateSuppressionUtils;

/**
 * INTERNAL: An CTM file topic map reference.
 */
public class CTMTopicMapReference extends AbstractOntopolyURLReference {
  
  public CTMTopicMapReference(URL url, String id, String title) {
    super(url, id, title, null);
  }
  
  public CTMTopicMapReference(URL url, String id, String title, LocatorIF base_address) {
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
  
  private CTMTopicMapReader makeReader() throws IOException {
    if (base_address == null)
      return new CTMTopicMapReader(url.toString());
    else
      return new CTMTopicMapReader(new InputSource("" + url), base_address);
  }
}
