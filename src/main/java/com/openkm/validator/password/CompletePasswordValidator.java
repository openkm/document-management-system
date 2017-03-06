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

package com.openkm.validator.password;

import com.openkm.core.Config;
import com.openkm.validator.ValidatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Complex password validator
 */
public class CompletePasswordValidator implements PasswordValidator {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(CompletePasswordValidator.class);

	@Override
	public void Validate(String password) throws ValidatorException {
		validateLength(password);
		checkLowerCase(password);
		checkUpperCase(password);
		checkDigits(password);
		checkSpecial(password);
	}

	/**
	 * Validate length
	 */
	private void validateLength(String password) throws ValidatorException {
		if (Config.VALIDATOR_PASSWORD_MIN_LENGTH > 0 &&
				password.length() < Config.VALIDATOR_PASSWORD_MIN_LENGTH) {
			throw new ValidatorException(Config.VALIDATOR_PASSWORD_ERROR_MIN_LENGTH);
		}

		if (Config.VALIDATOR_PASSWORD_MAX_LENGTH > 0 &&
				password.length() > Config.VALIDATOR_PASSWORD_MAX_LENGTH) {
			throw new ValidatorException(Config.VALIDATOR_PASSWORD_ERROR_MAX_LENGTH);
		}
	}

	/**
	 * Validate lowercase characters
	 */
	private void checkLowerCase(String password) throws ValidatorException {
		int count = 0;

		if (Config.VALIDATOR_PASSWORD_MIN_LOWERCASE > 0) {
			for (int i = 0; i < password.length(); i++) {
				if (Character.isLowerCase(password.charAt(i))) {
					count++;
				}
			}

			if (Config.VALIDATOR_PASSWORD_MIN_LOWERCASE > count) {
				throw new ValidatorException(Config.VALIDATOR_PASSWORD_ERROR_MIN_LOWERCASE);
			}
		}
	}

	/**
	 * Validate uppercase characters
	 */
	private void checkUpperCase(String password) throws ValidatorException {
		int count = 0;

		if (Config.VALIDATOR_PASSWORD_MIN_UPPERCASE > 0) {
			for (int i = 0; i < password.length(); i++) {
				if (Character.isUpperCase(password.charAt(i))) {
					count++;
				}
			}

			if (Config.VALIDATOR_PASSWORD_MIN_UPPERCASE > count) {
				throw new ValidatorException(Config.VALIDATOR_PASSWORD_ERROR_MIN_UPPERCASE);
			}
		}
	}

	/**
	 * Validate digits
	 */
	private void checkDigits(String password) throws ValidatorException {
		int count = 0;

		if (Config.VALIDATOR_PASSWORD_MIN_DIGITS > 0) {
			for (int i = 0; i < password.length(); i++) {
				if (Character.isDigit(password.charAt(i))) {
					count++;
				}
			}

			if (Config.VALIDATOR_PASSWORD_MIN_DIGITS > count) {
				throw new ValidatorException(Config.VALIDATOR_PASSWORD_ERROR_MIN_DIGITS);
			}
		}
	}

	/**
	 * Validate special characters
	 */
	private void checkSpecial(String password) throws ValidatorException {
		int count = 0;

		if (Config.VALIDATOR_PASSWORD_MIN_SPECIAL > 0) {
			for (int i = 0; i < password.length(); i++) {
				if (!Character.isLetterOrDigit(password.charAt(i)) &&
						!Character.isWhitespace(password.charAt(i))) {
					count++;
				}
			}

			if (Config.VALIDATOR_PASSWORD_MIN_SPECIAL > count) {
				throw new ValidatorException(Config.VALIDATOR_PASSWORD_ERROR_MIN_SPECIAL);
			}
		}
	}
}
