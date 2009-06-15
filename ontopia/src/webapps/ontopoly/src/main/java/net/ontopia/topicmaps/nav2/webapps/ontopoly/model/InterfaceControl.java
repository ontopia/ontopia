// $Id: InterfaceControl.java,v 1.1 2008/10/23 05:18:36 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.webapps.ontopoly.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.utils.OntopolyModelUtils;

/**
 * Represents a datatype which can be assigned to an association field.
 */
public class InterfaceControl extends Topic {

  public InterfaceControl(TopicIF topic, TopicMap tm) {
    super(topic, tm);
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   */
  public boolean equals(Object obj) {
    if (!(obj instanceof InterfaceControl))
      return false;

    InterfaceControl other = (InterfaceControl) obj;
    return (getTopicIF().equals(other.getTopicIF()));
  }

  /**
   * Tests whether this interface control is on:drop-down-list.
   * 
   * @return true if this interface control is on:drop-down-list.
   */
  public boolean isDropDownList() {
    return getTopicIF().getSubjectIdentifiers().contains(
        PSI.ON_INTERFACE_CONTROL_DROP_DOWN_LIST);
  }

  /**
   * Tests whether this interface control is on:search-dialog.
   * 
   * @return true if this interface control is on:search-dialog.
   */
  public boolean isSearchDialog() {
    return getTopicIF().getSubjectIdentifiers().contains(
        PSI.ON_INTERFACE_CONTROL_SEARCH_DIALOG);
  }

  /**
   * Tests whether this interface control is on:browse-dialog.
   * 
   * @return true if this interface control is on:browse-dialog.
   */
  public boolean isBrowseDialog() {
    return getTopicIF().getSubjectIdentifiers().contains(
        PSI.ON_INTERFACE_CONTROL_BROWSE_DIALOG);
  }

  /**
   * Tests whether this interface control is on:auto-complete
   * 
   * @return true if this interface control is on:auto-complete.
   */
  public boolean isAutoComplete() {
    return getTopicIF().getSubjectIdentifiers().contains(
        PSI.ON_INTERFACE_CONTROL_AUTO_COMPLETE);
  }

  public static InterfaceControl getDefaultInterfaceControl(TopicMap tm) {
    return new InterfaceControl(OntopolyModelUtils.getTopicIF(tm, PSI.ON, "drop-down-list"), tm);
  }

  public static List getInterfaceControlTypes(TopicMap tm) {
    String query = "instance-of($d, on:interface-control)?";

    Collection result = tm.getQueryWrapper().queryForList(query,
        OntopolyModelUtils.getRowMapperOneColumn());

    if (result.isEmpty())
      return Collections.EMPTY_LIST;

    List interfaceControlTypes = new ArrayList();
    Iterator it = result.iterator();
    while (it.hasNext()) {
      interfaceControlTypes.add(new InterfaceControl((TopicIF) it.next(), tm));
    }
    return interfaceControlTypes;
  }

}
