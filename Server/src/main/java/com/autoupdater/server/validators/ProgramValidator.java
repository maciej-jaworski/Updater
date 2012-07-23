package com.autoupdater.server.validators;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import com.autoupdater.server.interfaces.ProgramDao;
import com.autoupdater.server.models.Program;

/**
 * Extends default Prograam validation.
 */
@Component("ProgramValidator")
public class ProgramValidator {
	/**
	 * Validator's logger.
	 */
	protected static Logger logger = Logger.getLogger (Program.class);
	
	/**
	 * Defines whether class is supported by validator.
	 * 
	 * @param klass classname
	 * @return      whether class is supported by validator
	 */
	public boolean supports (Class<?> klass) { return Program.class.isAssignableFrom (klass); }

	/**
	 * Validates class.
	 * 
	 * @param target class to be validated
	 * @param errors Errors object
	 */
	public void validate (Object target, Errors errors) {
		
		Program prog = (Program) target;
		ProgramDao pd = this.getProgramDao();
		List<String> names = pd.findAllNames();
		
		
		if (names.contains(prog.getName()))
			errors.rejectValue ("name",
				"Same.prog.name",
				"Program with the same name already exists");
	}
	
	/**
	 * Returns ProgramDAO.
	 * 
	 * @return ProgramDAO.
	 */
	private ProgramDao getProgramDao () {
		ProgramDao pd = null;
	    
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext ("beans.xml");
			pd = (ProgramDao) context.getBean ("programDao");
		} catch (Exception e) {
			logger.error ("FAIL: " + e);
		}
		
		return pd;
	}
}
