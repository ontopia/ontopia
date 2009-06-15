package ontopoly.components;

import ontopoly.images.ImageResource;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

public class OntopolyImage extends Image {
  protected IModel titleModel;
  
  public OntopolyImage(String id, final String image, IModel titleModel) {
    super(id);
    this.titleModel = titleModel;
    
    setModel(new AbstractReadOnlyModel() {
      @Override
      public Object getObject() {
        return new ResourceReference(ImageResource.class, image);
      }      
    });
  }

  @Override
  protected void onComponentTag(ComponentTag tag) {
    tag.put("title", titleModel.getObject().toString());
    super.onComponentTag(tag);
  }

}
