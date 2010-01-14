
package util;

import com.liferay.portal.SystemException;
import com.liferay.portlet.wiki.model.WikiPage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class WikiPageData implements UuidIdentifiableIF{

  private String _uuid;
  private String _title;
  private String _pageId;
  private String _version;
  private String _createDate;
  private String _modifyDate;
  private List<WikiPageData> _parentPages;
  private String _userUuid;


  public WikiPageData(WikiPage wikipage){
    setUuid(wikipage.getUuid());
    setTitle(wikipage.getTitle());
    setPageId(wikipage.getPageId());
    setVersion(wikipage.getVersion());
    setCreateDate(wikipage.getCreateDate());
    setModifyDate(wikipage.getModifiedDate());
    setParentPages(wikipage.getParentPages());

    try {
      setUserUuid(wikipage.getUserUuid());
    } catch (SystemException ex) {
      setUserUuid("");
    }
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

  public void setPageId(long pageId){
    _pageId = String.valueOf(pageId);
  }

  public String getPageId(){
    return _pageId;
  }

  public void setVersion(double version){
    _version = String.valueOf(version);
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

  public List<WikiPageData> getParentPages() {
    return _parentPages;
  }

  public void setParentPages(List<WikiPage> parentPages) {
    if(_parentPages == null){
      _parentPages = new ArrayList<WikiPageData>();
    }
    for(WikiPage wp : parentPages){
      _parentPages.add(new WikiPageData(wp)); // this will be recursive!
    }
  }

  public String getUserUuid() {
    return _userUuid;
  }

  public void setUserUuid(String userUuid) {
    _userUuid = userUuid;
  }

}
