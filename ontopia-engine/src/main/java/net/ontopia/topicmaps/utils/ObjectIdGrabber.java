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

import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;

/**
 * INTERNAL: Grabber that grabs the object id of the TMObjectIF given to
 * it. This class is very similar to the ObjectIdGrabber, except that
 * this class implements GrabberIF instead of StringifierIF.</p>
 */

public class ObjectIdGrabber implements GrabberIF {
  
  /**
   * INTERNAL: Grabs the objectId of the given TMObjectIF
   *
   * @param object the given object; internally typecast to TMObjectIF
   * @return object which is the objectId of the given TMObjectIF
   */ 

  public Object grab(Object object) {
    return ((TMObjectIF)object).getObjectId();

  }

}





