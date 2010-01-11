package tm;


import util.StructureData;
import util.UserData;
import util.WebContentData;


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
  

}
