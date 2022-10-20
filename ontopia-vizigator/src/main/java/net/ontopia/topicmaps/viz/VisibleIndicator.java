/*
 * #!
 * Ontopia Vizigator
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

package net.ontopia.topicmaps.viz;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

/**
 * Indicator of whether a menu item has been checked, unchecked or
 * used default (the default being checked or unchecked).
 */
public class VisibleIndicator {

  public static final byte CHECKED = 3;
  public static final byte UNCHECKED = 2;
  public static final byte DEFAULT_CHECKED = 1;
  public static final byte DEFAULT_UNCHECKED = 0;
  private byte state = DEFAULT_CHECKED;
  
  VisibleIndicator(byte state) {
    this.state = state;
  }

  // --- State access and manipulation

  public byte getSelected() {
    return state;
  }

  public boolean isSelected() {
    return state == DEFAULT_CHECKED || state == CHECKED;
  }

  public void setDefault(byte state) {
    if (this.state == UNCHECKED || this.state == CHECKED) {
      return;
    }
    if (state == CHECKED || state == DEFAULT_CHECKED) {
      setSelected(DEFAULT_CHECKED);
    } else {
      setSelected(DEFAULT_UNCHECKED);
    }
  }

  public void setSelected(byte state) {
    this.state = state;
  }

  // --- Component painting
  
  /**
   * Draw a coloured square at the end of the display.
   */
  public void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
    float centerx = 7.0f;
    float centery = 9.0f;

    if (state == DEFAULT_UNCHECKED) {
      drawCross(g2, Color.lightGray, centerx, centery, 3);
    } else if (state == DEFAULT_CHECKED) {
      drawTick(g2, Color.lightGray, centerx, centery, 3);
    } else if (state == UNCHECKED) {
      drawCross(g2, Color.red, centerx, centery, 3);
    } else if (state == CHECKED) {
      drawTick(g2, Color.green, centerx, centery, 3);
    }
  }  
  
  protected void drawTick(Graphics2D g2, Color color,
                          float xCentre, float yCentre,
                          int width) {
    Color oldColor = g2.getColor();
    g2.setColor(color);
    
    GeneralPath generalPath = new GeneralPath();
    float size = 4f;

    for (int ix = 0; ix < width; ix++) {
      // drawing one stroke making up the width of the tick
      float offset = ix - (width/2f);
      generalPath.moveTo(xCentre - size + offset, yCentre);
      generalPath.lineTo(xCentre + offset, yCentre + size);
      generalPath.lineTo(xCentre + size + offset, yCentre - size);
      g2.draw(generalPath);
    }
    
    g2.setColor(oldColor);
  }

  protected void drawCross(Graphics2D g2, Color color,
                           float xCentre, float yCentre,
                           int width) {
    Color oldColor = g2.getColor();
    g2.setColor(color);
    
    GeneralPath generalPath = new GeneralPath();
    
    float size = 4f;
    for (int ix = 0; ix < width; ix++) {
      // drawing one stroke making up the width cross
      float offset = ix - (width/2f);
      generalPath.moveTo(xCentre - size + offset, yCentre - size);
      generalPath.lineTo(xCentre + size + offset, yCentre + size);
      generalPath.moveTo(xCentre - size + offset, yCentre + size);
      generalPath.lineTo(xCentre + size + offset, yCentre - size);
      g2.draw(generalPath);
    }

    g2.setColor(oldColor);
  }
}
