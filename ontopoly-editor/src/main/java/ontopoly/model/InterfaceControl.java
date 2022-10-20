/*
 * #!
 * Ontopoly Editor
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

package ontopoly.model;

import java.util.Collection;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import ontopoly.utils.OntopolyModelUtils;

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
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof InterfaceControl)) {
      return false;
    }

    InterfaceControl other = (InterfaceControl) obj;
    return (getTopicIF().equals(other.getTopicIF()));
  }

  public LocatorIF getLocator() {
    Collection<LocatorIF> subjectIdentifiers = getTopicIF().getSubjectIdentifiers();
    if (subjectIdentifiers.contains(PSI.ON_INTERFACE_CONTROL_AUTO_COMPLETE)) {
      return PSI.ON_INTERFACE_CONTROL_AUTO_COMPLETE;
    } else if (subjectIdentifiers.contains(PSI.ON_INTERFACE_CONTROL_BROWSE_DIALOG)) {
      return PSI.ON_INTERFACE_CONTROL_BROWSE_DIALOG;
    } else if (subjectIdentifiers.contains(PSI.ON_INTERFACE_CONTROL_DROP_DOWN_LIST)) {
      return PSI.ON_INTERFACE_CONTROL_DROP_DOWN_LIST;
    } else if (subjectIdentifiers.contains(PSI.ON_INTERFACE_CONTROL_SEARCH_DIALOG)) {
      return PSI.ON_INTERFACE_CONTROL_SEARCH_DIALOG;
    } else {
      return null;
    }
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
    return new InterfaceControl(OntopolyModelUtils.getTopicIF(tm, PSI.ON_INTERFACE_CONTROL_DROP_DOWN_LIST), tm);
  }

//  public static List<InterfaceControl> getInterfaceControlTypes(TopicMap tm) {
//    String query = "instance-of($d, on:interface-control)?";
//
//    QueryMapper<InterfaceControl> qm = tm.newQueryMapper(InterfaceControl.class);    
//    return qm.queryForList(query);
//  }

}
