
// $Id: QueryExecutingTag.java,v 1.16 2008/12/04 11:27:28 lars.garshol Exp $

package net.ontopia.topicmaps.nav2.taglibs.tolog;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.nav2.core.ContextManagerIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;

import org.apache.log4j.Logger;

/**
 * INTERNAL: Generic Tolog Tag that has support for executing one query.
 */
public abstract class QueryExecutingTag extends BodyTagSupport {
  // initialization of logging facility
  private static Logger log
          = Logger.getLogger(QueryExecutingTag.class.getName());

  // members
  protected String columnNames[];
  protected ContextManagerIF contextManager;
  protected ContextTag contextTag;
  protected QueryResultIF queryResult;

  // tag attributes
  protected String query;

  /**
   * Default constructor.
   */
  public QueryExecutingTag() {
    super();
  }

  /**
   * Process the start tag for this instance.
   * Post: queryResult and columnNames have values, but nothing is done to them.
   * It is up to the child class to make use of these.
   * Returns EVAL_BODY_BUFFERED by default.
   */
  public int doStartTag() throws JspTagException {
    this.contextTag = FrameworkUtils.getContextTag(pageContext);

    if (contextTag == null)
      throw new JspTagException("<tolog:*> tags must be nested directly or"
              + " indirectly within a <tolog:context> tag, but no"
              + " <tolog:context> tag was found.");

    this.contextManager = contextTag.getContextManager();
    if (query == null)
      throw new NavigatorRuntimeException("QueryExecutingTag didn't find "
              + "required parameter 'query'.");

    // get topicmap object on which we should compute
    TopicMapIF topicmap = contextTag.getTopicMap();
    if (topicmap == null)
      throw new NavigatorRuntimeException("QueryExecutingTag found no "
              + "topic map.");

    // Create a QueryProcessorIF for the topicmap.
    QueryProcessorIF queryProcessor = contextTag.getQueryProcessor();
    queryResult = contextTag.getQueryResult(query);
    if (queryResult == null)
      try {
        // Execute query, using any arguments from the context manager.
        queryResult = queryProcessor.execute(query,
                        new ContextManagerScopingMapWrapper(contextManager),
                        contextTag.getDeclarationContext());
      } catch (InvalidQueryException e) {
        log.debug("Parsing of query '" + query + "' failed with message: " + e);
        throw new NavigatorRuntimeException(e);
      }
    else
      ((BufferedQueryResultIF)queryResult).restart();

    columnNames = queryResult.getColumnNames();

    // Evaluate the body.
    return EVAL_BODY_BUFFERED;
  }

  /**
   * @return a reference to the tag attribute parameter query.
   */
  public String getQuery() {
    return query;
  }

  protected QueryResultIF getQueryResult() {
    return queryResult;
  }

  /**
   * Bind the variables of a result set to the current row.
   */
  protected void bindVariables() throws JspTagException {
    for (int i = 0; i < columnNames.length; i++) {
      Object currentValue = queryResult.getValue(columnNames[i]);
      contextManager.setValue(columnNames[i], currentValue == null
              ? Collections.EMPTY_LIST
              : currentValue);
    }
  }

  /**
   * Print the body content to the enclosing writer.
   * Return SKIP_BODY by default.
   */
  public int doAfterBody() throws JspTagException {
    // put out the evaluated body
    try {
      BodyContent body = getBodyContent();
      body.getEnclosingWriter().print( body.getString() );
    } catch (IOException ioe) {
      throw new NavigatorRuntimeException("Error in QueryExecutingTag.", ioe);
    }

    // Skip the body by default.
    return SKIP_BODY;
  }

  /**
   * return EVAL_PAGE by default.
   */
  public int doEndTag() throws JspException {
    return EVAL_PAGE;
  }

  /**
   * Resets the state of the Tag.
   */
  public void release() {
    // reset members
    columnNames = null;
    contextManager = null;
    contextTag = null;
    queryResult = null;

    // reset tag attributes
    query = null;
  }

  // -----------------------------------------------------------------
  // Set methods for tag attributes
  // -----------------------------------------------------------------

  public void setQuery(String query) {
    this.query = query;
  }
}
