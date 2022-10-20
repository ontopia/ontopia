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

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import net.ontopia.utils.OntopiaRuntimeException;

public class UndoManager {

  private List undoStack;
  private List operationStack;
  private List redoStack;
  private List currentUndo;
  private boolean operationInProgress;
  private boolean redoInProgress;
  private VizController controller;
  
  public static final boolean ENABLE_UNDO_MANAGER = true;
  
  public UndoManager(VizController controller) {
    this.controller = controller;
    undoStack = new ArrayList();
    operationStack = new ArrayList();
    redoStack = new ArrayList();
    currentUndo = null;
    operationInProgress = false;
    redoInProgress = false;
  }
  
  public void reset() {
    undoStack = new ArrayList();
    operationStack = new ArrayList();
    redoStack = new ArrayList();
    currentUndo = null;
    operationInProgress = false;
    redoInProgress = false;
    updateUndoRedoState();
  }

  public void startOperation(RecoveryObjectIF operation) {
    if (!ENABLE_UNDO_MANAGER) {
      return;
    }
    if (operationInProgress) {
      throw new OntopiaRuntimeException("Cannot start undoable operation. " +
          "Undoable operation already in progress.");
    }
    
    // Whenever the user performs an operation other than the redo operation,
    // the stack of redoable operations is emptied, since all such operations
    // start a new branch of operations, leaving other branches inaccessible.
    if (!redoInProgress) {
      redoStack = new ArrayList();
    }
    operationStack.add(operation);
    
    currentUndo = new ArrayList();
    undoStack.add(currentUndo);
    operationInProgress = true;
  }

  public void completeOperation() {
    if (!ENABLE_UNDO_MANAGER) {
      return;
    }
    if (!operationInProgress) {
      throw new OntopiaRuntimeException("Cannot complete undoable operation. " +
          "No undoable operation is currently in progress.");
    }

    // After an operation has been completed, any recoveries are ignored.
    operationInProgress = false;
    
    updateUndoRedoState();
  }

  public void addRecovery(RecoveryObjectIF recovery) {
    if (!ENABLE_UNDO_MANAGER) {
      return;
    }
    // Only receive recoveries during 
    if (operationInProgress) {
      currentUndo.add(recovery);
    } else {
      VizDebugUtils.debug("No undoable operation in progress. Ignored: " +
                          recovery);
    }
  }
  
  public void undo() {
    if (!ENABLE_UNDO_MANAGER) {
      return;
    }
    if (!canUndo()) {
      return;
    }
    int lastIndex = undoStack.size() - 1;
    ArrayList currentUndo = (ArrayList)undoStack.remove(lastIndex);
    currentUndo = new ArrayList(currentUndo);
    Iterator currentUndoIt = currentUndo.iterator();
    while (currentUndoIt.hasNext()) {
      RecoveryObjectIF currentRecObj = (RecoveryObjectIF)currentUndoIt.next();
      currentRecObj.execute(controller.getView());
    }
    
    // Whenever an operation is undone, push that operation on the operation
    // stack so it can be redone.
    redoStack.add(operationStack.remove(lastIndex));
    
    updateUndoRedoState();
  }

  public void redo() {
    if (!ENABLE_UNDO_MANAGER) {
      return;
    }
    if (!canRedo()) {
      return;
    }
    redoInProgress = true;
    RecoveryObjectIF operation = (RecoveryObjectIF)redoStack
        .remove(redoStack.size() - 1);
    operation.execute(controller.getView());
    redoInProgress = false;
    
    updateUndoRedoState();
  }
  
  public boolean canUndo() {
    if (!ENABLE_UNDO_MANAGER) {
      return false;
    }
    return !undoStack.isEmpty();
  }
  
  public boolean canRedo() {
    if (!ENABLE_UNDO_MANAGER) {
      return false;
    }
    return !redoStack.isEmpty();
  }

  private void updateUndoRedoState() {
    if (!ENABLE_UNDO_MANAGER) {
      return;
    }
    controller.getVizPanel().setUndoEnabled(canUndo());
    controller.getVizPanel().setRedoEnabled(canRedo());
  }
}
