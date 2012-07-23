package com.autoupdater.server.controllers;

import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
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
import org.springframework.web.bind.annotation.RequestParam;

import com.autoupdater.server.interfaces.ProgramDao;
import com.autoupdater.server.interfaces.UserDao;
import com.autoupdater.server.models.Program;
import com.autoupdater.server.models.User;
import com.autoupdater.server.validators.ProgramValidator;

/**
 * Responsible for managing programs.
 */
@Controller
@RequestMapping(value = "/programs")
public class ProgramsController {
	/**
	 * Controller's logger.
	 */
	protected static Logger logger = Logger.getLogger(ProgramsController.class);

	/**
	 * Renders programs list.
	 * 
	 * Let JSF run /views/programsShowAll.jsp on GET /server/programs request.
	 * 
	 * @param model passed programs model
	 * @return      facelet name
	 */
	@RequestMapping(method = RequestMethod.GET)
	    public String getPrograms (Model model) {
		    logger.debug ("Received request to show all programs");
		    
		    UserDao ud = this.getUserDao();
			String username= (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			model.addAttribute("user",ud.findByUsername(username));
		    
		    ProgramDao pd = this.getProgramDao();
		
		    List<Program> progs = pd.findAll();
		
		    model.addAttribute("progs", progs);
		
		    return "programsShowAll";
	    }
	
	/**
	 * Renders new program form.
	 * 
	 * Let JSF run /views/programsAddNew.jsp on GET /server/programs/add request.
	 * 
	 * @param model passed programs model
	 * @return      facelet name
	 */
	@RequestMapping(value = "/add",method = RequestMethod.GET)
   	public String addProgramForm (Model model) {
   		logger.info ("Generated new program form.");
   		
   		model.addAttribute ("prog", new Program());
   		
   		return "programsAddNew";
   	}
	
	/**
	 * Adds new program.
	 * 
	 * @param prog   program to be added
	 * @param result response that will be sent
	 * @param model  passed programs model
	 * @return       facelet name
	 */
	@RequestMapping(value = "/add",method = RequestMethod.POST)
   	public String addPackage (@Valid@ModelAttribute(value="prog") Program prog, BindingResult result, Model model) {
		(new ProgramValidator ()).validate (prog, result);
   		if (result.hasErrors ()) {
   			String err = "";
   			for(ObjectError error : result.getAllErrors ())
   				err += "Error: " + error.getCode () +  " - " + error.getDefaultMessage () + "\n";
   			logger.error (err);
   			
   			return "redirect:add";
   		} else {
   			logger.info ("Program "+prog.getName()+" successfully added!");
   			
   			ProgramDao pd = this.getProgramDao();
   			
   			pd.create(prog);

   			return "redirect:";
   		}
   	}
	
	/**
	 * Deletes program.
	 * 
	 * @param id    program ID
	 * @param model passed programs model
	 * @return      facelet name
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String deletePackage (@RequestParam(value="id", required=true) Integer id, Model model) {
    
		  logger.debug("Received request to delete existing program.");
		  
		  UserDao userDao = this.getUserDao();
		  String username= (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		  User user = userDao.findByUsername(username);
		  
		  if (user.isPackageAdmin())
		  {
			  ProgramDao pd = this.getProgramDao();
			  
			  pd.delete(id);
			  
			  model.addAttribute("id", id);
			  
			  return "programDeletedPage";
		  }
		  else
		  {
			  model.addAttribute("message", "You don't have permission to perform this action");
			  model.addAttribute("backUrl", "/programs");

			  return "errorPage";
		  }
	}
	
	/**
	 * Renders program edition form.
	 * 
	 * Let JSF run /views/programsEditPage.jsp on GET /server/programs/edit/[id] request.
	 * 
	 * @param id    program ID
	 * @param model passed programs model
	 * @return      facelet name
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String editPackage (@RequestParam(value="id", required=true) Integer id, Model model) {
	    logger.debug("Received request to show program edit page");
	     
	    ProgramDao pd = this.getProgramDao();
	
		Program prog = pd.findById(id);
		
	    model.addAttribute ("prog", prog);
	      
	    return "programEditPage";
   	}
	
	/**
	 * Edits program.
	 * 
	 * @param prog   program to be added
	 * @param id     program ID
	 * @param model  passed programs model
	 * @return       facelet name
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String saveEdit (@ModelAttribute("pack") Program prog, @RequestParam(value="id", required=true) Integer id, Model model) {
	    logger.debug("Received request to update program.");
	     
	    ProgramDao pd = this.getProgramDao();
	     
	    pd.update (prog);
	    model.addAttribute ("id", id);
	   
	    return "programEditedPage";
   	}
	
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
	 * Returns ProgramDao.
	 * 
	 * @return ProgramDao
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
