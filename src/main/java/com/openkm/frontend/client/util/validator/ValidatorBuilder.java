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

import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.form.GWTValidator;
import eu.maydu.gwt.validation.client.ValidationProcessor;
import eu.maydu.gwt.validation.client.actions.FocusAction;
import eu.maydu.gwt.validation.client.actions.StyleAction;
import eu.maydu.gwt.validation.client.validators.ListBoxValidator;
import eu.maydu.gwt.validation.client.validators.standard.NotEmptyValidator;
import eu.maydu.gwt.validation.client.validators.strings.EmailValidator;

/**
 * @author jllort
 *
 */
public class ValidatorBuilder {

	public static void addValidator(ValidationProcessor validationProcessor, FocusAction focusAction,
	                                HorizontalPanel hPanel, String name, GWTValidator validator, Widget widget) {
		String type = validator.getType();
		if (type.equals("req")) {
			HTML space = new HTML("");
			Label errorLabel = new Label(Main.i18n("validation.required.field"));
			errorLabel.setStyleName("okm-validationFailedText");
			hPanel.add(space);
			hPanel.add(errorLabel);
			hPanel.setCellWidth(space, "5px");
			if (widget instanceof TextBox) {
				hPanel.setCellVerticalAlignment(errorLabel, HasAlignment.ALIGN_MIDDLE);
				validationProcessor.addValidators(name + "_req",
						new NotEmptyValidator((TextBox) widget)
								.addActionForFailure(focusAction)
								.addActionForFailure(new StyleAction("okm-validationFailedBorder"))
								.addActionForFailure(new ErrorMsgLabelTextAction(errorLabel))
				);

			} else if (widget instanceof TextArea) {
				hPanel.setCellVerticalAlignment(errorLabel, HasAlignment.ALIGN_TOP);
				validationProcessor.addValidators(name + "_req",
						new NotEmptyValidator((TextArea) widget)
								.addActionForFailure(focusAction)
								.addActionForFailure(new StyleAction("okm-validationFailedBorder"))
								.addActionForFailure(new ErrorMsgLabelTextAction(errorLabel))
				);

			} else if (widget instanceof ListBox) {
				hPanel.setCellVerticalAlignment(errorLabel, HasAlignment.ALIGN_MIDDLE);
				validationProcessor.addValidators(name + "_req",
						new ListBoxValidator((ListBox) widget, "")
								.addActionForFailure(focusAction)
								.addActionForFailure(new StyleAction("okm-validationFailedBorder"))
								.addActionForFailure(new ErrorMsgLabelTextAction(errorLabel))
				);

			} else if (widget instanceof FlexTable) {
				hPanel.setCellVerticalAlignment(errorLabel, HasAlignment.ALIGN_TOP);
				validationProcessor.addValidators(name + "_req",
						new NotEmptyFlextTableValidator((FlexTable) widget)
								.addActionForFailure(new StyleAction("okm-validationFailedBorder"))
								.addActionForFailure(new ErrorMsgLabelTextAction(errorLabel))
				);

			} else if (widget instanceof FileUpload) {
				hPanel.setCellVerticalAlignment(errorLabel, HasAlignment.ALIGN_TOP);
				validationProcessor.addValidators(name + "_req",
						new NotEmptyFileUploadValidator((FileUpload) widget)
								.addActionForFailure(new StyleAction("okm-validationFailedBorder"))
								.addActionForFailure(new ErrorMsgLabelTextAction(errorLabel))
				);

			}

		} else if (type.equals("email")) {
			HTML space = new HTML("");
			Label errorLabel = new Label(Main.i18n("validation.mail.required.field"));
			errorLabel.setStyleName("okm-validationFailedText");
			hPanel.add(space);
			hPanel.add(errorLabel);
			hPanel.setCellWidth(space, "5px");
			if (widget instanceof TextBox) {
				hPanel.setCellVerticalAlignment(errorLabel, HasAlignment.ALIGN_MIDDLE);
				validationProcessor.addValidators(name + "_email",
						new EmailValidator((TextBox) widget)
								.addActionForFailure(focusAction)
								.addActionForFailure(new StyleAction("okm-validationFailedBorder"))
								.addActionForFailure(new ErrorMsgLabelTextAction(errorLabel))
				);

			}

		} else if (type.equals("url")) {
			HTML space = new HTML("");
			Label errorLabel = new Label(Main.i18n("validation.url.required.field"));
			errorLabel.setStyleName("okm-validationFailedText");
			hPanel.add(space);
			hPanel.add(errorLabel);
			hPanel.setCellWidth(space, "5px");
			if (widget instanceof TextBox) {
				hPanel.setCellVerticalAlignment(errorLabel, HasAlignment.ALIGN_MIDDLE);
				validationProcessor.addValidators(name + "_url",
						new URLValidator((TextBox) widget)
								.addActionForFailure(focusAction)
								.addActionForFailure(new StyleAction("okm-validationFailedBorder"))
								.addActionForFailure(new ErrorMsgLabelTextAction(errorLabel))
				);
			}

		} else if (type.equals("minlen")) {
			HTML space = new HTML("");
			Label errorLabel = new Label(Main.i18n("validation.minlen.required") + " - (" + validator.getParameter() + ")");
			errorLabel.setStyleName("okm-validationFailedText");
			hPanel.add(space);
			hPanel.add(errorLabel);
			hPanel.setCellWidth(space, "5px");
			if (widget instanceof TextBox) {
				hPanel.setCellVerticalAlignment(errorLabel, HasAlignment.ALIGN_MIDDLE);
				validationProcessor.addValidators(name + "_minlen",
						new StringMinLengthValidator((TextBox) widget, Integer.parseInt(validator.getParameter()))
								.addActionForFailure(focusAction)
								.addActionForFailure(new StyleAction("okm-validationFailedBorder"))
								.addActionForFailure(new ErrorMsgLabelTextAction(errorLabel))
				);
			} else if (widget instanceof TextArea) {
				hPanel.setCellVerticalAlignment(errorLabel, HasAlignment.ALIGN_TOP);
				validationProcessor.addValidators(name + "_minlen",
						new StringMinLengthValidator((TextArea) widget, Integer.parseInt(validator.getParameter()))
								.addActionForFailure(focusAction)
								.addActionForFailure(new StyleAction("okm-validationFailedBorder"))
								.addActionForFailure(new ErrorMsgLabelTextAction(errorLabel))
				);
			}

		} else if (type.equals("maxlen")) {
			HTML space = new HTML("");
			Label errorLabel = new Label(Main.i18n("validation.maxlen.required") + " - (" + validator.getParameter() + ")");
			errorLabel.setStyleName("okm-validationFailedText");
			hPanel.add(space);
			hPanel.add(errorLabel);
			hPanel.setCellWidth(space, "5px");
			if (widget instanceof TextBox) {
				hPanel.setCellVerticalAlignment(errorLabel, HasAlignment.ALIGN_MIDDLE);
				validationProcessor.addValidators(name + "_maxlen",
						new StringMaxLengthValidator((TextBox) widget, Integer.parseInt(validator.getParameter()))
								.addActionForFailure(focusAction)
								.addActionForFailure(new StyleAction("okm-validationFailedBorder"))
								.addActionForFailure(new ErrorMsgLabelTextAction(errorLabel))
				);
			} else if (widget instanceof TextArea) {
				hPanel.setCellVerticalAlignment(errorLabel, HasAlignment.ALIGN_TOP);
				validationProcessor.addValidators(name + "_maxlen",
						new StringMaxLengthValidator((TextArea) widget, Integer.parseInt(validator.getParameter()))
								.addActionForFailure(focusAction)
								.addActionForFailure(new StyleAction("okm-validationFailedBorder"))
								.addActionForFailure(new ErrorMsgLabelTextAction(errorLabel))
				);
			}

		} else if (type.equals("lt")) {
			HTML space = new HTML("");
			Label errorLabel = new Label(Main.i18n("validation.lt.required") + " - (" + validator.getParameter() + ")");
			errorLabel.setStyleName("okm-validationFailedText");
			hPanel.add(space);
			hPanel.add(errorLabel);
			hPanel.setCellWidth(space, "5px");
			if (widget instanceof TextBox) {
				hPanel.setCellVerticalAlignment(errorLabel, HasAlignment.ALIGN_MIDDLE);
				validationProcessor.addValidators(name + "_lt",
						new StringLtValidator((TextBox) widget, validator.getParameter())
								.addActionForFailure(focusAction)
								.addActionForFailure(new StyleAction("okm-validationFailedBorder"))
								.addActionForFailure(new ErrorMsgLabelTextAction(errorLabel))
				);
			} else if (widget instanceof TextArea) {
				hPanel.setCellVerticalAlignment(errorLabel, HasAlignment.ALIGN_TOP);
				validationProcessor.addValidators(name + "_lt",
						new StringLtValidator((TextArea) widget, validator.getParameter())
								.addActionForFailure(focusAction)
								.addActionForFailure(new StyleAction("okm-validationFailedBorder"))
								.addActionForFailure(new ErrorMsgLabelTextAction(errorLabel))
				);
			}

		} else if (type.equals("gt")) {
			HTML space = new HTML("");
			Label errorLabel = new Label(Main.i18n("validation.gt.required") + " - (" + validator.getParameter() + ")");
			errorLabel.setStyleName("okm-validationFailedText");
			hPanel.add(space);
			hPanel.add(errorLabel);
			hPanel.setCellWidth(space, "5px");
			if (widget instanceof TextBox) {
				hPanel.setCellVerticalAlignment(errorLabel, HasAlignment.ALIGN_MIDDLE);
				validationProcessor.addValidators(name + "_gt",
						new StringGtValidator((TextBox) widget, validator.getParameter())
								.addActionForFailure(focusAction)
								.addActionForFailure(new StyleAction("okm-validationFailedBorder"))
								.addActionForFailure(new ErrorMsgLabelTextAction(errorLabel))
				);
			} else if (widget instanceof TextArea) {
				hPanel.setCellVerticalAlignment(errorLabel, HasAlignment.ALIGN_TOP);
				validationProcessor.addValidators(name + "_gt",
						new StringGtValidator((TextArea) widget, validator.getParameter())
								.addActionForFailure(focusAction)
								.addActionForFailure(new StyleAction("okm-validationFailedBorder"))
								.addActionForFailure(new ErrorMsgLabelTextAction(errorLabel))
				);
			}

		} else if (type.equals("min")) {
			HTML space = new HTML("");
			Label errorLabel = new Label(Main.i18n("validation.min.required") + " - (" + validator.getParameter() + ")");
			errorLabel.setStyleName("okm-validationFailedText");
			hPanel.add(space);
			hPanel.add(errorLabel);
			hPanel.setCellWidth(space, "5px");
			if (widget instanceof TextBox) {
				hPanel.setCellVerticalAlignment(errorLabel, HasAlignment.ALIGN_MIDDLE);
				validationProcessor.addValidators(name + "_min",
						new IntegerMinValidator((TextBox) widget, Integer.parseInt(validator.getParameter()))
								.addActionForFailure(focusAction)
								.addActionForFailure(new StyleAction("okm-validationFailedBorder"))
								.addActionForFailure(new ErrorMsgLabelTextAction(errorLabel))
				);
			} else if (widget instanceof TextArea) {
				hPanel.setCellVerticalAlignment(errorLabel, HasAlignment.ALIGN_TOP);
				validationProcessor.addValidators(name + "_min",
						new IntegerMinValidator((TextArea) widget, Integer.parseInt(validator.getParameter()))
								.addActionForFailure(focusAction)
								.addActionForFailure(new StyleAction("okm-validationFailedBorder"))
								.addActionForFailure(new ErrorMsgLabelTextAction(errorLabel))
				);
			}

		} else if (type.equals("max")) {
			HTML space = new HTML("");
			Label errorLabel = new Label(Main.i18n("validation.max.required") + " - (" + validator.getParameter() + ")");
			errorLabel.setStyleName("okm-validationFailedText");
			hPanel.add(space);
			hPanel.add(errorLabel);
			hPanel.setCellWidth(space, "5px");
			if (widget instanceof TextBox) {
				hPanel.setCellVerticalAlignment(errorLabel, HasAlignment.ALIGN_MIDDLE);
				validationProcessor.addValidators(name + "_max",
						new IntegerMaxValidator((TextBox) widget, Integer.parseInt(validator.getParameter()))
								.addActionForFailure(focusAction)
								.addActionForFailure(new StyleAction("okm-validationFailedBorder"))
								.addActionForFailure(new ErrorMsgLabelTextAction(errorLabel))
				);
			} else if (widget instanceof TextArea) {
				hPanel.setCellVerticalAlignment(errorLabel, HasAlignment.ALIGN_TOP);
				validationProcessor.addValidators(name + "_max",
						new IntegerMaxValidator((TextArea) widget, Integer.parseInt(validator.getParameter()))
								.addActionForFailure(focusAction)
								.addActionForFailure(new StyleAction("okm-validationFailedBorder"))
								.addActionForFailure(new ErrorMsgLabelTextAction(errorLabel))
				);
			}

		} else if (type.equals("regexp")) {
			HTML space = new HTML("");
			Label errorLabel = new Label(Main.i18n("validation.regexp.required") + " - (" + validator.getParameter() + ")");
			errorLabel.setStyleName("okm-validationFailedText");
			hPanel.add(space);
			hPanel.add(errorLabel);
			hPanel.setCellWidth(space, "5px");
			if (widget instanceof TextBox) {
				hPanel.setCellVerticalAlignment(errorLabel, HasAlignment.ALIGN_MIDDLE);
				validationProcessor.addValidators(name + "_regexp",
						new RegularExpressionValidator((TextBox) widget, validator.getParameter())
								.addActionForFailure(focusAction)
								.addActionForFailure(new StyleAction("okm-validationFailedBorder"))
								.addActionForFailure(new ErrorMsgLabelTextAction(errorLabel))
				);
			} else if (widget instanceof TextArea) {
				hPanel.setCellVerticalAlignment(errorLabel, HasAlignment.ALIGN_TOP);
				validationProcessor.addValidators(name + "_regexp",
						new RegularExpressionValidator((TextArea) widget, validator.getParameter())
								.addActionForFailure(focusAction)
								.addActionForFailure(new StyleAction("okm-validationFailedBorder"))
								.addActionForFailure(new ErrorMsgLabelTextAction(errorLabel))
				);
			}

		} else if (type.equals("alpha")) {
			HTML space = new HTML("");
			Label errorLabel = new Label(Main.i18n("validation.alphanumeric.required"));
			errorLabel.setStyleName("okm-validationFailedText");
			hPanel.add(space);
			hPanel.add(errorLabel);
			hPanel.setCellWidth(space, "5px");
			if (widget instanceof TextBox) {
				hPanel.setCellVerticalAlignment(errorLabel, HasAlignment.ALIGN_MIDDLE);
				validationProcessor.addValidators(name + "_alpha",
						new AlphaNumericValidator((TextBox) widget)
								.addActionForFailure(focusAction)
								.addActionForFailure(new StyleAction("okm-validationFailedBorder"))
								.addActionForFailure(new ErrorMsgLabelTextAction(errorLabel))
				);
			} else if (widget instanceof TextArea) {
				hPanel.setCellVerticalAlignment(errorLabel, HasAlignment.ALIGN_TOP);
				validationProcessor.addValidators(name + "_alpha",
						new AlphaNumericValidator((TextArea) widget)
								.addActionForFailure(focusAction)
								.addActionForFailure(new StyleAction("okm-validationFailedBorder"))
								.addActionForFailure(new ErrorMsgLabelTextAction(errorLabel))
				);
			}

		} else if (type.equals("num")) {
			HTML space = new HTML("");
			Label errorLabel = new Label(Main.i18n("validation.numeric.required"));
			errorLabel.setStyleName("okm-validationFailedText");
			hPanel.add(space);
			hPanel.add(errorLabel);
			hPanel.setCellWidth(space, "5px");
			if (widget instanceof TextBox) {
				hPanel.setCellVerticalAlignment(errorLabel, HasAlignment.ALIGN_MIDDLE);
				validationProcessor.addValidators(name + "_num",
						new NumericValidator((TextBox) widget)
								.addActionForFailure(focusAction)
								.addActionForFailure(new StyleAction("okm-validationFailedBorder"))
								.addActionForFailure(new ErrorMsgLabelTextAction(errorLabel))
				);
			} else if (widget instanceof TextArea) {
				hPanel.setCellVerticalAlignment(errorLabel, HasAlignment.ALIGN_TOP);
				validationProcessor.addValidators(name + "_num",
						new NumericValidator((TextArea) widget)
								.addActionForFailure(focusAction)
								.addActionForFailure(new StyleAction("okm-validationFailedBorder"))
								.addActionForFailure(new ErrorMsgLabelTextAction(errorLabel))
				);
			}

		} else if (type.equals("dec")) {
			HTML space = new HTML("");
			Label errorLabel = new Label(Main.i18n("validation.decimal.required") + " - (000" + Main.i18n("general.decimal.pattern") + "00)");
			errorLabel.setStyleName("okm-validationFailedText");
			hPanel.add(space);
			hPanel.add(errorLabel);
			hPanel.setCellWidth(space, "5px");
			if (widget instanceof TextBox) {
				hPanel.setCellVerticalAlignment(errorLabel, HasAlignment.ALIGN_MIDDLE);
				validationProcessor.addValidators(name + "_dec",
						new DecimalValidator((TextBox) widget)
								.addActionForFailure(focusAction)
								.addActionForFailure(new StyleAction("okm-validationFailedBorder"))
								.addActionForFailure(new ErrorMsgLabelTextAction(errorLabel))
				);
			} else if (widget instanceof TextArea) {
				hPanel.setCellVerticalAlignment(errorLabel, HasAlignment.ALIGN_TOP);
				validationProcessor.addValidators(name + "_dec",
						new DecimalValidator((TextArea) widget)
								.addActionForFailure(focusAction)
								.addActionForFailure(new StyleAction("okm-validationFailedBorder"))
								.addActionForFailure(new ErrorMsgLabelTextAction(errorLabel))
				);
			}
		}
	}
}