
package net.ontopia.topicmaps.utils;

import java.util.Collection;
import java.util.Iterator;
import java.io.OutputStream;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import net.ontopia.utils.StringifierIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.utils.TopicStringifiers;

/**
 * EXPERIMENTAL.
 * @since 1.2
 */
public class TopicTreeRendrer {
  protected TopicTreeNode root;
  protected TopicIF current;
  protected Graphics2D g2d;
  protected StringifierIF str;

  protected String title;
  protected Collection bnscope;
  protected Collection vnscope;
  
  protected int x;
  protected int y;
  protected int xmargin;
  protected int ymargin;
  protected int xoffset;
  protected int yoffset;
  protected int xtextmargin;
  protected int ytextmargin;
  protected int height;

  public TopicTreeRendrer(TopicTreeNode root, TopicIF current) {
    xmargin = 10;
    ymargin = 10;
    xoffset = 40;
    yoffset = 35;
    xtextmargin = 15;
    ytextmargin = 15;
    height = 25;

    this.root = root;
    this.current = current;
  }

  public void draw(Graphics2D g2d) {
    this.g2d = g2d;
    this.g2d.setBackground(Color.white);
    this.g2d.clearRect(0, 0, getMaxX() + 100, getMaxY());
    this.g2d.setPaint(Color.black);
    x = 0;
    y = 0;
    
    drawNode(root);
  }

  public BufferedImage renderImage() {
    BufferedImage image = new BufferedImage(getMaxX(), getMaxY(),
                                            BufferedImage.TYPE_BYTE_INDEXED);
    Graphics2D g2d = image.createGraphics();
    draw(g2d);
    return image;
  }

  public void setTitle(String title) {
    this.title = title;
  }
  
  public int getMaxX() {
    return getMaxX(root, 0) + 25;
  }

  public int getMaxY() {
    return getBottom(getMaxY(root, 0));
  }

  public void setTopicNameScope(Collection bnscope) {
    this.bnscope = bnscope;
  }

  public void setVariantScope(Collection vnscope) {
    this.vnscope = vnscope;
  }
  
  // --- Internal methods

  protected int drawNode(TopicTreeNode node) {
    String title = getTitle(node);
    if (current != null && current.equals(node.getTopic()))
      g2d.setPaint(Color.red);

    node.setAttribute("x1", new Integer(getLeft(x)));
    node.setAttribute("x2", new Integer(getLeft(x) + getWidth(title)));
    node.setAttribute("y1", new Integer(getTop(y)));
    node.setAttribute("y2", new Integer(getTop(y) + height));
    g2d.drawString(title, getTextStart(x), getTextTop(y));
    g2d.draw(new Rectangle(getLeft(x), getTop(y), getWidth(title), height));
    g2d.setPaint(Color.black);
    int thisY = y++;

    int lasty = 0;
    x++;
    Iterator it = node.getChildren().iterator();
    while (it.hasNext()) {
      TopicTreeNode child = (TopicTreeNode) it.next();
      g2d.draw(new Line2D.Float(getLeft(x-1) + 10, getMiddle(y),
                                getLeft(x), getMiddle(y)));
      lasty = drawNode(child);
    }

    x--;
    if (!node.getChildren().isEmpty()) 
      g2d.draw(new Line2D.Float(getLeft(x) + 10, getBottom(thisY),
                                getLeft(x) + 10, getTop(lasty) + (height/2)));

    return thisY;
  }

  protected int getLeft(int x) {
    return xmargin + x * xoffset;
  }

  protected int getRight(int x, String title) {
    return getLeft(x) + getWidth(title);
  }

  protected int getWidth(String title) {
    if (g2d == null)
      return title.length()*8 + 30;
    
    Font font = g2d.getFont();
    int width = (int)
      font.getStringBounds(title, g2d.getFontRenderContext()).getWidth();
    return width + 30;
  }

  protected int getTop(int y) {
    return ymargin + y * yoffset;
  }

  protected int getMiddle(int y) {
    return ymargin + y * yoffset + (height/2);
  }

  protected int getBottom(int y) {
    return ymargin + y * yoffset + height;
  }

  protected int getTextStart(int x) {
    return getLeft(x) + xtextmargin;
  }

  protected int getTextTop(int y) {
    return getTop(y) + ytextmargin;
  }

  protected int getMaxX(TopicTreeNode node, int x) {
    int ourX = getRight(x, getTitle(node));
    Iterator it = node.getChildren().iterator();
    while (it.hasNext())
      ourX = Math.max(ourX, getMaxX((TopicTreeNode) it.next(), x+1));

    return ourX;
  }

  protected int getMaxY(TopicTreeNode node, int y) {
    y++;
    Iterator it = node.getChildren().iterator();
    while (it.hasNext())
      y = getMaxY((TopicTreeNode) it.next(), y);

    return y;
  }

  protected String getTitle(TopicTreeNode node) {
    TopicIF topic = node.getTopic();

    if (str == null)
      str = TopicStringifiers.getStringifier(bnscope, vnscope);
    
    if (topic != null) 
      return str.toString(topic);
    else if (title != null)
      return title;
    else
      return "<null>";
  }
  
}
