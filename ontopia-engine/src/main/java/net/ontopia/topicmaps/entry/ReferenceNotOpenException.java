
// $Id: ReferenceNotOpenException.java,v 1.2 2004/11/19 12:52:47 grove Exp $

package net.ontopia.topicmaps.entry;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: An exception that is thrown when a topic map reference is
 * accessed after the reference has been close.</p>
 *
 * Extends OntopiaRuntimeException but does not provide additional
 * methods; this is because the purpose is just to provide a different
 * exception, to allow API users to handle them differently.</p>
 */

public class ReferenceNotOpenException extends OntopiaRuntimeException {
  private static final long serialVersionUID = 1L;

  public ReferenceNotOpenException() {
    super("Cannot access topic map reference after it has been closed.");
  }
  
}





