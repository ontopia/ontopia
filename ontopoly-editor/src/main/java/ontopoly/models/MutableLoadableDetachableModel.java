/*
 * #!
 * Ontopoly Editor
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
package ontopoly.models;

import org.apache.wicket.model.IModel;

public abstract class MutableLoadableDetachableModel<T> implements IModel<T> {

  /** keeps track of whether this model is attached or detached */
  private transient boolean attached = false;

  /** temporary, transient object. */
  private transient T transientModelObject;

  public MutableLoadableDetachableModel() {
  }

  public MutableLoadableDetachableModel(T object) {
    this.transientModelObject = object;
    attached = true;
  }

  @Override
  public void detach() {
    if (attached) {
      attached = false;
      transientModelObject = null;
    }
  }
  
  @Override
  public T getObject() {
    if (!attached) {
      attached = true;
      transientModelObject = load();
    }
    return transientModelObject;
  }

  @Override
  public void setObject(T object) {
    this.transientModelObject = object;
    attached = true;
  }

  public final boolean isAttached() {
    return attached;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Model:classname=[");
    sb.append(getClass().getName()).append("]");
    sb.append(":attached=").append(attached).append(":tempModelObject=[").append(
        this.transientModelObject).append("]");
    return sb.toString();
  }

  protected abstract T load();
  
}
