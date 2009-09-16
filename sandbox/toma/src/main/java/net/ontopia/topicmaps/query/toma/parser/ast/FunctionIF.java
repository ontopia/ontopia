package net.ontopia.topicmaps.query.toma.parser.ast;

import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;

public interface FunctionIF extends ExpressionIF {
  public void addParam(String param) throws AntlrWrapException;
}
