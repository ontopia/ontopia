package net.ontopia.topicmaps.query.toma.parser;

import antlr.RecognitionException;

/**
 * INTERNAL: Exception used to wrap other exceptions so that they can
 * be thrown from inside ANTLR-generated code.
 */
@SuppressWarnings("serial")
public class AntlrWrapException extends RecognitionException 
{
  public Exception exception;

  public AntlrWrapException(Exception exception) {
    this.exception = exception;
  }

  public Exception getException() {
    return exception;
  }
}
