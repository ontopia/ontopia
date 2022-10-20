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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.ParsedQueryIF;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: This component can produce a model representing a menu
 * structure.
 */
public class Menu {

  private TopicIF topic;
  private TopicIF owner;

  public Menu(TopicIF topic) {
    try {
      this.topic = topic;

      TopicMapIF tm = topic.getTopicMap();
      ParsedQueryIF ownerQuery = MenuUtils.optimisticParse("select $OWNER from " +
          "menu:owned-by(%topic% : menu:owned, $OWNER : menu:owner)?", tm);
      owner = (TopicIF)MenuUtils.getFirstValue(topic, ownerQuery);
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  /**
   * The title of this Menu as a String.
   */
  public String getTitle() {
    return (topic != null ? TopicStringifiers.toString(topic) : null);
  }
  
  /**
   * Get the topic of this Menu.
   */
  public TopicIF getTopic() {
    return topic;
  }
  
  /**
   * Get the owner of this Menu.
   */
  public TopicIF getOwner() {
    return owner;
  }
  
  /**
   * Get the children of this Menu.
   */
  public List getChildren() {
    return buildChildren(topic);
  }

  /**
   * Check if this Menu has children.
   */
  public boolean getHasChildren() {
    return !getChildren().isEmpty();
  }

  /**
   * Sets the title of this Menu.
   */
  public void setTitle(String title) {
    MenuUtils.setUniqueTopicName(topic, title);
  }
  
  /**
   * Set related owner topic.
   */
  public void setOwner(TopicIF owner) {
    this.owner = owner;
    MenuUtils.setUniqueAssociation(topic, "menu:owned", "menu:owned-by",
                                          "menu:owner", owner);
  }

  /** 
   * Create new heading as child of this menu
   * @return The heading that was created.
   */
  public Heading createHeading(String title) {
    return MenuUtils.createHeading(topic, title);
  }

  /** 
   * Create new item as child of this menu 
   * @return The item that was created.
   */
  public Item createItem(String title) {
    return MenuUtils.createItem(topic, title);
  }

  /**
   * Delete this menu with all headings and items
   */
  public void delete() {
    Collection children = getChildren();
    Iterator childrenIt = children.iterator();
    while (childrenIt.hasNext()) {
      ChildIF currentChild = (ChildIF)childrenIt.next();
      currentChild.delete();
    }
    
    // Delete the topic, all associations it's part of and all its occurrences
    topic.remove();
  }
  
  /**
   * Build the Heading of a given topic, from the topic map content.
   */
  private static Heading buildHeading(TopicIF topic) {
    Heading heading = new Heading(topic);
    heading.children = buildChildren(topic);
    return heading;
  }
  
  /**
   * Build the Item of a given topic, from the topic map content.
   */
  private static Item buildItem(TopicIF topic) {
    TopicMapIF tm = topic.getTopicMap();
    ParsedQueryIF itemTopicQuery = MenuUtils.optimisticParse(
        "select $TOPIC from " +
        "menu:item-topic(%topic% : menu:item, $TOPIC : menu:topic)?", tm);
    ParsedQueryIF linkQuery = MenuUtils.optimisticParse(
        "select $LINK from menu:link(%topic%, $LINK)?", tm);
    ParsedQueryIF imageQuery = MenuUtils.optimisticParse(
        "select $IMAGE from menu:image(%topic%, $IMAGE)?", tm);

    Item item = new Item(topic);
    item.associatedTopic = (TopicIF)MenuUtils
        .getFirstValue(topic,itemTopicQuery);
    item.link = (String)MenuUtils.getFirstValue(topic, linkQuery);
    item.image = (String)MenuUtils.getFirstValue(topic, imageQuery);
    
    String query = item.getCondition();
    item.condition = (query == null) || MenuUtils
        .getResultTrue(item.associatedTopic, query);
    
    return item;
  }

  /**
   * Build the List of children of a given parent, from the topic map content.
   */
  private static List buildChildren(TopicIF topic) {    
    TopicMapIF tm = topic.getTopicMap();
    ParsedQueryIF childrenQuery = MenuUtils.optimisticParse(
        "select $CHILD, $SORT from " +
        "menu:parent-child(%topic% : menu:parent, $CHILD : menu:child), " +
        "{ menu:sort($CHILD, $SORT) } order by $SORT?", tm);
    ParsedQueryIF headingsQuery = MenuUtils.optimisticParse(
        "select $CHILD from " +
        "menu:parent-child(%topic% : menu:parent, $CHILD : menu:child), " +
        "instance-of($CHILD, menu:heading)?", tm);
    List childrenTopics = MenuUtils.getResultValues(topic, childrenQuery);
    List headingsTopics = MenuUtils.getResultValues(topic, headingsQuery);
    
    // populate children
    List children = new ArrayList(childrenTopics.size());
    for (int i=0; i < childrenTopics.size(); i++) {
      TopicIF child = (TopicIF)childrenTopics.get(i);
      if (headingsTopics.contains(child)) {
        children.add(buildHeading(child));
      } else {
        children.add(buildItem(child));
      }
    }
    return children;    
  }

  // --- MenuObjectIF

  public interface MenuObjectIF {
    /**
     * Get the title as a String.
     */
    String getTitle();
    
    /**
     * Get the topic representing this MenuObjectIF as a String.
     */
    TopicIF getTopic();

    /**
     * Check if this MenuObjectIF has children.
     */
    boolean getHasChildren();

    /**
     * Sets the title.
     */
    void setTitle(String title);
    
    /**
     * Delete this child.
     */
    void delete();
  }

  // --- ParentIF

  public interface ParentIF extends MenuObjectIF {
    /**
     * Get the children.
     */
    List getChildren();
    
    /** 
     * Create new heading as child of this menu
     * @return The heading that was created.
     */
    Heading createHeading(String title);

    /** 
     * Create new item as child of this menu 
     * @return The item that was created.
     */
    Item createItem(String title);
  }

  // --- ChildIF

  public interface ChildIF extends MenuObjectIF {
    /**
     * Check whether this is a Heading.
     */
    boolean getIsHeading();
    
    /**
     * Check whether this is an Item.
     */
    boolean getIsItem();
    
    /** 
     * Moves this child one step higher up the list of children on its parent.
     * has no effect if it is already first.
     */
    void moveOneUp();
    
    /**
     * Moves this child one step further down its parent's list of children.
     * Has no effect if it is already last.
     */
    void moveOneDown();
  }

  // --- Heading

  public static class Heading implements ChildIF, ParentIF {
    private TopicIF topic;
    protected List children;
    
    public Heading(TopicIF topic) {
      this.topic = topic;
    }

    /**
     * Check whether this is a Heading.
     * @return true, since it is a Heading.
     */
    @Override
    public boolean getIsHeading() {
      return true;
    }

    /**
     * Check whether this is an Item.
     * @return false, since it is not an Item.
     */
    @Override
    public boolean getIsItem() {
      return false;
    }
    
    /**
     * The title of this Heading as a String.
     */
    @Override
    public String getTitle() {
      return (topic != null ? TopicStringifiers.toString(topic) : null);
    }

    /**
     * Get the topic of this Heading.
     */
    @Override
    public TopicIF getTopic() {
      return topic;
    }

    /**
     * Get the children of this Heading.
     */
    @Override
    public List getChildren() {
      return buildChildren(topic);
    }

    /**
     * Check if this Heading has children.
     */
    @Override
    public boolean getHasChildren() {
      return !children.isEmpty();
    }

    /**
     * Sets the title of this Heading.
     */
    @Override
    public void setTitle(String title) {
      MenuUtils.setUniqueTopicName(topic, title);    
    }
    
    /** 
     * Moves this Heading one step up the list of children on its parent.
     * has no effect if it is already first
     */
    @Override
     public void moveOneUp() {
      MenuUtils.moveOne(topic, MenuUtils.UP);
    }

    /**
     * Moves this Heading one step down the list of children on its parent.
     * Has no effect if it is already last.
     */
    @Override
    public void moveOneDown() {
      MenuUtils.moveOne(topic, MenuUtils.DOWN);
    }

    /** 
     * Create new Heading as child of this Heading
     * @return The heading that was created.
     */
    @Override
    public Heading createHeading(String title) {
      return MenuUtils.createHeading(topic, title);
    }

    /**
     * Create new Item as child of this Heading.
     * @return The item that was created.
     */
    @Override
    public Item createItem(String title) {
      return MenuUtils.createItem(topic, title);
    }

    /**
     * Delete this Heading with all its descendants.
     */
    @Override
    public void delete() {
      // Delete all children.
      Collection children = getChildren();
      Iterator childrenIt = children.iterator();
      while (childrenIt.hasNext()) {
        ChildIF currentChild = (ChildIF)childrenIt.next();
        currentChild.delete();
      }
      
      // Delete the topic, all associations it's part of and all its occurrences
      topic.remove();
    }
  }

  // --- Item

  public static class Item implements ChildIF {
    protected TopicIF associatedTopic;
    protected String link;
    protected String image;
    protected String conditionString;
    protected boolean condition;
    private TopicIF topic;

    /**
     * Create a new Item, represented by the given topic.
     */
    public Item(TopicIF topic) {
      this.topic = topic;
    }

    /**
     * Check whether this Item is a Heading.
     * @return false, since it is not a Heading.
     */
    @Override
    public boolean getIsHeading() {
      return false;
    }

    /**
     * Check whether this is an Item.
     * @return true, since it is an Item.
     */
    @Override
    public boolean getIsItem() {
      return true;
    }

    /**
     * Get the title of this Item as a String.
     */
    @Override
    public String getTitle() {
      return (topic != null ? TopicStringifiers.toString(topic) : null);
    }

    /**
     * Get the associated topic of this Item.
     */
    public TopicIF getAssociatedTopic() {
      return associatedTopic;
    }

    /**
     * Get the topic of this Item.
     */
    @Override
    public TopicIF getTopic() {
      return topic;
    }

    /**
     * The link occurrence as a String.
     */
    public String getLink() {
      return link;
    }

    /**
     * Get the image occurrence as a String.
     */
    public String getImage() {
      return image;
    }

    /**
     * Check if this Item has children.
     * $return false, since an Item cannot have any children.
     */
    @Override
    public boolean getHasChildren() {
      return false;
    }

    /**
     * Check the condition of this Item.
     */
    public boolean getConditionTrue() {
      return condition;
    }
    
    /**
     * Sets the title of the Item topic.
     */
    @Override
    public void setTitle(String title) {
      MenuUtils.setUniqueTopicName(topic, title);
    }

    /** 
     * Set the associated topic.
     */
    public void setAssociatedTopic(TopicIF topic) {
      this.associatedTopic = topic;
      MenuUtils.setUniqueAssociation(this.topic, "menu:item", "menu:item-topic",
                                            "menu:topic", associatedTopic);
      updateConditionState();
    }

    /** 
     * Set the link occurrence value.
     */
    public void setLink(String link) {
      MenuUtils.setUniqueOccurrence(topic, "menu:link", link);
      this.link = link;
    }
    
    /**
     * Set the image occurrence value.
     */
    public void setImage(String image) {
      MenuUtils.setUniqueOccurrence(topic, "menu:image", image);
      this.image = image;
    }

    /** 
     * Get the condition occurrence value as a String.
     */
    public String getCondition() {
      TopicMapIF tm = topic.getTopicMap();
      ParsedQueryIF conditionQuery = MenuUtils.optimisticParse(
          "select $CONDITION from menu:condition(%topic%, $CONDITION)?", tm);
      return (String)MenuUtils.getFirstValue(topic, conditionQuery);
    }

    /**
     * Set the condition occurrence value.
     */
    public void setCondition(String condition) {
      MenuUtils.setUniqueOccurrence(topic, "menu:condition", condition);
      conditionString = condition;
      updateConditionState();
    }

    /** 
     * Moves this item one step higher up the list of children on its parent.
     * has no effect if it is already first.
     */
    @Override
    public void moveOneUp() {
      MenuUtils.moveOne(topic, MenuUtils.UP);
    }
    
    /**
     * Moves this item one step further down the list of children on its parent.
     * Has no effect if it is already last.
     */
    @Override
    public void moveOneDown() {
      MenuUtils.moveOne(topic, MenuUtils.DOWN);
    }

    /**
     * Delete this Item.
     */
    @Override
    public void delete() {
      // Delete the topic, all associations it's part of and all its occurrences
      topic.remove();
    }
    
    /**
     * Update the state of the boolean condition value on this item.
     * This method should be called whenver a new associated topic is set or
     * the conditionString has been changed, either of which may change the
     * condition state.
     */
    private void updateConditionState() {
      condition = conditionString == null || (associatedTopic != null &&
         MenuUtils.getResultTrue(associatedTopic, conditionString));
    }
  }
}
