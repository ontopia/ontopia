package ontopoly.pages;

import net.ontopia.Ontopia;
import ontopoly.components.TitleHelpPanel;
import ontopoly.models.HelpLinkResourceModel;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.ResourceModel;

public class InternalErrorPage extends AbstractOntopolyErrorPage {

  public InternalErrorPage() {
	  this(null);
  }
  
  public InternalErrorPage(PageParameters parameters) {
    super(parameters);
    
    createTitle();
    
    add(new BookmarkablePageLink("link", StartPage.class));
    
    add(new Label("java_version") {
      @Override
      protected void onComponentTag(ComponentTag tag) {
      super.onComponentTag(tag);
      tag.put("value", System.getProperty("java.vm.vendor")
          +", "+System.getProperty("java.vm.version"));
      }  
    });
    
    add(new Label("os_version") {
      @Override
      protected void onComponentTag(ComponentTag tag) {
      super.onComponentTag(tag);
      tag.put("value", System.getProperty("os.name")
          +", "+System.getProperty("os.version")
          +" ("+System.getProperty("os.arch")+")");
      }  
    });
    
    add(new Label("oks_version") {
      @Override
      protected void onComponentTag(ComponentTag tag) {
      super.onComponentTag(tag);
      tag.put("value", Ontopia.getVersion());
      }  
    });
    
    add(new Label("server_name") {
      @Override
      protected void onComponentTag(ComponentTag tag) {
      super.onComponentTag(tag);
      tag.put("value", getWebRequestCycle().getWebRequest().getHttpServletRequest().getServerName());
      }  
    });
    
    add(new Label("server_info") {
      @Override
      protected void onComponentTag(ComponentTag tag) {
      super.onComponentTag(tag);
      tag.put("value", getWebRequestCycle().getWebRequest().getHttpServletRequest().getSession(true).getServletContext().getServerInfo()); 
      }  
    });
    
    add(new Label("server_port") {
      @Override
      protected void onComponentTag(ComponentTag tag) {
      super.onComponentTag(tag);
      tag.put("value", getWebRequestCycle().getWebRequest().getHttpServletRequest().getServerPort());
      }  
    });
    
    add(new Label("remote_address") {
      @Override
      protected void onComponentTag(ComponentTag tag) {
      super.onComponentTag(tag);
      tag.put("value", getWebRequestCycle().getWebRequest().getHttpServletRequest().getRemoteAddr());
      }  
    });
    
    add(new Label("remote_host") {
      @Override
      protected void onComponentTag(ComponentTag tag) {
      super.onComponentTag(tag);
      tag.put("value", getWebRequestCycle().getWebRequest().getHttpServletRequest().getRemoteHost());
      }  
    });
  }
  
  private void createTitle() {
    add(new TitleHelpPanel("titlePartPanel",
          new ResourceModel("internal.error"), new HelpLinkResourceModel("help.link.startpage")));
  }

}
