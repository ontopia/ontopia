
package net.ontopia.infoset.fulltext.impl.rdbms;

import java.io.IOException;

import net.ontopia.infoset.fulltext.core.SearchResultIF;
import net.ontopia.infoset.fulltext.core.SearcherIF;
import net.ontopia.persistence.proxy.QueryResultIF;
import net.ontopia.persistence.proxy.TransactionIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.IndexIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapTransaction;
import net.ontopia.topicmaps.impl.utils.AbstractIndex;
import net.ontopia.topicmaps.impl.utils.TopicMapTransactionIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: A generic RDBMS fulltext searcher implementation. Note that the class
 * only accepts an RDBMS topic map in its constructor.
 * <p>
 */

public class RDBMSSearcher extends AbstractIndex implements SearcherIF {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(RDBMSSearcher.class.getName());

  protected final static int FT_PLATFORM_GENERIC = 1;
  protected final static int FT_PLATFORM_ORACLE_TEXT = 2;
  protected final static int FT_PLATFORM_TSEARCH2 = 4;
  protected final static int FT_PLATFORM_SQLSERVER = 8;

  protected RDBMSTopicMapTransaction tmtxn;

  protected int ft_platform = FT_PLATFORM_GENERIC;

  protected static String[] fnames = new String[] { "class", "object_id",
      "content", "score" // TODO: Add "address" and "notation" later
  };

  public RDBMSSearcher(TopicMapIF topicmap) {
		this(((RDBMSTopicMapStore)topicmap.getStore()).getTransaction());
	}

  public RDBMSSearcher(TopicMapTransactionIF txn) {
    this.tmtxn = (RDBMSTopicMapTransaction)txn;

    // figure out what the fulltext platform is
    String platform = ((net.ontopia.persistence.proxy.RDBMSStorage) this.tmtxn
                       .getTransaction().getStorageAccess().getStorage())
      .getProperty("net.ontopia.infoset.fulltext.impl.rdbms.RDBMSSearcher.type");
    if (platform != null) {
      if (platform.equals("oracle_text"))
        this.ft_platform = FT_PLATFORM_ORACLE_TEXT;
      else if (platform.equals("tsearch2"))
        this.ft_platform = FT_PLATFORM_TSEARCH2;
      else if (platform.equals("postgresql"))
        this.ft_platform = FT_PLATFORM_TSEARCH2;
      else if (platform.equals("sqlserver"))
        this.ft_platform = FT_PLATFORM_SQLSERVER;
      else if (platform.equals("generic"))
        this.ft_platform = FT_PLATFORM_GENERIC;
    }
    
    // DEPRECATED: check platforms property as fallback
    if (this.ft_platform == FT_PLATFORM_GENERIC) {
      String[] platforms = ((net.ontopia.persistence.proxy.RDBMSStorage) this.tmtxn
                            .getTransaction().getStorageAccess().getStorage()).getPlatforms();
      for (int i = 0; i < platforms.length; i++) {
        if ("oracle_text".equals(platforms[i])) {
          this.ft_platform = FT_PLATFORM_ORACLE_TEXT;
          break;
        }
      }
    }
  }

  public SearchResultIF search(String query) throws IOException {
    TransactionIF txn = tmtxn.getTransaction();

    Object[] params = getParameters(escapeQuery(query), tmtxn.getTopicMap());

    String queryName;
    if (ft_platform == FT_PLATFORM_ORACLE_TEXT)
      queryName = "RDBMSSearcher.searchLike:oracle_text";
    else if (ft_platform == FT_PLATFORM_TSEARCH2)
      queryName = "RDBMSSearcher.searchLike:tsearch2";
    else if (ft_platform == FT_PLATFORM_SQLSERVER)
      queryName = "RDBMSSearcher.searchLike:sqlserver";
    else
      queryName = "RDBMSSearcher.searchLike:generic";
    
    QueryResultIF result = (QueryResultIF) txn.executeQuery(queryName, params);
    return new RDBMSSearchResult(result, fnames);
  }

  protected String escapeQuery(String query) {
    if (ft_platform == FT_PLATFORM_GENERIC) {
      return '%' + query + '%';
    //!} else if (ft_platform == FT_PLATFORM_ORACLE_TEXT) { // no escaping for now
    //!  return '{' + StringUtils.replace(query, "}", "\\}") + '}';
    } else {
      return query;
    }
  }
    
  /**
   * INTERNAL: Override this method if the parameters to be used by the query is
   * different from the default. If the parameter types are different or the
   * order is different this method must be overridden.
   */
  protected Object[] getParameters(String query, TopicMapIF topicmap) {
    if (ft_platform == FT_PLATFORM_TSEARCH2) {
      query = query.replaceAll("\\s", " & ");
      return new Object[] { query, topicmap, query, query, topicmap, query, query, topicmap, query };
    } else if (ft_platform == FT_PLATFORM_SQLSERVER)
      return new Object[] { query, topicmap, query, topicmap, query, topicmap };
    else
      return new Object[] { topicmap, query, topicmap, query, topicmap, query };      
  }

  public void close() throws IOException {
    tmtxn = null;
  }

  // --- IndexIF implementation

  public IndexIF getIndex() {
    return this;
  }

  public boolean isAutoUpdated() {
    return true;
  }

  public void refresh() {
    // TODO: Issue query in database? One example is Oracle Text,
    // where you explicitly have to call alter index in order to
    // rebuild indexes.
  }

  // TODO: Add command-line support?

}
