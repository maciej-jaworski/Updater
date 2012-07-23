package com.autoupdater.server.interfaces;

import java.util.List;

import com.autoupdater.server.models.Program;

/**
 * Interface for Program DAO.
 * 
 * Which implementation is returned is set in WEB-INF/classes/beans.xml file.
 */
public interface ProgramDao {
	/**
	 * Creates program in database from passed object.
	 * 
	 * @param prog source of data
	 */
	public void create (Program prog);
	
	/**
	 * Updates program in database from passed object.
	 * 
	 * @param prog source of data
	 */
	public void update (Program prog);
	
	/**
	 * Deletes program in database from passed object.
	 * 
	 * @param id entry's ID
	 */
	public void delete (int id);
	
	/**
	 * Returns program for passed ID.
	 * 
	 * @param id entry's ID
	 * @return   program
	 */
	public Program findById (int id);
	
	/**
	 * Returns program for passed name.
	 * 
	 * @param name program's name
	 * @return         program
	 */
	public Program findByName (String name);
	
	/**
	 * Returns all programs.
	 * 
	 * @return collection of programs
	 */
	public List<Program> findAll ();
	
	/**
	 * Returns all program's names.
	 * 
	 * @return collection of names
	 */
	public List<String> findAllNames ();
	
	/**
	 * Returns program's id for given name.
	 * Returns -1 for non-existing names.
	 * 
	 * @return id number
	 */
	public int getIdFromName(String name);
}
