
package util;

import java.util.Date;


public class WikiPageData implements UuidIdentifiableIF{

  private String _uuid;
  private String _title;
  private String _pageId;
  private String _version;
  private String _createDate;
  private String _modifyDate;


  public WikiPageData(String uuid, String title, String pageId, String version, Date createDate, Date modifyDate){
    setUuid(uuid);
    setTitle(title);
    setPageId(pageId);
    setVersion(version);
    setCreateDate(createDate);
    setModifyDate(modifyDate);
  }

  private WikiPageData(){
  }
  
  public void setUuid(String uuid){
    _uuid = uuid;
  }

  public String getUuid(){
    return _uuid;
  }

  public void setTitle(String title){
    _title = title;
  }

  public String getTitle(){
    return _title;
  }

  public void setPageId(String pageId){
    _pageId = pageId;
  }

  public String getPageId(){
    return _pageId;
  }

  public void setVersion(String version){
    _version = version;
  }

  public String getVersion(){
    return _version;
  }

  public void setCreateDate(Date createDate){
    _createDate = DateFormater.format(createDate);
  }

  public String getCreateDate(){
    return _createDate;
  }

  public void setModifyDate(Date modifyDate){
    _modifyDate = DateFormater.format(modifyDate);
  }

  public String getModifyDate(){
    return _modifyDate;
  }

}
