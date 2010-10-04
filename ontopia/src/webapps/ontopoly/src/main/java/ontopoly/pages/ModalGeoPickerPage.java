package ontopoly.pages;

import java.util.Collection;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.RequestCycle;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import ontopoly.model.PSI;
import ontopoly.model.OntopolyTopicIF;
import ontopoly.models.TopicModel;
import ontopoly.components.FieldInstanceTextField;
  
public class ModalGeoPickerPage extends Panel {
  ModalWindow dialog;
  TextField<String> locallat;
  TextField<String> locallong;
  TopicModel<OntopolyTopicIF> thetopic;
  AbstractDefaultAjaxBehavior behave;
  Label thebloodylabel;
  
  public ModalGeoPickerPage(ModalWindow dialog, OntopolyTopicIF thetopic) {
    super(dialog.getContentId());
    System.out.println("Getting started");
    this.dialog = dialog;
    this.thetopic = new TopicModel<OntopolyTopicIF>(thetopic);

    //setInitialHeight(700);

    final WebMarkupContainer popupContent = new WebMarkupContainer("popupContent");
    popupContent.setOutputMarkupId(true);
    add(popupContent);

    popupContent.add(new Label("title", new Model<String>("Pick location for " +
                                                          thetopic.getName())));

    locallat = new TextField<String>("latitude");
    locallat.add(new ExperimentalUpdating());
    locallong = new TextField<String>("longitude");
    popupContent.add(locallat);
    popupContent.add(locallong);

    Button set = new Button("setPosition");
    set.add(new SetPosition("onclick", this));
    popupContent.add(set);

    behave = new ReceiveRequest(this);
    this.add(behave);

    //thebloodylabel = new Label("ajaxurl");
  }

  // protected void onAfterRender() {
  //   // trying this here because at this point at least it's added to the page
  //   // doing it in the constructor caused errors.
  //   //    add(new Label("ajaxUrl", "var ajaxurl = '" + behave.getCallbackUrl() + "';"));
  // }
  
  protected void onSetPosition(AjaxRequestTarget target) {
    System.out.println("onSetPosition");

    String lat = locallat.getValue();
    String lng = locallong.getValue();

    // lat = RequestCycle.get().getRequest().getParameter("lat");
    // lng = RequestCycle.get().getRequest().getParameter("long");
    
    set(thetopic.getTopic().getTopicIF(), lat, PSI.ON_LATITUDE);
    set(thetopic.getTopic().getTopicIF(), lng, PSI.ON_LONGITUDE);
        
    System.out.println("about to call onCloseOk");
    onCloseOk(target);
  }        

  protected void onCloseCancel(AjaxRequestTarget target) {
    dialog.close(target);              
  }

  protected void onCloseOk(AjaxRequestTarget target) {
    dialog.close(target);
  }

  private void set(TopicIF topic, String value, LocatorIF psi) {
    System.out.println("Looking for " + psi + "; value: " + value);
    for (OccurrenceIF occ : topic.getOccurrences()) {
      TopicIF type = occ.getType();
      if (type.getSubjectIdentifiers().contains(psi)) {
        System.out.println("FOUND IT!");
        occ.setValue(value);
        return;
      }
    }

    System.out.println("Not found, creating");
    TopicMapIF tm = topic.getTopicMap();
    TopicMapBuilderIF builder = tm.getBuilder();
    TopicIF type = tm.getTopicBySubjectIdentifier(psi);
    builder.makeOccurrence(topic, type, value);
  }

  // --- Ajax request receipt

  private class ReceiveRequest extends AbstractDefaultAjaxBehavior {
    private ModalGeoPickerPage parent;

    private ReceiveRequest(ModalGeoPickerPage parent) {
      this.parent = parent;
    }
    
    protected void respond(final AjaxRequestTarget target) {
      System.out.println("RECEIVED AJAX CALL!");
      parent.onSetPosition(target);
    }
  }

  // --- Set position behavior
  
  private static class SetPosition extends AjaxFormComponentUpdatingBehavior {
    ModalGeoPickerPage parent;

    public SetPosition(String action, ModalGeoPickerPage parent) {
      super(action);
      this.parent = parent;
    }

    public void onUpdate(AjaxRequestTarget target) {
      System.out.println("onUpdate");
      parent.onSetPosition(target);
    }
  }

  // --- Experimental updating

  private static class ExperimentalUpdating extends AjaxFormComponentUpdatingBehavior {

    public ExperimentalUpdating() {
      super("onchange");
    }
    
    public void onUpdate(AjaxRequestTarget target) {
      System.out.println("FIELD VALUE CHANGED, GODDAMMIT");
    }   
  }  
}