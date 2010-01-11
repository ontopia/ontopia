package listener;
import com.liferay.portal.ModelListenerException;
import com.liferay.portal.SystemException;
import com.liferay.portal.model.ModelListener;
import com.liferay.portal.model.Organization;
import com.liferay.portlet.journal.model.JournalArticle;



public class OrganizationListener implements ModelListener<Organization> {
  

  private void printOrganization(Organization arg0){
    
    System.out.println("CompanyID: " + arg0.getCompanyId());
    System.out.println("Name: " + arg0.getName());
    System.out.println("CountryID:" + arg0.getCountryId());
    System.out.println("OrganizationID: " + arg0.getOrganizationId());
    System.out.println("ParentOrg: " + arg0.getParentOrganizationId());
    System.out.println("PK: " + arg0.getPrimaryKey());
    System.out.println("GroupID: " + arg0.getGroup());
    System.out.println("RegionID: " + arg0.getRegionId());
    System.out.println("Type: " + arg0.getType());
    
  }
  
  private void printAssociation(Object arg0, String arg1, Object arg2){
    System.out.println(arg0.getClass().toString() + ": " + arg0 + "-" + arg1.getClass().toString() + ": " + arg1 + "-" + arg2.getClass().toString() + ": "+ arg2);
  }
  public void onAfterAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    System.out.println("OrganizationListener: AfterAddAssociation");    
  }
  
  public void onAfterCreate(Organization arg0) throws ModelListenerException {
    System.out.println("### onAfterCreateOrganization ###");
    printOrganization(arg0);
    
  }
  public void onAfterRemove(Organization arg0) throws ModelListenerException {
    System.out.println("### onAfterRemoveOrganization ###");
    printOrganization(arg0);
    
  }
  public void onAfterRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    System.out.println("OrganizationListener: AfterRemoveAssociation");    
    
  }
  public void onAfterUpdate(Organization arg0) throws ModelListenerException {
    System.out.println("### onAfterUpdateOrganization ###");
    printOrganization(arg0);
    
  }
  public void onBeforeAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }
  public void onBeforeCreate(Organization arg0) throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }
  public void onBeforeRemove(Organization arg0) throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }
  public void onBeforeRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }
  public void onBeforeUpdate(Organization arg0) throws ModelListenerException {
    // TODO Auto-generated method stub
    
  }

}
