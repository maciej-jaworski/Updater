package com.autoupdater.server.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.autoupdater.server.interfaces.PackageDao;
import com.autoupdater.server.interfaces.ProgramDao;
import com.autoupdater.server.interfaces.UpdateDao;
import com.autoupdater.server.models.ChangelogList;
import com.autoupdater.server.models.Program;
import com.autoupdater.server.models.ProgramList;
import com.autoupdater.server.models.ProgramPackages;
import com.autoupdater.server.models.Update;
import com.autoupdater.server.singletons.CacheStorage;

/**
 * Responsible for rendering informations for client and sending files. 
 */
@Controller
@RequestMapping("/update")
public class UpdateController {
	/**
	 * Controller's logger.
	 */
	protected static Logger logger = Logger.getLogger (UpdateController.class);
	
	/**
	 * Renders information about package by its name.
	 * 
	 * Runs on GET /server/update/info/[name] request.
	 * 
	 * @param name     package name
	 * @param response response that will be sent
	 * @return         response's content
	 */
	@RequestMapping(value="/info/{id}", method = RequestMethod.GET)
	public @ResponseBody Update getUpdateInXML (@PathVariable int id, HttpServletResponse response) {
		response.setContentType ("text/xml"); 

		UpdateDao ud = getUpdateDao ();
		
		Update info = ud.findNewestByPackageId (id);
 
		return info;
	}
	
	/**
	 * Renders package's changelog by its ID.
	 * 
	 * @param id       package ID
	 * @param response response that will be sent
	 * @return         response's content
	 */
	@RequestMapping(value="/changelogs/{id}", method = RequestMethod.GET)
	public @ResponseBody ChangelogList getChangelogs (@PathVariable int id, HttpServletResponse response) {
		response.setContentType ("text/xml"); 
		
		UpdateDao ud = getUpdateDao ();
		
		ChangelogList list = new ChangelogList(ud.findPackageChangelogs(id));
 
		return list;
	}
	
	/**
	 * Renders list of packages on server.
	 * 
	 * Runs on GET /server/update/getpackages request.
	 * 
	 * @param response response to be sent
	 * @return         response's content
	 */
	@RequestMapping(value="/getpackages", method = RequestMethod.GET)
	public @ResponseBody ProgramList getPackagesListInXML (HttpServletResponse response) {
		response.setContentType ("text/xml"); 
		
		PackageDao packd = getPackageDao();
		ProgramDao progd = getProgramDao();
		
		List<Program> programy = progd.findAll();
		List<ProgramPackages> temp = new ArrayList<ProgramPackages>();
		
		for (Program prog : programy)
		{
			temp.add(new ProgramPackages(prog.getName(),packd.findByProgramId(prog.getId())));
		}

		ProgramList list = new ProgramList(temp);
		
		return list;
	}
	
	/**
	 * Send file to client.
	 * 
	 * Runs on GET /server/update/download/[name] request.
	 * 
	 * @param name     filename
	 * @param response response to be sent
	 * @return         response's content - file 
	 */
	@RequestMapping(value = "/download/{id}", method = RequestMethod.GET)
	public @ResponseBody void getFile(@PathVariable int id, HttpServletResponse response, HttpServletRequest request) {   
		CacheStorage cs = getCacheStorage ();
		byte[] filedata = cs.getFile(id);
		
		String range = request.getHeader("Range");
		
		logger.info("Download"); 
		
		if (range != null)
		{
			logger.info("Values of range header : " + range); 
			String[] ranges = range.substring("bytes=".length()).split("-");
			int from = (ranges.length==1)? Integer.valueOf(ranges[0]) : 0;
			int to = (ranges.length==2)? Integer.valueOf(ranges[1]) : filedata.length;
			if (from > 0 && from < filedata.length && to > 0 && to <= filedata.length && from < to)
			{
				logger.info("Sending subpart of file from: " + from + " to " + to); 
				filedata = Arrays.copyOfRange(filedata, from, to);
			}
			else
				logger.error("Invalid values provided in range header, sending full file."); 
		}
	   
		try {
			response.setContentType("application/zip"); 
			response.setContentLength(filedata.length);
			InputStream is = new ByteArrayInputStream(filedata);
			IOUtils.copy(is, response.getOutputStream());
			response.flushBuffer();
	    } catch (IOException ex) {
	    	logger.error("Error sending file " + ex);
	    }
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
	
	/**
	 * Obtains UpdateDao.
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
	 * Obtains PackageDao.
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
	 * Obtains ProgramDao.
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
