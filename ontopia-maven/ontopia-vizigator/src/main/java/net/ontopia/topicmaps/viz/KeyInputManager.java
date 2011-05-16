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
  
  public void keyPressed(KeyEvent keyEvent) {
    if (keyEvent.getKeyCode() == KEY_MODIFIER)
      keyModifierDown = true;
  }
  
  public void keyReleased(KeyEvent keyEvent) {
    if (keyEvent == lastProcessed)
      return;
    else
      lastProcessed = keyEvent;
    
    if (keyEvent.getKeyCode() == KEY_MODIFIER)
      keyModifierDown = false;

    if (UndoManager.ENABLE_UNDO_MANAGER && keyModifierDown) {
      if (keyEvent.getKeyCode() == KeyEvent.VK_Z)
        controller.undo();
      if (keyEvent.getKeyCode() == KeyEvent.VK_Y)
        controller.redo();
    }
  }
  
  public void keyTyped(KeyEvent keyEvent) {
  }

  public void componentAdded(ContainerEvent e) {
    listenTo(e.getComponent());
  }

  public void componentRemoved(ContainerEvent e) {
  }
}
