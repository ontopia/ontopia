// $Id: NavigatorLink.java,v 1.5 2002/05/29 13:38:40 hca Exp $

package net.ontopia.topicmaps.nav.generic.test;

import java.net.URL;
import org.w3c.dom.Node;

import com.meterware.httpunit.WebLink;

/**
 * INTERNAL: Represents a extended WebLink object
 * which also stores if it was visited and allows
 * comparision with equals Method.
 *
 * @see net.ontopia.topicmaps.nav.generic.test.NavigatorSpy
 */
public class NavigatorLink {

    private WebLink link;
    private boolean visited;

    public NavigatorLink(WebLink link) {
        this(link, false);
    }

    public NavigatorLink(WebLink link, boolean visited) {
        this.link = link;
        this.visited = visited;
    }

    public void setLink(WebLink link) {
        this.link = link;
    }

    public WebLink getLink() {
        return link;
    }

    public String getLinkURLString() {
        return link.getURLString();
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean getVisited() {
        return visited;
    }

    public int hashCode() {
        return (link.asText() + "@@" + link.getURLString()).hashCode();
    }

    public boolean equals(Object o) {

        if (!(o instanceof NavigatorLink))
            return false;
        
        NavigatorLink cmp = (NavigatorLink) o;

        return (this.link.asText().equals(cmp.getLink().asText())
                && this.link.getURLString().equals(cmp.getLink().getURLString()));
    }
}





