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
import eu.maydu.gwt.validation.client.Validator;
import eu.maydu.gwt.validation.client.i18n.ValidationMessages;

/**
 * RegularExpressionValidator
 *
 * @author jllort
 *
 */
public class RegularExpressionValidator extends Validator<RegularExpressionValidator> {

	private TextBoxBase textBox = null;
	private SuggestBox suggestBox = null;
	private String regexPattern = null;

	public RegularExpressionValidator(TextBoxBase text, String regexPattern) {
		this.textBox = text;
		this.regexPattern = regexPattern;
	}

	public RegularExpressionValidator(SuggestBox suggest, String regexPattern) {
		this.suggestBox = suggest;
		this.regexPattern = regexPattern;
	}

	@Override
	public void invokeActions(ValidationResult result) {
		if (textBox != null) {
			for (ValidationAction<TextBoxBase> va : this.getFailureActions())
				va.invoke(result, textBox);
		} else {
			for (ValidationAction<SuggestBox> va : this.getFailureActions())
				va.invoke(result, suggestBox);
		}
	}

	@Override
	public <V extends ValidationMessages> ValidationResult validate(V messages) {

		String text;
		if (suggestBox != null)
			text = suggestBox.getText();
		else text = textBox.getText();

		if (text.equals("") && !isRequired())
			return null;

		try {
			if (!text.matches(regexPattern))
				return new ValidationResult(messages.getStandardMessages().notEqual());
		} catch (IllegalArgumentException ex) {
			return new ValidationResult(messages.getStandardMessages().notARegEx());
		}

		return null;
	}
}