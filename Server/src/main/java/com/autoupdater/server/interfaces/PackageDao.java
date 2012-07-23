package com.autoupdater.server.interfaces;

import java.util.List;

import com.autoupdater.server.models.Package;

/**
 * Interface for Package DAO.
 * 
 * Which implementation is returned is set in WEB-INF/classes/beans.xml file.
 */
public interface PackageDao {
	/**
	 * Creates package in database from passed object.
	 * 
	 * @param pack source of data
	 */
	public void create (Package pack);
	
	/**
	 * Updates package in database from passed object.
	 * 
	 * @param pack source of data
	 */
	public void update (Package pack);
	
	/**
	 * Deletes package in database from passed object.
	 * 
	 * @param id entry's ID
	 */
	public void delete (int id);
	
	/**
	 * Returns package for passed ID.
	 * 
	 * @param id entry's ID
	 * @return   package
	 */
	public Package findById (int id);
	
	/**
	 * Returns all packages.
	 * 
	 * @return collection of packages
	 */
	public List<Package> findAll ();
	
	/**
	 * Returns all packages for given programId.
	 * 
	 * @return collection of packages
	 */
	public List<Package> findByProgramId (int programId);
	
	/**
	 * Returns all package names.
	 * 
	 * @return collection of names
	 */
	public List<String> findAllNames ();
	
	/**
	 * Returns all package names for given programId.
	 * 
	 * @return collection of names
	 */
	public List<String> findAllNames (int programId);
	
	/**
	 * Returns program by ID of package that belongs to it.
	 * 
	 * @param  packageId package ID
	 * @return           program
	 */
	public int getProgramIdFromPackageId(int packageId);
}
