
// $Id: GenericDocument.java,v 1.11 2007/07/09 12:50:35 geir.gronmo Exp $

package net.ontopia.infoset.fulltext.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
  
/**
 * PUBLIC: The default document implementation. This class contains a
 * single straightforward implementation of the DocumentIF
 * interfaces. The class uses a map internally to hold its FieldIF
 * elements.<p>
 */

public class GenericDocument implements DocumentIF, java.io.Serializable {

  protected Map fields;

  public GenericDocument() {
    this.fields = new HashMap();
  }
  
  public FieldIF getField(String name) {
    return (FieldIF)fields.get(name);
  }
  
  public Collection getFields() {
    return fields.values();
  }

  public void addField(FieldIF field) {
    fields.put(field.getName(), field);
  }

  public void removeField(FieldIF field) {
    fields.remove(field.getName());
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("<Document");
    Iterator iter = getFields().iterator();
    while (iter.hasNext()) {
      FieldIF field = (FieldIF)iter.next();
      sb.append(" " + field.getName() + "='" + field.getValue() + "'");
    }
    sb.append('>');
    return sb.toString();
  }
  
}
