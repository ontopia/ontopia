package ontopoly.components;

import ontopoly.images.ImageResource;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

public abstract class OntopolyImageLink extends AjaxFallbackLink<Object> {
  
  protected String image;
  protected IModel<String> titleModel;
  
  public OntopolyImageLink(String id, String image) {
    this(id, image, null);
  }
  
  public OntopolyImageLink(String id, String image, IModel<String> titleModel) {
    super(id);
    this.image = image;
    this.titleModel = titleModel;

//    this.setRenderBodyOnly(true);
    
    add(new Image("image", new AbstractReadOnlyModel<ResourceReference>() {
      @Override
      public ResourceReference getObject() {
        return new ResourceReference(ImageResource.class, getImage());
      }      
    }));
  }

  @Override
  protected void onComponentTag(ComponentTag tag) {
    IModel<String> titleModel = getTitleModel();
    if (titleModel != null)
      tag.put("title", titleModel.getObject());
    super.onComponentTag(tag);
  }
  
  public String getImage() {
    return image;
  }

  public IModel<String> getTitleModel() {
    return titleModel;    
  }

}
