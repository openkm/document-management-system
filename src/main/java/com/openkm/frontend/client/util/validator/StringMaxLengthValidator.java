/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017  Paco Avila & Josep Llort
 * <p>
 * No bytes were intentionally harmed during the development of this application.
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.frontend.client.util.validator;

import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import eu.maydu.gwt.validation.client.ValidationAction;
import eu.maydu.gwt.validation.client.ValidationResult;
import eu.maydu.gwt.validation.client.ValidationResult.ValidationError;
import eu.maydu.gwt.validation.client.Validator;
import eu.maydu.gwt.validation.client.i18n.StandardValidationMessages;
import eu.maydu.gwt.validation.client.i18n.ValidationMessages;

/**
 * StringMaxLengthValidator
 *
 * @author jllort
 *
 */
public class StringMaxLengthValidator extends Validator<StringMaxLengthValidator> {

	protected TextBoxBase text;
	protected SuggestBox suggest;
	protected int max;

	public StringMaxLengthValidator(TextBoxBase text) {
		this(text, false);
	}

	public StringMaxLengthValidator(TextBoxBase text, int max) {
		this(text, max, false);
	}


	public StringMaxLengthValidator(TextBoxBase text, boolean preventsPropagationOfValidationChain) {
		this(text, preventsPropagationOfValidationChain, null);
	}

	public StringMaxLengthValidator(TextBoxBase text, boolean preventsPropagationOfValidationChain, String customMsgKey) {
		super();
		this.setPreventsPropagationOfValidationChain(preventsPropagationOfValidationChain);
		if (text == null)
			throw new IllegalArgumentException("text must not be null");
		this.text = text;
		this.setCustomMsgKey(customMsgKey);
	}

	public StringMaxLengthValidator(TextBoxBase text, int max, boolean preventsPropagationOfValidationChain) {
		this(text, max, preventsPropagationOfValidationChain, null);
	}

	public StringMaxLengthValidator(TextBoxBase text, int max, boolean preventsPropagationOfValidationChain, String customMsgKey) {
		super();
		this.setPreventsPropagationOfValidationChain(preventsPropagationOfValidationChain);
		if (text == null)
			throw new IllegalArgumentException("text must not be null");
		this.text = text;
		setMax(max);
		this.setCustomMsgKey(customMsgKey);
	}

	public StringMaxLengthValidator(SuggestBox suggest) {
		this(suggest, false);
	}

	public StringMaxLengthValidator(SuggestBox suggest, boolean preventsPropagationOfValidationChain) {
		this(suggest, preventsPropagationOfValidationChain, null);
	}

	public StringMaxLengthValidator(SuggestBox suggest, boolean preventsPropagationOfValidationChain, String customMsgKey) {
		super();
		this.setPreventsPropagationOfValidationChain(preventsPropagationOfValidationChain);
		if (suggest == null)
			throw new IllegalArgumentException("suggest must not be null");
		this.suggest = suggest;
		this.setCustomMsgKey(customMsgKey);
	}

	public StringMaxLengthValidator(SuggestBox suggest, int min, int max, boolean preventsPropagationOfValidationChain) {
		this(suggest, min, max, preventsPropagationOfValidationChain, null);
	}

	public StringMaxLengthValidator(SuggestBox suggest, int min, int max, boolean preventsPropagationOfValidationChain, String customMsgKey) {
		super();
		this.setPreventsPropagationOfValidationChain(preventsPropagationOfValidationChain);
		if (suggest == null)
			throw new IllegalArgumentException("suggest must not be null");
		this.suggest = suggest;
		setMax(max);
		this.setCustomMsgKey(customMsgKey);
	}

	@Override
	public ValidationResult validate(ValidationMessages allMessages) {
		StandardValidationMessages messages = allMessages.getStandardMessages();
		String str;
		if (text != null)
			str = text.getText();
		else str = suggest.getText();
		if (str == null)
			str = "";
		if (str.length() > this.max) {
			ValidationResult result = new ValidationResult();
			ValidationError error = result.new ValidationError(null, getErrorMessage(allMessages, messages.validator_max()));
			result.getErrors().add(error);
			return result;
		}

		return null;
	}

	public void invokeActions(ValidationResult result) {
		if (text != null) {
			for (ValidationAction<TextBoxBase> action : getFailureActions())
				action.invoke(result, text);
		} else {
			for (ValidationAction<SuggestBox> action : getFailureActions())
				action.invoke(result, suggest);
		}
	}

	public StringMaxLengthValidator setMax(int max) {
		this.max = max;
		return this;
	}
}
