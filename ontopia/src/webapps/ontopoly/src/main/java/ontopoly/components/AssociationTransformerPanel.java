package ontopoly.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.AssociationType;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.RoleType;
import ontopoly.models.AssociationTypeModel;
import ontopoly.models.CollectionModel;
import ontopoly.models.RoleTypeModel;
import ontopoly.models.TopicModel;
import ontopoly.pages.AssociationTransformPage;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;

public class AssociationTransformerPanel extends Panel {

  private String topicMapId;
  private AssociationTypeModel associationTypeModel;
  private CollectionModel roleTypesModel;
  private LoadableDetachableModel declaredRoleTypesModel;
  private List selectedModels = new ArrayList();
  
  public AssociationTransformerPanel(String id, AssociationType associationType, List roleTypes) {
    super(id);
    this.associationTypeModel = new AssociationTypeModel(associationType);
    this.topicMapId = associationType.getTopicMap().getId();
    this.setOutputMarkupId(true);
    
    // make list serializable by storing only object ids
    int size = roleTypes.size();
    List objectIds = new ArrayList(size);
    for (int i=0; i < size; i++) {
      RoleType rtype = (RoleType)roleTypes.get(i);
      objectIds.add(rtype.getId());
    }
    this.roleTypesModel = new CollectionModel(objectIds) {
      @Override
      protected Object getObjectFor(Object object) {
        String topicId = (String)object;
        return new RoleTypeModel(topicMapId, topicId);
      }      
    };
    
    this.declaredRoleTypesModel = new LoadableDetachableModel() {
      @Override
      public Object load() {
        return associationTypeModel.getAssociationType().getDeclaredRoleTypes();
      }
    };

//    WebMarkupContainer acontainer = new WebMarkupContainer("associationtype");
//    acontainer.add(new Label("label", "Association type:"));
//    acontainer.add(new Label("oldvalue", associationType.getName()));
//    acontainer.add(new Label("newvalue", ""));
//    add(acontainer);
//    acontainer.setVisible(false);
    
    RepeatingView rview = new RepeatingView("roletype");    
    Iterator riter = roleTypes.iterator();
    while (riter.hasNext()) {
      RoleType roleType = (RoleType)riter.next();
      WebMarkupContainer rcontainer = new WebMarkupContainer(rview.newChildId());
//      rcontainer.add(new Label("label", "Role type:"));
      rcontainer.add(new Label("oldvalue", roleType.getName()));
      
      IModel selectedModel = new TopicModel(null, TopicModel.TYPE_ROLE_TYPE);
      selectedModels.add(selectedModel);

      TopicDropDownChoice choice = new TopicDropDownChoice("newvalue", selectedModel, declaredRoleTypesModel);
      choice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
        @Override
        protected void onUpdate(AjaxRequestTarget target) {
//          target.addComponent(AssociationTransformerPanel.this);
        }
      });
      rcontainer.add(choice);
      rview.add(rcontainer);
    }
    add(rview);    
    
    Button transformButton = new Button("button", new ResourceModel("button.association.transform"));
    transformButton.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
//        System.out.println("Button clicked");
        boolean validCombination = true;
        List declaredRoleTypes = (List)declaredRoleTypesModel.getObject();
        List unusedRoleTypes = new ArrayList(declaredRoleTypes); // make copy
        int size = selectedModels.size();
        for (int i=0; i < size; i++) {
          IModel model = (IModel)selectedModels.get(i);
          RoleType selected = (RoleType)model.getObject();
//          System.out.println("S: " + selected);
          if (selected == null) { 
            validCombination = false;
            break;
          } else {
            unusedRoleTypes.remove(selected);
          }
        }
        if (validCombination && unusedRoleTypes.isEmpty()) {
          List roleTypesModels = (List)roleTypesModel.getObject();
          List roleTypesFrom = new ArrayList(size);
          List roleTypesTo = new ArrayList(size);
          for (int i=0; i < size; i++) {
            roleTypesFrom.add(((RoleTypeModel)roleTypesModels.get(i)).getRoleType());
            roleTypesTo.add(((TopicModel)selectedModels.get(i)).getTopic());
          }
//          System.out.println("FROM: " + roleTypesFrom);
//          System.out.println("TO: " + roleTypesTo);
          AssociationType at = associationTypeModel.getAssociationType();
          at.transformInstances(roleTypesFrom, roleTypesTo);
          
          // redirect to same page          
          Map pageParametersMap = new HashMap();
          pageParametersMap.put("topicMapId", at.getTopicMap().getId());
          pageParametersMap.put("topicId", at.getId());
          setResponsePage(AssociationTransformPage.class, new PageParameters(pageParametersMap));
          setRedirect(true);
        }
      }          
    });
    add(transformButton);
    
  }

  @Override
  public void onDetach() {
    associationTypeModel.detach();
    roleTypesModel.detach();
    declaredRoleTypesModel.detach();
    super.onDetach();
  }
  
}
