
package net.ontopia.topicmaps.utils.rdf;

import java.io.*;
import java.net.*;
import net.ontopia.utils.URIUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.entry.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.infoset.core.LocatorIF;

import com.hp.hpl.jena.shared.JenaException;

/**
 * INTERNAL: An RDF file topic map reference.
 */
public class RDFTopicMapReference extends AbstractURLTopicMapReference {
  private String syntax;
  private String mapfile;
  private boolean generateNames;
  private boolean lenient;
  
  public RDFTopicMapReference(URL url, String id, String title) {
    super(id, title, url, null);
  }
  
  public RDFTopicMapReference(URL url, String id, String title, LocatorIF base_address, String syntax) {
    super(id, title, url, base_address);
    this.syntax = syntax;
  }

  /**
   * @since 2.0.3
   */
  public void setMappingFile(String file) {
    this.mapfile = file;
  }

  /**
   * @since 2.0.5
   */
  public String getMappingFile() {
    return mapfile;
  }

  /**
   * @since 2.0.3
   */
  public void setSyntax(String syntax) {
    this.syntax = syntax;
  }

  /**
   * @since 2.1
   */
  public String getSyntax() {
    return syntax;
  }

  /**
   * @since 2.1
   */
  public void setLenient(boolean lenient) {
    this.lenient = lenient;
  }
  
  /**
   * @since 2.0.5
   */
  public void setGenerateNames(boolean generateNames) {
    this.generateNames = generateNames;
  }

  /**
   * @since 2.1
   */
  public boolean getGenerateNames() {
    return generateNames;
  }
  
  protected TopicMapIF loadTopicMap(boolean readonly) throws IOException {
    try {
      RDFTopicMapReader reader = new RDFTopicMapReader(url.toString(), syntax);
      reader.setDuplicateSuppression(duplicate_suppression);
      if (mapfile != null)
        reader.setMappingURL(URIUtils.getURI(mapfile).getAddress());
      reader.setGenerateNames(generateNames);
      reader.setLenient(lenient);
      
      // Load topic map
      InMemoryTopicMapStore store = new InMemoryTopicMapStore();
      store.setBaseAddress(new URILocator(url)); // bug #1550
      TopicMapIF tm = store.getTopicMap();
      reader.importInto(tm);

      return tm;
    } catch (JenaException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
}
