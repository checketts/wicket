/*
 * $Id$ $Revision$
 * $Date$
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
package wicket.protocol.http;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.IRequestCycleFactory;
import wicket.ISessionAttributeListener;
import wicket.Request;
import wicket.RequestCycle;
import wicket.Session;
import wicket.SessionAttributeEvent;

/**
 * Session subclass for HTTP protocol which holds an HttpSession object and
 * provides access to that object via getHttpSession(). A method which abstracts
 * session invalidation is also provided via invalidate().
 * 
 * @author Jonathan Locke
 */
public class WebSession extends Session
{
	private static final long serialVersionUID = 1L;

	/** log. careful, this log is used to trigger profiling too! */
	private static Log log = LogFactory.getLog(WebSession.class);

	/** The request cycle factory for the session */
	private transient IRequestCycleFactory requestCycleFactory;

	/** cached http session object. */
	private transient HttpSession httpSession = null;

	/** The attribute in the HttpSession where this WebSession object is stored */
	private transient String sessionAttributePrefix;

	/** True, if session has been invalidated */
	private transient boolean sessionInvalidated = false;

	/**
	 * Constructor
	 * 
	 * @param application
	 *            The application
	 */
	protected WebSession(final WebApplication application)
	{
		super(application);
		// add listener that will create the actual session lazily
		add(new FirstAttributeSessionListener());
	}

	/**
	 * Listener for adding the session only when it is needed.
	 */
	private final class FirstAttributeSessionListener implements ISessionAttributeListener
	{
		/** The default serial version uid */
		private static final long serialVersionUID = 1L;

		/** we start this session clean. */
		private boolean clean = true;

		/**
		 * @see wicket.ISessionAttributeListener#attributeAdded(wicket.SessionAttributeEvent)
		 */
		public void attributeAdded(SessionAttributeEvent evt)
		{
			if (clean)
			{
				// only do this once
				clean = false;

				// create the actual httpSession (in case it didn't exist yet)
				WebSession.this.httpSession = getHttpSession(true);

				if (log.isDebugEnabled())
				{
					log.info("created http session " + WebSession.this.httpSession.getId());
				}
			}
		}

		/**
		 * @see wicket.ISessionAttributeListener#attributeRemoved(wicket.SessionAttributeEvent)
		 */
		public void attributeRemoved(SessionAttributeEvent evt)
		{
		}

		/**
		 * @see wicket.ISessionAttributeListener#attributeReplaced(wicket.SessionAttributeEvent)
		 */
		public void attributeReplaced(SessionAttributeEvent evt)
		{
		}
	}

	/**
	 * Gets the id for this web session.
	 * 
	 * @return the id for this web session or super's invocation in case the
	 *         http session was not yet defined
	 */
	public String getId()
	{
		HttpSession httpSession = getHttpSession();
		if (httpSession != null)
		{
			return httpSession.getId();
		}
		return super.getId();
	}

	/**
	 * Invalidates this session immediately. Calling this method will remove all
	 * Wicket components from this session, which means that you will no longer
	 * be able to work with them.
	 */
	public void invalidateNow()
	{
		HttpSession httpSession = getHttpSession();
		if (httpSession != null)
		{
			try
			{
				httpSession.invalidate();
			}
			catch (IllegalStateException e)
			{
				// Ignore
			}
		}
	}

	/**
	 * Invalidates this session at the end of the current request. If you need
	 * to invalidate the session immediately, you can do this by calling
	 * invalidateNow(), however this will remove all Wicket components from this
	 * session, which means that you will no longer be able to work with them.
	 */
	public void invalidate()
	{
		sessionInvalidated = true;
	}

	/**
	 * Updates the session, e.g. for replication purposes.
	 */
	protected void update()
	{
		if (sessionInvalidated == false)
		{
			super.update();
		}
	}

	/**
	 * Gets the underlying HttpSession object or null.
	 * <p>
	 * WARNING: it is a bad idea to depend on the http session object directly.
	 * Please use the classes and methods that are exposed by Wicket instead.
	 * Send an email to the mailing list in case it is not clear how to do
	 * things or you think you miss funcionality which causes you to depend on
	 * this directly.
	 * </p>
	 * 
	 * @return The underlying HttpSession object (null if not created yet).
	 */
	public final HttpSession getHttpSession()
	{
		if (httpSession == null)
		{
			// try to get it without forcing creation
			httpSession = getHttpSession(false);
		}
		return httpSession;
	}

	/**
	 * @see wicket.Session#detach()
	 */
	protected void detach()
	{
		if (sessionInvalidated)
		{
			invalidateNow();
		}
	}

	/**
	 * @see Session#getAttributeNames()
	 */
	protected List getAttributeNames()
	{
		final List list = new ArrayList();
		HttpSession httpSession = getHttpSession();
		if (httpSession != null)
		{
			final Enumeration names = httpSession.getAttributeNames();
			final String prefix = sessionAttributePrefix + "-";
			while (names.hasMoreElements())
			{
				final String name = (String)names.nextElement();
				if (name.startsWith(prefix))
				{
					list.add(name.substring(prefix.length()));
				}
			}
		}
		return list;
	}

	/**
	 * @see wicket.Session#getRequestCycleFactory()
	 */
	protected IRequestCycleFactory getRequestCycleFactory()
	{
		if (requestCycleFactory == null)
		{
			this.requestCycleFactory = ((WebApplication)getApplication())
					.getDefaultRequestCycleFactory();
		}

		return this.requestCycleFactory;
	}

	/**
	 * @see Session#doGetAttribute(String)
	 */
	protected Object doGetAttribute(final String name)
	{
		HttpSession httpSession = getHttpSession();
		if (httpSession != null)
		{
			return httpSession.getAttribute(sessionAttributePrefix + name);
		}
		return null;
	}

	/**
	 * @see Session#doSetAttribute(String, Object)
	 */
	protected void doSetAttribute(final String name, final Object object)
	{
		HttpSession httpSession = getHttpSession();
		if (httpSession != null)
		{
			httpSession.setAttribute(sessionAttributePrefix + name, object);
		}
	}

	/**
	 * @see wicket.Session#doRemoveAttribute(java.lang.String)
	 */
	protected void doRemoveAttribute(final String name)
	{
		HttpSession httpSession = getHttpSession();
		if (httpSession != null)
		{
			httpSession.removeAttribute(sessionAttributePrefix + name);
		}
	}

	/**
	 * Initializes this session for a request.
	 * 
	 * @param sessionAttributePrefix
	 *            The session attribute name
	 */
	final void init(final String sessionAttributePrefix)
	{
		if (sessionAttributePrefix == null)
		{
			throw new NullPointerException("argument sessionAttributePrefix must be not null");
		}
		// Set session attribute name
		this.sessionAttributePrefix = sessionAttributePrefix;

		// Set the current session
		set(this);

		attach();
	}

	/**
	 * Get the http session.
	 * 
	 * @param create
	 *            whether the session should be created if null
	 * 
	 * @return the http session or null (if it didn't exist and create is false)
	 */
	private final HttpSession getHttpSession(boolean create)
	{
		RequestCycle requestCycle = getRequestCycle();
		if (requestCycle != null)
		{
			Request request = requestCycle.getRequest();
			if (request instanceof WebRequest)
			{
				WebRequest webRequest = (WebRequest)request;
				if (webRequest != null)
				{
					return webRequest.getHttpServletRequest().getSession(true);
				}
			}
		}
		return null;
	}
}