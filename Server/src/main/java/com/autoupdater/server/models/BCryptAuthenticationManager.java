package com.autoupdater.server.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.autoupdater.server.interfaces.UserDao;
 
/**
 * Authentication manager using BCrypt to encrypt user's password.
 */
@SuppressWarnings("deprecation")
public class BCryptAuthenticationManager implements AuthenticationManager {
	/**
	 * Manager's logger.
	 */
	protected static Logger logger = Logger.getLogger ("Authentication Manager");
	
	/**
	 * User DAO.
	 */
	private UserDao userDao = null;
  
	/**
	 * Authenticate user.
	 * 
	 * @param auth authentication data passed by Spring Security 
	 * @return     result of authentication
	 */
	public Authentication authenticate (Authentication auth)
	throws AuthenticationException {
		logger.info ("Performing custom authentication");
		
		if (userDao == null) {
			logger.info ("Connecting to DB");
		    try {
				ApplicationContext context = new ClassPathXmlApplicationContext ("beans.xml");
				userDao = (UserDao) context.getBean ("userDao");
			} catch (Exception e) {
				logger.error ("DB connection fail: " + e);
				throw new BadCredentialsException("Cannot connect to database!");
			}
		}
		
		User user = null;
	    
		logger.info ("Finding user");
		try {
			user = userDao.findByUsername (auth.getName ());
		} catch (Exception e) {
			logger.error ("User does not exists (exception)!");
			throw new BadCredentialsException("User does not exists!");
		}
		if (user == null) {
			logger.error ("User does not exists (null)!");
			throw new BadCredentialsException("User does not exists!");
		}
		
		logger.info ("Matching passwords hash("+auth.getCredentials ().toString ()+")=?="+user.getPassword ());
		if (!BCrypt.checkpw(auth.getCredentials ().toString (), user.getPassword ())) {
			logger.error ("Password doesn't match!");
			throw new BadCredentialsException("Password doesn't match!");
		}

		logger.info ("User details are good and ready to go");
		return new UsernamePasswordAuthenticationToken (
			auth.getName (), 
			auth.getCredentials(), 
			getAuthorities(user.isAdmin (), user.isPackageAdmin ())
		);
	}
  
	/**
	 * Creates collection of authorities basing on user data. 
	 * 
	 * @param admin        whether user is admin
	 * @param packageAdmin whether user is package admin
	 * @return             collection of authorities
	 */
	public Collection<GrantedAuthority> getAuthorities (boolean admin, boolean packageAdmin) {
		List<GrantedAuthority> authList = new ArrayList<GrantedAuthority> (3);
		
		logger.debug ("Grant ROLE_USER to this user");
		authList.add (new GrantedAuthorityImpl ("ROLE_USER"));
		
		if (admin) {
			logger.debug ("Grant ROLE_ADMIN to this user");
			authList.add (new GrantedAuthorityImpl ("ROLE_ADMIN"));
		}
		
		if (admin || packageAdmin) {
			logger.debug ("Grant ROLE_PACKAGE_ADMIN to this user");
			authList.add (new GrantedAuthorityImpl ("ROLE_PACKAGE_ADMIN"));
		}
   
		return authList;
	}
}