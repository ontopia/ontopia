package ontopoly.pages;

import ontopoly.OntopolyApplication;

import org.apache.wicket.PageParameters;
import org.apache.wicket.util.tester.WicketTester;

import junit.framework.TestCase;

public class TopicTypesPageTest extends TestCase {
  private WicketTester tester;
  
  protected void setUp() throws Exception {
    super.setUp();
    tester = new WicketTester(new OntopolyApplication());
    PageParameters pageParameters = new PageParameters("topicMapId=KevinsPlan.xtm");
    tester.startPage(TopicTypesPage.class, pageParameters);   
    tester.assertNoErrorMessage();
  }

  public void testDisplayTopicTypesPage() throws Exception {   
    tester.assertContains("Topic Types");
  }
}
