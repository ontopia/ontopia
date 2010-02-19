package listener;

import com.liferay.portal.ModelListenerException;
import com.liferay.portal.model.ModelListener;
import com.liferay.portal.model.Organization;



public class OrganizationListener implements ModelListener<Organization> {
  

  private void printOrganization(Organization org){
    
    System.out.println("CompanyID: " + org.getCompanyId());
    System.out.println("Name: " + org.getName());
    System.out.println("CountryID:" + org.getCountryId());
    System.out.println("OrganizationID: " + org.getOrganizationId());
    System.out.println("ParentOrg: " + org.getParentOrganizationId());
    System.out.println("PK: " + org.getPrimaryKey());
    System.out.println("GroupID: " + org.getGroup());
    System.out.println("RegionID: " + org.getRegionId());
    System.out.println("Type: " + org.getType());
    
  }
  
  private void printAssociation(Object arg0, String arg1, Object arg2){
    System.out.println(arg0.getClass().toString() + ": " + arg0 + "-" + arg1.getClass().toString() + ": " + arg1 + "-" + arg2.getClass().toString() + ": "+ arg2);
  }
  public void onAfterAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    System.out.println("OrganizationListener: AfterAddAssociation");    
  }
  
  public void onAfterCreate(Organization org) throws ModelListenerException {
    System.out.println("### onAfterCreateOrganization ###");
  }

  public void onAfterRemove(Organization org) throws ModelListenerException {
    System.out.println("### onAfterRemoveOrganization ###");
  }

  public void onAfterRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
    System.out.println("OrganizationListener: AfterRemoveAssociation");      
  }

  public void onAfterUpdate(Organization org) throws ModelListenerException {
    System.out.println("### onAfterUpdateOrganization ###");
  }

  public void onBeforeAddAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onBeforeCreate(Organization org) throws ModelListenerException {
  }

  public void onBeforeRemove(Organization org) throws ModelListenerException {
  }

  public void onBeforeRemoveAssociation(Object arg0, String arg1, Object arg2)
      throws ModelListenerException {
  }

  public void onBeforeUpdate(Organization org) throws ModelListenerException {
  }

}
