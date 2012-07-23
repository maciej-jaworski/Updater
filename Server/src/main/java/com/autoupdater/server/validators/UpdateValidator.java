package com.autoupdater.server.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.autoupdater.server.models.Update;

/**
 * Extends default update validation.
 */
@Component("UpdateValidator")
public class UpdateValidator {
	/**
	 * Defines whether class is supported by validator.
	 * 
	 * @param klass classname
	 * @return      whether class is supported by validator
	 */
	public boolean supports (Class<?> klass) { return Update.class.isAssignableFrom (klass); }

	/**
	 * Validates class.
	 * 
	 * @param target class to be validated
	 * @param errors Errors object
	 */
	public void validate (Object target, Errors errors) {
		Update update = (Update) target;
		ValidationUtils.rejectIfEmptyOrWhitespace (errors, "Version",
	        "NotEmpty.update.Version",
	        "Version number must not be empty.");
		ValidationUtils.rejectIfEmptyOrWhitespace (errors, "Changelog",
		        "NotEmpty.update.Changelog",
		        "Changelog must not be empty.");
		
		String[] temp = update.getVersion().split("\\.");
		
		if (temp.length > 4)
			errors.rejectValue ("Changelog",
				"regexMatch.update.Changelog",
				"Too many numbers! Version number must consist of 1 to 4 comma separated numbers");
		
		for (int i=0; i < temp.length; i++)
		{
			if (!temp[i].matches("[0-9]+"))
				errors.rejectValue ("Changelog",
					"regexMatch.update.Changelog",
					"Version number must consist of comma separated numbers");
			
		}		
	}
}
