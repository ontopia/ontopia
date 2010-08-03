package tm;

import com.liferay.portlet.journal.model.JournalArticle;

/**
 * INTERNAL: Factory class used to create wrappers around certain
 * Liferay objects. This is necessary because the Liferay API changes
 * incompatibly from release to release, and these wrappers can handle
 * the API changes. The factory itself is a singleton, so that we
 * don't have to keep checking what Liferay version we are using.  Not
 * using synchronization or any fancy singleton patterns, because
 * there's no real harm done if we create the singleton more than
 * once.
 *
 * <p>So far we are only wrapping JournalArticle objects, because this
 * is the only class whose API changed from 5.2 to 6.0.
 */
public class WrapperFactory {
  private static WrapperFactory instance;
  private static int liferay_version; // 60 = 6.0, 52 = 5.2

  // --- External static interface
  
  public static WrapperFactory getInstance() {
    if (instance == null)
      instance = new WrapperFactory();
    return instance;
  }

  public static int getLiferayVersion() {
    return liferay_version;
  }

  // --- Factory interface

  public static JournalArticleWrapper wrap(JournalArticle article) {
    return getInstance().wrapArticle(article);
  }

  // --- Implementation

  private WrapperFactory() {
    if (liferay_version == 0) // means not initialized
      liferay_version = findLiferayVersion();
  }

  private int findLiferayVersion() {
    try {
      JournalArticleWrapper.class.getMethod("getStatusByUserUuid", JournalArticleWrapper.NO_PARAMS);
      return 60; // this method was introduced in 6.0      
    } catch (NoSuchMethodException e) {
      // since we only support 6.0 and 5.2 we assume this means that we
      // are using 5.2.
      return 52;
    }
  }

  private JournalArticleWrapper wrapArticle(JournalArticle article) {
    return new JournalArticleWrapper(article);
  }

}