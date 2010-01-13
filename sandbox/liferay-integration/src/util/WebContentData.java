package util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.liferay.portal.SystemException;
import com.liferay.portlet.journal.model.JournalArticle;

/**
 * This class' only purpose is to be neutral centerpiece in between the liferay-world and the ontopia world
 * it shall take information from liferay, pre-process them if necessary, and provide these information to
 * ontopia. This saves the programmer from extremely long parameter-lists and offers a single point of
 * configuration (besides it is more easily to extend the amount of information which is passed).
 */
public class WebContentData implements UuidIdentifiableIF{
  

  private String _uuid;
  private String _createDate;
  private String _approvedDate;
  private String _reviewDate;
  private String _expiryDate;
  private String _modifyDate;
  private String _displayDate;
  private String _version;
  private String _articleId;
  private String _title;
  private String _userUuid;
  private String _userId;
  private String _approvingUserUuid;
  private String _approvingUserName;
  private String _approvingUserId;
  private String _structureId;
  
  private boolean _isNew;
  private boolean _isApproved;
  private boolean _isExpired;
  
  private WebContentData(){
    super();
  }
  
  public WebContentData(JournalArticle content){
    setUuid(content.getUuid());
    setCreateDate(content.getCreateDate());
    setApprovedDate(content.getApprovedDate());
    setReviewDate(content.getReviewDate());
    setExpiryDate(content.getExpirationDate());
    setModifyDate(content.getModifiedDate());
    setDisplayDate(content.getDisplayDate());
    setVersion(content.getVersion());
    setArticleId(content.getArticleId());
    setTitle(content.getTitle());
    setIsNew(content.isNew());
    setIsApproved(content.isApproved());
    setIsExpired(content.isExpired());
    setApprovingUserName(content.getApprovedByUserName());
    setStructureId(content.getStructureId());
    setApprovingUserId(content.getApprovedByUserId());
    setUserId(content.getUserId());

    try {
      setApprovingUserUuid(content.getApprovedByUserUuid());
    } catch (SystemException e1) {
      setApprovingUserUuid("");
      e1.printStackTrace();
    }
    
    try {
      setUserUuid(content.getUserUuid());
    } catch (SystemException e) {
      setUserUuid("");
      e.printStackTrace();
    }
  }

  // Setter and Getter 
  
  public void setUuid(String uuid) {
    _uuid = uuid;
  }

  public String getUuid() {
    return _uuid;
  }

  public void setCreateDate(Date createDate) {
    _createDate = DateFormater.format(createDate);
  }

  public String getCreateDate() {
    return _createDate;
  }

  public void setApprovedDate(Date approvedDate) {
    _approvedDate = DateFormater.format(approvedDate);
  }

  public String getApprovedDate() {
    return _approvedDate;
  }

  public void setReviewDate(Date reviewDate) {
    _reviewDate = DateFormater.format(reviewDate);
  }

  public String getReviewDate() {
    return _reviewDate;
  }

  public void setExpiryDate(Date expiryDate) {
    _expiryDate = DateFormater.format(expiryDate);
  }

  public String getExpiryDate() {
    return _expiryDate;
  }

  public void setModifyDate(Date modifyDate) {
    _modifyDate = DateFormater.format(modifyDate);
  }

  public String getModifyDate() {
    return _modifyDate;
  }

  public void setDisplayDate(Date displayDate) {
    _displayDate = DateFormater.format(displayDate);
  }

  public String getDisplayDate() {
    return _displayDate;
  }

  public void setVersion(Double version) {
    _version = String.valueOf(version);
  }

  public String getVersion() {
    return _version;
  }

  public void setArticleId(String articleId) {
    _articleId = articleId;
  }

  public String getArticleId() {
    return _articleId;
  }

  public void setTitle(String title) {
    _title = title;
  }

  public String getTitle() {
    return _title;
  }

  public void setIsNew(boolean isNew) {
    _isNew = isNew;
  }

  public boolean getIsNew() {
    return _isNew;
  }

  public void setIsApproved(boolean isApproved) {
    _isApproved = isApproved;
  }

  public boolean getIsApproved() {
    return _isApproved;
  }

  public void setIsExpired(boolean isExpired) {
    _isExpired = isExpired;
  }

  public boolean getIsExpired() {
    return _isExpired;
  }
  
  public void setUserUuid(String userUuid) {
    _userUuid = userUuid;
  }

  public String getUserUuid() {
    return _userUuid;
  }
  
  public void setApprovingUserUuid(String approvingUserUuid) {
    _approvingUserUuid = approvingUserUuid;
  }

  public String getApprovingUserUuid() {
    return _approvingUserUuid;
  }
  
  public void setApprovingUserName(String approvingUserName) {
    _approvingUserName = approvingUserName;
  }

  public String getApprovingUserName() {
    return _approvingUserName;
  }

  public void setStructureId(String structureId) {
    _structureId = structureId;
  }

  public String getStructureId() {
    return _structureId;
  }

  public void setApprovingUserId(long approvingUserId) {
    _approvingUserId = String.valueOf(approvingUserId);
  }

  public String getApprovingUserId() {
    return _approvingUserId;
  }

  public void setUserId(long userId) {
    _userId = String.valueOf(userId);
  }

  public String getUserId() {
    return _userId;
  }

}
