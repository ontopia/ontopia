package net.ontopia.topicmaps.query.toma.parser.ast;

import java.util.ArrayList;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

/**
 * INTERNAL: Represents a single select statement within a TOMA query. A Toma
 * query can contain multiple nested select statements as well as select
 * statements combined in a union-type style.
 */
public class SelectStatement implements ASTElementIF {
  public enum UNION_TYPE {
    NOUNION, UNION, UNIONALL, INTERSECT, EXCEPT
  };

  private boolean distinct;
  private ArrayList<ExpressionIF> selects;
  private ExpressionIF clause;
  private UNION_TYPE unionType;

  /**
   * Create a new empty Select Statement.
   */
  public SelectStatement() {
    distinct = false;
    selects = new ArrayList<ExpressionIF>();
    clause = null;
    unionType = UNION_TYPE.NOUNION;
  }

  /**
   * Set the distinct flag for this select statement, indicating whether
   * duplicate rows are present in the output or not.
   * 
   * @param enabled whether distinct behavior should be enabled or not.
   */
  public void setDistinct(boolean enabled) {
    distinct = enabled;
  }

  /**
   * Check whether distinct behavior is enabled or not.
   * 
   * @return the current distinct behavior.
   */
  public boolean isDistinct() {
    return distinct;
  }

  /**
   * Set the union type for this select statement.
   * 
   * @param type the union type to be set.
   */
  public void setUnionType(UNION_TYPE type) {
    unionType = type;
  }

  /**
   * Get the union type for this select statement.
   * 
   * @return the union type.
   */
  public UNION_TYPE getUnionType() {
    return unionType;
  }

  /**
   * Add another select clause expression to this statement.
   * 
   * @param sp the select expression to be added.
   */
  public void addSelect(ExpressionIF sp) {
    selects.add(sp);
  }

  /**
   * Get the number of select clauses present.
   * 
   * @return the number of select clauses.
   */
  public int getSelectCount() {
    return selects.size();
  }

  /**
   * Get the select clause at the given index.
   * 
   * @param index the specified index.
   * @return the select clause at the given index or null if the index is out of
   *         range.
   */
  public ExpressionIF getSelect(int index) {
    try {
      return selects.get(index);
    } catch (IndexOutOfBoundsException e) {
      return null;
    }
  }

  /**
   * Set the where clause for this select statement. As the where clauses are
   * structured in a tree, there is only one root clause that contains all other
   * available clauses.
   * 
   * @param clause the expression to be used as a where clause.
   */
  public void setClause(ExpressionIF clause) {
    this.clause = clause;
  }

  /**
   * Get the where clause for this select statement.
   * 
   * @return the where clause expression.
   */
  public ExpressionIF getClause() {
    return this.clause;
  }

  public boolean isAggregated() {
    // validate all select projections
    int numAggregate = 0;
    for (ExpressionIF expr : selects) {
      if (expr instanceof FunctionIF) {
        if (((FunctionIF) expr).isAggregateFunction()) {
          numAggregate++;
        }
      }
    }

    return (numAggregate > 0);
  }
  
  public boolean validate() throws AntlrWrapException {
    // validate all select projections
    int numAggregate = 0;
    for (ExpressionIF expr : selects) {
      if (expr instanceof FunctionIF) {
        if (((FunctionIF) expr).isAggregateFunction()) {
          numAggregate++;
        }
      }
      expr.validate();
    }

    if (numAggregate > 0 && numAggregate != selects.size()) {
      throw new AntlrWrapException(new InvalidQueryException(
          "All select-clauses must contain a aggregate function."));
    }
    
    // TODO: check that no aggregate functions are used in the where clause.
    
    // validate the where clauses
    clause.validate();
    
    return true;
  }

  public void fillParseTree(IndentedStringBuilder buf, int level) {
    switch (unionType) {
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
    for (ExpressionIF path : selects) {
      path.fillParseTree(buf, level + 1);
    }

    if (clause != null) {
      buf.append("(     WHERE)", level);
      clause.fillParseTree(buf, level + 1);
    }
  }
}
