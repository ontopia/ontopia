package ontopoly.pages;

import net.ontopia.utils.ObjectUtils;
import ontopoly.components.InstancePanel;
import ontopoly.model.FieldsView;
import ontopoly.model.Topic;
import ontopoly.model.TopicType;
import ontopoly.models.FieldsViewModel;
import ontopoly.models.TopicModel;
import ontopoly.models.TopicTypeModel;
import ontopoly.utils.OntopolyUtils;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;

public class EmbeddedInstancePage extends AbstractProtectedOntopolyPage {
  
  protected TopicModel<Topic> topicModel;
  protected TopicTypeModel topicTypeModel;
  protected FieldsViewModel fieldsViewModel;

  protected WebMarkupContainer instanceContainer;
  
  public EmbeddedInstancePage() {	  
  }
  
  public EmbeddedInstancePage(PageParameters parameters) {
    super(parameters);
	  
    String topicMapId = parameters.getString("topicMapId");
    this.topicModel = new TopicModel<Topic>(topicMapId, parameters.getString("topicId"));
    Topic topic = topicModel.getTopic();
    
    // if "topicType" parameter is specified, pull out most specific direct type    
    TopicType tt = null;
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
      this.fieldsViewModel = new FieldsViewModel(FieldsView.getDefaultFieldsView(topic.getTopicMap()));
    
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
      protected void onLockLost(AjaxRequestTarget target, Topic topic) {
        instanceContainer.replace(createInstancePanel(id));
        target.addComponent(instanceContainer);        
      }      
      @Override
      protected void onLockWon(AjaxRequestTarget target, Topic topic) {
        instanceContainer.replace(createInstancePanel(id));
        target.addComponent(instanceContainer);        
      }      
    };
  }
 
  protected Topic getTopic() {
    return topicModel.getTopic();
  }
  
  protected TopicType getTopicType() {
    return topicTypeModel.getTopicType();
  }
  
  protected FieldsView getFieldsView() {
    return fieldsViewModel.getFieldsView();
  }
  
  protected boolean isTraversable() {
    return false; // NOTE: hardcoded
  }
  
  @Override  
  public PageParameters getPageParameters(Topic topic) {
    PageParameters params = new PageParameters();            
    params.put("topicMapId", topic.getTopicMap().getId());
    params.put("topicId", topic.getId());            
    //! params.put("topicTypeId", getTopicType().getId());
    
    FieldsView fieldsView = getFieldsView();
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
