package com.autoupdater.server.controllers;

import java.util.Arrays;
import java.util.List;
 
import javax.validation.Valid;
 
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.autoupdater.server.interfaces.UserDao;
import com.autoupdater.server.models.User;
import com.autoupdater.server.validators.UserValidator;
 
/**
 * Responsible for rendering user panel.
 */
@Controller
@RequestMapping(value = "/users")
public class UserController {
	/**
	 * Controller's logger.
	 */
	protected static Logger logger = Logger.getLogger (UserController.class);
	
	/**
	 * Renders list of users.
	 * 
	 * Let JSF run /views/users.jsp on GET /server/users request.
	 * 
	 * @param  model passed users model
	 * @return       facelet name
	 */
    @RequestMapping(method = RequestMethod.GET)
    public String getUsers (Model model) {
    	logger.debug ("Received request to show all persons");
    	UserDao ud = this.getUserDao();

    	List<User> users = ud.findAll();

    	model.addAttribute("users", users);

    	return "userspage";
    }
    
    /**
     * Renders new users form. 
     * 
	 * Let JSF run /views/Register.jsp on GET /server/users/add request.
	 * 
	 * @param  model passed users model
	 * @return       facelet name
	 */
    @RequestMapping(value = "/add",method = RequestMethod.GET)
	public String RegisterInit (Model model) {
		logger.info ("Generated registration form.");
		
		model.addAttribute ("user", new User ());
		model.addAttribute("types", this.getUserTypes());
		
		return "Register";
	}
	
    /**
     * Parses sent new user data - renders form again if errors occurred, otherwise adds user and get back to list of users.
     * 
	 * On errors let JSF run /views/Register.jsp on POST /server/users/add request.
	 * Otherwise creates new users and redirects to list of users. 
	 * 
	 * @param  user    user
	 * @param  result  validation result
	 * @param  model   passed users model
	 * @return         facelet name or redirect
	 */
	@RequestMapping(value = "/add",method = RequestMethod.POST)
	public String RegisterParse (@Valid@ModelAttribute(value="user") User user, BindingResult result, Model model) {
		(new UserValidator ()).validate (user, result);
		if (result.hasErrors ()) {
			String err = "";
			for(ObjectError error : result.getAllErrors ())
				err += "Error: " + error.getCode() +  " - " + error.getDefaultMessage() + "\n";
			logger.error (err);
			
			model.addAttribute("user", user);
			
			return "Register";
		} else {
			logger.info ("User "+user+" successfully registered!");
			
			UserDao ud = this.getUserDao();
			ud.create (user);
			
			return "redirect:";
		}
	}
	
	/**
   	 * Deletes user from list.
   	 * 
	 * Let JSF run /views/deletedpage.jsp on GET /server/users/delete/[id] request.
	 * 
	 * @param  id      package's ID
	 * @param  model   passed users model
	 * @return         facelet name
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String delete (@RequestParam(value="id", required=true) Integer id, Model model) {
		logger.debug ("Received request to delete existing user");
		  
		UserDao ud = this.getUserDao();
		  
		ud.delete (id);
		   
		model.addAttribute ("id", id);
		      
		return "deletedpage";
	}
	
	/**
   	 * Renders form to edit existing user.
   	 * 
	 * Let JSF run /views/userAttribute.jsp on GET /server/updates/edit/[id] request.
	 * 
	 * @param  id    user's ID
	 * @param  model passed user model
	 * @return       facelet name
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String getEdit (@RequestParam(value="id", required=true) Integer id, Model model) {
	    logger.debug ("Received request to show edit page");
	     
	    UserDao ud = this.getUserDao();

	    User user = ud.findById(id);
	    
	    model.addAttribute("userAttribute", user);
	    List<String> types;
	    
	    if ( user.isAdmin() )
		    {
		    	types = Arrays.asList("System Admin", "User", "Package Admin");
		    }
	    else if ( user.isPackageAdmin() )
		    {
		    	types = Arrays.asList("Package Admin", "System Admin", "User");
		    }
	    else
		    {
		    	types = Arrays.asList("User", "Package Admin", "System Admin");	
		    }
	    
	    model.addAttribute("types", types);
	      
	    return "editpage";
	}
	 
	/**
   	 * Saves changes.
   	 * 
	 * Let JSF run /views/editedpage.jsp on POST /server/users/edit/[id] request.
	 * 
	 * On errors let JSF run /views/editedpage.jsp on POST /server/users/add request.
	 * Otherwise saves user and redirects to list of packages. 
	 * 
	 * @param  pack    update
	 * @param  id      updates's id
	 * @param  model   passed updates model
	 * @return         facelet name or redirect
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String saveEdit (@ModelAttribute("userAttribute") User user, @RequestParam(value="id", required=true) Integer id, Model model) {
		logger.debug ("Received request to update user");
	     
	    UserDao ud = this.getUserDao();
	    
	    user.setId(id);
	      
	    ud.update(user);
	      
	    model.addAttribute("id", id);
	   
	    return "editedpage";
	}
	
	/**
	 * Returns list of possible types.
	 * 
	 * @return list of possible types
	 */
	private List<String> getUserTypes () {
		return Arrays.asList("User", "Package Admin", "System Admin");
	}
	
	/**
	 * Returns UserDAO.
	 * 
	 *  @return UserDAO
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
}