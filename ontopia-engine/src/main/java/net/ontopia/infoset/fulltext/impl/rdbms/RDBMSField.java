
// $Id: RDBMSField.java,v 1.2 2005/07/07 13:15:08 grove Exp $

package net.ontopia.infoset.fulltext.impl.rdbms;

import java.io.Reader;

import net.ontopia.infoset.fulltext.core.FieldIF;
  
/**
 * INTERNAL: RDBMS FieldIF class implementation.<p>
 */

public class RDBMSField implements FieldIF {

  protected String fname;
  protected String value;
  
  RDBMSField(String fname, String value) {
    this.fname = fname;
    this.value = value;
  }
  
  public String getName() {
    return fname;
  }
  
  public String getValue() {
    return value;
  }

  public Reader getReader() {
    return new java.io.StringReader(value);
  }
  
  public boolean isStored() {
    return true;
  }

  public boolean isIndexed() {
    return true;
  }

  public boolean isTokenized() {
    return false;
  }

  public String toString() {
    return getName() + "=" + getValue() + " ";
  }
  
}
