/*
 * #!
 * Ontopia Navigator
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.nav2.taglibs.TMvalue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.nav2.core.ContextManagerIF;
import net.ontopia.topicmaps.nav2.core.NavigatorCompileException;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.core.VariableNotSetException;
import net.ontopia.topicmaps.nav2.impl.basic.JSPEngineWrapper;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.taglibs.value.BaseValueProducingTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryResultIterator;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.utils.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: TologQueryTag evalutes a tolog query and returns a
 * collection of maps.
 */
public class TologQueryTag extends BaseValueProducingTag {
  private static final Logger log = LoggerFactory.getLogger(TologQueryTag.class.getName());
  
  // tag attributes
  private String query;
  private String select;
  private String implementation;
  private String rulesfile;
  
  @Override
  public Collection process(Collection tmObjects) throws JspException {
    if (query == null) {
      throw new NavigatorCompileException("TologQueryTag: Ambiguous attribute " +
                                          "settings.");
    }
    
    // try to retrieve default value from ContextManager
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);

    // Get the ContextManager
    ContextManagerIF ctxtMgr = contextTag.getContextManager();

    // Create a map for the context
    Map argmap = new ContextManagerMapWrapper(ctxtMgr);
    
    // get topicmap object on which we should compute 
    TopicMapIF topicmap = contextTag.getTopicMap();
    if (topicmap == null) {
      throw new NavigatorRuntimeException("TologQueryTag found no topic map.");
    }
        
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
      if (index < 0) {
        throw new IndexOutOfBoundsException("No query result column named '" + select + "'");
      }

      List list = new ArrayList();
      while (result.next()) {
        list.add(result.getValue(index));
      }
      result.close();
      return list;
    }

    if (result instanceof net.ontopia.topicmaps.query.impl.basic.QueryResult) {
      // BASIC
      return net.ontopia.topicmaps.query.impl.basic.QueryResultWrappers.getWrapper(result);
    } else {
      // FIXME: Should pass collection size if available.
      return IteratorUtils.toList(new QueryResultIterator(result));
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

    @Override
    public Object get(Object key) {
      Collection coll = ctxtMgr.getValue((String) key);
      if (coll.isEmpty()) {
        throw new VariableNotSetException((String) key);
      }
      
      return CollectionUtils.getFirstElement(coll);
    }

    @Override
    public boolean containsKey(Object key) {
      return ctxtMgr.getValue((String)key) != null;
    }
  }
}
