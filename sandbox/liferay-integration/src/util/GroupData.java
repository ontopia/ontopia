
package util;

import com.liferay.portal.model.Group;


public class GroupData {
  
  private String _groupId;
  private String _name;
  private String _parentGroupId;
  private boolean _isOrg;
  private boolean _isStagingGroup;
  private boolean _isUserGroup;
  private boolean _isUser;
  private boolean _isCommunity;

  public GroupData(Group group){
    setGroupId(group.getGroupId());
    setName(group.getName());
    setParentGroupId(group.getParentGroupId());
    setIsOrg(group.isOrganization());
    setIsStagingGroup(group.isStagingGroup());
    setIsUserGroup(group.isUserGroup());
    setIsUser(group.isUser());
    setIsCommunity(group.isCommunity());

  }

  public String getGroupId() {
    return _groupId;
  }

  public void setGroupId(long groupId) {
    _groupId = String.valueOf(groupId);
  }

  public boolean getIsCommunity() {
    return _isCommunity;
  }

  public void setIsCommunity(boolean isCommunity) {
    _isCommunity = isCommunity;
  }

  public boolean getIsOrg() {
    return _isOrg;
  }

  public void setIsOrg(boolean isOrg) {
    _isOrg = isOrg;
  }

  public boolean getIsStagingGroup() {
    return _isStagingGroup;
  }

  public void setIsStagingGroup(boolean isStagingGroup) {
    _isStagingGroup = isStagingGroup;
  }

  public boolean getIsUser() {
    return _isUser;
  }

  public void setIsUser(boolean isUser) {
    _isUser = isUser;
  }

  public boolean getIsUserGroup() {
    return _isUserGroup;
  }

  public void setIsUserGroup(boolean isUserGroup) {
    _isUserGroup = isUserGroup;
  }

  public String getName() {
    return _name;
  }

  public void setName(String name) {
    _name = name;
  }

  public String getParentGroupId() {
    return _parentGroupId;
  }

  public void setParentGroupId(long parentGroupId) {
    _parentGroupId = String.valueOf(parentGroupId);
  }

}
