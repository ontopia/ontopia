/*
 * #!
 * Ontopia Classify
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

package net.ontopia.topicmaps.classify;

/**
 * INTERNAL: Callback interface used by format modules to tell the
 * classification framework about the structure of classifiable
 * content.<p>
 *
 * The calls to startRegion can be nested, but they must have
 * been unnested via calls to endRegion at the time when the end of
 * the classifiable content has been reached.
 */
public interface TextHandlerIF {

  /**
   * INTERNAL: Starts a new document region. Regions can be nested.
   */
  public void startRegion(String regionName);

  /**
   * INTERNAL: Text found in the classifiable content. Subsequent
   * calls to this method is allowed.
   */  
  public void text(char[] ch, int start, int length);

  /**
   * INTERNAL: Ends the current document region.
   */  
  public void endRegion();
    
}
