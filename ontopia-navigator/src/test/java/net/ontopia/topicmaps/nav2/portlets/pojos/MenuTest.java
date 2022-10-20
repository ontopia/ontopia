/*
 * #!
 * Ontopia Navigator
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.nav2.portlets.pojos;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.nav2.portlets.pojos.Menu.Heading;
import net.ontopia.topicmaps.nav2.portlets.pojos.Menu.Item;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.utils.PSI;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;

public class MenuTest {

  private final static String testdataDirectory = "nav2";

  // --- Tests

  @Test
  public void testSimpleMenu() throws IOException {
    // initialize
    TopicMapIF tm = load("simple-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    Assert.assertTrue("menu topic not found",
               topic != null);

    // test menu
    Menu menu = new Menu(topic);
    Assert.assertTrue("wrong menu title",
               menu.getTitle().equals("menu title"));
    TopicIF menu_owner = getTopicById(tm, "menu1-owner");
    Assert.assertTrue("wrong menu owner",
               menu.getOwner().equals(menu_owner));
    TopicIF menu_topic = getTopicById(tm, "menu1");
    Assert.assertTrue("wrong menu topic",
               menu.getTopic().equals(menu_topic));
    List children = menu.getChildren();
    Assert.assertTrue("wrong number of childen on menu",
               children.size() == 2);
    Assert.assertTrue("menu claims not to have children",
           menu.getHasChildren());

    // test headings
    Iterator iter = children.iterator();
    Heading h1 = (Heading)iter.next();
    Assert.assertTrue("wrong title on first heading",
               h1.getTitle().equals("heading1 title"));
    List h1_children = h1.getChildren();
    Assert.assertTrue("wrong number of childen on first heading",
               h1_children.size() == 2);
    Assert.assertTrue("first heading claims not to have children",
               h1.getHasChildren());
    
    Heading h2 = (Heading)iter.next();
    Assert.assertTrue("wrong title on second heading",
               h2.getTitle().equals("heading2 title"));
    List h2_children = h2.getChildren();
    Assert.assertTrue("wrong number of childen on second heading",
               h2_children.size() == 0);
    Assert.assertTrue("second heading claims to have children",
               !h2.getHasChildren());

    // test items
    iter = h1_children.iterator();
    Item i1 = (Item)iter.next();
    Assert.assertTrue("wrong title on first item",
               i1.getTitle().equals("item 1"));
    TopicIF i1_topic = getTopicById(tm, "item1-topic");
    Assert.assertTrue("wrong topic for first item",
               i1.getAssociatedTopic().equals(i1_topic));
    Assert.assertTrue("wrong link on first item",
               i1.getLink().equals("some link1"));
    Assert.assertTrue("wrong image on first item",
               i1.getImage().equals("some image1"));
    Assert.assertTrue("first item claims to have children",
               !i1.getHasChildren());

    Item i2 = (Item)iter.next();
    Assert.assertTrue("wrong title on second item",
               i2.getTitle().equals("item 2"));
    TopicIF i2_topic = getTopicById(tm, "item2-topic");
    Assert.assertTrue("wrong topic for second item",
               i2.getAssociatedTopic().equals(i2_topic));
    Assert.assertTrue("wrong link on second item",
               i2.getLink().equals("some link2"));
    Assert.assertTrue("wrong image on second item",
               i2.getImage().equals("some image2"));
    Assert.assertTrue("second item claims to have children",
               !i1.getHasChildren());   
  }

  @Test
  public void testErrorMenu() throws IOException {
    // initialize
    TopicMapIF tm = load("error-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    Assert.assertTrue("menu topic not found",
               topic != null);

    // test menu
    try {
      Menu menu = new Menu(topic);
      menu.getChildren();
      Assert.fail("menu with invalid condition queries was accepted");
    } catch (OntopiaRuntimeException e) {
      // we should get this exception, because the menu has an invalid
      // condition query in it
    }
  }

  @Test
  public void testConditionMenu() throws IOException {
    // initialize
    TopicMapIF tm = load("condition-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    Assert.assertTrue("menu topic not found",
               topic != null);

    // test menu
    Menu menu = new Menu(topic);
    Assert.assertTrue("wrong menu title",
               menu.getTitle().equals("menu title"));
    TopicIF menu_owner = getTopicById(tm, "menu1-owner");
    Assert.assertTrue("wrong menu owner",
               menu.getOwner().equals(menu_owner));
    Assert.assertTrue("wrong menu topic",
               menu.getTopic() == topic);
    List children = menu.getChildren();
    Assert.assertTrue("wrong number of childen on menu",
               children.size() == 2);
    Assert.assertTrue("menu claims not to have children",
           menu.getHasChildren());

    // test items
    Iterator iter = children.iterator();
    Item i1 = (Item)iter.next();
    Assert.assertTrue("wrong title on first item",
               i1.getTitle().equals("item 1"));
    TopicIF i1_topic = getTopicById(tm, "item1-topic");
    Assert.assertTrue("wrong topic for first item",
               i1.getAssociatedTopic().equals(i1_topic));
    Assert.assertTrue("wrong link on first item",
               i1.getLink() == null);
    Assert.assertTrue("wrong image on first item",
               i1.getImage() == null);
    Assert.assertTrue("first item claims to have children",
               !i1.getHasChildren());
    Assert.assertTrue("first item claims condition is false",
               i1.getConditionTrue());

    Item i2 = (Item)iter.next();
    Assert.assertTrue("wrong title on second item",
               i2.getTitle().equals("item 2"));
    TopicIF i2_topic = getTopicById(tm, "item2-topic");
    Assert.assertTrue("wrong topic for second item",
               i2.getAssociatedTopic().equals(i2_topic));
    Assert.assertTrue("wrong link on second item",
               i2.getLink() == null);
    Assert.assertTrue("wrong image on second item",
               i2.getImage() == null);
    Assert.assertTrue("second item claims to have children",
               !i2.getHasChildren());   
    Assert.assertTrue("second item claims condition is true",
               !i2.getConditionTrue());
  }
  
  @Test
  public void testMenuSetTitle() throws IOException {
    // initialize
    TopicMapIF tm = load("condition-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    Assert.assertTrue("menu topic not found",
               topic != null);

    // test menu
    Menu menu = new Menu(topic);
    Assert.assertTrue("wrong menu title",
               menu.getTitle().equals("menu title"));
    menu.setTitle("new menu title");
    Assert.assertTrue("wrong menu title",
        menu.getTitle().equals("new menu title"));

    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    Assert.assertTrue("wrong menu title",
        menu.getTitle().equals("new menu title"));
  }
  
  @Test
  public void testHeadingSetTitle() throws IOException {
    // initialize
    TopicMapIF tm = load("condition-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    Assert.assertTrue("menu topic not found",
               topic != null);

    // test menu
    Menu menu = new Menu(topic);
    Assert.assertTrue("wrong menu title",
               menu.getTitle().equals("menu title"));
    Menu.ChildIF heading = menu.createHeading("new heading");
    heading.setTitle("renamed new heading");
    Assert.assertTrue("wrong heading title",
        heading.getTitle().equals("renamed new heading"));

    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    List children = menu.getChildren();
    heading = (Menu.ChildIF)children.get(children.size() - 1);
    Assert.assertTrue("wrong heading title",
        heading.getTitle().equals("renamed new heading"));
  }
  
  @Test
  public void testItemSetTitle() throws IOException {
    // initialize
    TopicMapIF tm = load("condition-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    Assert.assertTrue("menu topic not found",
               topic != null);

    // test menu
    Menu menu = new Menu(topic);
    Assert.assertTrue("wrong menu title",
               menu.getTitle().equals("menu title"));
    Menu.ChildIF item = menu.createItem("new item");
    item.setTitle("renamed new item");
    Assert.assertTrue("wrong item title",
        item.getTitle().equals("renamed new item"));

    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    List children = menu.getChildren();
    item = (Menu.ChildIF)children.get(children.size() - 1);
    Assert.assertTrue("wrong item title",
        item.getTitle().equals("renamed new item"));
  }
  
  @Test
  public void testItemSetLink() throws IOException {
    // initialize
    TopicMapIF tm = load("condition-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    Assert.assertTrue("menu topic not found",
               topic != null);

    // test menu
    Menu menu = new Menu(topic);
    Assert.assertTrue("wrong menu title",
               menu.getTitle().equals("menu title"));
    Menu.Item item = menu.createItem("new item");
    item.setLink("item link");
    Assert.assertTrue("wrong item link",
        item.getLink().equals("item link"));

    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    List children = menu.getChildren();
    item = (Item)children.get(children.size() - 1);
    Assert.assertTrue("wrong item link",
        item.getLink().equals("item link"));
  }
  
  @Test
  public void testItemSetImage() throws IOException {
    // initialize
    TopicMapIF tm = load("condition-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    Assert.assertTrue("menu topic not found",
               topic != null);

    // test menu
    Menu menu = new Menu(topic);
    Assert.assertTrue("wrong menu title",
               menu.getTitle().equals("menu title"));
    Menu.Item item = menu.createItem("new item");
    item.setImage("item image");
    Assert.assertTrue("wrong item image",
        item.getImage().equals("item image"));

    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    List children = menu.getChildren();
    item = (Item)children.get(children.size() - 1);
    Assert.assertTrue("wrong item image",
        item.getImage().equals("item image"));
  }
  
  @Test
  public void testItemSetCondition() throws IOException {
    // initialize
    TopicMapIF tm = load("condition-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    Assert.assertTrue("menu topic not found",
               topic != null);

    // test menu
    Menu menu = new Menu(topic);
    List children = menu.getChildren();
    Assert.assertTrue("wrong menu title",
               menu.getTitle().equals("menu title"));
    Menu.Item item = menu.createItem("new item");
    Assert.assertFalse(item.getConditionTrue());
    String condition = "topic(%topic%)?";
    item.setCondition(condition);
    
    TopicMapBuilderIF builder = tm.getBuilder();
    TopicIF associate = builder.makeTopic();
    item.setAssociatedTopic(associate);
    
    Assert.assertTrue(item.getConditionTrue());
    Assert.assertTrue("wrong item condition",
        item.getCondition().equals(condition));
    
    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    children = menu.getChildren();
    item = (Item)children.get(children.size() - 1);
    Assert.assertTrue("wrong item condition",
        item.getCondition().equals(condition));
  }
  
  @Test
  public void testMenuSetOwner() throws IOException {
    // initialize
    TopicMapIF tm = load("condition-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    Assert.assertTrue("menu topic not found",
               topic != null);

    // test menu
    Menu menu = new Menu(topic);
    TopicIF menuOwner = getTopicById(tm, "menu1-owner");
    Assert.assertTrue("wrong menu owner",
               menu.getOwner().equals(menuOwner));
    TopicMapBuilderIF builder = tm.getBuilder();
    TopicIF otherOwner = builder.makeTopic();
    builder.makeTopicName(otherOwner, "other owner");
    menu.setOwner(otherOwner);
    Assert.assertFalse("wrong menu owner",
        menu.getOwner().equals(menuOwner));
    Assert.assertTrue("wrong menu owner",
        menu.getOwner().equals(otherOwner));
    
    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    Assert.assertFalse("wrong menu owner",
        menu.getOwner().equals(menuOwner));
    Assert.assertTrue("wrong menu owner",
        menu.getOwner().equals(otherOwner));
  }
  
  @Test
  public void testMenuChanges() throws IOException {
    Menu.ChildIF item0;
    Menu.ChildIF item1;
    Menu.ChildIF item2;
    
    // initialize
    TopicMapIF tm = load("condition-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    Assert.assertTrue("menu topic not found",
               topic != null);

    // test menu
    Menu menu = new Menu(topic);
    Assert.assertTrue("wrong number of childen on menu",
        menu.getChildren().size() == 2);
    
    // Test Menu.createHeading
    menu.createHeading("new heading");
    List children = menu.getChildren();
    Assert.assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item2 = (Menu.ChildIF)children.get(2);
    Assert.assertTrue("'new heading' should be the third and last child", 
        item2.getTitle().equals("new heading"));
    Assert.assertTrue("the forth and last child should be of class Heading", 
        item2 instanceof Heading);
    
    // Test Menu.moveOneUp
    item2.moveOneUp();
    children = menu.getChildren();
    Assert.assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item1 = (Menu.ChildIF)children.get(1);
    Assert.assertTrue("'new heading' should be the second child", 
        item1.getTitle().equals("new heading"));
    item2 = (Menu.ChildIF)children.get(2);
    Assert.assertTrue("'item 2' should be the third and last child", 
        item2.getTitle().equals("item 2"));
    
    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    children = menu.getChildren();
    Assert.assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item1 = (Menu.ChildIF)children.get(1);
    Assert.assertTrue("'new heading' should be the second child", 
        item1.getTitle().equals("new heading"));
    item2 = (Menu.ChildIF)children.get(2);
    Assert.assertTrue("'item 2' should be the third and last child", 
        item2.getTitle().equals("item 2"));

    // Test Menu.moveOneUp
    item1.moveOneUp();
    children = menu.getChildren();
    Assert.assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item0 = (Menu.ChildIF)children.get(0);
    Assert.assertTrue("'new heading' should be the first child", 
        item0.getTitle().equals("new heading"));
    item1 = (Menu.ChildIF)children.get(1);
    Assert.assertTrue("'item 1' should be the second child", 
        item1.getTitle().equals("item 1"));
    item2 = (Menu.ChildIF)children.get(2);
    Assert.assertTrue("'item 2' should be the third and last child", 
        item2.getTitle().equals("item 2"));
    
    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    children = menu.getChildren();
    Assert.assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item0 = (Menu.ChildIF)children.get(0);
    Assert.assertTrue("'new heading' should be the first child", 
        item0.getTitle().equals("new heading"));
    item1 = (Menu.ChildIF)children.get(1);
    Assert.assertTrue("'item 1' should be the second child", 
        item1.getTitle().equals("item 1"));
    item2 = (Menu.ChildIF)children.get(2);
    Assert.assertTrue("'item 2' should be the third and last child", 
        item2.getTitle().equals("item 2"));

    // Test Menu.moveOneUp (no effect when on top)
    item0.moveOneUp();
    children = menu.getChildren();
    Assert.assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item0 = (Menu.ChildIF)children.get(0);
    Assert.assertTrue("'new heading' should be the first child", 
        item0.getTitle().equals("new heading"));
    item1 = (Menu.ChildIF)children.get(1);
    Assert.assertTrue("'item 1' should be the second child", 
        item1.getTitle().equals("item 1"));
    item2 = (Menu.ChildIF)children.get(2);
    Assert.assertTrue("'item 2' should be the third and last child", 
        item2.getTitle().equals("item 2"));

    // Test Heading.moveOneDown
    item0.moveOneDown();
    children = menu.getChildren();
    Assert.assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item1 = (Menu.ChildIF)children.get(0);
    Assert.assertTrue("'item 1' should be the first child", 
        item1.getTitle().equals("item 1"));
    item1 = (Menu.ChildIF)children.get(1);
    Assert.assertTrue("'new heading' should be the second child", 
        item1.getTitle().equals("new heading"));
    item2 = (Menu.ChildIF)children.get(2);
    Assert.assertTrue("'item 2' should be the third and last child", 
        item2.getTitle().equals("item 2"));

    // Test Heading.moveOneDown
    item1.moveOneDown();
    children = menu.getChildren();
    Assert.assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item0 = (Menu.ChildIF)children.get(0);
    Assert.assertTrue("'item 1' should be the first child", 
        item0.getTitle().equals("item 1"));
    item1 = (Menu.ChildIF)children.get(1);
    Assert.assertTrue("'item 2' should be the second child", 
        item1.getTitle().equals("item 2"));
    item2 = (Menu.ChildIF)children.get(2);
    Assert.assertTrue("'new heading' should be the third and last child", 
        item2.getTitle().equals("new heading"));

    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    children = menu.getChildren();
    Assert.assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item0 = (Menu.ChildIF)children.get(0);
    Assert.assertTrue("'item 1' should be the first child", 
        item0.getTitle().equals("item 1"));
    item1 = (Menu.ChildIF)children.get(1);
    Assert.assertTrue("'item 2' should be the second child", 
        item1.getTitle().equals("item 2"));
    item2 = (Menu.ChildIF)children.get(2);
    Assert.assertTrue("'new heading' should be the third and last child", 
        item2.getTitle().equals("new heading"));

    // Test Menu.createItem
    menu.createItem("new item");
    children = menu.getChildren();
    Assert.assertTrue("wrong number of childen on menu",
        children.size() == 4);
    item0 = (Menu.ChildIF)children.get(0);
    Assert.assertTrue("'item 1' should be the first child", 
        item0.getTitle().equals("item 1"));
    item1 = (Menu.ChildIF)children.get(1);
    Assert.assertTrue("'item 2' should be the second child", 
        item1.getTitle().equals("item 2"));
    item2 = (Menu.ChildIF)children.get(2);
    Assert.assertTrue("'new heading' should be the third child", 
        item2.getTitle().equals("new heading"));
    Menu.ChildIF item3 = (Menu.ChildIF)children.get(3);
    Assert.assertTrue("the forth and last child should be of class Item", 
        item3 instanceof Item);
    Assert.assertTrue("'new item' should be the forth and last child", 
        item3.getTitle().equals("new item"));
    
    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    children = menu.getChildren();
    Assert.assertTrue("wrong number of childen on menu",
        children.size() == 4);
    item0 = (Menu.ChildIF)children.get(0);
    Assert.assertTrue("'item 1' should be the first child", 
        item0.getTitle().equals("item 1"));
    item1 = (Menu.ChildIF)children.get(1);
    Assert.assertTrue("'item 2' should be the second child", 
        item1.getTitle().equals("item 2"));
    item2 = (Menu.ChildIF)children.get(2);
    Assert.assertTrue("'new heading' should be the third child", 
        item2.getTitle().equals("new heading"));
    item3 = (Menu.ChildIF)children.get(3);
    Assert.assertTrue("the forth and last child should be of class Item", 
        item3 instanceof Item);
    Assert.assertTrue("'new item' should be the forth and last child", 
        item3.getTitle().equals("new item"));

    // Test Item.moveOneUp
    for (int i = 0; i < 3; i++) {
      item3.moveOneUp();
    }
    children = menu.getChildren();
    item0 = (Menu.ChildIF)children.get(0);
    Assert.assertTrue("'new item' should be the first child", 
        item0.getTitle().equals("new item"));
    item0.moveOneUp();
    Assert.assertTrue("'new item' should be the first child", 
        item0.getTitle().equals("new item"));

    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    children = menu.getChildren();
    item0 = (Menu.ChildIF)children.get(0);
    Assert.assertTrue("'new item' should be the first child", 
        item0.getTitle().equals("new item"));
    item0.moveOneUp();
    Assert.assertTrue("'new item' should be the first child", 
        item0.getTitle().equals("new item"));

    // Test Item.moveOneDown
    for (int i = 0; i < 3; i++) {
      item0.moveOneDown();
    }
    children = menu.getChildren();
    item3 = (Menu.ChildIF)children.get(3);
    Assert.assertTrue("'new item' should be the third and last child", 
        item3.getTitle().equals("new item"));
    item3.moveOneDown();
    Assert.assertTrue("'new item' should be the third and last child", 
        item3.getTitle().equals("new item"));

    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    children = menu.getChildren();
    item3 = (Menu.ChildIF)children.get(3);
    Assert.assertTrue("'new item' should be the third and last child", 
        item3.getTitle().equals("new item"));
    item3.moveOneDown();
    Assert.assertTrue("'new item' should be the third and last child", 
        item3.getTitle().equals("new item"));
  }
  
  @Test
  public void testHeadingChanges() throws IOException {
    Menu.ChildIF item0;
    Menu.ChildIF item1;
    Menu.ChildIF item2;
    
    // initialize
    TopicMapIF tm = load("simple-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    Assert.assertTrue("menu topic not found",
               topic != null);

    // test menu
    Menu menu = new Menu(topic);
    Assert.assertTrue("wrong number of childen on menu",
        menu.getChildren().size() == 2);
    
    List menuChildren = menu.getChildren();
    Heading heading1 = (Heading)menuChildren.get(0);
    
    // Test Heading.createHeading
    heading1.createHeading("new heading");
    List children = heading1.getChildren();
    Assert.assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item2 = (Menu.ChildIF)children.get(2);
    Assert.assertTrue("'new heading' should be the third and last child", 
        item2.getTitle().equals("new heading"));
    Assert.assertTrue("the forth and last child should be of class Heading", 
        item2 instanceof Heading);
    
    // Test Menu.moveOneUp
    item2.moveOneUp();
    children = heading1.getChildren();
    Assert.assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item1 = (Menu.ChildIF)children.get(1);
    Assert.assertTrue("'new heading' should be the second child", 
        item1.getTitle().equals("new heading"));
    item2 = (Menu.ChildIF)children.get(2);
    Assert.assertTrue("'item 2' should be the third and last child", 
        item2.getTitle().equals("item 2"));
    
    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    heading1 = (Heading)menuChildren.get(0);
    children = heading1.getChildren();
    Assert.assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item1 = (Menu.ChildIF)children.get(1);
    Assert.assertTrue("'new heading' should be the second child", 
        item1.getTitle().equals("new heading"));
    item2 = (Menu.ChildIF)children.get(2);
    Assert.assertTrue("'item 2' should be the third and last child", 
        item2.getTitle().equals("item 2"));

    // Test Menu.moveOneUp
    item1.moveOneUp();
    children = heading1.getChildren();
    Assert.assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item0 = (Menu.ChildIF)children.get(0);
    Assert.assertTrue("'new heading' should be the first child", 
        item0.getTitle().equals("new heading"));
    item1 = (Menu.ChildIF)children.get(1);
    Assert.assertTrue("'item 1' should be the second child", 
        item1.getTitle().equals("item 1"));
    item2 = (Menu.ChildIF)children.get(2);
    Assert.assertTrue("'item 2' should be the third and last child", 
        item2.getTitle().equals("item 2"));
    
    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    heading1 = (Heading)menuChildren.get(0);
    children = heading1.getChildren();
    Assert.assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item0 = (Menu.ChildIF)children.get(0);
    Assert.assertTrue("'new heading' should be the first child", 
        item0.getTitle().equals("new heading"));
    item1 = (Menu.ChildIF)children.get(1);
    Assert.assertTrue("'item 1' should be the second child", 
        item1.getTitle().equals("item 1"));
    item2 = (Menu.ChildIF)children.get(2);
    Assert.assertTrue("'item 2' should be the third and last child", 
        item2.getTitle().equals("item 2"));

    // Test Menu.moveOneUp (no effect when on top)
    item0.moveOneUp();
    children = heading1.getChildren();
    Assert.assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item0 = (Menu.ChildIF)children.get(0);
    Assert.assertTrue("'new heading' should be the first child", 
        item0.getTitle().equals("new heading"));
    item1 = (Menu.ChildIF)children.get(1);
    Assert.assertTrue("'item 1' should be the second child", 
        item1.getTitle().equals("item 1"));
    item2 = (Menu.ChildIF)children.get(2);
    Assert.assertTrue("'item 2' should be the third and last child", 
        item2.getTitle().equals("item 2"));

    // Test Heading.moveOneDown
    item0.moveOneDown();
    children = heading1.getChildren();
    Assert.assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item1 = (Menu.ChildIF)children.get(0);
    Assert.assertTrue("'item 1' should be the first child", 
        item1.getTitle().equals("item 1"));
    item1 = (Menu.ChildIF)children.get(1);
    Assert.assertTrue("'new heading' should be the second child", 
        item1.getTitle().equals("new heading"));
    item2 = (Menu.ChildIF)children.get(2);
    Assert.assertTrue("'item 2' should be the third and last child", 
        item2.getTitle().equals("item 2"));

    // Test Heading.moveOneDown
    item1.moveOneDown();
    children = heading1.getChildren();
    Assert.assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item0 = (Menu.ChildIF)children.get(0);
    Assert.assertTrue("'item 1' should be the first child", 
        item0.getTitle().equals("item 1"));
    item1 = (Menu.ChildIF)children.get(1);
    Assert.assertTrue("'item 2' should be the second child", 
        item1.getTitle().equals("item 2"));
    item2 = (Menu.ChildIF)children.get(2);
    Assert.assertTrue("'new heading' should be the third and last child", 
        item2.getTitle().equals("new heading"));

    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    heading1 = (Heading)menuChildren.get(0);
    children = heading1.getChildren();
    Assert.assertTrue("wrong number of childen on menu",
        children.size() == 3);
    item0 = (Menu.ChildIF)children.get(0);
    Assert.assertTrue("'item 1' should be the first child", 
        item0.getTitle().equals("item 1"));
    item1 = (Menu.ChildIF)children.get(1);
    Assert.assertTrue("'item 2' should be the second child", 
        item1.getTitle().equals("item 2"));
    item2 = (Menu.ChildIF)children.get(2);
    Assert.assertTrue("'new heading' should be the third and last child", 
        item2.getTitle().equals("new heading"));

    // Test Menu.createItem
    heading1.createItem("new item");
    children = heading1.getChildren();
    Assert.assertTrue("wrong number of childen on menu",
        children.size() == 4);
    item0 = (Menu.ChildIF)children.get(0);
    Assert.assertTrue("'item 1' should be the first child", 
        item0.getTitle().equals("item 1"));
    item1 = (Menu.ChildIF)children.get(1);
    Assert.assertTrue("'item 2' should be the second child", 
        item1.getTitle().equals("item 2"));
    item2 = (Menu.ChildIF)children.get(2);
    Assert.assertTrue("'new heading' should be the third child", 
        item2.getTitle().equals("new heading"));
    Menu.ChildIF item3 = (Menu.ChildIF)children.get(3);
    Assert.assertTrue("the forth and last child should be of class Item", 
        item3 instanceof Item);
    Assert.assertTrue("'new item' should be the forth and last child", 
        item3.getTitle().equals("new item"));
    
    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    heading1 = (Heading)menuChildren.get(0);
    children = heading1.getChildren();
    Assert.assertTrue("wrong number of childen on menu",
        children.size() == 4);
    item0 = (Menu.ChildIF)children.get(0);
    Assert.assertTrue("'item 1' should be the first child", 
        item0.getTitle().equals("item 1"));
    item1 = (Menu.ChildIF)children.get(1);
    Assert.assertTrue("'item 2' should be the second child", 
        item1.getTitle().equals("item 2"));
    item2 = (Menu.ChildIF)children.get(2);
    Assert.assertTrue("'new heading' should be the third child", 
        item2.getTitle().equals("new heading"));
    item3 = (Menu.ChildIF)children.get(3);
    Assert.assertTrue("the forth and last child should be of class Item", 
        item3 instanceof Item);
    Assert.assertTrue("'new item' should be the forth and last child", 
        item3.getTitle().equals("new item"));

    // Test Item.moveOneUp
    for (int i = 0; i < 3; i++) {
      item3.moveOneUp();
    }
    children = heading1.getChildren();
    item0 = (Menu.ChildIF)children.get(0);
    Assert.assertTrue("'new item' should be the first child", 
        item0.getTitle().equals("new item"));
    item0.moveOneUp();
    Assert.assertTrue("'new item' should be the first child", 
        item0.getTitle().equals("new item"));

    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    heading1 = (Heading)menuChildren.get(0);
    children = heading1.getChildren();
    item0 = (Menu.ChildIF)children.get(0);
    Assert.assertTrue("'new item' should be the first child", 
        item0.getTitle().equals("new item"));
    item0.moveOneUp();
    Assert.assertTrue("'new item' should be the first child", 
        item0.getTitle().equals("new item"));

    // Test Item.moveOneDown
    for (int i = 0; i < 3; i++) {
      item0.moveOneDown();
    }
    children = heading1.getChildren();
    item3 = (Menu.ChildIF)children.get(3);
    Assert.assertTrue("'new item' should be the third and last child", 
        item3.getTitle().equals("new item"));
    item3.moveOneDown();
    Assert.assertTrue("'new item' should be the third and last child", 
        item3.getTitle().equals("new item"));

    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    heading1 = (Heading)menuChildren.get(0);
    children = heading1.getChildren();
    item3 = (Menu.ChildIF)children.get(3);
    Assert.assertTrue("'new item' should be the third and last child", 
        item3.getTitle().equals("new item"));
    item3.moveOneDown();
    Assert.assertTrue("'new item' should be the third and last child", 
        item3.getTitle().equals("new item"));
  }
  
  @Test
  public void testMenuCreateItem() throws IOException {
    // initialize
    TopicMapIF tm = load("condition-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    Assert.assertTrue("menu topic not found",
               topic != null);

    // test menu
    Menu menu = new Menu(topic);
    Assert.assertTrue("wrong number of childen on menu",
        menu.getChildren().size() == 2);
    menu.createItem("new item");
    Assert.assertTrue("wrong number of childen on menu",
        menu.getChildren().size() == 3);

    // Reconstruct the menu to see if changes are mede to the TM, not just Menu.
    menu = new Menu(topic);
    Assert.assertTrue("wrong number of childen on menu",
        menu.getChildren().size() == 3);
  }
  
  @Test
  public void testDeleteMenu() throws IOException {
    // initialize
    TopicMapIF tm = load("condition-menu.ltm");
    TopicIF topic = getTopicById(tm, "menu1");
    Assert.assertTrue("menu topic not found",
               topic != null);

    // test menu
    Menu menu = new Menu(topic);
	
	// names must have types, check presence
	TopicIF tname = tm.getTopicBySubjectIdentifier(PSI.getSAMNameType());
	Assert.assertNotNull(tname);
	
	// 23 topics + nametype = 24
    Assert.assertEquals(24, tm.getTopics().size());
    Assert.assertEquals(6, tm.getAssociations().size());
    menu.delete();
	
	// 20 topics + nametype = 21
    Assert.assertEquals(21, tm.getTopics().size());
    Assert.assertEquals(1, tm.getAssociations().size());

    Assert.assertNull(topic.getTopicMap());
    // Since the topic is not part of a topic map anymore,
    // there's no further persistence tests to do.
    // The menu cannot be reconstructed as it doesn't exist.
  }
  
  // --- Helpers
  
  private TopicMapIF load(String filename) throws IOException {
    filename = TestFileUtils.getTestInputFile(testdataDirectory, "topicmaps", filename);
    return ImportExportUtils.getReader(filename).read();
  }

  private TopicIF getTopicById(TopicMapIF topicmap, String id) {
    LocatorIF srcloc =
      topicmap.getStore().getBaseAddress().resolveAbsolute("#" + id);
    return (TopicIF) topicmap.getObjectByItemIdentifier(srcloc);
  }
}
