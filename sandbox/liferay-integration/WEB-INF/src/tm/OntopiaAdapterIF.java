package tm;


import com.liferay.portal.model.Group;
import com.liferay.portal.model.User;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.model.JournalStructure;
import com.liferay.portlet.wiki.model.WikiNode;
import com.liferay.portlet.wiki.model.WikiPage;



public interface OntopiaAdapterIF {
  
  public void addWebContent(JournalArticle content);
  public void deleteWebContent(String uuid);
  public void updateWebContent(JournalArticle content);
  
  public void addUser(User user);
  public void deleteUser(String uuid);
  public void updateUser(User user);
  
  public void addStructure(JournalStructure structure);
  public void deleteStructure(String uuid);
  public void updateStructure(JournalStructure structure);

  public void addWikiNode(WikiNode wikinode);
  public void deleteWikiNode(String uuid);
  public void updateWikiNode(WikiNode wikinode);

  public void addWikiPage(WikiPage wikipage);
  public void deleteWikiPage(String uuid);
  public void updateWikiPage(WikiPage wikipage);

  public void addGroup(Group group);
  public void deleteGroup(Group group);
  public void updateGroup(Group group);

  public String getObjectIdForUuid(String uuid);
  public String getTopicMapId();
  public String getTopicTypeIdForUuid(String uuid);
  public String getConceptViewId();
}
