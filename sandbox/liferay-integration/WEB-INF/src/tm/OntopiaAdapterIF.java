package tm;

import com.liferay.portal.model.Group;
import com.liferay.portal.model.User;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.model.JournalStructure;
import com.liferay.portlet.wiki.model.WikiNode;
import com.liferay.portlet.wiki.model.WikiPage;

import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * This interface defines the featureset offered by the integration as of now.
 *
 * <p>The add/delete/update methods are triggered by their according listeners.
 * @see listener
 *
 * @author mfi
 */
public interface OntopiaAdapterIF {

  /**
   * Adds WebContent as provided by the object from liferay.  Includes
   * setting all applicable associations (<code>created_by</code>,
   * <code>contains</code>, etc.)
   * 
   * @param content A Liferay <code>JournalArticle</code> object
   */
  public void addWebContent(JournalArticle content);

  /**
   * Removes WebContent from the topicmap identified by the UUID
   *
   * @param uuid The <code>UUID</code> identifying the WebContent to be deleted
   */
  public void deleteWebContent(String uuid);

  /**
   * Updates the values of WebContent represented by the
   * JournalArticle object.  Updates also associations that originate
   * from Liferay (Metainfo from the portal), but not associations
   * that were user-made.
   *
   * @param content The updated <code>JournalArticle</code> object from Liferay
   */
  public void updateWebContent(JournalArticle content);


  /**
   * Adds a User using it's emailaddress value as a topicname.
   *
   * @param user The <code>User</code> object from Liferay
   */
  public void addUser(User user);

  /**
   * Deletes a user from the topicmap
   *
   * @param uuid The UUID identifying the user.
   */
  public void deleteUser(String uuid);

  /**
   * Updates the values of the User represented by the User object.
   * Associations (like created_by or approved_by) are preserved.
   *
   * @param user The updated <code>User</code> object from Liferay.
   */
  public void updateUser(User user);

  /**
   * Adds a Structure to the topicmap using its structureId as a topicname.
   *
   * @param structure The <code>JournalStructure</code> object as provided by Liferay
   */
  public void addStructure(JournalStructure structure);

  /**
   * Deletes a Structure form the topicmap.
   *
   * @param uuid The UUID identifying the Structure to be deleted.
   */
  public void deleteStructure(String uuid);

  /**
   * Updates the values of the Structure represented by the Structure object.
   *
   * @param structure The updated <code>JournalStructure</code> object from Liferay.
   */
  public void updateStructure(JournalStructure structure);


  /**
   * Adds a new Wiki (called WikiNode in Liferay) to the topicmap.
   * Sets applicable associations (i.e. contains).
   *
   * @param wikinode The <code>WikiNode</code> object as provided by Liferay.
   */
  public void addWikiNode(WikiNode wikinode);

  /**
   * Deletes a WikiNode from the topicmap
   *
   * @param uuid The UUID identifying the WikiNode to be deleted.
   */
  public void deleteWikiNode(String uuid);

  /**
   * Updates the values of the WikiNode represented by the WikiNode object.
   *
   * @param wikinode The updated <code>WikiNode</code> from Liferay.
   */
  public void updateWikiNode(WikiNode wikinode);


  /**
   * Adds a WikiPage into the topicmap.
   *
   * @param wikipage The <code>WikiPage</code> object as provided by Liferay.
   */
  public void addWikiPage(WikiPage wikipage);

  /**
   * Deletes a WikiPage from the topicmap.
   *
   * @param uuid The UUID identifying the WikiPage to be deleted.
   */
  public void deleteWikiPage(String uuid);

  /**
   * Updates the values of the WikiPage represented by the WikiPage object.
   *
   * @param wikipage The updated <code>WikiPage</code> from Liferay.
   */
  public void updateWikiPage(WikiPage wikipage);

  /**
   * Adds a Group into the topicmap.
   * Note that as of now only Communities are supported as a subclass of Groups.
   *
   * @param group The <code>Group</code> object as provided by Liferay.
   */
  public void addGroup(Group group);

  /**
   * Deletes a Group from the topicmap.
   * Note that this is *not* done using a UUID, because Groups can not be referenced to by the means of UUIDs.
   *
   * @param group The <code>Group</code> object to be deleted from the topicmap.
   */
  public void deleteGroup(Group group);

  /**
   * Updates the name the Group represented by the Group object.
   * Note that as of now there is not more to update than the name of the group.
   * Associations are not touched by this update as of now.
   *
   * @param group The updated <code>Group</code> object from Liferay.
   */
  public void updateGroup(Group group);


  /**
   * The following methods are used by the jsp integration and some of the portlets to allow quick code-wise access to
   * some information from the topicmap.
   */

  /**
   * Returns a String containing the object id of anything that can be referred to by using an UUID.
   * Is being used to gather all necessayry information the build the iFrame url for the ontopoly integration.
   *
   * @param uuid The UUID identifying the entity
   * @return A String containing the object Id of the entity.
   */
  public String getObjectIdForUuid(String uuid);

  /**
   * Checks what the Id of the current topicmap is, and returns the id
   * as a String.  Is being used to gather all necessayry information
   * the build the iFrame url for the ontopoly integration.
   *
   * @return A String representing the current topicmap id that is being used.
   */
  public String getTopicMapId();

  /**
   * Returns the topic map itself.
   */
  public TopicMapIF getTopicMap();
  
  /**
   * Returns the object id of the typing topic, that types another
   * topic which is only identified by the given UUID.  That sounds a
   * lot more complicated than it is. Seeing the code might bring
   * clarification.  Is being used to gather all necessayry
   * information the build the iFrame url for the ontopoly
   * integration.
   * 
   * @param uuid The UUID of the topic, which type's object Id is to be found out.
   * @return The object id of the type of the topic identified by the UUID.
   */
  public String getTopicTypeIdForUuid(String uuid);

  /**
   * Returns the object id of the topic representing the
   * "conceptView".  The concept view is used to show users a custom
   * part of ontopoly to enable them to set custom associations (like
   * "is-about").  Is being used to gather all necessayry information
   * the build the iFrame url for the ontopoly integration.
   * 
   * @return A String containing the object id of the topic representing the "conceptView".
   */
  public String getConceptViewId();

  /**
   * Finds out the latest (that is highest versioned) AND approved
   * article-topic in the topicmap using the article id provided.
   * This might be of use when trying to show a user references to
   * other articles. You would not want to point to an article the
   * user won't be able to view, because it hasn't been approved yet,
   * or is out of date.
   * 
   * @param articleId The article id of an article in liferay
   * @return A String containing the object id of that topic, which is represents the latest approved version for this article id.
   */
  public String getCurrentObjectIdForArticleId(String articleId);
}
