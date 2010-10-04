package ontopoly.pages;

import net.ontopia.utils.ObjectUtils;
import ontopoly.components.InstancePanel;
import ontopoly.model.FieldsViewIF;
import ontopoly.model.OntopolyTopicIF;
import ontopoly.model.TopicTypeIF;
import ontopoly.models.FieldsViewModel;
import ontopoly.models.TopicModel;
import ontopoly.models.TopicTypeModel;
import ontopoly.utils.NoSuchTopicException;
import ontopoly.utils.OntopolyUtils;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;

public class EmbeddedInstancePage extends AbstractProtectedOntopolyPage { 
  protected TopicModel<OntopolyTopicIF> topicModel;
  protected TopicTypeModel topicTypeModel;
  protected FieldsViewModel fieldsViewModel;

  protected WebMarkupContainer instanceContainer;
  
  public EmbeddedInstancePage() {	  
  }
  
  public EmbeddedInstancePage(PageParameters parameters) {
    super(parameters);
	  
    String topicMapId = parameters.getString("topicMapId");
    String topicId = parameters.getString("topicId");

    this.topicModel = new TopicModel<OntopolyTopicIF>(topicMapId, topicId);
    OntopolyTopicIF topic = topicModel.getTopic();
    if (topic == null)
      throw new NoSuchTopicException("No topic with id " + topicId + " found.");

    // if "topicType" parameter is specified, pull out most specific direct type    
    TopicTypeIF tt = null;
    String topicTypeId = parameters.getString("topicTypeId");
    if (topicTypeId != null)
      tt = topic.getMostSpecificTopicType(new TopicTypeModel(topicMapId, topicTypeId).getTopicType());
    
    // if not topic type found, use first available direct type
    if (tt == null)
      tt = OntopolyUtils.getDefaultTopicType(topic);
    this.topicTypeModel = new TopicTypeModel(tt);

    String viewId = parameters.getString("viewId");
    if (viewId != null)
      this.fieldsViewModel = new FieldsViewModel(topicMapId, viewId);
    else
      this.fieldsViewModel = new FieldsViewModel(topic.getTopicMap().getDefaultFieldsView());
    
    // page is read-only if topic type is read-only
    
    setReadOnlyPage(tt.isReadOnly() || ObjectUtils.equals(getRequest().getParameter("ro"), "true") || !((AbstractOntopolyPage)this).filterTopic(topic));

    Form form = new Form("form");
    add(form);

    this.instanceContainer = new WebMarkupContainer("instanceContainer");
    instanceContainer.setOutputMarkupId(true);
    form.add(instanceContainer);

    instanceContainer.add(createInstancePanel("instancePanel"));
  }

  protected InstancePanel createInstancePanel(final String id) {
    return new InstancePanel(id, topicModel, topicTypeModel, fieldsViewModel, isReadOnlyPage(), isTraversable()) {
      @Override
      protected void onLockLost(AjaxRequestTarget target, OntopolyTopicIF topic) {
        instanceContainer.replace(createInstancePanel(id));
        target.addComponent(instanceContainer);        
      }      
      @Override
      protected void onLockWon(AjaxRequestTarget target, OntopolyTopicIF topic) {
        instanceContainer.replace(createInstancePanel(id));
        target.addComponent(instanceContainer);        
      }      
    };
  }
 
  protected OntopolyTopicIF getTopic() {
    return topicModel.getTopic();
  }
  
  protected TopicTypeIF getTopicType() {
    return topicTypeModel.getTopicType();
  }
  
  protected FieldsViewIF getFieldsView() {
    return fieldsViewModel.getFieldsView();
  }
  
  protected boolean isTraversable() {
    return false; // NOTE: hardcoded
  }
  
  @Override  
  public PageParameters getPageParameters(OntopolyTopicIF topic) {
    PageParameters params = new PageParameters();            
    params.put("topicMapId", topic.getTopicMap().getId());
    params.put("topicId", topic.getId());            
    //! params.put("topicTypeId", getTopicType().getId());
    
    FieldsViewIF fieldsView = getFieldsView();
    if (!fieldsView.isDefaultView())
      params.put("viewId", fieldsView.getId());
    
    PageParameters thisParams = getPageParameters();
    // forward buttons parameter
    if (thisParams.getString("buttons") != null)
      params.put("buttons", "true");

    return params;
  }

  @Override
  public void onDetach() {
    topicModel.detach();
    topicTypeModel.detach();
    fieldsViewModel.detach();
    super.onDetach();
  }
  
}
