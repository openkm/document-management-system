package com.openkm.validator;

import com.openkm.core.Config;
import com.openkm.core.RepositoryException;
import com.openkm.validator.password.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidatorFactory {
	private static Logger log = LoggerFactory.getLogger(ValidatorFactory.class);
	private static PasswordValidator passwordValidator = null;

	/**
	 * Password validator
	 */
	public static PasswordValidator getPasswordValidator() throws RepositoryException {
		if (passwordValidator == null) {
			try {
				log.info("PasswordValidator: {}", Config.VALIDATOR_PASSWORD);
				Object object = Class.forName(Config.VALIDATOR_PASSWORD).newInstance();
				passwordValidator = (PasswordValidator) object;
			} catch (ClassNotFoundException e) {
				log.error(e.getMessage(), e);
				throw new RepositoryException(e.getMessage(), e);
			} catch (InstantiationException e) {
				log.error(e.getMessage(), e);
				throw new RepositoryException(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				log.error(e.getMessage(), e);
				throw new RepositoryException(e.getMessage(), e);
			}
		}

		return passwordValidator;
	}
}
