
// $Id: TologQueryTag.java,v 1.36 2005/06/28 13:23:57 grove Exp $

package net.ontopia.topicmaps.nav2.taglibs.TMvalue;

import java.util.*;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.query.core.*;
import net.ontopia.topicmaps.query.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.nav2.core.*;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.taglibs.value.BaseValueProducingTag;
import net.ontopia.infoset.core.*;
import net.ontopia.utils.CollectionUtils;
import net.ontopia.utils.StringTemplateUtils;
import net.ontopia.topicmaps.nav2.impl.basic.JSPEngineWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: TologQueryTag evalutes a tolog query and returns a
 * collection of maps.
 */
public class TologQueryTag extends BaseValueProducingTag {
  private static Logger log = LoggerFactory.getLogger(TologQueryTag.class.getName());
  
  // tag attributes
  private String query;
  private String select;
  private String implementation;
  private String rulesfile;
  
  public Collection process(Collection tmObjects) throws JspException {
    if (query == null)
      throw new NavigatorCompileException("TologQueryTag: Ambiguous attribute " +
                                          "settings.");
    
    // try to retrieve default value from ContextManager
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);

    // Get the ContextManager
    ContextManagerIF ctxtMgr = contextTag.getContextManager();

    // Create a map for the context
    Map argmap = new ContextManagerMapWrapper(ctxtMgr);
    
    // get topicmap object on which we should compute 
    TopicMapIF topicmap = contextTag.getTopicMap();
    if (topicmap == null)
      throw new NavigatorRuntimeException("TologQueryTag found no topic map.");
        
    // Get the result from the QueryProcessor
    QueryProcessorIF q_processor = (implementation == null ?
				    QueryUtils.createQueryProcessor(topicmap) :
				    QueryUtils.createQueryProcessor(topicmap, Collections.singletonMap("net.ontopia.topicmaps.query.core.QueryProcessorIF", implementation)));
						    
    if (rulesfile != null) {
      ServletContext ctxt = pageContext.getServletContext();
      try {
        q_processor.load(new InputStreamReader(ctxt.getResourceAsStream(rulesfile)));
      } catch (IOException e) {
        throw JSPEngineWrapper.getJspException("Problem loading tolog rules file: " +
                                               rulesfile, e);
      } catch (InvalidQueryException e) {
        throw JSPEngineWrapper.getJspException("Problem loading tolog rules file: " +
                                               rulesfile, e);
      }
    }

    try {
      // produce list of maps
      return getMapCollection(q_processor.execute(query, argmap));
    } catch (InvalidQueryException e) {
      log.debug("Parsing of query '" + query + "' failed with message: " + e);
      throw new NavigatorRuntimeException(e);
    }
  }

  /**
   * INTERNAL: Wraps a QueryResultIF instance in a suitable
   * MapCollection implementation.
   */ 
  protected Collection getMapCollection(QueryResultIF result) {

    if (select != null) {
      int index = result.getIndex(select);
      if (index < 0)
	throw new IndexOutOfBoundsException("No query result column named '" + select + "'");

      List list = new ArrayList();
      while (result.next())
        list.add(result.getValue(index));
      result.close();
      return list;
    }

    if (result instanceof net.ontopia.topicmaps.query.impl.basic.QueryResult)
      // BASIC
      return net.ontopia.topicmaps.query.impl.basic.QueryResultWrappers.getWrapper(result);
    else {
      // FIXME: Should pass collection size if available.
      return new net.ontopia.utils.IteratorCollection(new QueryResultIterator(result));
    }
  }
    
  // -----------------------------------------------------------------
  // Set methods for tag attributes
  // -----------------------------------------------------------------
  
  public void setQuery(String query) {
    this.query = query;
  }

  public void setSelect(String select) {
    this.select = select;
  }

  public void setRulesfile(String rulesfile) {
    this.rulesfile = rulesfile;
  }

  public void setImplementation(String implementation) {
    this.implementation = implementation;
  }
  
  // --- Helper class

  class ContextManagerMapWrapper extends HashMap {

    private ContextManagerIF ctxtMgr;

    public ContextManagerMapWrapper(ContextManagerIF ctxtMgr) {
      this.ctxtMgr = ctxtMgr;
    }

    public Object get(Object key) {
      Collection coll = ctxtMgr.getValue((String) key);
      if (coll.isEmpty())
        throw new VariableNotSetException((String) key);
      
      return CollectionUtils.getFirstElement(coll);
    }

    public boolean containsKey(Object key) {
      return ctxtMgr.getValue((String)key) != null;
    }
  }
}
