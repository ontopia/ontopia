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
import java.awt.event.ActionEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyInputManager implements KeyListener, ContainerListener {
  protected VizController controller;
  protected VizPanel vpanel;
  private KeyEvent lastProcessed;
  private boolean keyModifierDown;
  
  private static final String MAC_ID = "mac";
  protected static final int KEY_MODIFIER = 
      System.getProperty("os.name").toLowerCase().startsWith(MAC_ID) ? 
          KeyEvent.VK_META : KeyEvent.VK_CONTROL;
  protected static final int KEY_MASK = 
    System.getProperty("os.name").toLowerCase().startsWith(MAC_ID) ? 
        ActionEvent.META_MASK : ActionEvent.CTRL_MASK;
  
  public KeyInputManager(VizController controller) {
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
  
  @Override
  public void keyPressed(KeyEvent keyEvent) {
    if (keyEvent.getKeyCode() == KEY_MODIFIER) {
      keyModifierDown = true;
    }
  }
  
  @Override
  public void keyReleased(KeyEvent keyEvent) {
    if (keyEvent == lastProcessed) {
      return;
    } else {
      lastProcessed = keyEvent;
    }
    
    if (keyEvent.getKeyCode() == KEY_MODIFIER) {
      keyModifierDown = false;
    }

    if (UndoManager.ENABLE_UNDO_MANAGER && keyModifierDown) {
      if (keyEvent.getKeyCode() == KeyEvent.VK_Z) {
        controller.undo();
      }
      if (keyEvent.getKeyCode() == KeyEvent.VK_Y) {
        controller.redo();
      }
    }
  }
  
  @Override
  public void keyTyped(KeyEvent keyEvent) {
    // no-op
  }

  @Override
  public void componentAdded(ContainerEvent e) {
    listenTo(e.getComponent());
  }

  @Override
  public void componentRemoved(ContainerEvent e) {
    // no-op
  }
}
