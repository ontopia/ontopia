
// $Id: AntlrWrapException.java,v 1.5 2004/11/29 09:53:50 larsga Exp $

package net.ontopia.topicmaps.query.parser;

import antlr.RecognitionException;

/**
 * INTERNAL: Exception used to wrap other exceptions so that they can
 * be thrown from inside ANTLR-generated code.
 */
public class AntlrWrapException extends RecognitionException {
  public Exception exception;

  public AntlrWrapException(Exception exception) {
    this.exception = exception;
  }

  public Exception getException() {
    return exception;
  }
}
