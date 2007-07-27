/*
 * $Id$ $Revision:
 * 1.2 $ $Date$
 * 
 * ========================================================================
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
package wicket.markup.html.border;

import wicket.Component;
import wicket.MarkupContainer;
import wicket.markup.html.form.Form;


/**
 * Test the component: WicketComponentTree
 * 
 * @author Juergen Donnerstag
 */
public class FormBorder extends Border
{
	private static final long serialVersionUID = 1L;

	private Form form;

	/**
	 * 
	 * @param id
	 */
	public FormBorder(final String id)
	{
		super(id);
		
		this.form = new Form("myForm");
		add(this.form);
	}
	
	/**
	 * 
	 * @param child
	 * @return MarkupContainer
	 */
	public MarkupContainer addToForm(final Component child)
	{
		form.add(child);
		return this;
	}
}