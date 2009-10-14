package net.ontopia.topicmaps.query.toma.impl.basic;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.emory.mathcs.backport.java.util.Collections;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.ParsedQueryIF;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.toma.parser.LocalParseContext;
import net.ontopia.topicmaps.query.toma.parser.TomaParser;
import net.ontopia.topicmaps.query.toma.parser.ast.QueryOrder;
import net.ontopia.topicmaps.query.toma.parser.ast.SelectStatement;
import net.ontopia.topicmaps.query.toma.parser.ast.TomaQuery;

/**
 * PUBLIC: QueryProcessor implementation for the TOMA query language. This is
 * the basic implementation, that should be used for topic maps stored in a
 * memory backend. For database backends, this QueryProcessor will be quite
 * slow.
 */
public class BasicQueryProcessor implements QueryProcessorIF {

  private TopicMapIF topicmap;

  public BasicQueryProcessor(TopicMapIF topicmap) {
    this.topicmap = topicmap;
  }

  public int update(String query) throws InvalidQueryException {
    throw new InvalidQueryException("Not implemented yet.");
  }

  public QueryResultIF execute(String query, Map<String, ?> arguments,
      DeclarationContextIF context) throws InvalidQueryException {
    ParsedQueryIF pq = parse(query, context);
    return pq.execute();
  }

  public QueryResultIF execute(String query, DeclarationContextIF context)
      throws InvalidQueryException {
    return execute(query, null, context);
  }

  public QueryResultIF execute(String query, Map<String, ?> arguments)
      throws InvalidQueryException {
    return execute(query, arguments, null);
  }

  /**
   * Executes the parsed TOMA query and returns a QueryResultIF object.
   *
   * @param query the parsed TOMA query.
   * @return the result of the query.
   * @throws InvalidQueryException if the query can not be evaluated properly.
   */
  public QueryResultIF execute(TomaQuery query) throws InvalidQueryException {
    ResultSet rs = null;
    for (int i = 0; i < query.getStatementCount(); i++) {
      SelectStatement stmt = query.getStatement(i);

      ResultSet curr = satisfy(stmt);
      curr = aggregate(stmt, curr);

      if (rs == null) {
        rs = curr;
      } else {
        switch (stmt.getUnionType()) {
        case UNION:
          rs.union(curr, true);
          break;
        case UNIONALL:
          rs.union(curr, false);
          break;
        case INTERSECT:
          rs.intersect(curr);
          break;
        case EXCEPT:
          rs.except(curr);
          break;
        }
      }
    }

    List<Row> rows = convertToList(rs);
    sort(rows, query.getOrderBy());
    return new QueryResult(rs.getColumnDefinitions(), rows, query.getLimit(),
        query.getOffset());
  }

  private ResultSet satisfy(SelectStatement stmt) throws InvalidQueryException {
    BasicExpressionIF expr = (BasicExpressionIF) stmt.getClause();
    LocalContext context = new LocalContext(topicmap);

    // set column names
    ResultSet rs = new ResultSet(stmt.getSelectCount(), stmt.isDistinct());
    for (int i = 0; i < stmt.getSelectCount(); i++) {
      BasicExpressionIF selectPath = (BasicExpressionIF) stmt.getSelect(i);
      rs.setColumnName(i, selectPath.toString());
    }

    // evaluate WHERE expression
    expr.evaluate(context);

    Row r = rs.createRow();
    calculateMatches(context, 0, r, rs, stmt);

    return rs;
  }

  private void calculateMatches(LocalContext context, int index, Row row,
      ResultSet rs, SelectStatement stmt) throws InvalidQueryException {
    BasicExpressionIF expr = (BasicExpressionIF) stmt.getSelect(index);
    ResultSet values = expr.evaluate(context);
    try {
      LocalContext newContext = (LocalContext) context.clone();
      
      for (Row r : values) {
        Object val = r.getLastValue();
        // ignore null values in the first select clause
        if (val == null && index == 0) {
          continue;
        }

        Row newRow = (Row) row.clone();
        newRow.setValue(index, val);

        // TODO: make code cleaner
        ResultSet newRS = new ResultSet(1, false);
        newRS.setColumnName(0, values.getColumnName(0));
        Row myRow = newRS.createRow();
        myRow.setValue(0, r.getFirstValue());
        newRS.addRow(myRow);
        newContext.addResultSet(newRS);

        if (index < (stmt.getSelectCount() - 1)) {
          calculateMatches(newContext, index + 1, newRow, rs, stmt);
        } else {
          rs.addRow(newRow);
        }
      }
    } catch (CloneNotSupportedException e) {
      throw new InvalidQueryException("Internal QueryProcessor error", e);
    }
  }

  private ResultSet aggregate(SelectStatement stmt, ResultSet rs)
      throws InvalidQueryException {

    // if the select is aggregated, evaluate the aggregate functions now;
    // otherwise just return the ResultSet.
    if (stmt.isAggregated()) {
      ResultSet result = new ResultSet(rs);

      Row aggregatedRow = result.createRow();
      for (int i = 0; i < stmt.getSelectCount(); i++) {
        // we know, that it has to be a FunctionIF
        BasicFunctionIF expr = (BasicFunctionIF) stmt.getSelect(i);
        aggregatedRow.setValue(i, expr.aggregate(rs.getValues(i)));
      }
      result.addRow(aggregatedRow);
      return result;
    } else {
      return rs;
    }
  }

  private List<Row> convertToList(ResultSet rs) {
    List<Row> l = new ArrayList<Row>(rs.getRowCount());
    for (Row r : rs) {
      l.add(r);
    }
    return l;
  }

  private void sort(List<Row> matches, List<QueryOrder> orderings) {
    if (!orderings.isEmpty())
      Collections.sort(matches, new RowComparator(orderings));
  }

  public QueryResultIF execute(String query) throws InvalidQueryException {
    return execute(query, null, null);
  }

  /**
   * Not supported.
   */
  public void load(Reader ruleset) throws InvalidQueryException, IOException {
    throw new InvalidQueryException("Not supported by this QueryProcessor");
  }

  /**
   * Not supported.
   */
  public void load(String ruleset) throws InvalidQueryException {
    throw new InvalidQueryException("Not supported by this QueryProcessor");
  }

  public ParsedQueryIF parse(String query, DeclarationContextIF context)
      throws InvalidQueryException {
    ExpressionFactory ef = new ExpressionFactory();
    PathExpressionFactory pef = new PathExpressionFactory();
    LocalParseContext lc = new LocalParseContext(pef, ef);
    return new ParsedQuery(this, TomaParser.parse(query, lc));
  }

  public ParsedQueryIF parse(String query) throws InvalidQueryException {
    return parse(query, null);
  }
}
