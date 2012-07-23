package com.autoupdater.server.interfaces;

import java.util.List;

import com.autoupdater.server.models.PasswordEdit;
import com.autoupdater.server.models.User;

/**
 * Interface for User DAO.
 * 
 * Which implementation is returned is set in WEB-INF/classes/beans.xml file.
 */
public interface UserDao {
	/**
	 * Creates user in database from passed object.
	 * 
	 * @param user source of data
	 */
	public void create (User user);
	
	/**
	 * Updates user in database from passed object.
	 * 
	 * @param user source of data
	 */
	public void update (User user);
	
	/**
	 * Deletes user in database from passed object.
	 * 
	 * @param id entry's ID
	 */
	public void delete (int id);
	
	/**
	 * Creates new password for user.
	 * 
	 * @param id entry's ID
	 * @return   new password
	 */
	public String resetPwById (int id);
	
	/**
	 * Returns user for passed ID.
	 * 
	 * @param id entry's ID
	 * @return   user
	 */
	public User findById (int id);
	
	/**
	 * Returns user for passed login.
	 * 
	 * @param filename user's login
	 * @return         user
	 */
	public User findByUsername (String username);
	
	/**
	 * Returns user for passed name.
	 * 
	 * @param id user's name
	 * @return   user
	 */
	public User findByName (String name);
	
	/**
	 * Returns all users.
	 * 
	 * @return collection of users
	 */
	public List<User> findAll ();
	
	/**
	 * Returns all usernames.
	 * 
	 * @return collection of names
	 */
	public List<String> findAllUsernames ();
	
	public void changePasswordForId(PasswordEdit pe);
}
