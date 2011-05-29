
package net.ontopia.topicmaps.query.parser;

import java.util.Collection;
import java.util.List;

/**
 * INTERNAL: Common superclass for or clauses and other kinds of clauses.
 */
public abstract class AbstractClause {

  /**
   * INTERNAL: Returns all the variables bound by this clause when it
   * is satisfied.
   */
  public abstract Collection getAllVariables();

  /**
   * INTERNAL: Returns all the literals used by this clause as
   * parameters. (Literals in the second half of pair arguments are
   * ignored.)
   */
  public abstract Collection getAllLiterals();

  /**
   * INTERNAL: Returns the arguments of this clause. For OrClause this
   * is the list of all arguments to all the subclauses in the OrClause.
   * Likewise for NotClause.
   */
  public abstract List getArguments();
  
}
