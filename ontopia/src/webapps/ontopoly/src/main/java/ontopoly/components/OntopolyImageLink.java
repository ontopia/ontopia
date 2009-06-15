package ontopoly.components;

import ontopoly.images.ImageResource;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

public abstract class OntopolyImageLink extends Panel {
  
  protected AjaxFallbackLink link;  
  protected String image;
  protected IModel titleModel;
  
  public OntopolyImageLink(String id, String image) {
    this(id, image, null);
  }
  
  public OntopolyImageLink(String id, String image, IModel titleModel) {
    super(id);
    this.image = image;
    this.titleModel = titleModel;

    this.setRenderBodyOnly(true);
    
    this.link = new AjaxFallbackLink("link") {       
      @Override
      public void onClick(AjaxRequestTarget target) {
        OntopolyImageLink.this.onClick(target);
      }       
      @Override
      protected void onComponentTag(ComponentTag tag) {
        IModel titleModel = getTitleModel();
        if (titleModel != null)
          tag.put("title", titleModel.getObject().toString());
        super.onComponentTag(tag);
      }
    };
    add(link);    
    link.add(new Image("image", new AbstractReadOnlyModel() {
      @Override
      public Object getObject() {
        return new ResourceReference(ImageResource.class, getImage());
      }      
    }));
  }
  
  public abstract void onClick(AjaxRequestTarget target);
  
  public String getImage() {
    return image;
  }

  public IModel getTitleModel() {
    return titleModel;    
  }
  
//  @Override
//  public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
//    replaceComponentTagBody(markupStream, openTag, "<img src='images/" + getImage() + "' />");
//  }

}
