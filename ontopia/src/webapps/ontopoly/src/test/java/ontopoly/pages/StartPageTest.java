package ontopoly.pages;

import ontopoly.OntopolyApplication;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.tester.WicketTester;

import junit.framework.TestCase;

public class StartPageTest extends TestCase {
  private WicketTester tester;
  
  protected void setUp() throws Exception {
    super.setUp();
    tester = new WicketTester(new OntopolyApplication());
    tester.startPage(StartPage.class);
    tester.assertNoErrorMessage();
  }
  
  public void testDisplayStartPage() throws Exception {
    tester.assertComponent("titlePartPanel", Panel.class);
    tester.assertComponent("createNewTopicMapPanel", Panel.class);
  }

}
