package ontopoly.components;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.util.string.Strings;

public class OntopolyBookmarkablePageLink extends BookmarkablePageLink<Page> {
  private String label;

  public OntopolyBookmarkablePageLink(String id, Class<? extends Page> pageClass) {
    super(id, pageClass);
    label = null;
  }

  public OntopolyBookmarkablePageLink(String id, Class<? extends Page> pageClass,
      PageParameters parameters) {
    super(id, pageClass, parameters);
    label = null;
  }

  public OntopolyBookmarkablePageLink(String id, Class<? extends Page> pageClass, String label) {
    super(id, pageClass);
    this.label = label;
  }

  public OntopolyBookmarkablePageLink(String id, Class<? extends Page> pageClass,
      PageParameters parameters, String label) {
    super(id, pageClass, parameters);
    this.label = label;
  }

  @Override
  protected void onComponentTag(ComponentTag tag) {
    tag.setName("a");
    super.onComponentTag(tag);
  }

  @Override
  public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
    if (label != null) {
      replaceComponentTagBody(markupStream, openTag, 
          "<span>" + Strings.escapeMarkup(label) + "</span>");
    }
  }
}
