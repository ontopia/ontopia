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

import java.util.Objects;

/**
 * INTERNAL: Default implementation of the ImageInformationIF
 * interface.
 */
public class ImageInformation implements ImageInformationIF {

  protected String name;
  protected String relative_url;
  protected String width;
  protected String height;
  protected String border;
  protected String align;
  
  public ImageInformation(String name, String relative_url,
                          String width, String height, 
                          String border, String align) {
    this.name = name;
    this.relative_url = relative_url;
    this.width = width;
    this.height = height;
    this.border = border;
    this.align = align;
  }
  
  public String getName() {
    return name;
  }

  public String getRelativeURL() {
    return relative_url;
  }

  public String getWidth() {
    return width;
  }

  public String getHeight() {
    return height;
  }

  public String getBorder() {
    return border;
  }

  public String getAlign() {
    return align;
  }
  

  // --- overwrite methods from java.lang.Object

  public int hashCode() {
    StringBuilder sb = new StringBuilder(32);
    sb.append(name).append(relative_url).append(width).append(height)
      .append(border).append(align);
    return sb.toString().hashCode();
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof ImageInformation))
      return false;
    ImageInformation compObj = (ImageInformation) obj;
    return (Objects.equals(compObj.getName(), name)
            && Objects.equals(compObj.getRelativeURL(), relative_url)
            && Objects.equals(compObj.getWidth(), width)
            && Objects.equals(compObj.getHeight(), height)
            && Objects.equals(compObj.getBorder(), border)
            && Objects.equals(compObj.getAlign(), align));
  }

  public String toString() {
    StringBuilder sb = new StringBuilder(48);
    sb.append("[ImageInformation: ").append(name).append(", ")
      .append(relative_url).append(", ")
      .append(width).append(", ")
      .append(height).append(", ")
      .append(border).append(", ")
      .append(align).append("]");
    return sb.toString();
  }
  
}
