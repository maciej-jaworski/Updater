package com.autoupdater.server.controllers;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.autoupdater.server.interfaces.UserDao;
import com.autoupdater.server.models.PasswordEdit;
import com.autoupdater.server.models.User;
import com.autoupdater.server.validators.PasswordChangeValidator;

/**
 * Responsible for rendering sign in form.
 */
@Controller
public class SignInController extends AppController {
	/**
	 * Controller's logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger (SignInController.class);
	
	/**
	 * Renders sign in form.
	 * 
	 * Let JSF run /views/SignIn.jsp on GET /server/sign_in request.
	 * 
	 * @return facelet name
	 */
	@RequestMapping("/sign_in")
	public String SignInInit () {
		logger.info ("Generated sign in form.");
		
		return "SignIn";
	}
	
	/**
	 * Renders change-password form.
	 * 
	 * Let JSF run /views/userPasswordEditPage.jsp on GET /server/changepw request.
	 * 
	 * @param  model passed user model
	 * @return       facelet name
	 */
	@RequestMapping(value = "/changepw", method = RequestMethod.GET)
	public String editPassword(Model model) {
		logger.debug ("Received request to change password");
	    
	    UserDao userDao = this.getUserDao();
		String username= (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user = userDao.findByUsername(username);
	    
		PasswordEdit pe = new PasswordEdit();
		
		pe.setUserId(user.getId());
		
	    model.addAttribute("pe", pe);
	   
	    return "userPasswordEditPage";
	}
	
	/**
	 * Handles change-password request.
	 * 
	 * Tries to change password on POST /server/changepw request.
	 * 
	 * @param  pe     PasswordEdit model
	 * @param  result response that will be sent
	 * @param  model  passed user model
	 * @return        facelet name
	 */
	@RequestMapping(value = "/changepw",method = RequestMethod.POST)
   	public String editPassword(@Valid@ModelAttribute(value="pe") PasswordEdit pe, BindingResult result, Model model) {
   		(new PasswordChangeValidator ()).validate (pe, result);
   		if (result.hasErrors ()) {
   			String err = "";
   			for(ObjectError error : result.getAllErrors ())
   				err += "Error: " + error.getCode () +  " - " + error.getDefaultMessage () + "\n";
   			logger.error (err);
   			
   			UserDao userDao = this.getUserDao();
   			String username= (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
   			User user = userDao.findByUsername(username);
   			
   			pe.setUserId(user.getId());
   			
   		    model.addAttribute("pe", pe);
   			
   			return "userPasswordEditPage";
   		} else {
   			
   			UserDao ud = this.getUserDao();
   			
   			ud.changePasswordForId(pe);
   			
   			return "userPasswordEditedPage";
   		}
   	}
	
	/**
	 * Returns UserDAO.
	 * 
	 * @return UserDAO
	 */
	private UserDao getUserDao() {
		UserDao ud = null;
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext ("beans.xml");
			ud = (UserDao) context.getBean ("userDao");
		} catch (Exception e) {
			logger.error ("FAIL: " + e);
		}
		return ud;
	}
}
