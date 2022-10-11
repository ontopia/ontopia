/*
 * #!
 * Ontopia Engine
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

package net.ontopia.topicmaps.utils;

import java.util.Collection;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.utils.GrabberIF;

/**
 * INTERNAL: Grabber that grabs the association roles of an association.
 */

@Deprecated
public class RolesGrabber implements GrabberIF<AssociationIF, Collection<AssociationRoleIF>> {
  
  /**
   * INTERNAL: Grabs the association roles of the given association
   *
   * @param object the given object; AssociationIF
   * @return object which is a collection of AssociationRoleIF objects
   */ 
  @Override
  public Collection<AssociationRoleIF> grab(AssociationIF object) {
    return object.getRoles();
  }

}





