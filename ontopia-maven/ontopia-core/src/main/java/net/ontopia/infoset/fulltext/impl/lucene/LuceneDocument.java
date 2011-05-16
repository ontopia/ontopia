
// $Id: LuceneDocument.java,v 1.3 2005/07/07 13:15:09 grove Exp $

package net.ontopia.infoset.fulltext.impl.lucene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import net.ontopia.infoset.fulltext.core.DocumentIF;
import net.ontopia.infoset.fulltext.core.FieldIF;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
  
/**
 * INTERNAL: DocumentIF wrapper for Lucene's own internal document class.<p>
 */

public class LuceneDocument implements DocumentIF {

  protected Document document;

  LuceneDocument(Document document) {
    this.document = document;
  }
  
  public FieldIF getField(String name) {
    Field field = document.getField(name);
    if (field == null) return null;
    return new LuceneField(field);
  }
  
  public Collection<FieldIF> getFields() {
    Collection<FieldIF> result = new ArrayList<FieldIF>();
    Enumeration enumeration = document.fields();
    while (enumeration.hasMoreElements()) {
      result.add(new LuceneField((Field)enumeration.nextElement()));
    }
    return result;
  }

  public void addField(FieldIF field) {
    throw new UnsupportedOperationException("Cannot modify wrapped document object.");
  }

  public void removeField(FieldIF field) {
    throw new UnsupportedOperationException("Cannot modify wrapped document object.");
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("<lucene.Document ");
    Iterator<FieldIF> iter = getFields().iterator();
    while (iter.hasNext()) {
      FieldIF field = iter.next();
      sb.append(field.toString());
    }
    sb.append(">");
    return sb.toString();
  }
}
