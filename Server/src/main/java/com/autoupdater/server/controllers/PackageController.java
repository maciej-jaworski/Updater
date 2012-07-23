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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.autoupdater.server.interfaces.PackageDao;
import com.autoupdater.server.interfaces.UserDao;
import com.autoupdater.server.models.Package;
import com.autoupdater.server.validators.PackageValidator;

/**
 * Responsible for handling package panel.
 */
@Controller
@RequestMapping(value = "/packages")
public class PackageController {
	/**
	 * Controller's logger.
	 */
	protected static Logger logger = Logger.getLogger(PackageController.class);
	
	/**
	 * Renders list of packages.
	 * 
	 * Let JSF run /views/packagespage.jsp on GET /server/packages(/[id]) request.
	 * 
	 * @param  model passed packages model
	 * @return       facelet name
	 */
    @RequestMapping(method = RequestMethod.GET)
    public String getPackages (@RequestParam(value="id", required=false) Integer programId,Model model) {
	    logger.debug ("Received request to show all packages");
	    PackageDao pd = this.getPackageDao();
	    
	    List<Package> packs;
	    
	    if (programId != null)
		    {
	    		packs = pd.findByProgramId(programId);
		    }
	    else
		    {
	    		packs = pd.findAll();
		    }
	    
	    UserDao ud = this.getUserDao();
		String username= (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		model.addAttribute("user",ud.findByUsername(username));
	
	    model.addAttribute("packs", packs);
	    model.addAttribute("programId", programId);
	    
	
	    return "packageShowAll";
    }
    
    /**
	 * Alternative way to renders list of packages.
	 * 
	 * Let JSF run /views/packageShowAll.jsp on GET /server/packages/[id] request.
	 * 
	 * @param  model passed packages model
	 * @return       facelet name
	 */
    @RequestMapping(value="/{programId}", method = RequestMethod.GET)
	public String getPackagesMethod2(@PathVariable Integer programId,Model model) {
	    logger.debug ("Received request to show all packages");
	    PackageDao pd = null;
	    try {
			ApplicationContext context = new ClassPathXmlApplicationContext ("beans.xml");
			pd = (PackageDao) context.getBean ("packageDao");
		} catch (Exception e) {
			logger.error ("FAIL: " + e);
		}
	    List<Package> packs;
	    if (programId != null)
		    {
	    		packs = pd.findByProgramId(programId);
		    }
	    else
		    {
	    		packs = pd.findAll();
		    }
	    
	    UserDao ud = this.getUserDao();
		String username= (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		model.addAttribute("user",ud.findByUsername(username));
	
	    model.addAttribute("packs", packs);
	    model.addAttribute("programId", programId);
	    
	    return "packageShowAll";
    }
    /**
     * Renders new package form. 
     * 
	 * Let JSF run /views/newPackage.jsp on GET /server/packages/add request.
	 * 
	 * @param  model passed packages model
	 * @return       facelet name
	 */
    @RequestMapping(value = "/add",method = RequestMethod.GET)
   	public String addPackageForm (@RequestParam(value="id", required=true) Integer programId, Model model) {
   		logger.info ("Generated new package form.");

   		model.addAttribute ("pack", new Package());
   		model.addAttribute("programId", programId);
   		
   		return "packageAddNew";
   	}
   	
    /**
     * Parses sent new package data - renders form again if errors occurred, otherwise adds package and get back to list of packages.
     * 
	 * On errors let JSF run /views/newPackage.jsp on POST /server/packages/add request.
	 * Otherwise creates new package and redirects to list of packages. 
	 * 
	 * @param  pack    package
	 * @param  result  validation result
	 * @param  model   passed packages model
	 * @return         facelet name or redirect
	 */
   	@RequestMapping(value = "/add",method = RequestMethod.POST)
   	public String addPackage (@RequestParam(value="id", required=true) Integer programId, 
   			@Valid@ModelAttribute(value="pack") Package pack, BindingResult result, Model model) {
   		(new PackageValidator ()).validate (pack, result);
   		if (result.hasErrors ()) {
   			String err = "";
   			for(ObjectError error : result.getAllErrors ())
   				err += "Error: " + error.getCode () +  " - " + error.getDefaultMessage () + "\n";
   			logger.error (err);
   			
   			model.addAttribute ("pack", pack);
   	   		model.addAttribute("programId", programId);
   	   		
   	   		return "packageAddNew";
   		} else {
   			logger.info ("Package "+pack+" successfully added!");
   			
   			try {
   				ApplicationContext context = new ClassPathXmlApplicationContext ("beans.xml");
   				PackageDao packageDao = (PackageDao) context.getBean ("packageDao");
   				
   				packageDao.create(pack);
   				
   				
   			} catch (Exception e) {
   				logger.error ("FAIL: " + e);
   			}
   			
   			return "redirect:" + pack.getProgramId();
   		}
   	}
   	
   	/**
   	 * Deletes package from list.
   	 * 
	 * Let JSF run /views/packageDeletedPage.jsp on GET /server/packages/delete/[id] request.
	 * 
	 * @param  id      package's ID
	 * @param  model   passed packages model
	 * @return         facelet name
	 */
   	@RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String deletePackage (@RequestParam(value="id", required=true) Integer id, 
    		@RequestParam(value="pid", required=true) Integer programId, Model model) {
    
		  logger.debug("Received request to delete existing package.");
		  
		  PackageDao pd = null;
		     try {
					ApplicationContext context = new ClassPathXmlApplicationContext ("beans.xml");
					pd = (PackageDao) context.getBean ("packageDao");
				} catch (Exception e) {
					logger.error ("FAIL: " + e);
				}
		  
		  pd.delete(id);
		 
		  model.addAttribute("id", id);
		  model.addAttribute("programId", programId);
		      
		  return "packageDeletedPage";
	}
   	
   	/**
   	 * Renders form to edit existing package.
   	 * 
	 * Let JSF run /views/packageEditPage.jsp on GET /server/packages/edit/[id] request.
	 * 
	 * @param  id    package's ID
	 * @param  model passed packages model
	 * @return       facelet name
	 */
   	@RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String editPackage (@RequestParam(value="id", required=true) Integer id, 
    		@RequestParam(value="pid", required=true) Integer programId, Model model) {
	    logger.debug("Received request to show package edit page");
	     
	    PackageDao packageDao = null;
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext ("beans.xml");
	   		packageDao = (PackageDao) context.getBean ("packageDao");
		} catch (Exception e) {
			logger.error ("FAIL: " + e);
		}
	
		Package tempPack = packageDao.findById(id);
		
	    model.addAttribute ("pack", tempPack);
	    model.addAttribute ("programId", programId);
	      
	    return "packageEditPage";
   	}
 
   	/**
   	 * Saves changes.
   	 * 
	 * Let JSF run /views/packageEditPage.jsp on POST /server/packages/edit/[id] request.
	 * 
	 * On errors let JSF run /views/newPackage.jsp on POST /server/packages/add request.
	 * Otherwise creates new package and redirects to list of packages. 
	 * 
	 * @param  pack    package
	 * @param  id      package's id
	 * @param  model   passed packages model
	 * @return         facelet name
	 */
   	@RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String saveEdit (@ModelAttribute("pack") Package pack, @RequestParam(value="id", required=true) Integer id, Model model) {
	    logger.debug("Received request to update package");
	     
	    PackageDao pd = null;
	    try {
			ApplicationContext context = new ClassPathXmlApplicationContext ("beans.xml");
			pd = (PackageDao) context.getBean ("packageDao");
		} catch (Exception e) {
			logger.error ("FAIL: " + e);
		}
	     
	    pd.update (pack);
	    model.addAttribute ("id", id);
	    model.addAttribute ("programId", pack.getProgramId());
	   
	    return "packageEditedPage";
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
	 * Returns PackageDao.
	 * 
	 * @return PackageDao
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
