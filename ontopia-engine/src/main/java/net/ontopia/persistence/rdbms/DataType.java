
package net.ontopia.persistence.rdbms;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** 
 * INTERNAL: Represents the definition of a relational database datatype.
 */

public class DataType {

  protected String name;
  protected String type;
  protected String size;
  protected boolean variable;

  protected Map properties;
  
  public DataType() {
  }

  /**
   * INTERNAL: Gets the name of the datatype.
   */
  public String getName() {
    return name;
  }

  /**
   * INTERNAL: Sets the name of the datatype.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * INTERNAL: Gets the table properties.
   */
  public Collection getProperties() {
    return (properties == null ? Collections.EMPTY_SET : properties.keySet());
  }

  /**
   * INTERNAL: Gets the property value
   */
  public String getProperty(String property) {
    if (properties == null)
      return null;
    else
      return (String)properties.get(property);
  }

  /**
   * INTERNAL: Adds table property.
   */
  public void addProperty(String property, String value) {
    if (properties == null)
      properties = new HashMap();
    properties.put(property, value);
  }
  
  /**
   * INTERNAL: Removes table property.
   */
  public void removeProperty(String property, String value) {
    if (properties == null) return;
    properties.remove(property);
    if (properties.isEmpty())
      properties = null;
  }

  /**
   * INTERNAL: Gets the datatype type.
   */
  public String getType() {
    return type;
  }

  /**
   * INTERNAL: Sets the datatype type.
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * INTERNAL: Gets the datatype size.
   */
  public String getSize() {
    return size;
  }

  /**
   * INTERNAL: Sets the datatype size.
   */
  public void setSize(String size) {
    this.size = size;
  }

  /**
   * INTERNAL: Returns true if the database is a variable sized
   * datatype (i.e. not constant).
   */
  public boolean isVariable() {
    return variable;
  }

  /**
   * INTERNAL: Sets whether the datatype is a variable sized datatype
   * or not.
   */
  public void setVariable(boolean variable) {
    this.variable = variable;
  }
  
}





