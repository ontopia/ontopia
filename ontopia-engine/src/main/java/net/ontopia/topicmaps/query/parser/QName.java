
package net.ontopia.topicmaps.query.parser;

/**
 * INTERNAL: Used to represent qualified name references in tolog queries.
 */
public class QName {
  protected String prefix;
  protected String localname;
  
  public QName(String qname) {
    int pos = qname.indexOf(':');
    if (pos == -1)
      localname = qname;
    else {
      prefix = qname.substring(0, pos);
      localname = qname.substring(pos+1);
    }
  }

  public String getPrefix() {
    return prefix;
  }
  
  public String getLocalName() {
    return localname;
  }

  /// Object

  public String toString() {
    if (prefix == null)
      return localname;
    else
      return prefix + ':' + localname;
  }
  
}
