/**
 * TMQL4J Plugin for Ontopia
 * 
 * Copyright: Copyright 2009 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Author: Sven Krosse
 * 
 */
package net.ontopia.topicmaps.query.tmql.impl.util;

import java.util.LinkedList;
import java.util.List;

import net.ontopia.topicmaps.query.tmql.impl.tmql4jextension.WrappedOntopiaResult;
import net.ontopia.topicmaps.query.tmql.impl.tmql4jextension.WrappedOntopiaResultSet;
import de.topicmapslab.tmql4j.common.core.exception.TMQLRuntimeException;
import de.topicmapslab.tmql4j.common.core.process.TMQLRuntime;
import de.topicmapslab.tmql4j.parser.core.expressions.Content;
import de.topicmapslab.tmql4j.parser.core.expressions.FlwrExpression;
import de.topicmapslab.tmql4j.parser.core.expressions.PathExpression;
import de.topicmapslab.tmql4j.parser.core.expressions.Postfix;
import de.topicmapslab.tmql4j.parser.core.expressions.PostfixedExpression;
import de.topicmapslab.tmql4j.parser.core.expressions.PredicateInvocation;
import de.topicmapslab.tmql4j.parser.core.expressions.ProjectionPostfix;
import de.topicmapslab.tmql4j.parser.core.expressions.QueryExpression;
import de.topicmapslab.tmql4j.parser.core.expressions.ReturnClause;
import de.topicmapslab.tmql4j.parser.core.expressions.SelectClause;
import de.topicmapslab.tmql4j.parser.core.expressions.SelectExpression;
import de.topicmapslab.tmql4j.parser.core.expressions.SimpleContent;
import de.topicmapslab.tmql4j.parser.core.expressions.TupleExpression;
import de.topicmapslab.tmql4j.parser.model.IExpression;
import de.topicmapslab.tmql4j.parser.model.IParserTree;

public class ColumnHeaderGenerator {

	private ColumnHeaderGenerator() {

	}

	public static final List<String> columnHeaders(TMQLRuntime runtime)
			throws TMQLRuntimeException {
		IParserTree tree = (IParserTree) runtime
				.getStoredValue(TMQLRuntime.TMQL_RUNTIME_PARSER_TREE);
		IExpression query = tree.root();
		return columnHeaders(runtime, query);
	}

	private static final List<String> columnHeaders(TMQLRuntime runtime,
			IExpression query) throws TMQLRuntimeException {
		List<SelectExpression> selectExpressions = query
				.subExpressionsByType(SelectExpression.class);
		if (!selectExpressions.isEmpty()) {
			return columnHeadersOfSelectExpression(runtime, selectExpressions
					.get(0));
		} else {
			List<FlwrExpression> flwrExpressions = query
					.subExpressionsByType(FlwrExpression.class);
			if (!flwrExpressions.isEmpty()) {
				return columnHeadersOfFlwrExpression(runtime, flwrExpressions
						.get(0));
			} else {
				List<PathExpression> expressions = query
						.subExpressionsByType(PathExpression.class);
				if (!expressions.isEmpty()) {
					return columnHeadersOfPathExpression(runtime, expressions
							.get(0));
				} else {
					throw new TMQLRuntimeException(
							"Invalid parser-tree structure.");
				}

			}
		}
	}

	private static final List<String> columnHeadersOfSelectExpression(
			TMQLRuntime runtime, IExpression expression)
			throws TMQLRuntimeException {
		List<String> headers = new LinkedList<String>();
		for (IExpression ex : expression.subExpressionsByType(
				SelectClause.class).get(0).subExpressions()) {
			headers.add(expressionToString(ex));
		}
		return headers.isEmpty() ? defaultColumnHeaders(runtime) : headers;
	}

	private static final List<String> columnHeadersOfFlwrExpression(
			TMQLRuntime runtime, IExpression expression)
			throws TMQLRuntimeException {
		List<String> headers = new LinkedList<String>();
		IExpression returnClause = expression.subExpressionsByType(
				ReturnClause.class).get(0);
		IExpression contentClause = returnClause.subExpressionsByType(
				Content.class).get(0);
		if (contentClause.subExpressionsByType(QueryExpression.class).isEmpty()) {
			headers.add(expressionToString(contentClause));
		} else {
			headers.addAll(columnHeaders(runtime, contentClause
					.subExpressionsByType(QueryExpression.class).get(0)));
		}

		return headers.isEmpty() ? defaultColumnHeaders(runtime) : headers;
	}

	private static final List<String> columnHeadersOfPathExpression(
			TMQLRuntime runtime, IExpression expression)
			throws TMQLRuntimeException {
		List<String> headers = new LinkedList<String>();
		IExpression path = expression;

		if (!path.subExpressionsByType(PredicateInvocation.class).isEmpty()) {
			headers.add(expressionToString(path));
		} else {
			IExpression postfixedExpression = path.subExpressionsByType(
					PostfixedExpression.class).get(0);
			/*
			 * tuple-expression | simple-content {postfix}
			 */
			List<String> filters = new LinkedList<String>();
			if (!postfixedExpression.subExpressionsByType(Postfix.class)
					.isEmpty()) {
				IExpression postfix = postfixedExpression.subExpressionsByType(
						Postfix.class).get(0);
				if (postfix.getType() == 1) {
					IExpression tupleExpression = postfix.subExpressionsByType(
							ProjectionPostfix.class).get(0)
							.subExpressionsByType(TupleExpression.class).get(0);
					for (IExpression valueExpression : tupleExpression
							.subExpressions()) {
						filters.add("(" + expressionToString(valueExpression)
								+ ")");
					}
				} else {
					filters.add(expressionToString(postfix));
				}
			}
			/*
			 * tuple-expression
			 */
			if (postfixedExpression.getType() == 0) {
				IExpression tupleExpression = postfixedExpression
						.subExpressionsByType(TupleExpression.class).get(0);
				for (IExpression valueExpression : tupleExpression
						.subExpressions()) {
					String prefix = expressionToString(valueExpression);
					if (filters.isEmpty()) {
						headers.add(prefix);
					} else {
						for (String filter : filters) {
							headers.add(prefix + filter);
						}
					}
				}
			}
			/*
			 * simple-content
			 */
			else {
				String prefix = expressionToString(postfixedExpression
						.subExpressionsByType(SimpleContent.class).get(0));
				if (filters.isEmpty()) {
					headers.add(prefix);
				} else {
					for (String filter : filters) {
						headers.add(prefix + filter);
					}
				}

			}
		}
		return headers.isEmpty() ? defaultColumnHeaders(runtime) : headers;
	}

	private static final List<String> defaultColumnHeaders(TMQLRuntime runtime)
			throws TMQLRuntimeException {
		List<String> headers = new LinkedList<String>();
		WrappedOntopiaResultSet resultSet = (WrappedOntopiaResultSet) runtime
				.getStoredValue(TMQLRuntime.TMQL_RUNTIME_RESULTPROCESSING_RESULT);
		if (resultSet != null) {
			long size = 0;
			if (resultSet.size() > 0) {
				size = ((WrappedOntopiaResult) resultSet.first()).size();
			}
			for (int index = 0; index < size; index++) {
				headers.add("Column " + index);
			}
		}
		return headers;
	}

	private static final String expressionToString(IExpression expression) {
		StringBuilder builder = new StringBuilder();
		for (String token : expression.tokens()) {
			builder.append(token + " ");
		}
		return builder.toString().trim();
	}

}
