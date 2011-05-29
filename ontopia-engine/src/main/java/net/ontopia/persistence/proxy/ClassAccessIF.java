
package net.ontopia.persistence.proxy;

import java.util.Collection;

/**
 * INTERNAL: Interface for accessing class instances in the
 * database. This include loading, creating, and deleting objects.
 */

public interface ClassAccessIF {

  /**
   * INTERNAL: Loads the object identity from the database.
   *
   * @return true if object was found in the data store, false
   * otherwise.
   */
  public boolean load(AccessRegistrarIF registrar, IdentityIF identity) throws Exception;

  /**
   * INTERNAL: Loads the specified object field for the given identity
   * from the database.
   */
  public Object loadField(AccessRegistrarIF registrar, IdentityIF identity, int field) throws Exception;

  /**
   * INTERNAL: Loads the specified object field for the given
   * identitys from the database.
   */
  public Object loadFieldMultiple(AccessRegistrarIF registrar, Collection identities, 
				  IdentityIF current, int field) throws Exception;
  
  /**
   * INTERNAL: Creates the new object in the database. Note that the
   * object identity can be extracted from the object using the
   * supplied object access instance.
   */
  public void create(ObjectAccessIF oaccess, Object object) throws Exception;

  /**
   * INTERNAL: Deletes the object identity from the database.
   */
  public void delete(ObjectAccessIF oaccess, Object object) throws Exception;

  /**
   * INTERNAL: Stores object fields that are dirty in the
   * database. Note that the object identity can be extracted from the
   * object using the supplied object access instance.
   */
  public void storeDirty(ObjectAccessIF oaccess, Object object) throws Exception;

}






