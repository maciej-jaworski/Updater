package com.autoupdater.server.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Responsible for rendering home page.
 */
@Controller
@RequestMapping("/")
public class HomeController extends AppController {
	/**
	 * Controller's logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger (HomeController.class);

	/**
	 * Renders home page.
	 * 
	 * Let JSF run /views/home.jsp on GET /server/home request.
	 * 
	 * @return facelet name
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String Home () {
		logger.info ("Rendering homepage");
		
		return "Home";
	}
}