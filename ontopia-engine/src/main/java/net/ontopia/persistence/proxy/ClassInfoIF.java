// $Id: ClassInfoIF.java,v 1.8 2006/01/20 16:49:46 grove Exp $

package net.ontopia.persistence.proxy;

/**
 * INTERNAL: A interface for class descriptor-like object types that
 * is used by the proxy implementation to access the information it
 * needs about the object relational class descriptor in an optimized
 * manner.<p>
 */

public interface ClassInfoIF {

  /**
   * Flag indicating that the descriptor class is identifiable.
   */
  public static final int TYPE_IDENTIFIABLE = 1;
  
  /**
   * Flag indicating that the descriptor class is an aggregate.
   */
  public static final int TYPE_AGGREGATE = 2;
  
  //! /**
  //!  * Flag indicating that the descriptor class is an aggregrate.
  //!  */
  //! public static final int PRIMITIVE = 4;

  /**
   * Flag indicating that the descriptor class has object structure.
   */
  public static final int STRUCTURE_OBJECT = 8;
  
  /**
   * Flag indicating that the descriptor class has collection structure.
   */
  public static final int STRUCTURE_COLLECTION = 16;
  
  //! /**
  //!  * Flag indicating that the descriptor class has map structure.
  //!  */
  //! public static final int STRUCTURE_MAP = 32;

  /**
   * INTERNAL: Returns the owning object relational mapping instance.
   */
  public ObjectRelationalMappingIF getMapping();

  /**
   * INTERNAL: Returns the name of the descriptor class (the mapped
   * class).
   */
  public String getName();

  /**
   * INTERNAL: Return the descriptor class described by the
   * descriptor.
   */
  public Class getDescriptorClass();

  /**
   * INTERNAL: Creates an instance of the descriptor class. Actual
   * implementation will depend on the immutable argument.
   */  
  public Object createInstance(boolean immutable) throws Exception;
  /**
   * INTERNAL: Get the field info by name.
   */
  public FieldInfoIF getFieldInfoByName(String name);
  
  /**
   * INTERNAL: Get the identity field info. Note that this field info
   * is a wrapper instance that wraps the value field infos
   * representing the fields used to represent the object identity.
   */
  public FieldInfoIF getIdentityFieldInfo();
  
  /**
   * INTERNAL: Get the value field infos. The field infos are returned
   * in the same order as specified by their index property.
   */
  public FieldInfoIF[] getValueFieldInfos();
  
  /**
   * INTERNAL: Get the 1:1 field infos.
   */
  public FieldInfoIF[] getOne2OneFieldInfos();
  
  /**
   * INTERNAL: Get the 1:M field infos.
   */
  public FieldInfoIF[] getOne2ManyFieldInfos();
  
  /**
   * INTERNAL: Get the M:M field infos.
   */
  public FieldInfoIF[] getMany2ManyFieldInfos();

  /**
   * INTERNAL: Returns true if the descriptor class is declared as an
   * abstract descriptor.
   */
  public boolean isAbstract();

  /**
   * INTERNAL: Returns true if the descriptor class is declared as a
   * an identifiable type.
   */
  public boolean isIdentifiable();

  /**
   * INTERNAL: Returns true if the descriptor class is declared as an
   * aggregate type.
   */
  public boolean isAggregate();
  
  //! /**
  //!  * INTERNAL: Returns true if the descriptor class is declared as a
  //!  * primitive type.
  //!  */
  //! public boolean isPrimitive();

  //! /**
  //!  * INTERNAL: Returns the type of the descriptor class. This can
  //!  * either be IDENTIFIABLE, AGGREGATE, or PRIMITIVE.
  //!  */
  //! public int getType();

  /**
   * INTERNAL: Returns the structure of the descriptor class. This can
   * either be OBJECT, COLLECTION or MAP.
   */
  public int getStructure();
  
  //! public boolean isTypeObject();
  //! public boolean isTypeCollection();
  //! public boolean isTypeMap();    
  
  /**
   * INTERNAL: Returns the name of the master table in which the class
   * is stored. This is the table which typically contains the
   * instance identity.
   */
  public String getMasterTable();

  //! public IdentityIF createIdentity(Object key);
  //! 
  //! public IdentityIF createIdentity(Object[] keys);

}
