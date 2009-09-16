package net.ontopia.topicmaps.query.toma.parser.ast;

import java.util.Set;

import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

public interface PathElementIF 
{
  public enum TYPE
  {
    TOPIC,
    ASSOCIATION,
    NAME,
    VARIANT,
    OCCURRENCE,
    LOCATOR,
    STRING
  };
  
  public void setLevel(Level l) throws AntlrWrapException;
  public void setType(PathExpressionIF path) throws AntlrWrapException;
  public void setScope(PathExpressionIF path) throws AntlrWrapException;
  public void bindVariable(PathRootIF name) throws AntlrWrapException;
  
  public void addChild(PathExpressionIF path) throws AntlrWrapException;

  public Set<TYPE> validInput();
  public TYPE output();
  
  public void fillParseTree(IndentedStringBuilder buf, int level);
}
