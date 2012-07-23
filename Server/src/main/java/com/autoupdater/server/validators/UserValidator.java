package com.autoupdater.server.validators;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.autoupdater.server.interfaces.UserDao;
import com.autoupdater.server.models.User;

/**
 * Extends default user validation.
 */
@Component("UserValidator")
public class UserValidator {
	/**
	 * Validator's logger.
	 */
	protected static Logger logger = Logger.getLogger(UserValidator.class);
	
	/**
	 * Defines whether class is supported by validator.
	 * 
	 * @param klass classname
	 * @return      whether class is supported by validator
	 */
	public boolean supports (Class<?> klass) { return User.class.isAssignableFrom (klass); }
	
	/**
	 * Returns UserDao.
	 * 
	 * @return UserDao
	 */
	private UserDao getUserDao () {
		UserDao ud = null;
	    
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext ("beans.xml");
			ud = (UserDao) context.getBean ("userDao");
		} catch (Exception e) {
			logger.error ("FAIL: " + e);
		}
		
		return ud;
	}
	
	/**
	 * Validates class.
	 * 
	 * @param target class to be validated
	 * @param errors Errors object
	 */
	public void validate (Object target, Errors errors) {
		User user = (User) target;
		ValidationUtils.rejectIfEmptyOrWhitespace (errors, "Username",
	        "NotEmpty.user.Username",
	        "Username must not be empty.");
		ValidationUtils.rejectIfEmptyOrWhitespace (errors, "Password",
		        "NotEmpty.user.Password",
		        "Password must not be empty.");
		if (!(user.getPassword ()).equals(user.getConfirmPassword ()))
			errors.rejectValue ("Password",
				"matchingPassword.user.ConfirmPassword",
				"Passwords do not match.");
		
		UserDao ud = this.getUserDao();
		List<String> usernames = ud.findAllUsernames();
		if (usernames.contains(user.getUsername()))
			errors.rejectValue ("Username",
					"Taken.user.username",
					"This username is already taken.");
	}
}
