/*
 * #!
 * Ontopia Webed
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

package net.ontopia.topicmaps.webed.impl.basic;

/**
 * INTERNAL: Container for storing information about an image used to
 * display a graphical button.
 */
public interface ImageInformationIF {

  /**
   * INTERNAL: Gets the name of the image.
   */
  public String getName();

  /**
   * INTERNAL: Gets the relative URL to the image location.
   */
  public String getRelativeURL();

  /**
   * INTERNAL: Gets the width of the image in pixels.
   */
  public String getWidth();

  /**
   * INTERNAL: Gets the height of the image in pixels.
   */
  public String getHeight();

  /**
   * INTERNAL: Gets the width of the border displayed around the image
   * in pixels. Default is to display no border (equivalent to 0
   * pixels).
   */
  public String getBorder();

  /**
   * INTERNAL: Gets the align mode in which the image should be rendered. 
   * Default mode is "middle".
   */
  public String getAlign();
  
}
