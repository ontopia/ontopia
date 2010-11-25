package ontopoly.components;

import ontopoly.images.ImageResource;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;

public class OntopolyImage extends Image {
  protected IModel<String> titleModel;
  
  public OntopolyImage(String id, final String image) {
    this(id, image, null);
  }
  
  public OntopolyImage(String id, final String image, IModel<String> titleModel) {
    super(id, new ResourceReference(ImageResource.class, image));
    this.titleModel = titleModel;
  }

  @Override
  protected void onComponentTag(ComponentTag tag) {
    if (titleModel != null) {
      if (titleModel.getObject() != null) {
        tag.put("title", titleModel.getObject());
      }
    }
    super.onComponentTag(tag);
  }

}
