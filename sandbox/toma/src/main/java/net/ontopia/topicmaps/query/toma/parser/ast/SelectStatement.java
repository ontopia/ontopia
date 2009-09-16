package net.ontopia.topicmaps.query.toma.parser.ast;

import java.util.ArrayList;

import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

public class SelectStatement
{
  public enum UNION_TYPE
  {
    NOUNION,
    UNION,
    UNIONALL,
    INTERSECT,
    EXCEPT
  };
  
  private boolean distinct;
  private ArrayList<ExpressionIF> selects;
  private ExpressionIF clause;
  private UNION_TYPE unionType;

  public SelectStatement() {
    distinct = false;
    selects = new ArrayList<ExpressionIF>();
    clause = null;
    unionType = UNION_TYPE.NOUNION;
  }

  public void setDistinct(boolean enabled) {
    distinct = enabled;
  }
  
  public boolean isDistinct() {
    return distinct;
  }
  
  public void setUnionType(UNION_TYPE type) {
    unionType = type;
  }
  
  public UNION_TYPE getUnionType() {
    return unionType;
  }
  
  public void addSelect(ExpressionIF sp) {
    selects.add(sp);
  }
  
  public int getSelectCount() {
    return selects.size();
  }
  
  public ExpressionIF getSelect(int index) {
    return selects.get(index);
  }
  
  public void setClause(ExpressionIF clause) {
    this.clause = clause;
  }
  
  public ExpressionIF getClause() {
    return this.clause;
  }
  
  public void fillParseTree(IndentedStringBuilder buf, int level) {
    switch (unionType)
    {
    case UNION:
      buf.append("(     UNION)", level);
      break;
      
    case UNIONALL:
      buf.append("(     UNION) [ALL]", level);
      break;
      
    case INTERSECT:
      buf.append("(    EXCEPT)", level);
      break;
      
    case EXCEPT:
      buf.append("( INTERSECT)", level);
      break;
    }
    
    buf.append("(    SELECT) [" + (distinct ? "DISTINCT" : "ALL") + "]", level);
    for (ExpressionIF path : selects)
    {
      path.fillParseTree(buf, level+1);
    }
    
    if (clause != null)
    {
      buf.append("(     WHERE)", level);
      clause.fillParseTree(buf, level+1);
    }
  }
}
