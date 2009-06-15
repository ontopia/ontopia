
// $Id: QueryTag.java,v 1.16 2005/07/06 14:03:37 grove Exp $

package net.ontopia.topicmaps.nav2.taglibs.tolog;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;

import org.apache.log4j.Logger;

/**
 * INTERNAL.
 */
public class QueryTag extends BodyTagSupport {

  private static final long serialVersionUID = -2505209031791690173L;

  // initialization of logging facility
  protected static Logger log = Logger.getLogger(QueryTag.class.getName());

  // tag attributes
  protected String query;
  protected String name;


  /**
   * Default constructor.
   */
  public QueryTag() {
    super();
  }

  /**
   * Process the start tag for this instance.
   */
  public int doStartTag() throws JspTagException {
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);

    if (contextTag == null)
      throw new JspTagException("<tolog:query> must be nested directly or"
              + " indirectly within a <tolog:context> tag, but no"
              + " <tolog:context> tag was found.");

    contextTag.getContextManager().pushScope();

    if (name == null)
      throw new JspTagException("<tolog:query> : Missing 'name'"
              + " attribute.\n");

    return EVAL_BODY_BUFFERED;
  }

  /**
   * Actions after some body has been evaluated.
   */
  public int doAfterBody() throws JspTagException {
    query = getBodyContent().getString();
    return SKIP_BODY;
  }

  /**
   * Process the end tag.
   */
  public int doEndTag() throws JspException {
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);

    // Get the TopicMap from the context.
    TopicMapIF topicmap = contextTag.getTopicMap();
    if (topicmap == null)
      throw new JspTagException("<tolog:query> : found no topic map.\n");

    // Create a QueryProcessorIF for the topicmap.
    QueryProcessorIF queryProcessor = contextTag.getQueryProcessor();

    QueryResultIF queryResult;
    try {
      // Execute query, using any arguments from the context manager.
      queryResult = new BufferedQueryResult(queryProcessor.execute(query,
          new ContextManagerScopingMapWrapper(contextTag
              .getContextManager()), contextTag.getDeclarationContext()),
              query);
    } catch (InvalidQueryException e) {
      log.info("Parsing of query \"" + query + "\" failed with message: "
              + e);
      throw new NavigatorRuntimeException(e);
    }

    contextTag.registerQueryResult(name, queryResult);

    return EVAL_PAGE;
  }

  /**
   * Resets the state of the Tag.
   */
  public void release() {
    // reset members

    // reset tag attributes
    query = null;
    name = null;

    // do not set parent to null!!!
  }

  // -----------------------------------------------------------------
  // Set methods for tag attributes
  // -----------------------------------------------------------------

  public void setName(String name) {
    this.name = name;
  }
}
