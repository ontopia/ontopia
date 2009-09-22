package net.ontopia.topicmaps.query.toma.impl.basic;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Map;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.ParsedQueryIF;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.toma.impl.basic.expression.PathExpression;
import net.ontopia.topicmaps.query.toma.parser.LocalParseContext;
import net.ontopia.topicmaps.query.toma.parser.TomaParser;
import net.ontopia.topicmaps.query.toma.parser.ast.ExpressionIF;
import net.ontopia.topicmaps.query.toma.parser.ast.SelectStatement;
import net.ontopia.topicmaps.query.toma.parser.ast.TomaQuery;

public class BasicQueryProcessor implements QueryProcessorIF {

  private TopicMapIF topicmap;

  public BasicQueryProcessor(TopicMapIF topicmap) {
    this.topicmap = topicmap;
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

  public QueryResultIF execute(TomaQuery query) throws InvalidQueryException {
    for (int i = 0; i < query.getStatementCount(); i++) {
      SelectStatement stmt = query.getStatement(i);
      return new QueryResult(evaluateSelect(stmt));
    }
    return null;
  }

  private ResultSet evaluateSelect(SelectStatement stmt) {
    BasicExpressionIF expr = (BasicExpressionIF) stmt.getClause();
    LocalContext context = new LocalContext(topicmap);

    // set column names
    ResultSet rs = new ResultSet(stmt.getSelectCount(), stmt.isDistinct());
    for (int i = 0; i < stmt.getSelectCount(); i++) {
      BasicExpressionIF selectPath = (BasicExpressionIF) stmt.getSelect(i);
      rs.setColumnName(i, selectPath.toString());
    }

    // get first select expression
    BasicExpressionIF selectExpr = (BasicExpressionIF) stmt.getSelect(0);
    ResultSet bound = null;

    if (expr != null) {
      try {
        bound = expr.evaluate(context);
      } catch (InvalidQueryException e) {
        // TODO: error handling
        e.printStackTrace();
      }
    } else {
      try {
        bound = selectExpr.evaluate(context);
      } catch (InvalidQueryException e) {
        // TODO: error handling
        e.printStackTrace();
      }
    }

    Collection<?> boundVariable = null;
    String boundName;
    if (selectExpr instanceof PathExpression) {
      boundName = ((PathExpression) selectExpr).getRoot().toString();
    } else {
      while (!(selectExpr instanceof PathExpression)) {
        selectExpr = (BasicExpressionIF) selectExpr.getChild(0);
      }
      boundName = ((PathExpression) selectExpr).getRoot().toString();
    }

    if (bound.containsColumn(boundName)) {
      boundVariable = bound.getValues(boundName);
    } else {
    }

    for (Object obj : boundVariable) {
      Row r = rs.createRow();
      fillFinalResultSet(context, 0, obj, r, rs, stmt);
    }

    return rs;
  }

  private void fillFinalResultSet(LocalContext context, int index,
      Object input, Row row, ResultSet rs, SelectStatement stmt) {
    if (index < stmt.getSelectCount()) {
      BasicExpressionIF selectExpr = (BasicExpressionIF) stmt.getSelect(index);
      PathExpression pathExpr;
      if (selectExpr instanceof PathExpression) {
        pathExpr = (PathExpression) selectExpr;
      } else {
        pathExpr = (PathExpression) selectExpr.getChild(0);
      }
      Collection<?> values = pathExpr.evaluate(context, input);
      for (Object v : values) {
        // ignore null values in the first select clause
        if (v == null && index == 0) continue;
        
        try {
          Row newRow = (Row) row.clone();

          if (selectExpr instanceof BasicFunctionIF) {
            v = ((BasicFunctionIF) selectExpr).evaluate(v);
          }

          newRow.setValue(index, v);
          fillFinalResultSet(context, index + 1, input, newRow, rs, stmt);
        } catch (Exception e) {
          // TODO: better error handling
          e.printStackTrace();
        }
      }
    } else {
      rs.addRow(row);
    }
  }

  public QueryResultIF execute(String query) throws InvalidQueryException {
    return execute(query, null, null);
  }

  public void load(Reader ruleset) throws InvalidQueryException, IOException {
  }

  public void load(String ruleset) throws InvalidQueryException {
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
