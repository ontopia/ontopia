
package tm;

import java.util.Date;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import net.ontopia.utils.OntopiaRuntimeException;

import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portal.kernel.exception.SystemException;

public class JournalArticleWrapper {
  private JournalArticle article;
  protected static final Class[] NO_PARAMS = new Class[] { };
  protected static final Object[] NO_ARGS = new Object[] { };

  public JournalArticleWrapper(JournalArticle article) {
    this.article = article;
  }

  /**
   * Returns a UUID identifying this particular article.
   */
  public String getUuid() {
    return article.getUuid();
  }

  /**
   * Returns the database ID of this particular article.
   */
  public String getArticleId() {
    return article.getArticleId();
  }
  
  /**
   * Returns the title of the article.
   */
  public String getTitle() {
    return article.getTitle();
  }

  /**
   * Returns the last version number of the article.
   */
  public double getVersion() {
    return article.getVersion();
  }
  
  /**
   * Returns the database ID of the structure this article uses. If
   * the article has no structure it returns the empty string.
   */
  public String getStructureId() {
    return article.getStructureId();
  }
  
  // so, what does this method actually do? wish I knew.
  public String getUserUuid() throws SystemException {
    return article.getUserUuid();
  }

  // so, what does this method actually do? wish I knew.
  public long getUserId() throws SystemException {
    return article.getUserId();
  }
  
  // so, what does this method actually do? wish I knew.
  public long getGroupId() {
    return article.getGroupId();
  }

  /**
   * Returns true iff this article is approved.
   */
  public boolean isApproved() {
    return article.isApproved();
  }

  /**
   * Returns true iff this article is expired.
   */
  public boolean isExpired() {
    return article.isExpired();
  }

  /**
   * Returns the time when the article was created.
   */
  public Date getCreateDate() {
    return article.getCreateDate();
  }
  
  /**
   * Returns the time of the article's last modification.
   */
  public Date getModifiedDate() {
    return article.getModifiedDate();
  }

  // ???
  public Date getReviewDate() {
    return article.getReviewDate();
  }

  // ???
  public Date getDisplayDate() {
    return article.getDisplayDate();
  }

  // ???
  public Date getExpirationDate() {
    return article.getExpirationDate();
  }
  
  // not entirely clear what this method does. it seems to return the
  // uuid of the user which put the article into its current workflow
  // state. in 5.2 this was called getApprovedByUserUuid, so here we
  // must take Liferay version into account.
  public String getStatusByUserUuid() {
    String methodname;
    if (WrapperFactory.getLiferayVersion() == 60)
      methodname = "getStatusByUserUuid";
    else if (WrapperFactory.getLiferayVersion() == 52)
      methodname = "getApprovedByUserUuid";
    else
      throw new OntopiaRuntimeException("Unknown Liferay version");

    return (String) invoke(methodname);
  }

  // not entirely clear what this method does. it seems to return the
  // database id of the user which put the article into its current
  // workflow state. in 5.2 this was called getApprovedByUserId, so
  // here we must take Liferay version into account.
  public long getStatusByUserId() {
    String methodname;
    if (WrapperFactory.getLiferayVersion() == 60)
      methodname = "getStatusByUserId";
    else if (WrapperFactory.getLiferayVersion() == 52)
      methodname = "getApprovedByUserId";
    else
      throw new OntopiaRuntimeException("Unknown Liferay version");

    return ((Long) invoke(methodname)).longValue();
  }

  // not entirely clear what this method does. it seems to return the
  // some form of name for the user which put the article into its
  // current workflow state. in 5.2 this was called
  // getApprovedByUserName, so here we must take Liferay version into
  // account.
  public String getStatusByUserName() {
    String methodname;
    if (WrapperFactory.getLiferayVersion() == 60)
      methodname = "getStatusByUserName";
    else if (WrapperFactory.getLiferayVersion() == 52)
      methodname = "getApprovedByUserName";
    else
      throw new OntopiaRuntimeException("Unknown Liferay version");

    return (String) invoke(methodname);
  }

  // not entirely clear what this method does. it seems to return the
  // when the article entered its current workflow state. in 5.2 this
  // was called getApprovedDate, so here we must take Liferay
  // version into account.
  public Date getStatusDate() {
    String methodname;
    if (WrapperFactory.getLiferayVersion() == 60)
      methodname = "getStatusDate";
    else if (WrapperFactory.getLiferayVersion() == 52)
      methodname = "getApprovedDate";
    else
      throw new OntopiaRuntimeException("Unknown Liferay version");

    return (Date) invoke(methodname);
  }
  
  // ---

  private Object invoke(String methodname) {
    try {
      Method method = article.getClass().getMethod(methodname, NO_PARAMS);
      return method.invoke(article, NO_ARGS);
    } catch (NoSuchMethodException e) {
      throw new OntopiaRuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new OntopiaRuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
}