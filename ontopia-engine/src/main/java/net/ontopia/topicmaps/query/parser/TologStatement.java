
package net.ontopia.topicmaps.query.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.ontopia.topicmaps.query.core.InvalidQueryException;

/**
 * INTERNAL: Common abstract superclass for all kinds of tolog
 * statements, including SELECT and the update statements.
 */
public abstract class TologStatement {
  protected TologOptions options;

  public TologStatement() {
  }
  
  public TologOptions getOptions() {
    return options;
  }
  
  public void setOptions(TologOptions options) {
    this.options = options;
  }

  public abstract void close() throws InvalidQueryException; // FIXME: not sure
  
}