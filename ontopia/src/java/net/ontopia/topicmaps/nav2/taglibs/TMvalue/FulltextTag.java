
// $Id: FulltextTag.java,v 1.14 2008/06/11 16:56:00 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.taglibs.TMvalue;

import java.io.File;
import java.util.*;

import javax.servlet.jsp.JspTagException;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.taglibs.value.BaseValueProducingTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.infoset.fulltext.core.*;
import net.ontopia.infoset.fulltext.topicmaps.*;
import net.ontopia.infoset.fulltext.impl.lucene.*;
import net.ontopia.infoset.fulltext.impl.rdbms.*;

import org.apache.log4j.Logger;

/**
 * INTERNAL: Tag used for executing fulltext queries and producing
 * ranked list of topic map objects.
 */
public class FulltextTag extends BaseValueProducingTag {

  // initialization of logging facility
  protected static Logger log = Logger.getLogger(FulltextTag.class.getName());

  // tag attributes
  protected String index_path;
  protected String query;
  protected String idfield;
  
  public Collection process(Collection tmObjects) throws JspTagException {

    // Get topic map
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);    

    // Get topic map from context
    TopicMapIF topicmap = contextTag.getTopicMap();
    String tmid = contextTag.getTopicMapId();
    
    if (topicmap == null)
      throw new NavigatorRuntimeException("FulltextTag found no topic map.");

    try {
      // Calculate index path
      String path;
      if (index_path != null) {
	if (index_path.equals("rdbms"))
	  path = index_path;
	else
	  path = pageContext.getServletContext().getRealPath("") + File.separator + index_path;
      } else {
        path = pageContext.getServletContext().getRealPath("") + File.separator +
          "WEB-INF" + File.separator + "indexes" + File.separator + tmid;
      }

      // Note: this one actually makes an absolute filename relative to the current page.
      // path = FrameworkUtils.getAbsoluteFileName("WEB-INF" + File.separator + "indexes" + File.separator + tmid, pageContext);

      // Output debugging information
      if (log.isDebugEnabled()) {
        log.debug("Index path: '" + path + "'");
        log.debug("Query: '" + query + "'");
      }
      
      // Create search engine
      SearcherIF sengine;
      if (topicmap instanceof net.ontopia.topicmaps.impl.basic.TopicMap || 
					!path.equals("rdbms"))
				sengine = new LuceneSearcher(path);
      else
        sengine = new RDBMSSearcher(topicmap);
      
      // Perform search
      TopicMapSearchResult result = new TopicMapSearchResult(topicmap, sengine.search(query));

      // Set object id field
      if (idfield != null) result.setObjectIdField(idfield);

      // Return search result
      return result;
    } catch (java.io.IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  // -----------------------------------------------------------------
  // Set methods for tag attributes
  // -----------------------------------------------------------------

  /**
   * INTERNAL: Sets the fulltext index location.
   */
  public void setIndex(String index_path) {
    this.index_path = index_path;
  }
  
  /**
   * INTERNAL: Set fulltext query.
   */
  public void setQuery(String query) {
    this.query = query;
  }
  
  /**
   * INTERNAL: Set fulltext index object id field.
   */
  public void setIdField(String idfield) {
    this.idfield = idfield;
  }
  
}
