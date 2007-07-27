/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package signin2;

import com.voicetribe.wicket.PageParameters;
import com.voicetribe.wicket.RequestCycle;

/**
 * Simple home page.
 * @author Jonathan Locke
 */
public class Logout extends AuthenticatedHtmlPage
{
    /**
     * Constructor
     * @param parameters Page parameters (ignored since this is the home page)
     */
    public Logout(final PageParameters parameters)
    {
    }
    
	/**
	 * @see com.voicetribe.wicket.Page#checkAccess(com.voicetribe.wicket.RequestCycle)
	 */
	protected boolean checkAccess(RequestCycle cycle) 
	{
	    SignIn2.logout(cycle);
		return true;
	}
}

///////////////////////////////// End of File /////////////////////////////////