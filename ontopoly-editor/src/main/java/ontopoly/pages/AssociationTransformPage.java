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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ontopia.topicmaps.utils.ObjectIdComparator;
import ontopoly.components.AssociationTransformerPanel;
import ontopoly.components.FunctionBoxesPanel;
import ontopoly.components.LinkFunctionBoxPanel;
import ontopoly.components.LockPanel;
import ontopoly.components.OntopolyBookmarkablePageLink;
import ontopoly.components.TitleHelpPanel;
import ontopoly.model.AssociationType;
import ontopoly.model.RoleType;
import ontopoly.model.Topic;
import ontopoly.models.AssociationTypeModel;
import ontopoly.models.HelpLinkResourceModel;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

public class AssociationTransformPage extends OntopolyAbstractPage {

  private AssociationTypeModel associationTypeModel;
  private TitleHelpPanel titlePartPanel;
    
  public AssociationTransformPage() {	  
  }
  
  public AssociationTransformPage(PageParameters parameters) {    
    super(parameters);

    String topicMapId = parameters.getString("topicMapId");
    this.associationTypeModel = new AssociationTypeModel(topicMapId, parameters.getString("topicId"));

    // Add lock panel
    LockPanel lockPanel = new LockPanel("lockPanel", associationTypeModel, isReadOnlyPage()) {
      @Override
      protected void onLockLost(AjaxRequestTarget target, Topic topic) {
        PageParameters pageParameters = new PageParameters();
        pageParameters.put("topicMapId", topic.getTopicMap().getId());
        pageParameters.put("topicId", topic.getId());
        setResponsePage(AssociationTransformPage.class, pageParameters);
      }
      @Override
      protected void onLockWon(AjaxRequestTarget target, Topic topic) {        
        PageParameters pageParameters = new PageParameters();
        pageParameters.put("topicMapId", topic.getTopicMap().getId());
        pageParameters.put("topicId", topic.getId());
        setResponsePage(AssociationTransformPage.class, pageParameters);
      }
    };
    if (lockPanel.isLockedByOther()) {
      setReadOnlyPage(true);
    }
    add(lockPanel);
    
    // Adding part containing title and help link
    createTitle();
    
    createPanel();
    
    // Function boxes
    createFunctionBoxes();

    // initialize parent components
    initParentComponents();    
  }

  private void createTitle() {
    // Adding part containing title and help link
    this.titlePartPanel = new TitleHelpPanel("titlePartPanel", 
        new PropertyModel<String>(associationTypeModel, "name"), new HelpLinkResourceModel("help.link.instancepage"));
    titlePartPanel.setOutputMarkupId(true);
    add(titlePartPanel);    
  }
  
  private void createPanel() {
    Form<Object> form = new Form<Object>("form");
    add(form);
    AssociationType associationType = getAssociationType();

    // get used role type combinations
    Collection<List<RoleType>> roleCombos = associationType.getUsedRoleTypeCombinations();

    // then remove the combination that is valid according to declaration
    List<RoleType> declaredRoleTypes = associationType.getDeclaredRoleTypes();
    Collections.sort(declaredRoleTypes, new Comparator<RoleType>() {
      @Override
      public int compare(RoleType rt1, RoleType rt2) {
        return ObjectIdComparator.INSTANCE.compare(rt1.getTopicIF(), rt2.getTopicIF());
      }      
    });
    roleCombos.remove(declaredRoleTypes);
    
    RepeatingView rview = new RepeatingView("combos");    
    Iterator<List<RoleType>> citer = roleCombos.iterator();
    while (citer.hasNext()) {
      List<RoleType> roleTypes = citer.next();
      if (roleTypes.size() != declaredRoleTypes.size()) {
        citer.remove();
        continue;
      }
      rview.add(new AssociationTransformerPanel(rview.newChildId(), associationType, roleTypes));
    }
    form.add(rview);
    
    Label message = new Label("message", new ResourceModel("transform.association.instances.none"));
    message.setVisible(roleCombos.isEmpty());
    form.add(message);
    
    Button button = new Button("button", new ResourceModel("button.ok"));
    button.setVisible(roleCombos.isEmpty());
    button.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        Topic t = getAssociationType();
        Map<String,String> pageParametersMap = new HashMap<String,String>();
        pageParametersMap.put("topicMapId", t.getTopicMap().getId());
        pageParametersMap.put("topicId", t.getId());
        pageParametersMap.put("ontology", "true");
        setResponsePage(InstancePage.class, new PageParameters(pageParametersMap));
      }          
    });
    form.add(button);    
  }
  
  private void createFunctionBoxes() {

    add(new FunctionBoxesPanel("functionBoxes") {

      @Override
      protected List<Component> getFunctionBoxesList(String id) {
        List<Component> list = new ArrayList<Component>();

        list.add(new LinkFunctionBoxPanel(id) {
          @Override
          protected Component getLabel(String id) {
            return new Label(id, new ResourceModel("edit.association.type"));
          }
          @Override
          protected Component getLink(String id) {
            Topic t = getAssociationType();
            Map<String,String> pageParametersMap = new HashMap<String,String>();
            pageParametersMap.put("topicMapId", getTopicMap().getId());
            pageParametersMap.put("topicId", t.getId());
            pageParametersMap.put("ontology", "true");
            return new OntopolyBookmarkablePageLink(id, InstancePage.class, new PageParameters(pageParametersMap), t.getName());
          }
        });
        return list;
      }      
    });
  }
  
  @Override
  protected int getMainMenuIndex() {
    return ONTOLOGY_INDEX_IN_MAINMENU; 
  }

  public AssociationType getAssociationType() {
    return getAssociationTypeModel().getAssociationType();
  }
  
  public AssociationTypeModel getAssociationTypeModel() {
    return associationTypeModel;
  }

  @Override
  public void onDetach() {
    associationTypeModel.detach();
    super.onDetach();
  }

}
