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

import com.google.gwt.user.client.ui.FileUpload;
import eu.maydu.gwt.validation.client.ValidationAction;
import eu.maydu.gwt.validation.client.ValidationResult;
import eu.maydu.gwt.validation.client.ValidationResult.ValidationError;
import eu.maydu.gwt.validation.client.Validator;
import eu.maydu.gwt.validation.client.i18n.StandardValidationMessages;
import eu.maydu.gwt.validation.client.i18n.ValidationMessages;

/**
 * NotEmptyFileUploadValidator
 *
 * @author jllort
 *
 */
public class NotEmptyFileUploadValidator extends Validator<NotEmptyFileUploadValidator> {

	protected FileUpload fileUpload;

	public NotEmptyFileUploadValidator(FileUpload fileUpload) {
		this.fileUpload = fileUpload;
		this.setCustomMsgKey(null);
		this.preventsPropagationOfValidationChain();
	}

	@Override
	public ValidationResult validate(ValidationMessages allMessages) {
		StandardValidationMessages messages = allMessages.getStandardMessages();
		String str = null;
		if (fileUpload != null)
			str = fileUpload.getFilename();
		if (str == null || str.length() == 0) {
			ValidationResult result = new ValidationResult();
			ValidationError error = result.new ValidationError(null, getErrorMessage(allMessages, messages.mustSelectValue()));
			result.getErrors().add(error);
			return result;
		}

		return null;
	}

	public void invokeActions(ValidationResult result) {
		if (fileUpload != null) {
			for (ValidationAction<FileUpload> action : getFailureActions())
				action.invoke(result, fileUpload);
		}
	}
}
