package net.ontopia.topicmaps.query.toma.impl.utils;

import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.parser.ast.ExpressionIF;

/**
 * INTERNAL: Used for testing and timing of queries.
 */
public interface QueryTraceListenerIF {
  public void startQuery();

  public void endQuery();

  public void enter(ExpressionIF expr);

  public void leave(ResultSet result);

  public void enterOrderBy();

  public void leaveOrderBy();

  public void enterSelect(ResultSet result);

  public void leaveSelect(ResultSet result);

  public void trace(String message);
}
