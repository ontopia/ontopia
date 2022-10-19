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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.ontopia.topicmaps.core.TopicIF;

/**
 * INTERNAL: Prompter for selecting a specific topic from a given list
 */
public class TopicSelectionPrompter extends JDialog {
  private JList jList;
  private TopicIF selectedTopic;

  public TopicSelectionPrompter(Frame aFrame, Collection list,
          Function<TopicIF, String> stringifier) {

    super(aFrame, Messages.getString("Viz.SelectInitialTopic"), true);
    buildContents(list, stringifier);
    setLocationRelativeTo(aFrame);
  }

  private void buildContents(Collection aList, Function<TopicIF, String> stringifier) {
    Vector list = new Vector(aList.size());
    Iterator iterator = aList.iterator();
    while (iterator.hasNext()) {
      TopicIF type = (TopicIF) iterator.next();
      list.add(new TopicListItem(type, stringifier));
    }

    JPanel main = new JPanel();
    main.setLayout(new BorderLayout());
    main.setBorder(BorderFactory.createTitledBorder(BorderFactory
        .createEtchedBorder(), Messages.getString("Viz.AvailableTopics")));

    jList = new JList();
    jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    jList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        jList.ensureIndexIsVisible(jList.getSelectedIndex());
      }
    });
    jList.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent anEvent) {
        if (anEvent.getClickCount() == 2)
            validateAndAccept();
      }
    });
    jList.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
          validateAndAccept();
      }
    });

    JScrollPane scroll = new JScrollPane(jList);

    setListData(list);
    main.add(scroll, BorderLayout.CENTER);

    JPanel buttons = new JPanel();
    buttons.setLayout(new GridLayout(1, 2));
    JButton ok = new JButton(Messages.getString("Viz.OK"));
    ok.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        validateAndAccept();
      }
    });

    buttons.add(ok);

    JButton cancel = new JButton(Messages.getString("Viz.Cancel"));
    cancel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        cancelSelection();
      }
    });

    buttons.add(cancel);

    main.add(buttons, BorderLayout.SOUTH);

    getContentPane().add(main);
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    pack();
  }

  protected void cancelSelection() {
    selectedTopic = null;
   hide();
  }

  protected void validateAndAccept() {
    selectedTopic = null;
    TopicListItem item = (TopicListItem) jList.getSelectedValue();

    if (item == null)
      if (warnNoSelection()) {
        hide();
      }
      else
        return;
    else {
      selectedTopic = item.getTopic();
      hide();
    }
  }

  private boolean warnNoSelection() {
    return JOptionPane.showConfirmDialog(this,
        Messages.getString("Viz.NoTopicSelected"),
        Messages.getString("Viz.TopicSelection"), JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION;
  }

  private void setListData(Vector aList) {
    TopicListItem.sort(aList);
    jList.setListData(aList);
    if (aList.size() > 0)
      jList.setSelectedIndex(0);
  }

  public TopicIF getSelection() {
    show();
    
    // There seems to be a bug in Swing which means that all
    // instances of this class are held onto as JNI Global's
    // Therefore to prevent excess memory leakage, sever the
    // link to the selectedTopic.
    
    setListData(new Vector());
    TopicIF tmp = selectedTopic;
    selectedTopic = null;
    
    return tmp;
  }
}
