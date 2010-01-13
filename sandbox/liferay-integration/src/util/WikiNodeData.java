
package util;

import java.util.Date;


public class WikiNodeData implements UuidIdentifiableIF {
  
  private String _uuid;
  private String _name;
  private String _nodeId;
  private String _createDate;
  private String _modifiedDate;
  private String _lastPostDate;

  public WikiNodeData(String uuid, String name, String nodeId, Date createDate, Date modifiedDate, Date lastPostDate){
    setUuid(uuid);
    setName(name);
    setNodeId(nodeId);
    setCreateDate(createDate);
    setModifiedDate(modifiedDate);
    setLastPostDate(lastPostDate);
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

  public void setNodeId(String nodeId) {
    _nodeId = nodeId;
  }

  public String getUuid() {
    return _uuid;
  }

  public void setUuid(String uuid) {
    _uuid = uuid;
  }


}
