
// $Id: QNameRegistry.java,v 1.1 2009/04/27 11:05:24 lars.garshol Exp $

package net.ontopia.topicmaps.utils;

import java.util.Map;
import java.util.HashMap;
import java.net.MalformedURLException;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * PUBLIC: A utility class for producing full URIs from QNames. Allows
 * QName prefixes to be registered, and has a set of predefined QName
 * prefixes. Also allows topics to be looked up, via the QNameLookup
 * class.
 * @since %NEXT%
 */
public class QNameRegistry {
  private Map prefixes;
  
  /**
   * PUBLIC: Creates a registry populated with the default bindings.
   */
  public QNameRegistry() {
    this(true);
  }

  /**
   * PUBLIC: Creates a new registry.
   * @param populate Populate with default bindings or not.
   */
  public QNameRegistry(boolean populate) {
    this.prefixes = new HashMap();
    if (populate)
      addDefaultPrefixes();
  }

  /**
   * PUBLIC: Registers a new prefix.
   */
  public void registerPrefix(String prefix, String uri) {
    prefixes.put(prefix, uri);
  }

  /**
   * PUBLIC: Creates a locator from a QName.
   * @throws OntopiaRuntimeException if the syntax is incorrect, the prefix
   *    is not bound, or the resulting locator is not a valid URI.
   */
  public LocatorIF resolve(String qname) {
    int pos = qname.indexOf(':');
    if (pos == -1)
      throw new OntopiaRuntimeException("Qname " + qname + " has no colon!");

    String prefix = qname.substring(0, pos);
    String localpart = qname.substring(pos + 1);

    String uri = (String) prefixes.get(prefix);
    if (uri == null)
      throw new OntopiaRuntimeException("Unknown prefix " + prefix + " in " +
                                        qname);

    try {
      return new URILocator(uri + localpart);
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException("QName " + qname + " produced invalid " +
                                        "URI", e);
    }
  }

  /**
   * PUBLIC: Returns a QNameLookup object bound to a specific topic map.
   */
  public QNameLookup getLookup(TopicMapIF topicmap) {
    return new QNameLookup(this, topicmap);
  }

  // --- Internal

  private void addDefaultPrefixes() {
    // xsd
    // tmdm
    // xtm
    // dc
    // op -> ontopedia?
  }
}
