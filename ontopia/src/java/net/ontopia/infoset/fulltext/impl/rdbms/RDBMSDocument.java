
// $Id: RDBMSDocument.java,v 1.4 2005/07/07 13:15:08 grove Exp $

package net.ontopia.infoset.fulltext.impl.rdbms;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import net.ontopia.infoset.fulltext.core.DocumentIF;
import net.ontopia.infoset.fulltext.core.FieldIF;
  
/**
 * INTERNAL: RDBMS DocumentIF class implementation.<p>
 */

public class RDBMSDocument implements DocumentIF {

  protected Map fields;
  protected float score;
  
  RDBMSDocument(Map fields, float score) {
    this.fields = fields;
    this.score = score;
  }

  float getScore() {
    return score;
  }
  
  public FieldIF getField(String name) {
    return (FieldIF)fields.get(name);
  }
  
  public Collection getFields() {
    return fields.values();
  }

  public void addField(FieldIF field) {
    throw new UnsupportedOperationException("Cannot modify RDBMS document object.");
  }

  public void removeField(FieldIF field) {
    throw new UnsupportedOperationException("Cannot modify RDBMS document object.");
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("<rdbms.Document ");
    Iterator iter = getFields().iterator();
    while (iter.hasNext()) {
      FieldIF field = (FieldIF)iter.next();
      sb.append(field.toString());
    }
    sb.append(">");
    return sb.toString();
  }
  
}
