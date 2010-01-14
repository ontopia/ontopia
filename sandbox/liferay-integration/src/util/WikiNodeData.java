
package util;

import com.liferay.portal.SystemException;
import com.liferay.portlet.wiki.model.WikiNode;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


public class WikiNodeData implements UuidIdentifiableIF {
  
  private String _uuid;
  private String _name;
  private String _nodeId;
  private String _createDate;
  private String _modifiedDate;
  private String _lastPostDate;
  private String _userUuid;

  public WikiNodeData(WikiNode wikinode){
    setUuid(wikinode.getUuid());
    setName(wikinode.getName());
    setNodeId(wikinode.getNodeId());
    setCreateDate(wikinode.getCreateDate());
    setModifiedDate(wikinode.getModifiedDate());
    setLastPostDate(wikinode.getLastPostDate());

    try {
      setUserUuid(wikinode.getUserUuid());
    } catch (SystemException ex) {
      setUserUuid("");
    }
  }

  private WikiNodeData(){
  }

  public String getCreateDate() {
    return _createDate;
  }

  public void setCreateDate(Date createDate) {
    _createDate = DateFormater.format(createDate);
  }

  public String getLastPostDate() {
    return _lastPostDate;
  }

  public void setLastPostDate(Date lastPostDate) {
    _lastPostDate = DateFormater.format(lastPostDate);
  }

  public String getModifiedDate() {
    return _modifiedDate;
  }

  public void setModifiedDate(Date modifiedDate) {
    _modifiedDate = DateFormater.format(modifiedDate);
  }

  public String getName() {
    return _name;
  }

  public void setName(String name) {
    _name = name;
  }

  public String getNodeId() {
    return _nodeId;
  }

  public void setNodeId(long nodeId) {
    _nodeId = String.valueOf(nodeId);
  }

  public String getUuid() {
    return _uuid;
  }

  public void setUuid(String uuid) {
    _uuid = uuid;
  }

  public String getUserUuid() {
    return _userUuid;
  }

  public void setUserUuid(String userUuid) {
    _userUuid = userUuid;
  }


}
