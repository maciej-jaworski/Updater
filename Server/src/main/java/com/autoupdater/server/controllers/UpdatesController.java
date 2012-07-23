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

import com.autoupdater.server.interfaces.PackageDao;
import com.autoupdater.server.interfaces.UpdateDao;
import com.autoupdater.server.interfaces.UserDao;
import com.autoupdater.server.models.Update;
import com.autoupdater.server.models.User;
import com.autoupdater.server.singletons.CacheStorage;
import com.autoupdater.server.validators.UpdateValidator;

/**
 * Responsible for rendering updates panel.
 */
@Controller
@RequestMapping(value = "/updates")
public class UpdatesController {
	/**
	 * Controller's logger.
	 */
	protected static Logger logger = Logger.getLogger (UpdatesController.class);
	
	/**
	 * Renders list of packages.
	 * 
	 * Let JSF run /views/updatesShowPackages.jsp on GET /server/updates request.
	 * 
	 * @param model passed updates model
	 * @return      facelet name
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String getUpdates (Model model) {
	    logger.debug("Received request to show all updates");
			PackageDao pd = this.getPackageDao();
			
			UserDao ud = this.getUserDao();
			String username= (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			model.addAttribute("user",ud.findByUsername(username));
			
			model.addAttribute("packs", pd.findAll());
			
			return "updatesShowPackages";
	}
	
	/**
	 * Renders list of updates for package by its ID and its program ID.
	 * 
	 * Let JSF run /views/updatesShowAll.jsp on GET /server/updates/list?id=[id]&pid=[pid] request.
	 * 
	 * @param model passed updates model
	 * @return      facelet name
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String showUpdates (@RequestParam(value="id", required=true) Integer id,
			@RequestParam(value="pid", required=true) Integer programId, Model model) {
	    logger.debug("Received request to show all updates");

		    UpdateDao up = getUpdateDao();
	
		    List<Update> updates = up.findAllByPackageId(id);
	
		    model.addAttribute("updates", updates);
		    model.addAttribute("packId", id);
		    model.addAttribute("programId", programId);
		    
		    return "updatesShowAll";
	}
	
	/**
	 * Renders new update form. 
	 * 
	 * Let JSF run /views/updateAdd.jsp on GET /server/updates/add request.
	 * 
	 * @param model passed update model
	 * @return      facelet name
	 */
	@RequestMapping(value = "/add",method = RequestMethod.GET)
	public String addUpdate(@RequestParam(value="id", required=true) Integer id, 
    		@RequestParam(value="pid", required=true) Integer programId, Model model) {
		logger.info ("Generated new update form.");
			
		model.addAttribute ("update", new Update ());
		model.addAttribute("packageId", id);
			
		return "updateAdd";
	}
	
	/**
	 * Parses sent new updates data - renders form again if errors occurred, otherwise adds package and get back to list of updates.
     * 
	 * On errors let JSF run /views/updateAdd.jsp on POST /server/updates/add request.
	 * Otherwise creates new package and redirects to list of packages.
	 * 
	 * @param update update
	 * @param result validation result
	 * @param model  passed updates model
	 * @return       facelet name or redirect
	 */
	@RequestMapping(value = "/add",method = RequestMethod.POST)
	public String RegisterParse (@Valid@ModelAttribute(value="update") Update update, BindingResult result, Model model) {
		(new UpdateValidator ()).validate (update, result);
		if (result.hasErrors ()) {
			String err = "";
			for(ObjectError error : result.getAllErrors ())
				err += "Error: " + error.getCode () +  " - " + error.getDefaultMessage () + "\n";
			logger.error (err);
			
			return "redirect:add";
		} else {	
			
			UserDao ud = this.getUserDao();
			String username= (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			User user = ud.findByUsername(username);
			
			update.setUploader_id(user.getId());
			
			try {
				UpdateDao up = getUpdateDao ();
				up.create (update);
			} catch (Exception e) {
				logger.error ("FAIL: " + e);
			}
				
			logger.info ("Update "+update+" successfully added!");
			
			PackageDao pd = this.getPackageDao();
			
			return "redirect:list?id=" + update.getPackage_id() + "&pid=" + pd.getProgramIdFromPackageId(update.getPackage_id());
		}
	}
		
	/**
   	 * Renders form to edit existing package.
   	 * 
	 * Let JSF run /views/updateEditPage.jsp on GET /server/updates/edit/[id] request.
	 * 
	 * @param  id    updates's ID
	 * @param  model passed updates model
	 * @return       facelet name
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String editUpdate (@RequestParam(value="id", required=true) Integer id, Model model) {
		logger.debug("Received request to show update edit page");
	     
	    UpdateDao ud = this.getUpdateDao();
	    Update up = ud.findById(id);
	  
		model.addAttribute("update", up); 
	     
		return "updateEditPage";
	}
		
	/**
   	 * Saves changes.
   	 * 
	 * Let JSF run /views/updateEditPage.jsp on POST /server/packages/edit/[id] request.
	 * 
	 * On errors let JSF run /views/newPackage.jsp on POST /server/packages/add request.
	 * Otherwise saves update and redirects to list of packages. 
	 * 
	 * @param  pack    update
	 * @param  id      updates's id
	 * @param  model   passed updates model
	 * @return         facelet name or redirect
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String executeUpdateEdit(@Valid@ModelAttribute(value="update") Update update, BindingResult result, @RequestParam(value="id", required=true) Integer id, Model model) {
		(new UpdateValidator ()).validate (update, result);
		if (result.hasErrors ()) {
			String err = "";
			for(ObjectError error : result.getAllErrors ())
				err += "Error: " + error.getCode () +  " - " + error.getDefaultMessage () + "\n";
			logger.error (err); 
				
			return "redirect:edit";
		} else {			
			try {
				UpdateDao up = this.getUpdateDao();
				up.update (update);
			} catch (Exception e) {
				logger.error ("FAIL: " + e);
			}
				
			logger.info ("Update "+update+" successfully edited!");
			
			PackageDao pd = this.getPackageDao();
			
			return "redirect:list?id=" + update.getPackage_id() + "&pid=" + pd.getProgramIdFromPackageId(update.getPackage_id());
		}
	}
	
	/**
	 * Deletes update.
	 * 
	 * @param id     update ID
	 * @param packId package ID
	 * @param progId program ID
	 * @param model  model
	 * @return       facelet name
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String delete (@RequestParam(value="id", required=true) Integer id, 
    		@RequestParam(value="packid", required=true) Integer packId, 
    		@RequestParam(value="progid", required=true) Integer progId, Model model) {
		logger.debug ("Received request to delete existing update");
		  
		UpdateDao ud = this.getUpdateDao ();
		Update up = ud.findById(id);
		
		UserDao userDao = this.getUserDao();
		String username= (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user = userDao.findByUsername(username);
		
		if (user.getId() == up.getUploader_id() || user.isPackageAdmin())
		{
			ud.delete (id);
			CacheStorage cs = this.getCacheStorage ();
			cs.resetFile (id);   
			
			model.addAttribute ("packId", packId);
			model.addAttribute ("progId", progId);
			model.addAttribute ("id", id);
			      
			return "updateDeletedPage";
		}
		else
		{
			model.addAttribute("message", "You don't have permission to perform this action");
			model.addAttribute("backUrl", "/updates/list?id=" + packId + "&pid=" + progId);
			
			return "errorPage";
		}
	}
	
	/**
	 * Returns UpdateDao.
	 * 
	 * @return UpdateDao
	 */
	private UpdateDao getUpdateDao () {
		UpdateDao up = null;
	    try {
			ApplicationContext context = new ClassPathXmlApplicationContext ("beans.xml");
			up = (UpdateDao) context.getBean ("updateDao");
		} catch (Exception e) {
			logger.error ("FAIL: " + e);
		}
	     
		return up;
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
	 * Obtains CacheStorage.
	 * 
	 * @return CacheStorage
	 */
	private CacheStorage getCacheStorage () {
		CacheStorage cs = null;
	    try {
			ApplicationContext context = new ClassPathXmlApplicationContext ("beans.xml");
			cs = (CacheStorage) context.getBean ("cacheStorage");
		} catch (Exception e) {
			logger.error ("FAIL: " + e);
		}
	     
		return cs;
	}
}
