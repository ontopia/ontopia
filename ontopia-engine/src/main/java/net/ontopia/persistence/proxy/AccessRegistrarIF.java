/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

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
  public IdentityIF createIdentity(Class<?> type, long key);

  /**
   * INTERNAL: Factory method for creating new identity objects. Key
   * is guaranteed to have a width of 1.
   */
  public IdentityIF createIdentity(Class<?> type, Object key);
  
  /**
   * INTERNAL: Factory method for creating new identity objects. Key
   * can have any width.
   */
  public IdentityIF createIdentity(Class<?> type, Object[] keys);

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
