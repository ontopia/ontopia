package ontopoly.components;

import java.util.Collection;
import java.util.Iterator;

import ontopoly.model.OntopolyTopicIF;
import ontopoly.models.TopicModel;
import ontopoly.pages.AbstractOntopolyPage;
import ontopoly.pages.ModalConfirmPage;
import ontopoly.utils.WicketHacks;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

public abstract class DeleteTopicFunctionBoxPanel extends Panel {
  
  public DeleteTopicFunctionBoxPanel(String id) {
    super(id);
    add(new Label("title", new ResourceModel("delete.this.topic")));

    final ModalWindow deleteModal = new ModalWindow("deleteModal");
    ModalConfirmPage modalDeletePanel = new ModalConfirmPage(deleteModal.getContentId()) {
      @Override
      protected void onCloseCancel(AjaxRequestTarget target) {
        // close modal
        deleteModal.close(target);
      }
      @Override
      protected void onCloseOk(AjaxRequestTarget target) {
        // close modal
        deleteModal.close(target);
        // notify listeners
        OntopolyTopicIF instance = (OntopolyTopicIF)getTopicModel().getObject();
        onDeleteConfirmed(instance);

        // remove dependent objects
        AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
        Collection dependentObjects = instance.getDependentObjects();
//        System.out.println("RO: " + dependentObjects.size() + " " + dependentObjects);
        Iterator diter = dependentObjects.iterator();
        while (diter.hasNext()) {
          OntopolyTopicIF dtopic = (OntopolyTopicIF) diter.next();
//          System.out.println("Removing: " + dtopic.getName());
          dtopic.remove(page);
        }
        // remove topic
//        System.out.println("Removing selected: " + instance.getName());
        instance.remove(page);
      }
      @Override
      protected Component getTitleComponent(String id) {
        return new Label(id, new ResourceModel("delete.confirm"));
      }
      @Override
      protected Component getMessageComponent(String id) {
        return new Label(id, new ResourceModel("delete.message.topic"));        
      }
    };
    
    deleteModal.setContent(modalDeletePanel);
    deleteModal.setTitle(new ResourceModel("ModalWindow.title.delete.topic").getObject().toString());
    deleteModal.setCookieName("deleteModal");
    add(deleteModal);

    Button createButton = new Button("deleteButton");
    createButton.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        WicketHacks.disableWindowUnloadConfirmation(target);        
        deleteModal.show(target);
      }          
    });
    add(createButton);
  }

  public abstract TopicModel getTopicModel();
  
  public abstract void onDeleteConfirmed(OntopolyTopicIF topic);
  
}
