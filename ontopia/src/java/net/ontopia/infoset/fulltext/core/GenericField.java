
// $Id: GenericField.java,v 1.8 2005/07/07 13:15:09 grove Exp $

package net.ontopia.infoset.fulltext.core;

import java.io.Reader;
  
/**
 * INTERNAL: A generic document field.<p>
 */

public class GenericField implements FieldIF {

  protected String name;
  protected String value;
  protected Reader reader;
  protected boolean store;
  protected boolean index;
  protected boolean tokenize;
  
  public GenericField(String name, String value, boolean store, boolean index, boolean tokenize) {
    this.name = name;
    this.value = value;
    this.store = store;
    this.index = index;
    this.tokenize = tokenize;
  }

  public GenericField(String name, Reader value, boolean store, boolean index, boolean tokenize) {
    this.name = name;
    this.reader = value;
    this.store = store;
    this.index = index;
    this.tokenize = tokenize;
  }

  public static FieldIF createUnstoredField(String name, String value) {
    return new GenericField(name, value, false, true, true);
  }
  public static FieldIF createUnstoredField(String name, Reader reader) {
    return new GenericField(name, reader, false, true, true);
  }
  public static FieldIF createKeywordField(String name, String value) {
    return new GenericField(name, value, true, true, false);
  }
  public static FieldIF createTextField(String name, String value) {
    return new GenericField(name, value, true, true, true);
  }
  
  public String getName() {
    return name;
  }
  
  public String getValue() {
    return value;
  }

  public Reader getReader() {
    return reader;
  }

  public boolean isStored() {
    return store;
  }

  public boolean isIndexed() {
    return index;
  }

  public boolean isTokenized() {
    return tokenize;
  }
  
}
