package com.autoupdater.server.validators;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import com.autoupdater.server.interfaces.PackageDao;
import com.autoupdater.server.models.Package;

@Component("PackageValidator")
public class PackageValidator {
	/**
	 * Validator's logger.
	 */
	protected static Logger logger = Logger.getLogger (Package.class);
	
	/**
	 * Defines whether class is supported by validator.
	 * 
	 * @param klass classname
	 * @return      whether class is supported by validator
	 */
	public boolean supports (Class<?> klass) { return Package.class.isAssignableFrom (klass); }

	/**
	 * Validates class.
	 * 
	 * @param target class to be validated
	 * @param errors Errors object
	 */
	public void validate (Object target, Errors errors) {
		
		Package pack = (Package) target;
		PackageDao pd = this.getPackageDao();
		List<String> names = pd.findAllNames(pack.getProgramId());
		
		
		if (names.contains(pack.getName()))
			errors.rejectValue ("name",
				"Same.pack.name",
				"This program already contains a package with this name.");
	}
	
	/**
	 * Returns PackageDAO.
	 * 
	 * @return PackageDAO
	 */
	private PackageDao getPackageDao () {
		PackageDao pd = null;
	    
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext ("beans.xml");
			pd = (PackageDao) context.getBean ("packageDao");
		} catch (Exception e) {
			logger.error ("FAIL: " + e);
		}
		
		return pd;
	}
}
