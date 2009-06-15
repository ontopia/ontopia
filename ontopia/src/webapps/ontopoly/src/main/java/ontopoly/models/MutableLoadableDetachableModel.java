package ontopoly.models;

import org.apache.wicket.model.IModel;

public abstract class MutableLoadableDetachableModel implements IModel {

  /** keeps track of whether this model is attached or detached */
  private transient boolean attached = false;

  /** temporary, transient object. */
  private transient Object transientModelObject;

  public MutableLoadableDetachableModel() {
  }

  public MutableLoadableDetachableModel(Object object) {
    this.transientModelObject = object;
    attached = true;
  }

  public void detach() {
    if (attached) {
      attached = false;
      transientModelObject = null;
      onDetach();
    }
  }
  
  public Object getObject() {
    if (!attached) {
      attached = true;
      transientModelObject = load();
      onAttach();
    }
    return transientModelObject;
  }

  public void setObject(Object object) {
    this.transientModelObject = object;
    attached = true;
  }

  public final boolean isAttached() {
    return attached;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer("Model:classname=[");
    sb.append(getClass().getName()).append("]");
    sb.append(":attached=").append(attached).append(":tempModelObject=[").append(
        this.transientModelObject).append("]");
    return sb.toString();
  }

  protected abstract Object load();

  protected void onAttach() {
  }

  protected void onDetach() {
  }
  
}
