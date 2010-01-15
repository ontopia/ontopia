package tm;


import util.GroupData;
import util.StructureData;
import util.UserData;
import util.WebContentData;
import util.WikiNodeData;
import util.WikiPageData;


public interface OntopiaAdapterIF {
  
  public void addWebContent(WebContentData content);
  public void deleteWebContent(String uuid);
  public void updateWebContent(WebContentData content);
  
  public void addUser(UserData user);
  public void deleteUser(String uuid);
  public void updateUser(UserData user);
  
  public void addStructure(StructureData structure);
  public void deleteStructure(String uuid);
  public void updateStructure(StructureData structure);

  public void addWikiNode(WikiNodeData wikinode);
  public void deleteWikiNode(String uuid);
  public void updateWikiNode(WikiNodeData wikinode);

  public void addWikiPage(WikiPageData wikipage);
  public void deleteWikiPage(String uuid);
  public void updateWikiPage(WikiPageData wikipage);

  public void addGroup(GroupData group);
  public void deleteGroup(GroupData group);
  public void updateGroup(GroupData group);
}
