
package net.ontopia.topicmaps.viz;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.JMenuItem;

public class ColouredSquareMenuItem extends JMenuItem {
  /** The colour to be used when rendering the square. */
  private Color squareColor;
  
  
  /** Indicates the selection state of this item. */
  private VisibleIndicator visibleIndicator;

  public ColouredSquareMenuItem(String displayText, byte state) {
    super(displayText + "             ");
    this.visibleIndicator = new VisibleIndicator(state);
  }

  /** Sets the color of the square. */
  public void setSquareColor(Color c) {
    squareColor = c;
  }

  /** Draw a coloured square at the end of the display. */
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D) g;

    Shape clip = g2.getClip();
    Rectangle bounds = clip.getBounds();
    double width = bounds.getWidth();
    double height = bounds.getHeight();

    g2.setColor(squareColor);
    g2.fill3DRect(((int) width - 25), 0, 25, (int) height, true);
    
    visibleIndicator.paintComponent(g);
  }
  
  public VisibleIndicator getVisibleIndicator() {
    return visibleIndicator;
  }
}
