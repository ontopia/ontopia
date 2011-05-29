
package net.ontopia.persistence.proxy;

  
/**
 * INTERNAL: Interface for receiving notification when data is being
 * read from database storage. This interface is usually implemented
 * by the various cache implementations, which maintains a copy of the
 * known database state.<p>
 */

public interface AccessRegistrarIF {

  // -----------------------------------------------------------------------------
  // FieldAccessIF callbacks
  // -----------------------------------------------------------------------------

  /**
   * INTERNAL: Factory method for creating new identity objects. Key
   * is guaranteed to have a width of 1 and key value which a Long.
   */
  public IdentityIF createIdentity(Object type, long key);

  /**
   * INTERNAL: Factory method for creating new identity objects. Key
   * is guaranteed to have a width of 1.
   */
  public IdentityIF createIdentity(Object type, Object key);
  
  /**
   * INTERNAL: Factory method for creating new identity objects. Key
   * can have any width.
   */
  public IdentityIF createIdentity(Object type, Object[] keys);

  /**
   * INTERNAL: Get ticket that should be used as first argument to
   * register methods. The ticket is used figure out if value should
   * be registered or not.
   */
  public TicketIF getTicket();
  
  /**
   * INTERNAL: Called by storage accessors (QueryIFs or FieldAccessIF)
   * when they locate the identity of an object in the database.
   */  
  public void registerIdentity(TicketIF ticket, IdentityIF identity);

  /**
   * INTERNAL: Called by storage accessors (FieldAccessIF) when they
   * read the value of an object field from the database.
   */  
  public void registerField(TicketIF ticket, IdentityIF identity, int field, Object value);
  
}
