
// $Id: MenuTest.java,v 1.12 2008/06/13 08:36:27 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.portlets.pojos;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.nav2.portlets.pojos.Menu;
import net.ontopia.topicmaps.nav2.portlets.pojos.Menu.Heading;
import net.ontopia.topicmaps.nav2.portlets.pojos.Menu.Item;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.utils.OntopiaRuntimeException;

public class MenuTest extends AbstractOntopiaTestCase {
  private String base;

  public MenuTest(String name) {
    super(name);
  }

  public void setUp() throws MalformedURLException {
    String root = AbstractOntopiaTestCase.getTestDirectory();
    base = root + File.separator + "nav2" + File.separator;
  }

  // --- Tests

  public void testSimpleMenu() throws IOException {
    // initialize
    TopicMapIF tm = load("simple-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    assertTrue("menu topic not found",
               topic != null);

    // test menu
    Menu menu = new Menu(topic);
    assertTrue("wrong menu title",
               menu.getTitle().equals("menu title"));
    TopicIF menu_owner = getTopicById(tm, "menu1-owner");
    assertTrue("wrong menu owner",
               menu.getOwner().equals(menu_owner));
    TopicIF menu_topic = getTopicById(tm, "menu1");
    assertTrue("wrong menu topic",
               menu.getTopic().equals(menu_topic));
    List children = menu.getChildren();
    assertTrue("wrong number of childen on menu",
               children.size() == 2);
    assertTrue("menu claims not to have children",
           menu.getHasChildren());

    // test headings
    Iterator iter = children.iterator();
    Heading h1 = (Heading)iter.next();
    assertTrue("wrong title on first heading",
               h1.getTitle().equals("heading1 title"));
    List h1_children = h1.getChildren();
    assertTrue("wrong number of childen on first heading",
               h1_children.size() == 2);
    assertTrue("first heading claims not to have children",
               h1.getHasChildren());
    
    Heading h2 = (Heading)iter.next();
    assertTrue("wrong title on second heading",
               h2.getTitle().equals("heading2 title"));
    List h2_children = h2.getChildren();
    assertTrue("wrong number of childen on second heading",
               h2_children.size() == 0);
    assertTrue("second heading claims to have children",
               !h2.getHasChildren());

    // test items
    iter = h1_children.iterator();
    Item i1 = (Item)iter.next();
    assertTrue("wrong title on first item",
               i1.getTitle().equals("item 1"));
    TopicIF i1_topic = getTopicById(tm, "item1-topic");
    assertTrue("wrong topic for first item",
               i1.getAssociatedTopic().equals(i1_topic));
    assertTrue("wrong link on first item",
               i1.getLink().equals("some link1"));
    assertTrue("wrong image on first item",
               i1.getImage().equals("some image1"));
    assertTrue("first item claims to have children",
               !i1.getHasChildren());

    Item i2 = (Item)iter.next();
    assertTrue("wrong title on second item",
               i2.getTitle().equals("item 2"));
    TopicIF i2_topic = getTopicById(tm, "item2-topic");
    assertTrue("wrong topic for second item",
               i2.getAssociatedTopic().equals(i2_topic));
    assertTrue("wrong link on second item",
               i2.getLink().equals("some link2"));
    assertTrue("wrong image on second item",
               i2.getImage().equals("some image2"));
    assertTrue("second item claims to have children",
               !i1.getHasChildren());   
  }

  public void testErrorMenu() throws IOException {
    // initialize
    TopicMapIF tm = load("error-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    assertTrue("menu topic not found",
               topic != null);

    // test menu
    try {
      Menu menu = new Menu(topic);
      menu.getChildren();
      fail("menu with invalid condition queries was accepted");
    } catch (OntopiaRuntimeException e) {
      // we should get this exception, because the menu has an invalid
      // condition query in it
    }
  }

  public void testConditionMenu() throws IOException {
    // initialize
    TopicMapIF tm = load("condition-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    assertTrue("menu topic not found",
               topic != null);

    // test menu
    Menu menu = new Menu(topic);
    assertTrue("wrong menu title",
               menu.getTitle().equals("menu title"));
    TopicIF menu_owner = getTopicById(tm, "menu1-owner");
    assertTrue("wrong menu owner",
               menu.getOwner().equals(menu_owner));
    assertTrue("wrong menu topic",
               menu.getTopic() == topic);
    List children = menu.getChildren();
    assertTrue("wrong number of childen on menu",
               children.size() == 2);
    assertTrue("menu claims not to have children",
           menu.getHasChildren());

    // test items
    Iterator iter = children.iterator();
    Item i1 = (Item)iter.next();
    assertTrue("wrong title on first item",
               i1.getTitle().equals("item 1"));
    TopicIF i1_topic = getTopicById(tm, "item1-topic");
    assertTrue("wrong topic for first item",
               i1.getAssociatedTopic().equals(i1_topic));
    assertTrue("wrong link on first item",
               i1.getLink() == null);
    assertTrue("wrong image on first item",
               i1.getImage() == null);
    assertTrue("first item claims to have children",
               !i1.getHasChildren());
    assertTrue("first item claims condition is false",
               i1.getConditionTrue());

    Item i2 = (Item)iter.next();
    assertTrue("wrong title on second item",
               i2.getTitle().equals("item 2"));
    TopicIF i2_topic = getTopicById(tm, "item2-topic");
    assertTrue("wrong topic for second item",
               i2.getAssociatedTopic().equals(i2_topic));
    assertTrue("wrong link on second item",
               i2.getLink() == null);
    assertTrue("wrong image on second item",
               i2.getImage() == null);
    assertTrue("second item claims to have children",
               !i2.getHasChildren());   
    assertTrue("second item claims condition is true",
               !i2.getConditionTrue());
  }
  
  public void testMenuSetTitle() throws IOException {
    // initialize
    TopicMapIF tm = load("condition-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    assertTrue("menu topic not found",
               topic != null);

    // test menu
    Menu menu = new Menu(topic);
    assertTrue("wrong menu title",
               menu.getTitle().equals("menu title"));
    menu.setTitle("new menu title");
    assertTrue("wrong menu title",
        menu.getTitle().equals("new menu title"));

    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    assertTrue("wrong menu title",
        menu.getTitle().equals("new menu title"));
  }
  
  public void testHeadingSetTitle() throws IOException {
    // initialize
    TopicMapIF tm = load("condition-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    assertTrue("menu topic not found",
               topic != null);

    // test menu
    Menu menu = new Menu(topic);
    assertTrue("wrong menu title",
               menu.getTitle().equals("menu title"));
    Menu.ChildIF heading = menu.createHeading("new heading");
    heading.setTitle("renamed new heading");
    assertTrue("wrong heading title",
        heading.getTitle().equals("renamed new heading"));

    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    List children = menu.getChildren();
    heading = (Menu.ChildIF)children.get(children.size() - 1);
    assertTrue("wrong heading title",
        heading.getTitle().equals("renamed new heading"));
  }
  
  public void testItemSetTitle() throws IOException {
    // initialize
    TopicMapIF tm = load("condition-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    assertTrue("menu topic not found",
               topic != null);

    // test menu
    Menu menu = new Menu(topic);
    assertTrue("wrong menu title",
               menu.getTitle().equals("menu title"));
    Menu.ChildIF item = menu.createItem("new item");
    item.setTitle("renamed new item");
    assertTrue("wrong item title",
        item.getTitle().equals("renamed new item"));

    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    List children = menu.getChildren();
    item = (Menu.ChildIF)children.get(children.size() - 1);
    assertTrue("wrong item title",
        item.getTitle().equals("renamed new item"));
  }
  
  public void testItemSetLink() throws IOException {
    // initialize
    TopicMapIF tm = load("condition-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    assertTrue("menu topic not found",
               topic != null);

    // test menu
    Menu menu = new Menu(topic);
    assertTrue("wrong menu title",
               menu.getTitle().equals("menu title"));
    Menu.Item item = menu.createItem("new item");
    item.setLink("item link");
    assertTrue("wrong item link",
        item.getLink().equals("item link"));

    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    List children = menu.getChildren();
    item = (Item)children.get(children.size() - 1);
    assertTrue("wrong item link",
        item.getLink().equals("item link"));
  }
  
  public void testItemSetImage() throws IOException {
    // initialize
    TopicMapIF tm = load("condition-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    assertTrue("menu topic not found",
               topic != null);

    // test menu
    Menu menu = new Menu(topic);
    assertTrue("wrong menu title",
               menu.getTitle().equals("menu title"));
    Menu.Item item = menu.createItem("new item");
    item.setImage("item image");
    assertTrue("wrong item image",
        item.getImage().equals("item image"));

    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    List children = menu.getChildren();
    item = (Item)children.get(children.size() - 1);
    assertTrue("wrong item image",
        item.getImage().equals("item image"));
  }
  
  public void testItemSetCondition() throws IOException {
    // initialize
    TopicMapIF tm = load("condition-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    assertTrue("menu topic not found",
               topic != null);

    // test menu
    Menu menu = new Menu(topic);
    List children = menu.getChildren();
    assertTrue("wrong menu title",
               menu.getTitle().equals("menu title"));
    Menu.Item item = menu.createItem("new item");
    assertFalse(item.getConditionTrue());
    String condition = "topic(%topic%)?";
    item.setCondition(condition);
    
    TopicMapBuilderIF builder = tm.getBuilder();
    TopicIF associate = builder.makeTopic();
    item.setAssociatedTopic(associate);
    
    assertTrue(item.getConditionTrue());
    assertTrue("wrong item condition",
        item.getCondition().equals(condition));
    
    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    children = menu.getChildren();
    item = (Item)children.get(children.size() - 1);
    assertTrue("wrong item condition",
        item.getCondition().equals(condition));
  }
  
  public void testMenuSetOwner() throws IOException {
    // initialize
    TopicMapIF tm = load("condition-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    assertTrue("menu topic not found",
               topic != null);

    // test menu
    Menu menu = new Menu(topic);
    TopicIF menuOwner = getTopicById(tm, "menu1-owner");
    assertTrue("wrong menu owner",
               menu.getOwner().equals(menuOwner));
    TopicMapBuilderIF builder = tm.getBuilder();
    TopicIF otherOwner = builder.makeTopic();
    builder.makeTopicName(otherOwner, "other owner");
    menu.setOwner(otherOwner);
    assertFalse("wrong menu owner",
        menu.getOwner().equals(menuOwner));
    assertTrue("wrong menu owner",
        menu.getOwner().equals(otherOwner));
    
    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    assertFalse("wrong menu owner",
        menu.getOwner().equals(menuOwner));
    assertTrue("wrong menu owner",
        menu.getOwner().equals(otherOwner));
  }
  
  public void testMenuChanges() throws IOException {
    Menu.ChildIF item0;
    Menu.ChildIF item1;
    Menu.ChildIF item2;
    
    // initialize
    TopicMapIF tm = load("condition-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    assertTrue("menu topic not found",
               topic != null);

    // test menu
    Menu menu = new Menu(topic);
    assertTrue("wrong number of childen on menu",
        menu.getChildren().size() == 2);
    
    // Test Menu.createHeading
    menu.createHeading("new heading");
    List children = menu.getChildren();
    assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item2 = (Menu.ChildIF)children.get(2);
    assertTrue("'new heading' should be the third and last child", 
        item2.getTitle().equals("new heading"));
    assertTrue("the forth and last child should be of class Heading", 
        item2 instanceof Heading);
    
    // Test Menu.moveOneUp
    item2.moveOneUp();
    children = menu.getChildren();
    assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item1 = (Menu.ChildIF)children.get(1);
    assertTrue("'new heading' should be the second child", 
        item1.getTitle().equals("new heading"));
    item2 = (Menu.ChildIF)children.get(2);
    assertTrue("'item 2' should be the third and last child", 
        item2.getTitle().equals("item 2"));
    
    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    children = menu.getChildren();
    assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item1 = (Menu.ChildIF)children.get(1);
    assertTrue("'new heading' should be the second child", 
        item1.getTitle().equals("new heading"));
    item2 = (Menu.ChildIF)children.get(2);
    assertTrue("'item 2' should be the third and last child", 
        item2.getTitle().equals("item 2"));

    // Test Menu.moveOneUp
    item1.moveOneUp();
    children = menu.getChildren();
    assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item0 = (Menu.ChildIF)children.get(0);
    assertTrue("'new heading' should be the first child", 
        item0.getTitle().equals("new heading"));
    item1 = (Menu.ChildIF)children.get(1);
    assertTrue("'item 1' should be the second child", 
        item1.getTitle().equals("item 1"));
    item2 = (Menu.ChildIF)children.get(2);
    assertTrue("'item 2' should be the third and last child", 
        item2.getTitle().equals("item 2"));
    
    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    children = menu.getChildren();
    assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item0 = (Menu.ChildIF)children.get(0);
    assertTrue("'new heading' should be the first child", 
        item0.getTitle().equals("new heading"));
    item1 = (Menu.ChildIF)children.get(1);
    assertTrue("'item 1' should be the second child", 
        item1.getTitle().equals("item 1"));
    item2 = (Menu.ChildIF)children.get(2);
    assertTrue("'item 2' should be the third and last child", 
        item2.getTitle().equals("item 2"));

    // Test Menu.moveOneUp (no effect when on top)
    item0.moveOneUp();
    children = menu.getChildren();
    assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item0 = (Menu.ChildIF)children.get(0);
    assertTrue("'new heading' should be the first child", 
        item0.getTitle().equals("new heading"));
    item1 = (Menu.ChildIF)children.get(1);
    assertTrue("'item 1' should be the second child", 
        item1.getTitle().equals("item 1"));
    item2 = (Menu.ChildIF)children.get(2);
    assertTrue("'item 2' should be the third and last child", 
        item2.getTitle().equals("item 2"));

    // Test Heading.moveOneDown
    item0.moveOneDown();
    children = menu.getChildren();
    assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item1 = (Menu.ChildIF)children.get(0);
    assertTrue("'item 1' should be the first child", 
        item1.getTitle().equals("item 1"));
    item1 = (Menu.ChildIF)children.get(1);
    assertTrue("'new heading' should be the second child", 
        item1.getTitle().equals("new heading"));
    item2 = (Menu.ChildIF)children.get(2);
    assertTrue("'item 2' should be the third and last child", 
        item2.getTitle().equals("item 2"));

    // Test Heading.moveOneDown
    item1.moveOneDown();
    children = menu.getChildren();
    assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item0 = (Menu.ChildIF)children.get(0);
    assertTrue("'item 1' should be the first child", 
        item0.getTitle().equals("item 1"));
    item1 = (Menu.ChildIF)children.get(1);
    assertTrue("'item 2' should be the second child", 
        item1.getTitle().equals("item 2"));
    item2 = (Menu.ChildIF)children.get(2);
    assertTrue("'new heading' should be the third and last child", 
        item2.getTitle().equals("new heading"));

    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    children = menu.getChildren();
    assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item0 = (Menu.ChildIF)children.get(0);
    assertTrue("'item 1' should be the first child", 
        item0.getTitle().equals("item 1"));
    item1 = (Menu.ChildIF)children.get(1);
    assertTrue("'item 2' should be the second child", 
        item1.getTitle().equals("item 2"));
    item2 = (Menu.ChildIF)children.get(2);
    assertTrue("'new heading' should be the third and last child", 
        item2.getTitle().equals("new heading"));

    // Test Menu.createItem
    menu.createItem("new item");
    children = menu.getChildren();
    assertTrue("wrong number of childen on menu",
        children.size() == 4);
    item0 = (Menu.ChildIF)children.get(0);
    assertTrue("'item 1' should be the first child", 
        item0.getTitle().equals("item 1"));
    item1 = (Menu.ChildIF)children.get(1);
    assertTrue("'item 2' should be the second child", 
        item1.getTitle().equals("item 2"));
    item2 = (Menu.ChildIF)children.get(2);
    assertTrue("'new heading' should be the third child", 
        item2.getTitle().equals("new heading"));
    Menu.ChildIF item3 = (Menu.ChildIF)children.get(3);
    assertTrue("the forth and last child should be of class Item", 
        item3 instanceof Item);
    assertTrue("'new item' should be the forth and last child", 
        item3.getTitle().equals("new item"));
    
    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    children = menu.getChildren();
    assertTrue("wrong number of childen on menu",
        children.size() == 4);
    item0 = (Menu.ChildIF)children.get(0);
    assertTrue("'item 1' should be the first child", 
        item0.getTitle().equals("item 1"));
    item1 = (Menu.ChildIF)children.get(1);
    assertTrue("'item 2' should be the second child", 
        item1.getTitle().equals("item 2"));
    item2 = (Menu.ChildIF)children.get(2);
    assertTrue("'new heading' should be the third child", 
        item2.getTitle().equals("new heading"));
    item3 = (Menu.ChildIF)children.get(3);
    assertTrue("the forth and last child should be of class Item", 
        item3 instanceof Item);
    assertTrue("'new item' should be the forth and last child", 
        item3.getTitle().equals("new item"));

    // Test Item.moveOneUp
    for (int i = 0; i < 3; i++)
      item3.moveOneUp();
    children = menu.getChildren();
    item0 = (Menu.ChildIF)children.get(0);
    assertTrue("'new item' should be the first child", 
        item0.getTitle().equals("new item"));
    item0.moveOneUp();
    assertTrue("'new item' should be the first child", 
        item0.getTitle().equals("new item"));

    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    children = menu.getChildren();
    item0 = (Menu.ChildIF)children.get(0);
    assertTrue("'new item' should be the first child", 
        item0.getTitle().equals("new item"));
    item0.moveOneUp();
    assertTrue("'new item' should be the first child", 
        item0.getTitle().equals("new item"));

    // Test Item.moveOneDown
    for (int i = 0; i < 3; i++)
      item0.moveOneDown();
    children = menu.getChildren();
    item3 = (Menu.ChildIF)children.get(3);
    assertTrue("'new item' should be the third and last child", 
        item3.getTitle().equals("new item"));
    item3.moveOneDown();
    assertTrue("'new item' should be the third and last child", 
        item3.getTitle().equals("new item"));

    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    children = menu.getChildren();
    item3 = (Menu.ChildIF)children.get(3);
    assertTrue("'new item' should be the third and last child", 
        item3.getTitle().equals("new item"));
    item3.moveOneDown();
    assertTrue("'new item' should be the third and last child", 
        item3.getTitle().equals("new item"));
  }
  
  public void testHeadingChanges() throws IOException {
    Menu.ChildIF item0;
    Menu.ChildIF item1;
    Menu.ChildIF item2;
    
    // initialize
    TopicMapIF tm = load("simple-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    assertTrue("menu topic not found",
               topic != null);

    // test menu
    Menu menu = new Menu(topic);
    assertTrue("wrong number of childen on menu",
        menu.getChildren().size() == 2);
    
    List menuChildren = menu.getChildren();
    Heading heading1 = (Heading)menuChildren.get(0);
    
    // Test Heading.createHeading
    heading1.createHeading("new heading");
    List children = heading1.getChildren();
    assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item2 = (Menu.ChildIF)children.get(2);
    assertTrue("'new heading' should be the third and last child", 
        item2.getTitle().equals("new heading"));
    assertTrue("the forth and last child should be of class Heading", 
        item2 instanceof Heading);
    
    // Test Menu.moveOneUp
    item2.moveOneUp();
    children = heading1.getChildren();
    assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item1 = (Menu.ChildIF)children.get(1);
    assertTrue("'new heading' should be the second child", 
        item1.getTitle().equals("new heading"));
    item2 = (Menu.ChildIF)children.get(2);
    assertTrue("'item 2' should be the third and last child", 
        item2.getTitle().equals("item 2"));
    
    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    heading1 = (Heading)menuChildren.get(0);
    children = heading1.getChildren();
    assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item1 = (Menu.ChildIF)children.get(1);
    assertTrue("'new heading' should be the second child", 
        item1.getTitle().equals("new heading"));
    item2 = (Menu.ChildIF)children.get(2);
    assertTrue("'item 2' should be the third and last child", 
        item2.getTitle().equals("item 2"));

    // Test Menu.moveOneUp
    item1.moveOneUp();
    children = heading1.getChildren();
    assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item0 = (Menu.ChildIF)children.get(0);
    assertTrue("'new heading' should be the first child", 
        item0.getTitle().equals("new heading"));
    item1 = (Menu.ChildIF)children.get(1);
    assertTrue("'item 1' should be the second child", 
        item1.getTitle().equals("item 1"));
    item2 = (Menu.ChildIF)children.get(2);
    assertTrue("'item 2' should be the third and last child", 
        item2.getTitle().equals("item 2"));
    
    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    heading1 = (Heading)menuChildren.get(0);
    children = heading1.getChildren();
    assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item0 = (Menu.ChildIF)children.get(0);
    assertTrue("'new heading' should be the first child", 
        item0.getTitle().equals("new heading"));
    item1 = (Menu.ChildIF)children.get(1);
    assertTrue("'item 1' should be the second child", 
        item1.getTitle().equals("item 1"));
    item2 = (Menu.ChildIF)children.get(2);
    assertTrue("'item 2' should be the third and last child", 
        item2.getTitle().equals("item 2"));

    // Test Menu.moveOneUp (no effect when on top)
    item0.moveOneUp();
    children = heading1.getChildren();
    assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item0 = (Menu.ChildIF)children.get(0);
    assertTrue("'new heading' should be the first child", 
        item0.getTitle().equals("new heading"));
    item1 = (Menu.ChildIF)children.get(1);
    assertTrue("'item 1' should be the second child", 
        item1.getTitle().equals("item 1"));
    item2 = (Menu.ChildIF)children.get(2);
    assertTrue("'item 2' should be the third and last child", 
        item2.getTitle().equals("item 2"));

    // Test Heading.moveOneDown
    item0.moveOneDown();
    children = heading1.getChildren();
    assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item1 = (Menu.ChildIF)children.get(0);
    assertTrue("'item 1' should be the first child", 
        item1.getTitle().equals("item 1"));
    item1 = (Menu.ChildIF)children.get(1);
    assertTrue("'new heading' should be the second child", 
        item1.getTitle().equals("new heading"));
    item2 = (Menu.ChildIF)children.get(2);
    assertTrue("'item 2' should be the third and last child", 
        item2.getTitle().equals("item 2"));

    // Test Heading.moveOneDown
    item1.moveOneDown();
    children = heading1.getChildren();
    assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item0 = (Menu.ChildIF)children.get(0);
    assertTrue("'item 1' should be the first child", 
        item0.getTitle().equals("item 1"));
    item1 = (Menu.ChildIF)children.get(1);
    assertTrue("'item 2' should be the second child", 
        item1.getTitle().equals("item 2"));
    item2 = (Menu.ChildIF)children.get(2);
    assertTrue("'new heading' should be the third and last child", 
        item2.getTitle().equals("new heading"));

    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    heading1 = (Heading)menuChildren.get(0);
    children = heading1.getChildren();
    assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item0 = (Menu.ChildIF)children.get(0);
    assertTrue("'item 1' should be the first child", 
        item0.getTitle().equals("item 1"));
    item1 = (Menu.ChildIF)children.get(1);
    assertTrue("'item 2' should be the second child", 
        item1.getTitle().equals("item 2"));
    item2 = (Menu.ChildIF)children.get(2);
    assertTrue("'new heading' should be the third and last child", 
        item2.getTitle().equals("new heading"));

    // Test Menu.createItem
    heading1.createItem("new item");
    children = heading1.getChildren();
    assertTrue("wrong number of childen on menu",
        children.size() == 4);
    item0 = (Menu.ChildIF)children.get(0);
    assertTrue("'item 1' should be the first child", 
        item0.getTitle().equals("item 1"));
    item1 = (Menu.ChildIF)children.get(1);
    assertTrue("'item 2' should be the second child", 
        item1.getTitle().equals("item 2"));
    item2 = (Menu.ChildIF)children.get(2);
    assertTrue("'new heading' should be the third child", 
        item2.getTitle().equals("new heading"));
    Menu.ChildIF item3 = (Menu.ChildIF)children.get(3);
    assertTrue("the forth and last child should be of class Item", 
        item3 instanceof Item);
    assertTrue("'new item' should be the forth and last child", 
        item3.getTitle().equals("new item"));
    
    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    heading1 = (Heading)menuChildren.get(0);
    children = heading1.getChildren();
    assertTrue("wrong number of childen on menu",
        children.size() == 4);
    item0 = (Menu.ChildIF)children.get(0);
    assertTrue("'item 1' should be the first child", 
        item0.getTitle().equals("item 1"));
    item1 = (Menu.ChildIF)children.get(1);
    assertTrue("'item 2' should be the second child", 
        item1.getTitle().equals("item 2"));
    item2 = (Menu.ChildIF)children.get(2);
    assertTrue("'new heading' should be the third child", 
        item2.getTitle().equals("new heading"));
    item3 = (Menu.ChildIF)children.get(3);
    assertTrue("the forth and last child should be of class Item", 
        item3 instanceof Item);
    assertTrue("'new item' should be the forth and last child", 
        item3.getTitle().equals("new item"));

    // Test Item.moveOneUp
    for (int i = 0; i < 3; i++)
      item3.moveOneUp();
    children = heading1.getChildren();
    item0 = (Menu.ChildIF)children.get(0);
    assertTrue("'new item' should be the first child", 
        item0.getTitle().equals("new item"));
    item0.moveOneUp();
    assertTrue("'new item' should be the first child", 
        item0.getTitle().equals("new item"));

    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    heading1 = (Heading)menuChildren.get(0);
    children = heading1.getChildren();
    item0 = (Menu.ChildIF)children.get(0);
    assertTrue("'new item' should be the first child", 
        item0.getTitle().equals("new item"));
    item0.moveOneUp();
    assertTrue("'new item' should be the first child", 
        item0.getTitle().equals("new item"));

    // Test Item.moveOneDown
    for (int i = 0; i < 3; i++)
      item0.moveOneDown();
    children = heading1.getChildren();
    item3 = (Menu.ChildIF)children.get(3);
    assertTrue("'new item' should be the third and last child", 
        item3.getTitle().equals("new item"));
    item3.moveOneDown();
    assertTrue("'new item' should be the third and last child", 
        item3.getTitle().equals("new item"));

    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    heading1 = (Heading)menuChildren.get(0);
    children = heading1.getChildren();
    item3 = (Menu.ChildIF)children.get(3);
    assertTrue("'new item' should be the third and last child", 
        item3.getTitle().equals("new item"));
    item3.moveOneDown();
    assertTrue("'new item' should be the third and last child", 
        item3.getTitle().equals("new item"));
  }
  
  public void testMenuCreateItem() throws IOException {
    // initialize
    TopicMapIF tm = load("condition-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    assertTrue("menu topic not found",
               topic != null);

    // test menu
    Menu menu = new Menu(topic);
    assertTrue("wrong number of childen on menu",
        menu.getChildren().size() == 2);
    menu.createItem("new item");
    assertTrue("wrong number of childen on menu",
        menu.getChildren().size() == 3);

    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    assertTrue("wrong number of childen on menu",
        menu.getChildren().size() == 3);
  }
  
  public void testDeleteMenu() throws IOException {
    // initialize
    TopicMapIF tm = load("condition-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    assertTrue("menu topic not found",
               topic != null);

    // test menu
    Menu menu = new Menu(topic);
    assertEquals(23, tm.getTopics().size());
    assertEquals(6, tm.getAssociations().size());
    menu.delete();
    assertEquals(20, tm.getTopics().size());
    assertEquals(1, tm.getAssociations().size());

    assertNull(topic.getTopicMap());
    // Since the topic is not part of a topic map anymore,
    // there's no further persistence tests to do.
    // The menu cannot be reconstructed as it doesn't exist.
  }
  
  // --- Helpers
  
  private TopicMapIF load(String filename) throws IOException {
    filename = base + File.separator + "topicmaps" + File.separator + filename;
    return ImportExportUtils.getReader(filename).read();
  }

  private TopicIF getTopicById(TopicMapIF topicmap, String id) {
    LocatorIF srcloc =
      topicmap.getStore().getBaseAddress().resolveAbsolute("#" + id);
    return (TopicIF) topicmap.getObjectByItemIdentifier(srcloc);
  }
}
