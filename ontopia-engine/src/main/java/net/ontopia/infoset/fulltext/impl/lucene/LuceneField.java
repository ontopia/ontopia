
package net.ontopia.infoset.fulltext.impl.lucene;

import java.io.Reader;

import net.ontopia.infoset.fulltext.core.FieldIF;

import org.apache.lucene.document.Field;
  
/**
 * INTERNAL: FieldIF wrapper for Lucene's own internal field class.<p>
 */

public class LuceneField implements FieldIF {

  protected Field field;
  
  LuceneField(Field field) {
    this.field = field;
  }
  
  public String getName() {
    return field.name();
  }
  
  public String getValue() {
    return field.stringValue();
  }

  public Reader getReader() {
    return field.readerValue();
  }
  
  public boolean isStored() {
    return field.isStored();
  }

  public boolean isIndexed() {
    return field.isIndexed();
  }

  public boolean isTokenized() {
    return field.isTokenized();
  }

  public String toString() {
    if (getReader() == null)
      return getName() + "=" + getValue() + " ";
    else
      return getName() + "=" + getReader() + " ";
  }
}
