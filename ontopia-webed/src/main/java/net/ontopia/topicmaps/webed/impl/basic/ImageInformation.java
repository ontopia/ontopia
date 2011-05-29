
package net.ontopia.topicmaps.webed.impl.basic;

import net.ontopia.utils.ObjectUtils;

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
    StringBuffer sb = new StringBuffer(32);
    sb.append(name).append(relative_url).append(width).append(height)
      .append(border).append(align);
    return sb.toString().hashCode();
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof ImageInformation))
      return false;
    ImageInformation compObj = (ImageInformation) obj;
    return (ObjectUtils.equals(compObj.getName(), name)
            && ObjectUtils.equals(compObj.getRelativeURL(), relative_url)
            && ObjectUtils.equals(compObj.getWidth(), width)
            && ObjectUtils.equals(compObj.getHeight(), height)
            && ObjectUtils.equals(compObj.getBorder(), border)
            && ObjectUtils.equals(compObj.getAlign(), align));
  }

  public String toString() {
    StringBuffer sb = new StringBuffer(48);
    sb.append("[ImageInformation: ").append(name).append(", ")
      .append(relative_url).append(", ")
      .append(width).append(", ")
      .append(height).append(", ")
      .append(border).append(", ")
      .append(align).append("]");
    return sb.toString();
  }
  
}
