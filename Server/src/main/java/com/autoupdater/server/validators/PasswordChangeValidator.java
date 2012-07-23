package com.autoupdater.server.validators;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import com.autoupdater.server.interfaces.UserDao;
import com.autoupdater.server.models.PasswordEdit;
import com.autoupdater.server.models.User;

/**
 * Extends default user validation.
 */
@Component("PasswordChangeValidator")
public class PasswordChangeValidator {
	/**
	 * Validator's logger.
	 */
	protected static Logger logger = Logger.getLogger(PasswordChangeValidator.class);
	
	/**
	 * Defines whether class is supported by validator.
	 * 
	 * @param klass classname
	 * @return      whether class is supported by validator
	 */
	public boolean supports (Class<?> klass) { return PasswordEdit.class.isAssignableFrom (klass); }
	
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
		PasswordEdit pe = (PasswordEdit) target;
		if (!(pe.getNewPw()).equals(pe.getConfirmPw()))
			errors.rejectValue ("newPw",
				"matchingPassword.passwordEdit.ConfirmPassword",
				"Passwords do not match.");
		
		UserDao ud = this.getUserDao();
		User user = ud.findById(pe.getUserId());
		if (!BCrypt.checkpw(pe.getCurrentPw(), user.getPassword()))
			errors.rejectValue ("currentPw",
					"wrong.passwordEdit.currentPw",
					"Current password is wrong.");
	}
}
