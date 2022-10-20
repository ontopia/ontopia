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

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class HighlightNode implements KeyListener, ContainerListener {
  protected TMAbstractNode node = null;
  protected boolean fixed = false;
  protected VizController controller;
  protected VizPanel vpanel;
  
  public HighlightNode(VizController controller) {
    this.controller = controller;
    vpanel = controller.getVizPanel();
    listenTo(vpanel);
  }
  
  private void listenTo(Component component) {
    component.addKeyListener(this);
    if (component instanceof Container) {
      Container container = (Container)component;
      container.addContainerListener(this);
      
      Component components[] = container.getComponents();
      for (int i = 0; i < components.length; i++) {
        Component currentComponent = components[i];
        listenTo(currentComponent);
      }
    }
  }
  
  public void setNode(TMAbstractNode node, Graphics g) {
    if (!fixed) {
      this.node = node;
    }
    
    if (this.node != null) {
      highlight(g);
    }
  }
  
  protected void highlight(Graphics g) {
    if (controller.showNeighboursOnMouseover) {
      TopicMapView view = controller.getView();
      node.drawNeighboursInForeground(g, view.getTGPanel());
      view.processForegroundQueue(g);
    }
  }
  
  @Override
  public void keyPressed(KeyEvent keyEvent) {
    if (keyEvent.getKeyCode() == KeyEvent.VK_CONTROL) {
      fixed = true;
      TopicMapView view = controller.getView();
      if (view != null) {
        view.getTGPanel().repaint();
      }
    }
  }
  
  @Override
  public void keyReleased(KeyEvent keyEvent) {
    if (keyEvent.getKeyCode() == KeyEvent.VK_CONTROL) {
      fixed = false;
      TopicMapView view = controller.getView();
      if (view != null) {
        view.getTGPanel().repaint();
      }
    }
  }
  
  @Override
  public void keyTyped(KeyEvent keyEvent) {
    // Do nothing. Required by interface KeyListener.
  }

  @Override
  public void componentAdded(ContainerEvent e) {
    listenTo(e.getComponent());
  }

  @Override
  public void componentRemoved(ContainerEvent e) {
    // Do nothing. Required by interface ComponentListener.
  }
}
