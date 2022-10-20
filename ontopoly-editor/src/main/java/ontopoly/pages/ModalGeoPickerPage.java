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
package ontopoly.pages;

import java.util.Collection;
import java.util.Iterator;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import ontopoly.components.FieldInstanceOccurrencePanel;
import ontopoly.components.FieldInstancesPanel;
import ontopoly.model.FieldAssignment;
import ontopoly.model.FieldDefinition;
import ontopoly.model.FieldInstance;
import ontopoly.model.OccurrenceField;
import ontopoly.model.OccurrenceType;
import ontopoly.model.PSI;
import ontopoly.model.Topic;
import ontopoly.models.FieldInstanceModel;
import ontopoly.models.TopicModel;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
  
public class ModalGeoPickerPage extends Panel {
  private ModalWindow dialog;
  private TopicModel<Topic> thetopic;
  private AbstractDefaultAjaxBehavior behave;
  private Label ajaxurllabel;
  private Model<String> ajaxurlmodel;

  private FieldInstanceOccurrencePanel latpan;
  private FieldInstanceOccurrencePanel lngpan;
  
  public ModalGeoPickerPage(ModalWindow dialog, Topic thetopic) {
    super(dialog.getContentId());
    this.dialog = dialog;
    this.thetopic = new TopicModel<Topic>(thetopic);

    //setInitialHeight(700);

    final WebMarkupContainer popupContent = new WebMarkupContainer("popupContent");
    popupContent.setOutputMarkupId(true);
    add(popupContent);

    popupContent.add(new Label("title", new Model<String>("Pick location for '"+
                                                          thetopic.getName() +
                                                          "'")));

    behave = new ReceiveRequest(this);
    this.add(behave);

    // using a label to provide the callback URI to the JavaScript code
    // in the page. unfortunately, we don't know the URI here, so we set
    // things up, then insert it later.
    ajaxurlmodel = new Model<String>("// and the url is ...");
    ajaxurllabel = new Label("ajaxurl", ajaxurlmodel);
    ajaxurllabel.setEscapeModelStrings(false);
    popupContent.add(ajaxurllabel);
  }

  @Override
  protected void onBeforeRender() {
    super.onBeforeRender();

    String lat = get(thetopic.getTopic().getTopicIF(), PSI.ON_LATITUDE);
    if (lat == null) {
      lat = "59.92";
    }
    String lng = get(thetopic.getTopic().getTopicIF(), PSI.ON_LONGITUDE);
    if (lng == null) {
      lng = "10.74";
    }
    
    // we can't call getCallbackUrl() in the constructor, but here it's
    // possible, so we insert the URI into the label we created above,
    // and everything is hunky dory.
    String js = "var ajaxurl = '" + behave.getCallbackUrl() + "';\n";
    js += "var latitude = " + lat + ";\n";
    js += "var longitude = " + lng + ";\n";

    ajaxurllabel.modelChanging();
    ajaxurlmodel.setObject(js);
    ajaxurllabel.modelChanged();
    
  }
  
  protected void onSetPosition(AjaxRequestTarget target) {
    String lat = RequestCycle.get().getRequest().getParameter("lat");
    String lng = RequestCycle.get().getRequest().getParameter("long");

    findFields();
    
    set(thetopic.getTopic().getTopicIF(), lat, PSI.ON_LATITUDE);
    set(thetopic.getTopic().getTopicIF(), lng, PSI.ON_LONGITUDE);

    latpan.getFieldValuesModel().setShowExtraField(false, false);
    latpan.onUpdate(target);
    lngpan.getFieldValuesModel().setShowExtraField(false, false);
    lngpan.onUpdate(target);
    
    onCloseOk(target);
  }        

  protected void onCloseCancel(AjaxRequestTarget target) {
    dialog.close(target);              
  }

  protected void onCloseOk(AjaxRequestTarget target) {
    dialog.close(target);
  }

  private void findFields() {
    MarkupContainer container = this;
    while (!(container instanceof FieldInstancesPanel)) {
      container = container.getParent();
    }

    FieldInstancesPanel parent = (FieldInstancesPanel) container;
    ListView<FieldInstanceModel> listView = parent.getFieldList();
    Iterator<? extends ListItem<FieldInstanceModel>> itfim = listView.iterator();
    while (itfim.hasNext()) {
      ListItem<FieldInstanceModel> li = itfim.next();
      FieldInstance fi = li.getModelObject().getFieldInstance();
      FieldAssignment fa = fi.getFieldAssignment();
      FieldDefinition fd = fa.getFieldDefinition();
      if (fd.getFieldType() != FieldDefinition.FIELD_TYPE_OCCURRENCE) {
        continue;
      }
      OccurrenceField of = (OccurrenceField)fd;
      OccurrenceType ot = of.getOccurrenceType();
      if (ot == null) {
        continue;
      }
      Collection<LocatorIF> psis = ot.getTopicIF().getSubjectIdentifiers();

      if (psis.contains(PSI.ON_LATITUDE) ||
          psis.contains(PSI.ON_LONGITUDE)) {
        Iterator<? extends Component> it = li.iterator();
        while (it.hasNext()) {
          Object component = it.next();
          if (component instanceof FieldInstanceOccurrencePanel) {
            FieldInstanceOccurrencePanel fiop = (FieldInstanceOccurrencePanel) component;
            if (psis.contains(PSI.ON_LONGITUDE)) {
              lngpan = fiop;
            } else {
              latpan = fiop;
            }
          }
        }
      }
    }
  }
  
  private void set(TopicIF topic, String value, LocatorIF psi) {
    for (OccurrenceIF occ : topic.getOccurrences()) {
      TopicIF type = occ.getType();
      if (type.getSubjectIdentifiers().contains(psi)) {
        occ.setValue(value);
        return;
      }
    }

    TopicMapIF tm = topic.getTopicMap();
    TopicMapBuilderIF builder = tm.getBuilder();
    TopicIF type = tm.getTopicBySubjectIdentifier(psi);
    builder.makeOccurrence(topic, type, value);
  }

  private String get(TopicIF topic, LocatorIF psi) {
    for (OccurrenceIF occ : topic.getOccurrences()) {
      TopicIF type = occ.getType();
      if (type.getSubjectIdentifiers().contains(psi)) {
        return occ.getValue();
      }
    }

    return null;
  }
  
  // --- Ajax request receipt

  public class ReceiveRequest extends AbstractDefaultAjaxBehavior {
    private ModalGeoPickerPage parent;

    public ReceiveRequest(ModalGeoPickerPage parent) {
      this.parent = parent;
    }
    
    @Override
    protected void respond(final AjaxRequestTarget target) {
      parent.onSetPosition(target);
    }
  } 
}