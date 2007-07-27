/*
 * $Id: AutoLinkResolver.java,v 1.2 2005/02/10 18:01:32 jonathanlocke
 * Exp $ $Revision$ $Date$
 *
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.markup.ComponentTag;
import wicket.markup.MarkupStream;
import wicket.markup.html.BodyOnLoadContainer;
import wicket.markup.html.HtmlHeaderContainer;
import wicket.markup.html.link.BookmarkablePageLink;
import wicket.markup.html.link.ExternalLink;
import wicket.util.string.Strings;
import wicket.util.value.ValueMap;

/**
 * The AutoLinkResolver is responsible to handle automatic link
 * resolution. Autolink components are automatically created by MarkupParser for
 * anchor tags with no explicit wicket component. E.g. &lt;a
 * href="Home.html"&gt;
 * <p>
 * For each such tag a BookmarkablePageLink will automatically be created, with
 * one exception. An ExternalLink is created if the URL is absolute (starts
 * with "/") and does not reference a valid Page class.
 * <p>
 * It resolves the given URL by searching for a page class, either relative or
 * absolute, specified by the href attribute of the tag. If relative the href
 * URL must be relative to the package containing the associated page. An
 * exception is thrown if no Page class was found.
 * <p>
 *
 * @see wicket.markup.parser.filter.WicketLinkTagHandler
 * @author Juergen Donnerstag
 */
public final class AutoLinkResolver implements IComponentResolver
{
	/** Logging */
	private static Log log = LogFactory.getLog(AutoLinkResolver.class);

	/**
	 * Automatically creates a BookmarkablePageLink component.
	 *
	 * @see wicket.IComponentResolver#resolve(MarkupContainer,
	 *      MarkupStream, ComponentTag)
	 * @param markupStream
	 *            The current markupStream
	 * @param tag
	 *            The current component tag while parsing the markup
	 * @param container
	 *            The container parsing its markup
	 * @return true, if componentId was handle by the resolver. False,
	 *         otherwise
	 */
	public final boolean resolve(final MarkupContainer container, final MarkupStream markupStream,
			final ComponentTag tag)
	{
		// Must be marked as autolink tag
		if (tag.isAutolinkEnabled())
		{
			// Try to find the Page matching the href
			final Component link = resolveAutomaticLink(container, tag.getId(), tag);

			// Add the link to the container
			container.autoAdd(link);
			if (log.isDebugEnabled())
			{
				log.debug("Added autolink " + link);
			}

			// Tell the container, we resolved the id
			return true;
		}

		// We were not able to resolve the id
		return false;
	}

	/**
	 * Resolves the given tag's page class and page parameters by parsing the
	 * tag component name and then searching for a page class at the absolute or
	 * relative URL specified by the href attribute of the tag.
	 *
	 * @param container
	 *            The container where the link is
	 * @param id
	 *            the name of the component
	 * @param tag
	 *            the component tag
	 * @return A BookmarkablePageLink to handle the href
	 */
	private final Component resolveAutomaticLink(final MarkupContainer container,
			final String id, final ComponentTag tag)
	{
	    // Init
		final Page page = container.getPage();
		final String originalHref = tag.getAttributes().getString("href");
		
		PageParameters pageParameters = null;
		String classPath = originalHref;
		
		// get query string 
		int pos = originalHref.indexOf("?");
		if (pos != -1)
		{
			final String queryString = originalHref.substring(pos + 1);
			pageParameters = new PageParameters(new ValueMap(queryString, "&"));
			classPath = originalHref.substring(0, pos);
		}

		// Make the id (page-)unique
		final String autoId = id + Integer.toString(page.getAutoIndex());

		// By setting the component name, the tag becomes a Wicket component 
		// tag, which needs a Component attached to it.
		tag.setId(autoId);

		// remove the file extension, but remember it
		String extension = null;
		pos = classPath.lastIndexOf(".");
		if (pos != -1)
		{
		    extension = classPath.substring(pos + 1);
		    classPath = classPath.substring(0, pos);
		}

		// HTML hrefs are validated differently
		if ("html".equalsIgnoreCase(extension))
        {
			// Obviously a href like href="myPkg.MyLabel.html" will do as well.
			// Wicket will not throw an exception. It accepts it.
			classPath = Strings.replaceAll(classPath,"/", ".");
			
		    if (!classPath.startsWith("."))
			{
				// Href is relative. Resolve the url given relative to the current
				// page
				final String className = page.getClass().getPackage().getName() + "." + classPath;
				final Class clazz = page.getApplicationSettings().getDefaultClassResolver()
						.resolveClass(className);
	
				return new AutolinkBookmarkablePageLink(autoId, clazz, pageParameters);
			}
			else
			{
				// href is absolute. If class with the same absolute path exists,
				// use it. Else don't change the href.
				final String className = classPath.substring(1);
				try
				{
					final Class clazz = page.getApplicationSettings().getDefaultClassResolver()
							.resolveClass(className);
	
					return new AutolinkBookmarkablePageLink(autoId, clazz, pageParameters);
				}
				catch (WicketRuntimeException ex)
				{
					; // fall through
				}
			}
        }
		// Validate all other hrefs
		else
		{
		    if (classPath.startsWith("/") || classPath.startsWith("\\"))
			{
				// href is absolute. Don't change it at all.
			}
		    else
		    {
				// Href is relative, prepend the package name. Thus we keep
		        // pre-view capabilities in Dreamweaver etc. and creat a proper
		        // href at runtime.
		        
		        // <wicket:head> component are handled differently. We can not use
		        // the container, because it is the container the header has been
		        // added to (e.g. the Page). What we need however, is the component
		        // (e.g. a Panel) which contributed it.
		        Component relevantContainer = container;
		        if (container instanceof HtmlHeaderContainer)
		        {
		            relevantContainer = findPanelComponent(container);
		        }

		        if (container instanceof BodyOnLoadContainer)
		        {
		            relevantContainer = container.getParent();
		        }
		        
		        // Create the runtime href which has the proper package name
		        // prepended.
		        String href = relevantContainer.getClass().getPackage().getName();
		        href = Strings.replaceAll(href, ".", "/") + "/" + originalHref;
		        
		        // Create the component implementing the link
				return new AutolinkExternalLink(autoId, href);
			}
		}

		// Don't change the href. Did not find a proper Wicket page
		return new AutolinkExternalLink(autoId, originalHref);
	}

	/**
	 * 
	 * @param container
	 * @return the Component found
	 */
	private Component findPanelComponent(MarkupContainer container)
	{
        final Class panelClass = container.getMarkupStream().getContainerClass(); 
        
        while (container instanceof HtmlHeaderContainer)
        {
            container = container.getParent();
        }
        
        Iterator iter = container.iterator();
        while (iter.hasNext())
        {
            final Component panelComponent = (Component) iter.next();
            if (panelClass.isInstance(panelComponent))
            {
                return panelComponent;
            }
        }
        
        throw new WicketRuntimeException("programming error: did not find panel or border component with class name: " 
                + panelClass);
	}
	
	/**
	 * Autolink components delegate component resolution to their parent components.
	 * Reason: autolink tags don't have wicket:id and users wouldn't know where to
	 * add the component to.
	 *
	 * @author Juergen Donnerstag
	 */
	public static final class AutolinkBookmarkablePageLink extends BookmarkablePageLink implements IComponentResolver
	{
	    /**
	     * Construct
	     * @see BookmarkablePageLink#BookmarkablePageLink(String, Class, PageParameters)
	     *
	     * @param id
	     * @param pageClass
	     * @param parameters
	     */
	    public AutolinkBookmarkablePageLink(final String id,
	            final Class pageClass, final PageParameters parameters)
	    {
	        super(id, pageClass, parameters);
	    }

		/**
		 * Because the autolink component is not able to resolve any inner
		 * component, it'll passed it down to its parent.
		 *
		 * @param container
		 *            The container parsing its markup
		 * @param markupStream
		 *            The current markupStream
		 * @param tag
		 *            The current component tag while parsing the markup
		 * @return True if componentId was handled by the resolver, false otherwise.
		 */
		public final boolean resolve(final MarkupContainer container,
		        final MarkupStream markupStream, final ComponentTag tag)
		{
		    // Delegate the request to the parent component
		    final Component component = this.getParent().get(tag.getId());
		    if (component == null)
		    {
		        return false;
		    }

		    component.render();
		    return true;
		}
	}

	/**
	 * Autolink component delegate component resolution to their parent components.
	 * Reason: autolink tags don't have wicket:id and users wouldn't know where to
	 * add the component to.
	 *
	 * @author Juergen Donnerstag
	 */
	public static final class AutolinkExternalLink extends ExternalLink implements IComponentResolver
	{
	    /**
	     * Construct
	     * @see ExternalLink#ExternalLink(String, String)
	     *
	     * @param id
	     * @param href
	     */
	    public AutolinkExternalLink(final String id, final String href)
	    {
	        super(id, href);
	    }

		/**
		 * Because the autolink component is not able to resolve any inner
		 * component, it'll passed it down to its parent.
		 *
		 * @param container
		 *            The container parsing its markup
		 * @param markupStream
		 *            The current markupStream
		 * @param tag
		 *            The current component tag while parsing the markup
		 * @return True if componentId was handled by the resolver, false otherwise.
		 */
		public final boolean resolve(final MarkupContainer container,
		        final MarkupStream markupStream, final ComponentTag tag)
		{
		    // Delegate the request to the parent component
		    final Component component = this.getParent().get(tag.getId());
		    if (component == null)
		    {
		        return false;
		    }

		    component.render();
		    return true;
		}
	}
}