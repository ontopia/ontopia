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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicArrowButton;

/**
 * INTERNAL: Description: OSpinner is introduced to replace JSpinner so that we
 * could compile under JRE_1.3
 */

public class OSpinner extends JPanel {
  private int min = 1;
  private int max = 5;
  private int increment = 1;
  private int value = min;

  private JButton north;
  private JButton south;
  
  private Box buttonPanel = new Box(BoxLayout.Y_AXIS);
  private NumberFormat formatter = NumberFormat.getNumberInstance();
  private JTextField input = new JTextField();

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    input.setEnabled(enabled);
    north.setEnabled(enabled);
    south.setEnabled(enabled);
  }
  
  public OSpinner() {
    super();

    north = new BasicArrowButton(BasicArrowButton.NORTH);
    south = new BasicArrowButton(BasicArrowButton.SOUTH);

    this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    buttonPanel.add(north);
    buttonPanel.add(south);
    buttonPanel.validate();
    this.add(input);
    input.setColumns(2);
    input.setHorizontalAlignment(JTextField.TRAILING);
    input.setText(String.valueOf(value));
    this.add(buttonPanel);

    input.addFocusListener(new FocusAdapter() {

      @Override
      public void focusLost(FocusEvent evt) {

        setValue(input.getText());
        super.focusLost(evt);
      }
    });

    input.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == (KeyEvent.VK_UP)) {
          incrementValue();
        } else if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
          decrementValue();
        }
      }
    });

    input.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent aE) {
        setValue(input.getText());
      }
    });

    north.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        incrementValue();
      }
    });

    south.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        decrementValue();
      }
    });
    this.validate();
  }

  public void setExtents(int start, int end, int step, int show) {
    min = start;
    max = end;
    increment = step;
    this.setValue(show);
  }

  public void setMax(int max) {
    VizDebugUtils.debug("OSpinner - max: " + max);
    this.max = max;
  }

  protected void incrementValue() {
    this.setValue(value + increment);
  }

  protected void decrementValue() {
    this.setValue(value - increment);
  }

  protected boolean validateInput(int newValue) {
    return (min <= newValue) && (newValue <= max);
  }

  public void setValue(int newValue) {
    if (validateInput(newValue)) {
      int oldValue = value;
      value = newValue;
      firePropertyChange("value", oldValue, value);
    }
    input.setText(String.valueOf(value));
  }

  public int getValue() {
    return value;
  }

  public void setValue(String text) {
    int nextValue;
    try {
      nextValue = (formatter.parse(text).intValue());
    } catch (ParseException e) {
      nextValue = value;
    }
    setValue(nextValue);
  }
}
