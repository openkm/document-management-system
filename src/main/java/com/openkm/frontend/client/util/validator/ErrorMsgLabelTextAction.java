/*
	Copyright 2009 Anatol Gregory Mayen
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License. 
	You may obtain a copy of the License at 
	
	http://www.apache.org/licenses/LICENSE-2.0 
	
	Unless required by applicable law or agreed to in writing, software 
	distributed under the License is distributed on an "AS IS" BASIS, 
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
	See the License for the specific language governing permissions and 
	limitations under the License. 
*/
package com.openkm.frontend.client.util.validator;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.UIObject;
import eu.maydu.gwt.validation.client.ValidationAction;
import eu.maydu.gwt.validation.client.ValidationResult;


/**
 * Action that sets the specified error message on the specified
 * label.
 *
 * @author jllort
 */
public class ErrorMsgLabelTextAction extends ValidationAction<Object> {

	private Label errorLabel;

	public ErrorMsgLabelTextAction(Label errorLabel) {
		errorLabel.setVisible(false);
		this.errorLabel = errorLabel;
	}

	@Override
	public void invoke(ValidationResult result, Object notUsed) {
		if (result == null)
			return;

		errorLabel.setVisible(true);

	}

	@Override
	public void reset(UIObject obj) {
		reset();
	}

	@Override
	public void reset() {
		errorLabel.setVisible(false);
	}

}