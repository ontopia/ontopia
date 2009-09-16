package net.ontopia.topicmaps.query.toma.parser.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

public abstract class AbstractExpression implements ExpressionIF 
{
  protected String name;
  protected ArrayList<ExpressionIF> childs;

  protected AbstractExpression(String name)
  {
    this.name = name;
    childs = new ArrayList<ExpressionIF>();
  }
  
  public String getName() 
  {
    return name;
  }

  public void addChild(ExpressionIF child) throws AntlrWrapException 
  {
    childs.add(child);
  }
  
  public int getChildCount()
  {
    return childs.size();
  }
  
  public ExpressionIF getChild(int idx) {
    return childs.get(idx);
  }
  
  public List<ExpressionIF> getChilds()
  {
    return Collections.unmodifiableList(childs);
  }
  
  public void fillParseTree(IndentedStringBuilder buf, int level) 
  {
    switch (childs.size())
    {
    case 0:
      buf.append(String.format("(%1$10s)", getName()), level);
      break;
      
    case 1:
      buf.append(String.format("(%1$10s)", getName()), level);
      childs.get(0).fillParseTree(buf, level+1);
      break;
      
    case 2:
      childs.get(0).fillParseTree(buf, level+1);
      buf.append(String.format("(%1$10s)", getName()), level);
      childs.get(1).fillParseTree(buf, level+1);
      break;
      
    default:
      buf.append(String.format("(%1$10s)", getName()), level);
      for (ExpressionIF child : childs)
      {
        child.fillParseTree(buf, level+1);
      }
      break;  
    }
  }
  
  public String toString()
  {
    return getName();
  }
}
