
package ontopoly.components;

import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.IPageMap;
import org.apache.wicket.Page;
import org.apache.wicket.PageMap;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.collections.MiniMap;
import org.apache.wicket.util.lang.Classes;

/**
 * HACK: Had to make a copy of org.apache.wicket.markup.html.link.BookmarkablePageLink because the getPageClass() method was final.
 */
public abstract class AbstractBookmarkablePageLink extends Link {
	private static final long serialVersionUID = 1L;

	/** The page class that this link links to. */
	protected String pageClassName = null;

	/** Any page map for this link */
	private String pageMapName = null;

	/** The parameters to pass to the class constructor when instantiated. */
	protected MiniMap parameters;

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            The name of this component
	 * @param pageClass
	 *            The class of page to link to
	 */
  public AbstractBookmarkablePageLink(final String id) {
    this(id, null, null);
  }
	public AbstractBookmarkablePageLink(final String id, final Class pageClass) {
		this(id, pageClass, null);
	}

	private MiniMap pageParametersToMiniMap(PageParameters parameters)
	{
		if (parameters != null)
		{
			MiniMap map = new MiniMap(parameters, parameters.keySet().size());
			return map;
		}
		else
		{
			return null;
		}

	}

	public PageParameters getPageParameters()
	{
		PageParameters result = new PageParameters();
		if (parameters != null)
		{
			for (Iterator i = parameters.entrySet().iterator(); i.hasNext();)
			{
				Entry entry = (Entry)i.next();
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	private void setParameterImpl(String key, Object value)
	{
		PageParameters parameters = getPageParameters();
		parameters.put(key, value);
		this.parameters = pageParametersToMiniMap(parameters);
	}

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            See Component
	 * @param pageClass
	 *            The class of page to link to
	 * @param parameters
	 *            The parameters to pass to the new page when the link is clicked
	 */
	public AbstractBookmarkablePageLink(final String id, final Class pageClass,
			final PageParameters parameters)
	{
		super(id);

		this.parameters = pageParametersToMiniMap(parameters);

		if (pageClass != null && Page.class.isAssignableFrom(pageClass)) {
		  pageClassName = pageClass.getName();
		}
	}

	/**
	 * Get tge page class registered with the link
	 * 
	 * @return Page class
	 */
	public Class getPageClass()
	{
		return Classes.resolveClass(pageClassName);
	}

	/**
	 * @return Page map for this link
	 */
	public final IPageMap getPageMap()
	{
		if (pageMapName != null)
		{
			return PageMap.forName(pageMapName);
		}
		else
		{
			return getPage().getPageMap();
		}
	}

	/**
	 * Whether this link refers to the given page.
	 * 
	 * @param page
	 *            the page
	 * @see org.apache.wicket.markup.html.link.Link#linksTo(org.apache.wicket.Page)
	 */
	public boolean linksTo(final Page page)
	{
		return page.getClass() == getPageClass();
	}

	protected boolean getStatelessHint()
	{
		return true;
	}

	/**
	 * THIS METHOD IS NOT USED! Bookmarkable links do not have a click handler. It is here to
	 * satisfy the interface only, as bookmarkable links will be dispatched by the handling servlet.
	 * 
	 * @see org.apache.wicket.markup.html.link.Link#onClick()
	 */
	public final void onClick()
	{
		// Bookmarkable links do not have a click handler.
		// Instead they are dispatched by the request handling servlet.
	}

	/**
	 * @param pageMap
	 *            The pagemap for this link's destination
	 * @return This
	 */
	public final AbstractBookmarkablePageLink setPageMap(final IPageMap pageMap)
	{
		if (pageMap != null)
		{
			pageMapName = pageMap.getName();
			add(new AttributeModifier("target", true, new Model(pageMapName)));
		}
		return this;
	}

	/**
	 * Adds a given page property value to this link.
	 * 
	 * @param property
	 *            The property
	 * @param value
	 *            The value
	 * @return This
	 */
	public AbstractBookmarkablePageLink setParameter(final String property, final int value)
	{
		setParameterImpl(property, Integer.toString(value));
		return this;
	}

	/**
	 * Adds a given page property value to this link.
	 * 
	 * @param property
	 *            The property
	 * @param value
	 *            The value
	 * @return This
	 */
	public AbstractBookmarkablePageLink setParameter(final String property, final long value)
	{
		setParameterImpl(property, Long.toString(value));
		return this;
	}

	/**
	 * Adds a given page property value to this link.
	 * 
	 * @param property
	 *            The property
	 * @param value
	 *            The value
	 * @return This
	 */
	public AbstractBookmarkablePageLink setParameter(final String property, final String value)
	{
		setParameterImpl(property, value);
		return this;
	}

	/**
	 * Gets the url to use for this link.
	 * 
	 * @return The URL that this link links to
	 * @see org.apache.wicket.markup.html.link.Link#getURL()
	 */
	protected CharSequence getURL()
	{
		if (pageMapName != null && getPopupSettings() != null)
		{
			throw new IllegalStateException("You cannot specify popup settings and a page map");
		}

		PageParameters parameters = getPageParameters();

		if (getPopupSettings() != null)
		{
			return urlFor(getPopupSettings().getPageMap(this), getPageClass(), parameters);
		}
		else
		{
			return urlFor(getPageMap(), getPageClass(), parameters);
		}
	}
}
