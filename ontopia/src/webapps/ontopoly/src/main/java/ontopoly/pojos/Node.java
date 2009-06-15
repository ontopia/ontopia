package ontopoly.pojos;

import java.io.Serializable;

import org.apache.wicket.PageParameters;

public class Node implements Serializable {
  
  private String name;
  private Class pageClass;
  private PageParameters pageParameters;

  /**
   * Used for serialization only.
   */
  public Node() {
  }
  
  public Node(String name, Class pageClass, PageParameters pageParameters) {
    this.name = name;
    this.pageClass = pageClass;
    this.pageParameters = pageParameters;
  }

  public String getName() {
    return name;
  }

  public Class getPageClass() {
    return pageClass;
  }
  
  public PageParameters getPageParameters() {
    return pageParameters;
  }

}
