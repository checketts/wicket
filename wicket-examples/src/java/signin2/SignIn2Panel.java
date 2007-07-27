///////////////////////////////////////////////////////////////////////////////////
//
// Created Jun 26, 2004
//
// Copyright 2004, Jonathan W. Locke
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package signin2;

import com.voicetribe.wicket.markup.html.form.CheckBox;
import com.voicetribe.wicket.markup.html.form.Form;
import com.voicetribe.wicket.markup.html.panel.SignInPanel;

/**
 * Extends standard SignInPanel with a rememberMe checkbox
 * 
 * @author Juergen Donnerstag
 */
public abstract class SignIn2Panel extends SignInPanel
{
    // rememberMe enabled or not
	private boolean rememberMe = true;
	
    /**
     * Constructor
     * @param componentName
     */
    public SignIn2Panel(String componentName)
    {
        super(componentName);
        
        // get singInForm created by base class
        Form form = (Form) get("signInForm");
        
        // add rememberMe checkbox
        form.add(new CheckBox("rememberMe", this, "rememberMe"));
        
        // set default persistence
        this.setPersistent(this.getRememberMe());
    }

    /**
     * Get model object of the rememberMe checkbox
     * @return
     */
    public boolean getRememberMe() 
    {
    	return rememberMe;
    }

    /**
     * Set model object for rememberMe checkbox
     * @param rememberMe
     */
    public void setRememberMe(boolean rememberMe) 
    {
    	this.rememberMe = rememberMe;
        this.setPersistent(rememberMe);
    }
}

///////////////////////////////// End of File /////////////////////////////////