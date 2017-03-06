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

import com.google.gwt.user.client.ui.FlexTable;
import eu.maydu.gwt.validation.client.ValidationAction;
import eu.maydu.gwt.validation.client.ValidationResult;
import eu.maydu.gwt.validation.client.ValidationResult.ValidationError;
import eu.maydu.gwt.validation.client.Validator;
import eu.maydu.gwt.validation.client.i18n.StandardValidationMessages;
import eu.maydu.gwt.validation.client.i18n.ValidationMessages;

/**
 * NotEmptyFlextTableValidator
 *
 * @author jllort
 *
 */
public class NotEmptyFlextTableValidator extends Validator<NotEmptyFlextTableValidator> {

	private FlexTable flexTable = null;

	public NotEmptyFlextTableValidator(FlexTable flexTable) {
		this.flexTable = flexTable;
		this.setCustomMsgKey(null);
		this.preventsPropagationOfValidationChain();
	}

	@Override
	public void invokeActions(ValidationResult result) {
		if (flexTable != null) {
			for (ValidationAction<FlexTable> va : this.getFailureActions())
				va.invoke(result, flexTable);
		}
	}

	@Override
	public ValidationResult validate(ValidationMessages allMessages) {
		StandardValidationMessages messages = allMessages.getStandardMessages();
		if (flexTable.getRowCount() == 0) {
			ValidationResult result = new ValidationResult();
			ValidationError error = result.new ValidationError(null, getErrorMessage(allMessages, messages.mustSelectValue()));
			result.getErrors().add(error);
			return result;
		}

		return null;
	}
}
